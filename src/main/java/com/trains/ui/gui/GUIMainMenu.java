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
		buttonContainer.getChildren().addAll(
				createMenuButton("menu.main.button.start", () -> openMenu(new GUISimulation(stage, this))),
				createButtonRow(
						createMenuButton("menu.main.button.about", () -> openMenu(new GUIAbout(stage, this))),
						createMenuButton("menu.main.button.language", () -> openMenu(new GUILanguage(stage, this)))
				),
				createMenuButton("menu.main.button.quit", Platform::exit)
		);
	}
}
