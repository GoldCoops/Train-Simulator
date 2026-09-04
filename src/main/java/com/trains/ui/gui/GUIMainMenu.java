package com.trains.ui.gui;

import javax.swing.JFrame;

public class GUIMainMenu extends GUI {

	public GUIMainMenu(JFrame frame) {
		super(frame, null);
		setTItleSize(32.0f);
	}

	@Override
	protected String getTitle() {
		return "menu.title.main";
	}

	@Override
	protected void drawMenuItems() {

	}
}
