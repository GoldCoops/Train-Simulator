package com.trains;

import com.trains.sim.Simulation;
import com.trains.ui.SimulationController;
import com.trains.ui.SimulationView;
import javafx.application.Application;

import javafx.stage.Stage;


public class App extends Application {
    public static void main(String[] args) {
        Simulation sim = new Simulation();
        SimulationView view = new SimulationView(200);
        SimulationController controller = new SimulationController();
    }

    // I don't think we need this anymore? Uses different library for visual, we seem to be using swing
    @Override
    public void start(Stage stage) throws Exception {



    }
}
