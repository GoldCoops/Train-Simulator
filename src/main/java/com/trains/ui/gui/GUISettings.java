package com.trains.ui.gui;

import javafx.stage.Stage;

public class GUISettings extends GUI {
	public GUISettings(Stage stage, GUI previous) {
		super(stage, previous);
	}

	@Override
	protected String getTitle() {
		return "menu.title.settings";
	}

	@Override
	protected void drawMenuItems() {
		buttonContainer.getChildren().addAll(
				createMenuButton("menu.settings.button.language", () -> openMenu(new GUILanguage(stage, this)))
		);
	}
}
