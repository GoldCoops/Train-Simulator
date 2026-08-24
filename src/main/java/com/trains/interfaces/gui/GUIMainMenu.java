package com.trains.interfaces.gui;

import javafx.application.Platform;
import javafx.stage.Stage;

public class GUIMainMenu extends GUI {

    public GUIMainMenu(Stage stage) {
        super(stage);
    }

    @Override
    protected String getTitle() {
        return "Train Simulator";
    }

    @Override
    protected void drawMenuItems() {
        buttonContainer.getChildren().addAll(
                createMenuButton("About", () -> openMenu(new GUIAbout(stage))),
                createMenuButton("Quit", Platform::exit));
    }
}
