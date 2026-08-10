package com.trains;

import com.trains.sim.Simulation;
import com.trains.ui.SimulationController;
import com.trains.ui.SimulationView;
import javafx.application.Application;

import javafx.stage.Stage;


public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Simulation sim = new Simulation();
        SimulationView view = new SimulationView();
        SimulationController controller = new SimulationController();


    }
}
