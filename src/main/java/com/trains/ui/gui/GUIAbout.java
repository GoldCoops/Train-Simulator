package com.trains.ui.gui;

import com.trains.utils.lang.I18N;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GUIAbout extends GUI {

	public GUIAbout(Stage stage, GUI previous) {
		super(stage, previous);
	}

	@Override
	protected String getTitle() {
		return "menu.title.about";
	}

	@Override
	protected void drawMenuItems() {
		Label textBlock = new Label();
		textBlock.textProperty().bind(I18N.createStringBinding("menu.about.description"));

		textBlock.setTextAlignment(TextAlignment.CENTER);
		buttonContainer.getChildren().addAll(
				textBlock
		);
	}

}
