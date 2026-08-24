package com.trains.interfaces.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class GUI extends BorderPane {
    protected final Stage stage;
    protected final VBox buttonContainer;

    public GUI(Stage stage) {
        this.stage = stage;
        this.buttonContainer = new VBox(15);
        buttonContainer.setAlignment(Pos.CENTER);

        setPadding(new Insets(50));

        Label menuTitle = new Label(getTitle());
        HBox titleContainer = new HBox(menuTitle);
        titleContainer.setAlignment(Pos.CENTER);

        drawMenuItems();
        BorderPane.setAlignment(buttonContainer, Pos.CENTER);

        setTop(titleContainer);
        setCenter(buttonContainer);
    }

    protected abstract String getTitle();

    protected abstract void drawMenuItems();

    protected Button createMenuButton(String label, Runnable action) {
        Button button = new Button(label);
        button.setMaxWidth(300);
        button.setOnAction(event -> action.run());
        return button;
    }

    protected void openMenu(GUI menu) {
        stage.getScene().setRoot(menu);
    }
}
