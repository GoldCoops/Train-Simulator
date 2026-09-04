package com.trains;

import com.trains.ui.gui.GUIMainMenu;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class App {
	public static void main(String[] args) {
		// Main should only contain this line
		SwingUtilities.invokeLater(App::start);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// All other init logic should be here
		// Simulation sim = new Simulation();
		// SimulationView view = new SimulationView(200);
		// SimulationController controller = new SimulationController();

		/*
		Australian English is the only supported language at the moment
		and will be the default locale if a users system default is not available
		 */

		GUIMainMenu mainMenu = new GUIMainMenu(primaryStage);
		Scene scene = new Scene(mainMenu, 800, 600);

		primaryStage.setTitle("Train Simulator");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
