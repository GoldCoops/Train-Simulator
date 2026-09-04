package com.trains.ui.gui;

import javax.swing.JFrame;

public class GUIMainMenu extends GUI {

	public GUIMainMenu(JFrame frame) {
		super(frame, null);
		setTitleSize(32.0f);
	}

	@Override
	protected String getTitle() {
		return "menu.title.main";
	}

	@Override
	protected void drawMenuItems() {
		addMenuItems(
				createMenuButton("menu.main.button.start", () -> openMenu(new GUISimulation(frame, this))),
				createButtonRow(
						createMenuButton("menu.main.button.settings", () -> openMenu(new GUISettings(frame, this))),
						createMenuButton("menu.main.button.about", () -> openMenu(new GUIAbout(frame, this)))
				),
				createMenuButton("menu.main.button.quit", () -> {
					frame.dispose();
					System.exit(0);
				})
		);
	}
}
