package com.trains.ui;

import com.trains.sim.Simulation;

public class SimulationController {
    // Decides when the simulation runs
    /*
     * The Simulation class in sim/ decides what happens in one tick
     * vs this class which decides when those steps happen.
     * 
     * Keep apart so sim does not have to touch UI at all; which means that we can
     * run JUnit tests on Simulation.tick() and ensure the result is as expected.
     */
    private final Simulation sim;
    public SimulationController() {
        this.sim = new Simulation();
    }
    public SimulationController(Simulation sim) {
        this.sim = sim;
    }

    public Simulation getSimulation() {
        return sim;
    }

}
