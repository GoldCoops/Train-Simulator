package com.trains;

import com.trains.sim.Simulation;
import com.trains.interfaces.gui.GUIMainMenu;
import com.trains.interfaces.ui.SimulationController;
import com.trains.interfaces.ui.SimulationView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
	public static void main(String[] args) {
		// Main should only contain this line
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// All other init logic should be here
		// Simulation sim = new Simulation();
		// SimulationView view = new SimulationView(200);
		// SimulationController controller = new SimulationController();

		GUIMainMenu mainMenu = new GUIMainMenu(primaryStage);
		Scene scene = new Scene(mainMenu, 800, 600);

		primaryStage.setTitle("Train Simulator");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
