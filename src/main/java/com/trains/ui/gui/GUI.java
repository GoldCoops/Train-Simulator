package com.trains.ui.gui;

import javax.swing.*;

import com.trains.utils.lang.I18N;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class GUI extends JPanel {
	private static final float DEFAULT_TITLE_SIZE = 24.0f;
	protected static final int MAX_BUTTON_SIZE = 300;
	protected final JFrame frame;
	protected final JPanel buttonContainer;
	protected float titleSize = DEFAULT_TITLE_SIZE;

	protected GUI previous;

	private final JLabel menuTitle;
	private final List<Runnable> i18nRefreshers = new Arrayist<>();
	private final Runnable localeRefresh = this::refreshI18n;

	public GUI(Stage stage, GUI previous) {
		this.stage = stage;
		this.previous = previous;
		this.buttonContainer = new VBox(15);
		buttonContainer.setAlignment(Pos.CENTER);

		Label menuTitle = new Label();
		menuTitle.textProperty().bind(I18N.createStringBinding(getTitle()));
		menuTitle.styleProperty().bind(Bindings.concat(
				"-fx-font-size:",
				titleSize.asString(),
				"px"));

		HBox titleContainer = new HBox(menuTitle);
		titleContainer.setAlignment(Pos.CENTER);
		BorderPane.setMargin(titleContainer, new Insets(25,0, 0, 0));

		drawMenuItems();

		setTop(titleContainer);
		setCenter(buttonContainer);

		BorderPane.setMargin(buttonContainer, new Insets(50));

		BorderPane.setAlignment(titleContainer, Pos.CENTER);
		BorderPane.setAlignment(buttonContainer, Pos.TOP_CENTER);

		if (previous != null) {
			HBox backButtonContainer = new HBox(createBackButton());
			backButtonContainer.setAlignment(Pos.CENTER);
			setBottom(backButtonContainer);

			BorderPane.setAlignment(backButtonContainer, Pos.CENTER);
			BorderPane.setMargin(backButtonContainer, new Insets(15,0, 50, 0));
		}
	}

	protected abstract String getTitle();

	protected abstract void drawMenuItems();

	protected Button createMenuButton(String label, Runnable action) {
		Button button = new Button();
		button.textProperty().bind(I18N.createStringBinding(label));
		button.setPrefWidth(MAX_BUTTON_SIZE);
		button.setMaxWidth(MAX_BUTTON_SIZE);
		button.setOnAction(event -> action.run());
		return button;
	}

	protected HBox createButtonRow(Button... buttons) {
		double spacing = 15;
		HBox buttonRow = new HBox(spacing, buttons);
		buttonRow.setAlignment(Pos.CENTER);

		for (Button button : buttons) {
			HBox.setHgrow(button, Priority.ALWAYS);
			button.setMaxWidth((MAX_BUTTON_SIZE / 2) - (spacing / 2));
		}

		return buttonRow;
	}

	protected Button createBackButton() {
		return createMenuButton("menu.button.back", () -> openMenu(previous));
	}

	protected void openMenu(GUI menu) {
		stage.getScene().setRoot(menu);
	}

	
	protected void setTItleSize(float size) {
		
	}

		private void refreshI18n() {
		for (Runnable refresher : i18nRefreshers) {
			refresher.run();
		}
	}
	
}
