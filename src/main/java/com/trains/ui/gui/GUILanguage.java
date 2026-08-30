package com.trains.ui.gui;

import com.trains.utils.lang.I18N;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GUILanguage extends GUI {

	public GUILanguage(Stage stage, GUI previous) {
		super(stage, previous);
	}

	@Override
	protected String getTitle() {
		return "menu.title.settings.language";
	}

	@Override
	protected void drawMenuItems() {
		ScrollPane scrollPane = new ScrollPane();
		VBox toggleHolder = new VBox(15);
		ToggleGroup toggleGroup = new ToggleGroup();
		List<Locale> locales = new ArrayList<>(I18N.availableLocales);

		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.viewportBoundsProperty().addListener((ov, oldVb, newVb) -> {
			toggleHolder.setPrefWidth(newVb.getWidth());
		});
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background-color: transparent; -fx-viewport-background: transparent; -fx-border-color: transparent;");

		toggleHolder.setAlignment(Pos.CENTER);

		for (Locale locale : locales) {
			ToggleButton button = new ToggleButton(locale.getDisplayName());
			button.setToggleGroup(toggleGroup);
			button.setSelected(locale.equals(I18N.getLocale()));
			button.setMaxWidth(MAX_BUTTON_SIZE);
			button.setOnAction(event -> {
				I18N.setLocale(locale);
			});

			toggleHolder.getChildren().add(button);
		}

		scrollPane.setContent(toggleHolder);
		buttonContainer.getChildren().add(scrollPane);
	}
}
