package com.trains.ui;

import com.trains.cargo.Cargo;
import com.trains.cargo.CargoTransfer;
import com.trains.network.Node;
import com.trains.network.Station;
import com.trains.routing.Route;
import com.trains.routing.Router;
import com.trains.sim.CargoSpawner;
import com.trains.sim.Simulation;
import com.trains.utils.GridPos;
import com.trains.vehicles.Vehicle;
import com.trains.cargo.CargoHold;
import com.trains.vehicles.Itinerary;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class SimulationController {
    // Decides when the simulation runs
    /*
     * The Simulation class in sim/ decides what happens in one tick
     * vs this class which decides when those steps happen.
     *
     * Keep apart so sim does not have to touch UI at all; which means that we can
     * run JUnit tests on Simulation.tick() and ensure the result is as expected.
     */

    // While the comment above is the desired structure, this is not how it played out, we need to move much of this class into Simulation

    /**
     * Simulated seconds per tick. Vehicle.update integrates speed against dt, so holding the step
     * constant keeps movement identical no matter the frame rate or the speed multiplier.
     */

    private static final int TRAIN_CAPACITY = 12;
    private static final float TRAIN_MAX_SPEED = 14f;
    private static final float TRAIN_ACCELERATION = 6f;
    private static final double FIXED_DT = 1.0 / 60.0;
    /** Longest real frame we will act on, so a stall cannot teleport vehicles across the network. */
    private static final double MAX_FRAME_SECONDS = 0.25;
    /** Simulated seconds a vehicle waits at a station to unload and board. */
    private static final double DWELL_SECONDS = 2.0;
    /** Simulated seconds between automatic passenger spawns. */
    private static final double SPAWN_INTERVAL_SECONDS = 3.0;
    /**
     * How long a station may go unserved before it outranks every other candidate on demand.
     * This is what bounds the worst case, rather than only improving the average.
     */
    private static final double STARVATION_SECONDS = 60.0;
    /** Origin/destination pairs tried per spawn before giving up until the next spawn. */
    private static final int SPAWN_ATTEMPTS = 8;
    /** Passengers placed on the network up front, so the screen is not empty at t=0. */
    private static final int SEED_PASSENGERS = 12;
    private static final int FRAME_DELAY_MS = 16;

    public static final double MIN_SPEED_MULTIPLIER = 0.25;
    public static final double MAX_SPEED_MULTIPLIER = 8.0;

    private final Simulation sim;
    private final Router router;
    /*
     * Simulation keeps its own CargoSpawner private and its tick() is still a no-op, so the
     * controller drives a spawner of its own. CargoSpawner only needs the network, so both
     * spawners act on exactly the same stations.
     */
    private final CargoSpawner spawner;
    private final Random random = new Random();
    /** Remaining dwell time per vehicle. Identity keyed because Vehicle has no value equality. */
    private final Map<Vehicle, Double> dwellRemaining = new IdentityHashMap<>();
    /**
     * When each station was last called at, as a simulated timestamp.
     * <p>
     *     Keyed by position rather than by Station so that removing a station cannot leave this map
     *     holding the only reference to it. Network keys its own nodes the same way.
     * </p>
     */
    private final Map<GridPos, Double> lastServed = new HashMap<>();

    /**
     * Created on the first {@link #start()} rather than in the constructor, because
     * AnimationTimer's constructor reaches for the JavaFX toolkit and so needs a display. Building
     * it lazily keeps a controller constructible on a headless machine.
     */
    private Timer timer;

    private Runnable onFrame;
    private boolean running;
    private double speedMultiplier = 1.0;
    private double accumulator;
    private double elapsedSeconds;
    private double spawnCountdown = SPAWN_INTERVAL_SECONDS;
    private int deliveredCount;
    private long lastFrameNanos;

    public SimulationController() {
        this(new Simulation());
    }

    public SimulationController(Simulation sim) {
        this.sim = Objects.requireNonNull(sim);
        this.router = new Router(sim.getNetwork());
        this.spawner = new CargoSpawner(sim.getNetwork());

        // seed through the same validated path as everything else, so there is one rule
        for (int i = 0; i < SEED_PASSENGERS; i++) {
            spawnPassenger();
        }
    }

    public Simulation getSimulation() {
        return sim;
    }

    /**
     * Sets what to run at the end of every frame, whether or not the simulation is advancing.
     * <p>
     *     This is how rendering gets driven without the controller having to know about the view.
     * </p>
     * @param onFrame the action to run per frame, or null for none
     */
    public void setOnFrame(Runnable onFrame) {
        this.onFrame = onFrame;
    }

    /**
     * Starts the frame timer and begins advancing the simulation
     */
    public void start() {
        if (timer == null) {
            timer = new Timer(FRAME_DELAY_MS, event -> pulse(System.nanoTime()));
        }
        lastFrameNanos = 0;
        running = true;
        timer.start();
    }

    /**
     * Stops the frame timer entirely.
     * <p>
     *     Must be called when leaving the screen, otherwise the timer keeps firing after the scene
     *     root has been swapped out.
     * </p>
     */
    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        running = false;
    }

    /**
     * Resumes advancing simulated time. Frames keep being drawn either way.
     */
    public void play() {
        running = true;
    }

    /**
     * Pauses simulated time. Frames keep being drawn, so the canvas still repaints.
     */
    public void pause() {
        running = false;
    }

    /**
     * @return true if simulated time is advancing
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets how much faster than real time the simulation runs
     * @param speedMultiplier the multiplier, clamped between {@value #MIN_SPEED_MULTIPLIER} and {@value #MAX_SPEED_MULTIPLIER}
     */
    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = Math.clamp(speedMultiplier, MIN_SPEED_MULTIPLIER, MAX_SPEED_MULTIPLIER);
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    /**
     * @return simulated seconds run so far, not counting paused time
     */
    public double getElapsedSeconds() {
        return elapsedSeconds;
    }

    /**
     * @return how many cargo units have reached their destination
     */
    public int getDeliveredCount() {
        return deliveredCount;
    }

    /**
     * Spawns one passenger between two random stations that a train can actually travel between.
     * <p>
     *     Deliberately lenient: a network with no routable pair simply produces no passengers rather
     *     than an error, because once nodes can be added and removed from the UI a disconnected
     *     network is a legal state. The point is only to never create a journey that cannot be
     *     completed, since undeliverable cargo would sit on a platform forever.
     * </p>
     * <p>
     *     Goes through the two argument {@link CargoSpawner#spawnPassenger(Station, Station)} rather
     *     than the no argument one, which picks its own pair without checking for a route.
     * </p>
     * @return true if a passenger was spawned
     */
    public boolean spawnPassenger() {
        List<Station> stations = connectedStations();
        if (stations.size() < 2) {
            return false;
        }

        for (int attempt = 0; attempt < SPAWN_ATTEMPTS; attempt++) {
            Station origin = stations.get(random.nextInt(stations.size()));
            Station destination = stations.get(random.nextInt(stations.size()));
            if (origin == destination) {
                continue; // findRoute rejects an origin equal to its destination
            }
            if (router.findRoute(origin, destination) == null) {
                continue; // different components, so this passenger could never arrive
            }
            return spawner.spawnPassenger(origin, destination);
        }
        return false;
    }

    /**
     * @return the total cargo units waiting across every station
     */
    public int getWaitingUnits() {
        int waiting = 0;
        for (Node node : sim.getNetwork().getNodes().values()) {
            if (node instanceof Station station) {
                waiting += station.getCargoHold().getUsedUnits();
            }
        }
        return waiting;
    }

    /**
     * @return the total cargo units currently aboard vehicles
     */
    public int getOnboardUnits() {
        int onboard = 0;
        for (Vehicle vehicle : sim.getVehicles()) {
            onboard += vehicle.getCargoHold().getUsedUnits();
        }
        return onboard;
    }

    private void pulse(long now) {
        if (lastFrameNanos == 0) {
            lastFrameNanos = now; // first frame after start, no delta to act on yet
        }
        double frameSeconds = (now - lastFrameNanos) / 1_000_000_000.0;
        lastFrameNanos = now;

        if (running) {
            advance(Math.min(frameSeconds, MAX_FRAME_SECONDS) * speedMultiplier);
        }

        if (onFrame != null) {
            onFrame.run(); // every pulse, so the canvas still repaints while paused or resizing
        }
    }

    /**
     * Runs whole fixed steps out of the time handed in, keeping the remainder for the next call.
     * <p>
     *     Package private rather than private so the loop can be driven directly, without a frame
     *     timer and so without a display.
     * </p>
     * @param simSeconds simulated seconds to advance by
     */
    void advance(double simSeconds) {
        accumulator += simSeconds;
        while (accumulator >= FIXED_DT) {
            sim.tick(FIXED_DT);
            afterTick(FIXED_DT);
            accumulator -= FIXED_DT;
            elapsedSeconds += FIXED_DT;
        }
    }

    /**
     * The station side of a tick, which Simulation.tick does not do itself.
     */
    private void afterTick(double dt) {
        // safe to iterate: nothing in here adds or removes vehicles
        for (Vehicle vehicle : sim.getVehicles()) {
            serviceVehicle(vehicle, dt);
        }

        spawnCountdown -= dt;
        if (spawnCountdown <= 0) {
            spawnPassenger(); // false just means a full origin or too few stations, both fine here
            spawnCountdown = SPAWN_INTERVAL_SECONDS;
        }
    }

    private void serviceVehicle(Vehicle vehicle, double dt) {
        if (!vehicle.isStopped()) {
            return;
        }

        /*
         * A finished vehicle picks up its next route before it boards anyone. Itinerary.willVisit
         * only looks at the legs still ahead, so on a completed route it always answers false and
         * nobody would ever board at a terminus.
         */
        if (vehicle.isRouteComplete() && !dwellRemaining.containsKey(vehicle)) {
            redispatch(vehicle);
        }

        /*
         * Itinerary.advance() moves entryNode onto the node just reached, so for a stopped vehicle
         * getEntryNode() is where it actually is. Vehicle only stops at stations or at the end of a
         * route, so anything else here is a vehicle that has nothing to wait for.
         */
        if (vehicle.getEntryNode() instanceof Station station && !dwell(vehicle, station, dt)) {
            return;
        }

        if (!vehicle.isRouteComplete()) {
            vehicle.go();
        }
        // still no route, so it stays put and redispatch is retried after the next dwell
    }

    /**
     * Runs a vehicle's stop at a station, unloading and boarding on arrival
     * @return true once the dwell has elapsed and the vehicle is free to leave
     */
    private boolean dwell(Vehicle vehicle, Station station, double dt) {
        Double remaining = dwellRemaining.get(vehicle);

        if (remaining == null) { // just pulled in
            // Vehicle stops at every Station on its route, so this branch sees every visit
            lastServed.put(station.getPos(), elapsedSeconds);
            deliveredCount += unload(vehicle, station);
            vehicle.boardPassengers(station);
            dwellRemaining.put(vehicle, DWELL_SECONDS);
            return false;
        }

        remaining -= dt;
        if (remaining > 0) {
            dwellRemaining.put(vehicle, remaining);
            return false;
        }

        dwellRemaining.remove(vehicle);
        return true;
    }

    /**
     * Drops off any cargo that has reached its destination
     * @return how many cargo units were delivered
     */
    private int unload(Vehicle vehicle, Station station) {
        int delivered = 0;
        /*
         * getContents() hands back a copy, so removing from the hold while iterating is safe, and
         * deliverCargo does the destination check itself rather than us repeating it here.
         */
        for (Cargo cargo : vehicle.getCargoHold().getContents()) {
            if (CargoTransfer.deliverCargo(vehicle.getCargoHold(), cargo, station.getPos())) {
                delivered += cargo.getUnits();
            }
        }
        return delivered;
    }

    /**
     * Gives a vehicle that has finished its route a new one, so trains keep circulating.
     * <p>
     *     Leaves the vehicle stopped - departure is the dwell's job.
     * </p>
     */
    private void redispatch(Vehicle vehicle) {
        Node origin = vehicle.getEntryNode();
        List<Candidate> candidates = scoreCandidates(origin);
        if (candidates.isEmpty()) {
            return; // nothing reachable, so it stays put and tries again after the next dwell
        }

        /*
         * Shuffle before sorting: List.sort is stable, so without this every equal score would
         * resolve to network insertion order and the same station would win every time.
         */
        Collections.shuffle(candidates, random);
        candidates.sort(BY_PRIORITY);

        vehicle.dispatch(candidates.get(0).route());
    }

    /**
     * Ranks where a vehicle standing at {@code origin} should go next.
     * <p>
     *     A starving station beats everything, which is the fairness bound. Otherwise it is demand
     *     first, and route length only ever separates otherwise equal candidates.
     * </p>
     */
    private static final Comparator<Candidate> BY_PRIORITY =
            Comparator.comparing(Candidate::starving).reversed()
                    .thenComparing(Comparator.comparingInt(Candidate::demand).reversed())
                    .thenComparing(Comparator.comparingInt(Candidate::pickup).reversed())
                    .thenComparing(Comparator.comparingDouble(Candidate::staleness).reversed())
                    .thenComparingDouble(candidate -> candidate.route().getTotalLength());

    /**
     * A possible next destination, with everything the ranking needs.
     * @param station the candidate destination
     * @param route how to get there from where the vehicle is now
     * @param demand units waiting at the vehicle's current station bound for this candidate
     * @param pickup units waiting at the candidate itself
     * @param staleness simulated seconds since the candidate was last called at
     * @param starving whether that staleness has passed {@link #STARVATION_SECONDS}
     */
    private record Candidate(Station station, Route route, int demand, int pickup,
                             double staleness, boolean starving) {
    }

    private List<Candidate> scoreCandidates(Node origin) {
        List<Candidate> candidates = new ArrayList<>();

        for (Station station : stations()) {
            if (station == origin) {
                continue;
            }
            /*
             * Routing every candidate up front both supplies the length for the tiebreak and drops
             * anything in another component, which is what a partially connected network needs.
             * A handful of A* runs once per dwell is not worth optimising.
             */
            Route route = router.findRoute(origin, station);
            if (route == null) {
                continue;
            }
            double staleness = elapsedSeconds - lastServed.getOrDefault(station.getPos(), 0.0);
            candidates.add(new Candidate(
                    station,
                    route,
                    demandFor(origin, station),
                    station.getCargoHold().getUsedUnits(),
                    staleness,
                    staleness > STARVATION_SECONDS));
        }

        return candidates;
    }

    /**
     * How much cargo waiting where the vehicle is now is trying to reach the given station.
     * <p>
     *     This is the signal that actually converts into deliveries: boardPassengers only lets cargo
     *     on if Itinerary.willVisit says the route reaches its destination, so those passengers
     *     cannot move at all until a train is dispatched their way.
     * </p>
     */
    private int demandFor(Node origin, Station destination) {
        if (!(origin instanceof Station originStation)) {
            return 0;
        }
        int demand = 0;
        for (Cargo cargo : originStation.getCargoHold().getContents()) {
            if (cargo.getDestination().equals(destination.getPos())) {
                demand += cargo.getUnits();
            }
        }
        return demand;
    }

    private List<Station> stations() {
        List<Station> stations = new ArrayList<>();
        for (Node node : sim.getNetwork().getNodes().values()) {
            if (node instanceof Station station) {
                stations.add(station);
            }
        }
        return stations;
    }

    /**
     * @return the stations that have at least one pathway, so are worth considering at all
     */
    private List<Station> connectedStations() {
        List<Station> stations = new ArrayList<>();
        for (Station station : stations()) {
            if (!station.getConnections().isEmpty()) {
                stations.add(station);
            }
        }
        return stations;
    }

    public boolean spawnVehicle() {
    List<Station> stations = connectedStations();
    if (stations.size() < 2) {
        return false;
    }

    for (int attempt = 0; attempt < SPAWN_ATTEMPTS; attempt++) {
        Station origin = stations.get(random.nextInt(stations.size()));
        Station destination = stations.get(random.nextInt(stations.size()));
        if (origin == destination) {
            continue;
        }
        Route route = router.findRoute(origin, destination);
        if (route == null) {
            continue;
        }
        sim.addVehicle(new Vehicle(
                new Itinerary(route),
                TRAIN_MAX_SPEED,
                TRAIN_ACCELERATION,
                CargoHold.mixed(TRAIN_CAPACITY)));
        return true;
    }
    return false;
    }

}
