package com.trains.ui.gui;

import javax.swing.JFrame;

public class GUISettings extends GUI {
	public GUISettings(JFrame frame, GUI previous) {
		super(frame, previous);
	}

	@Override
	protected String getTitle() {
		return "Settings";
	}

	@Override
	protected void drawMenuItems() {
		addMenuItems(
				createMenuButton("Settings", () -> openMenu(new GUILanguage(frame, this)))
		);
	}
}
