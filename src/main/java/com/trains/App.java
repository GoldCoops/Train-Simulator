package com.trains;

import com.trains.ui.gui.GUIMainMenu;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * The main class for the application
 */
public class App {
	/**
	 * The main method
	 * <p>
	 *     Invokes the {@link App#start()} method on the Swing thread
	 * </p>
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(App::start);
	}

	/**
	 * Starts the application
	 */
	private static void start() {
		/*
		Australian English is the only supported language at the moment
		and will be the default locale if a users system default is not available
		 */

		JFrame frame = new JFrame("Train Simulator");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(800, 800);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(new GUIMainMenu(frame));
		frame.setVisible(true);
	}
}
