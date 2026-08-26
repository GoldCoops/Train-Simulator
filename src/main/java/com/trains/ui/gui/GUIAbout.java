package com.trains.ui.gui;

import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GUIAbout extends GUI {

    public GUIAbout(Stage stage) {
        super(stage);
    }

    @Override
    protected String getTitle() {
        return "About";
    }

    @Override
    protected void drawMenuItems() {
        Label textBlock = new Label("""
                Train Simulator
                Made by This Group
                """);

        textBlock.setTextAlignment(TextAlignment.CENTER);
        buttonContainer.getChildren().addAll(
                textBlock,
                createMenuButton("Back", () -> openMenu(new GUIMainMenu(stage))));
    }

}
