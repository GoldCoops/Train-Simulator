package com.trains;

import com.trains.ui.gui.GUIMainMenu;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(App::start);
	}

	private static void start() {
		/*
		Australian English is the only supported language at the moment
		and will be the default locale if a users system default is not available
		 */

		JFrame frame = new JFrame("Train Simulator");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(new GUIMainMenu(frame));
		frame.setVisible(true);
	}
}
