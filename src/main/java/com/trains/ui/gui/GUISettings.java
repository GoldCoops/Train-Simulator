package com.trains.ui.gui;

import javax.swing.JFrame;

/*
Until more settings for the app get added (if there are any)
this class will be unused.
 */

public class GUISettings extends GUI {
	public GUISettings(JFrame frame, GUI previous) {
		super(frame, previous);
	}

	@Override
	protected String getTitle() {
		return "menu.title.settings";
	}

	@Override
	protected void drawMenuItems() {
		addMenuItems(
				createMenuButton("menu.main.button.language", () -> openMenu(new GUILanguage(frame, this))));
	}
}
