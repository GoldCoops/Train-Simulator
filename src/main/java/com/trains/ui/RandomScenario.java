package com.trains.ui;

import com.trains.cargo.CargoHold;
import com.trains.network.Network;
import com.trains.network.Node;
import com.trains.network.Station;
import com.trains.routing.Route;
import com.trains.routing.Router;
import com.trains.sim.Simulation;
import com.trains.utils.GridPos;
import com.trains.vehicles.Itinerary;
import com.trains.vehicles.Vehicle;
import com.trains.utils.StationNames;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Builds a random network in place of {@link DemoScenario}'s fixed one.
 * <p>
 *     Connectivity is guaranteed by wiring the stations into a random spanning tree first, then
 *     adding a few extra tracks on top for loops. Without the extra tracks the network would be a
 *     tree, and a tree gives {@code Router.findRoute} exactly one path between any two stations,
 *     which makes redispatch's route-length tiebreak meaningless.
 * </p>
 * <p>
 *     Follows the same shape as {@link DemoScenario#build()} so either can be dropped into
 *     {@code new SimulationController(...)} interchangeably.
 * </p>
 */
public final class RandomScenario {
    private static final int STATION_CAPACITY = 30;
    private static final int TRAIN_CAPACITY = 12;
    private static final float TRAIN_MAX_SPEED = 14f;
    private static final float TRAIN_ACCELERATION = 6f;

    /** GridPos requires non-negative multiples of 5, so stations are placed on a grid of this cell size. */
    private static final int CELL = 5;

    private static final int DEFAULT_STATION_COUNT = 6;
    private static final int DEFAULT_GRID_COLS = 24;
    private static final int DEFAULT_GRID_ROWS = 24;
    private static final int DEFAULT_EXTRA_TRACKS = 2;

    /** Real Sydney station names, used instead of "Station 1", "Station 2", etc. */
    private static final List<String> STATION_NAMES = List.of(
            "Central", "Town Hall", "Wynyard", "Circular Quay", "Redfern",
            "Parramatta", "Chatswood", "Bondi Junction", "Strathfield", "Hornsby");

    private RandomScenario() {
    }

    /**
     * Builds a random simulation with a fresh, unseeded {@link Random} and default sizing.
     * @return a simulation holding a random network and its trains, with no cargo yet
     */
    public static Simulation build() {
        return build(new Random());
    }

    /**
     * Builds a random simulation from an existing {@link Random} and default sizing.
     * @param random source of randomness; pass a seeded instance for a reproducible layout
     * @return a simulation holding a random network and its trains, with no cargo yet
     */
    public static Simulation build(Random random) {
        return build(random, DEFAULT_STATION_COUNT, DEFAULT_GRID_COLS, DEFAULT_GRID_ROWS, DEFAULT_EXTRA_TRACKS);
    }

    /**
     * Builds a random simulation from a seed, so the same seed always produces the same network.
     * @param seed the seed to drive generation with
     * @return a simulation holding a random network and its trains, with no cargo yet
     */
    public static Simulation build(long seed) {
        return build(new Random(seed), DEFAULT_STATION_COUNT, DEFAULT_GRID_COLS, DEFAULT_GRID_ROWS, DEFAULT_EXTRA_TRACKS);
    }

    /**
     * Builds a random simulation with full control over sizing.
     * @param random source of randomness; pass a seeded instance for a reproducible layout
     * @param stationCount how many stations to place
     * @param gridCols width of the placement grid, in cells of {@value #CELL} world units
     * @param gridRows height of the placement grid, in cells of {@value #CELL} world units
     * @param extraTracks tracks added on top of the spanning tree, for alternate routes
     * @return a simulation holding a random network and its trains, with no cargo yet
     * @throws NullPointerException if random is null
     */
    public static Simulation build(Random random, int stationCount, int gridCols, int gridRows, int extraTracks) {
        Network network = new Network();
        List<Station> stations = placeStations(network, random, stationCount, gridCols, gridRows);

        connectSpanningTree(network, random, stations);
        addExtraTracks(network, random, stations, extraTracks);

        // mutable list, so Simulation.addVehicle works
        Simulation sim = new Simulation(network);
        Router router = new Router(network);
        addVehicles(sim, router, random, stations);

        // no passengers here - SimulationController seeds them, same as DemoScenario
        return sim;
    }

    /**
     * Places stations at distinct random grid cells.
     * <p>
     *     Uses {@code network.getNodes()} to reject an already-occupied cell rather than a minimum
     *     spacing check, since GridPos equality is exact and that is the only collision that matters
     *     to {@code network.addStation}.
     * </p>
     */
    private static List<Station> placeStations(Network network, Random random, int count, int gridCols, int gridRows) {
        List<Station> stations = new ArrayList<>();
        List<String> names = StationNames.randomNames(count);
        int attempts = 0;
        int maxAttempts = count * 50; // generous; only runs out on a grid too small for the count

        while (stations.size() < count && attempts < maxAttempts) {
            attempts++;
            GridPos pos = new GridPos(random.nextInt(gridCols) * CELL, random.nextInt(gridRows) * CELL);
            if (network.getNodes().containsKey(pos)) {
                continue; // taken, try another cell
            }

            Station station = network.addStation(pos, STATION_CAPACITY);
            station.setName(names.get(stations.size()));
            stations.add(station);
        }

        return stations;
    }

    /**
     * Wires every station into one component with exactly {@code stations.size() - 1} tracks.
     * <p>
     *     Each new station connects to a random already-connected one rather than to the previous
     *     station added, so the result branches instead of coming out as a single long corridor.
     * </p>
     */
    private static void connectSpanningTree(Network network, Random random, List<Station> stations) {
        if (stations.size() < 2) {
            return;
        }

        List<Station> unconnected = new ArrayList<>(stations);
        List<Station> connected = new ArrayList<>();
        connected.add(unconnected.remove(random.nextInt(unconnected.size())));

        while (!unconnected.isEmpty()) {
            Station from = connected.get(random.nextInt(connected.size()));
            Station to = unconnected.remove(random.nextInt(unconnected.size()));
            connectManhattan(network, random, from, to);
            connected.add(to);
        }
    }

    /**
     * Connects two nodes with straight horizontal/vertical track only, never a diagonal.
     * <p>
     *     {@code SimulationView} draws one straight line per track, so two arbitrary stations
     *     connected directly usually come out as a diagonal, and a handful of those crossing each
     *     other is what makes a random layout look tangled. If the pair doesn't already share a row
     *     or column, this bends the track through an elbow junction instead - the same shape
     *     {@link DemoScenario} uses by hand (station - junction - station).
     * </p>
     */
    private static void connectManhattan(Network network, Random random, Node a, Node b) {
        int ax = (int) a.getX();
        int ay = (int) a.getY();
        int bx = (int) b.getX();
        int by = (int) b.getY();

        if (ax == bx || ay == by) {
            network.connectNodes(a, b); // already lined up, one straight segment is enough
            return;
        }

        // two possible elbows - (a's column, b's row) or (b's column, a's row) - pick one at
        // random so tracks don't all bend the same way, and fall back to the other if it's occupied
        // by an unrelated node that isn't safe to route through
        GridPos elbowA = new GridPos(ax, by);
        GridPos elbowB = new GridPos(bx, ay);
        GridPos first = random.nextBoolean() ? elbowA : elbowB;
        GridPos second = (first == elbowA) ? elbowB : elbowA;

        Node elbow = junctionAt(network, first);
        if (elbow == null) {
            elbow = junctionAt(network, second);
        }
        if (elbow == null) {
            network.connectNodes(a, b); // both elbows blocked; rare, a diagonal beats no track at all
            return;
        }

        network.connectNodes(a, elbow);
        network.connectNodes(elbow, b);
    }

    /**
     * A node to route a track's bend through at the given position: whatever is already there if
     * it's safe to pass through, a freshly added plain junction if the cell is empty, or null if
     * the cell holds something that shouldn't be treated as a waypoint.
     */
    private static Node junctionAt(Network network, GridPos pos) {
        Node existing = network.getNodes().get(pos);
        if (existing != null) {
            return existing; // routing a track through an existing station/junction is fine
        }
        return network.addNode(pos);
    }

    /**
     * Adds a handful of random tracks on top of the spanning tree, so more than one route exists
     * between at least some stations.
     */
    private static void addExtraTracks(Network network, Random random, List<Station> stations, int extraTracks) {
        if (stations.size() < 2) {
            return;
        }

        for (int i = 0; i < extraTracks; i++) {
            Station a = stations.get(random.nextInt(stations.size()));
            Station b = stations.get(random.nextInt(stations.size()));
            if (a == b || a.getConnections().contains(b)) {
                continue; // no self loops, and no doubling up an existing track
            }
            connectManhattan(network, random, a, b);
        }
    }

    /**
     * Adds one train per three stations, each between a random routable pair.
     * <p>
     *     Goes through {@code Router.findRoute} first rather than trusting the spanning tree blindly,
     *     the same defensive check {@link DemoScenario#addVehicle} makes.
     * </p>
     */
    private static void addVehicles(Simulation sim, Router router, Random random, List<Station> stations) {
        if (stations.size() < 2) {
            return;
        }

        int vehicleCount = Math.max(1, stations.size() / 3);
        for (int i = 0; i < vehicleCount; i++) {
            Station origin = stations.get(random.nextInt(stations.size()));
            Station destination = stations.get(random.nextInt(stations.size()));
            if (origin == destination) {
                continue;
            }

            Route route = router.findRoute(origin, destination);
            if (route == null) {
                continue; // unreachable, so no train rather than a broken one
            }

            sim.addVehicle(new Vehicle(
                    new Itinerary(route),
                    TRAIN_MAX_SPEED,
                    TRAIN_ACCELERATION,
                    CargoHold.mixed(TRAIN_CAPACITY)));
        }
    }
}