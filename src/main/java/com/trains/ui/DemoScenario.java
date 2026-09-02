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

import java.util.ArrayList;

/**
 * Builds the sample network the simulation screen opens on, since {@code new Simulation()} starts
 * with an empty one.
 * <p>
 *     This is display data rather than domain logic, so it lives with the UI for now. It only uses
 *     the public API of the network, routing and vehicle packages, so it can be lifted into
 *     {@code com.trains.sim} unchanged if a scenario factory is wanted there.
 * </p>
 * <p>
 *     The layout is two east-west corridors joined by three junctions, which leaves more than one
 *     way round and so gives the router something to actually choose between:
 * </p>
 * <pre>
 *                Northgate        Riverside
 *                    |                |
 *   Central --- Junction1 --- Junction2 --- Eastfield
 *      |             |                |
 *   Southbank -- Junction3 -------- Harbour
 * </pre>
 */
public final class DemoScenario {
    private static final int STATION_CAPACITY = 30;
    private static final int TRAIN_CAPACITY = 12;
    private static final float TRAIN_MAX_SPEED = 14f;
    private static final float TRAIN_ACCELERATION = 6f;

    private DemoScenario() {
    }

    /**
     * Builds the demo simulation
     * @return a simulation holding the sample network and its trains, with no cargo yet
     */
    public static Simulation build() {
        Network network = new Network();

        // GridPos requires non negative multiples of 5
        Station central = station(network, 5, 40, "Central");
        Station northgate = station(network, 45, 5, "Northgate");
        Station riverside = station(network, 85, 5, "Riverside");
        Station eastfield = station(network, 115, 40, "Eastfield");
        Station southbank = station(network, 5, 75, "Southbank");
        Station harbour = station(network, 85, 75, "Harbour");

        Node junction1 = network.addNode(new GridPos(45, 40));
        Node junction2 = network.addNode(new GridPos(85, 40));
        Node junction3 = network.addNode(new GridPos(45, 75));

        network.connectNodes(central, junction1);
        network.connectNodes(junction1, northgate);
        network.connectNodes(junction1, junction2);
        network.connectNodes(junction2, riverside);
        network.connectNodes(junction2, eastfield);
        network.connectNodes(central, southbank);
        network.connectNodes(southbank, junction3);
        network.connectNodes(junction3, junction1);
        network.connectNodes(junction3, harbour);
        network.connectNodes(harbour, junction2);

        // mutable list, so Simulation.addVehicle works
        Simulation sim = new Simulation(network, new ArrayList<>());
        Router router = new Router(network);

        addVehicle(sim, router, central, eastfield);
        addVehicle(sim, router, northgate, harbour);
        addVehicle(sim, router, riverside, southbank);

        // no passengers here - SimulationController seeds them, so there is a single spawn path
        // that checks a route exists before creating anyone
        return sim;
    }

    private static Station station(Network network, int x, int y, String name) {
        Station station = network.addStation(new GridPos(x, y), STATION_CAPACITY);
        station.setName(name);
        return station;
    }

    private static void addVehicle(Simulation sim, Router router, Node origin, Node destination) {
        Route route = router.findRoute(origin, destination);
        if (route == null) {
            return; // unreachable, so no train rather than a broken one
        }
        sim.addVehicle(new Vehicle(
                new Itinerary(route),
                TRAIN_MAX_SPEED,
                TRAIN_ACCELERATION,
                CargoHold.mixed(TRAIN_CAPACITY)));
    }
}
