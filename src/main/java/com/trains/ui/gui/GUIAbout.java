package com.trains.ui.gui;

import com.trains.utils.lang.I18N;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;


public class GUIAbout extends GUI {

	public GUIAbout(JFrame frame, GUI previous) {
		super(frame, previous);
	}

	@Override
	protected String getTitle() {
		return "menu.title.about";
	}

	@Override
	protected void drawMenuItems() {
		JLabel textBlock = new JLabel("", SwingConstants.CENTER);
		bind(() -> textBlock.setText(stringToHTML(I18N.getString("menu.about.description"), "center")));
		addMenuItems(textBlock);
	}
}
