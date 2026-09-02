package com.trains.ui.gui;

import com.trains.ui.DemoScenario;
import com.trains.ui.SimulationController;
import com.trains.ui.SimulationView;
import com.trains.utils.lang.I18N;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The simulation screen.
 * <p>
 *     Reuses {@link GUI} for the title, the button helpers and the back button, then takes over the
 *     centre with the {@link SimulationView} canvas and moves the inherited button container to the
 *     right to act as a sidebar.
 * </p>
 */
public class GUISimulation extends GUI {
	private static final double SIDEBAR_WIDTH = MAX_BUTTON_SIZE;
	private static final double TITLE_SIZE = 28.0;

	private final SimulationView view;
	private final SimulationController controller;
	private final Button playPauseButton;
	private final Label speedLabel;
	private final Label timeLabel;
	private final Label vehicleLabel;
	private final Label waitingLabel;
	private final Label onboardLabel;
	private final Label deliveredLabel;

	public GUISimulation(Stage stage, GUI previous) {
		super(stage, previous);
		titleSize.set(TITLE_SIZE);

		this.view = new SimulationView();
		this.controller = new SimulationController(DemoScenario.build());

		this.playPauseButton = createMenuButton("sim.button.pause", this::togglePlayPause);
		this.speedLabel = createStatLabel();
		this.timeLabel = createStatLabel();
		this.vehicleLabel = createStatLabel();
		this.waitingLabel = createStatLabel();
		this.onboardLabel = createStatLabel();
		this.deliveredLabel = createStatLabel();

		buildSidebar();

		// GUI put buttonContainer in the centre, so claim the centre first and then re-home it
		setCenter(view.getRoot());
		setRight(buttonContainer);
		BorderPane.setMargin(buttonContainer, new Insets(25));
		BorderPane.setAlignment(buttonContainer, Pos.TOP_CENTER);

		controller.setOnFrame(this::onFrame);
		controller.start();
	}

	@Override
	protected String getTitle() {
		return "menu.title.simulation";
	}

	/**
	 * Deliberately empty.
	 * <p>
	 *     GUI calls this from its constructor, before this class has assigned its fields, so the
	 *     sidebar is built from the constructor body instead once the controller and view exist.
	 * </p>
	 */
	@Override
	protected void drawMenuItems() {
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 *     Overridden to stop the frame timer, which would otherwise keep firing after the scene root
	 *     has been swapped back to the previous menu.
	 * </p>
	 */
	@Override
	protected Button createBackButton() {
		// called during super(), but the action only runs on click, by which point controller exists
		return createMenuButton("menu.button.back", () -> {
			controller.stop();
			openMenu(previous);
		});
	}

	private void buildSidebar() {
		Slider speedSlider = new Slider(
				SimulationController.MIN_SPEED_MULTIPLIER,
				SimulationController.MAX_SPEED_MULTIPLIER,
				controller.getSpeedMultiplier());
		speedSlider.setMaxWidth(SIDEBAR_WIDTH);
		speedSlider.valueProperty().addListener(
				(observable, oldValue, newValue) -> controller.setSpeedMultiplier(newValue.doubleValue()));

		buttonContainer.setAlignment(Pos.TOP_CENTER);
		buttonContainer.setPrefWidth(SIDEBAR_WIDTH);
		buttonContainer.setMaxWidth(SIDEBAR_WIDTH);
		buttonContainer.getChildren().addAll(
				createSectionLabel("sim.label.controls"),
				createButtonRow(playPauseButton, createMenuButton("sim.button.reset", this::reset)),
				speedLabel,
				speedSlider,
				createMenuButton("sim.button.spawn", controller::spawnPassenger),
				createMenuButton("sim.button.fit", view::requestFit),
				createSectionLabel("sim.label.stats"),
				timeLabel,
				vehicleLabel,
				waitingLabel,
				onboardLabel,
				deliveredLabel
		);
	}

	/**
	 * Runs once per frame: redraws the canvas and refreshes the statistics.
	 */
	private void onFrame() {
		view.render(controller.getSimulation());

		/*
		 * setText rather than a binding, because createStringBinding captures its parameters once
		 * and so cannot show a changing value. getString still reads the current locale each call,
		 * so these stay translated.
		 */
		speedLabel.setText(I18N.getString("sim.label.speed", controller.getSpeedMultiplier()));
		timeLabel.setText(I18N.getString("sim.label.time", (int) controller.getElapsedSeconds()));
		vehicleLabel.setText(I18N.getString("sim.label.vehicles", controller.getSimulation().getVehicles().size()));
		waitingLabel.setText(I18N.getString("sim.label.waiting", controller.getWaitingUnits()));
		onboardLabel.setText(I18N.getString("sim.label.onboard", controller.getOnboardUnits()));
		deliveredLabel.setText(I18N.getString("sim.label.delivered", controller.getDeliveredCount()));
	}

	private void togglePlayPause() {
		if (controller.isRunning()) {
			controller.pause();
		} else {
			controller.play();
		}
		updatePlayPauseText();
	}

	private void updatePlayPauseText() {
		// binding again replaces the previous one, so the button follows both state and locale
		playPauseButton.textProperty().bind(
				I18N.createStringBinding(controller.isRunning() ? "sim.button.pause" : "sim.button.play"));
	}

	/**
	 * Starts the scenario over by replacing this screen with a fresh one.
	 */
	private void reset() {
		controller.stop();
		openMenu(new GUISimulation(stage, previous));
	}

	private Label createSectionLabel(String key) {
		Label label = new Label();
		label.textProperty().bind(I18N.createStringBinding(key));
		label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		return label;
	}

	private Label createStatLabel() {
		Label label = new Label();
		label.setStyle("-fx-font-size: 13px;");
		return label;
	}
}
