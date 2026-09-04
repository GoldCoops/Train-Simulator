package com.trains.ui.gui;


import com.trains.utils.lang.I18N;

import javax.swing.*;
import java.awt.*;
import java.util.*;

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

	public GUI(JFrame frame, GUI previous) {
		super(new BorderLayout());
		this.frame = frame;
		this.previous = previous;
		this.buttonContainer = new JPanel();
		buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
		buttonContainer.setOpaque(false);
		buttonContainer.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

		menuTitle = new JLabel("", SwingConstants.CENTER);
		applyTitleFont();
		bind(menuTitle, getTitle());

		JPanel titleContainer = new JPanel(new BorderLayout());
		titleContainer.setOpaque(false);
		titleContainer.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
		titleContainer.add(menuTitle, BorderLayout.CENTER);

		drawMenuItems();

		add(titleContainer, BorderLayout.NORTH);
		add(buttonContainer, BorderLayout.CENTER);

		if (previous != null) {
			JPanel backButtonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
			backButtonContainer.setOpaque(false);
			backButtonContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
			backButtonContainer.add(createBackButton());
			add(backButtonContainer, BorderLayout.SOUTH);
		}

		I18N.addListener(localeRefresh);
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

	
	protected void setTitleSize(float size) {
		this.titleSize = size;
		applyTitleFont();
	}

	protected void bind(Runnable refresher) {
		i18nRefreshers.add(refresher);
		refresher.run();
	}

	private void refreshI18n() {
		for (Runnable refresher : i18nRefreshers) {
			refresher.run();
		}
	}

	private void applyTitleFont() {
		menuTitle.setFont(menuTitle.getFont().deriveFont(Font.BOLD, titleSize));
	}
	
}
