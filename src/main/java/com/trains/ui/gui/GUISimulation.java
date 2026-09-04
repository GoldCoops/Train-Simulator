package com.trains.ui.gui;

import com.trains.ui.DemoScenario;
import com.trains.ui.SimulationController;
import com.trains.ui.SimulationView;
import com.trains.utils.lang.I18N;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

/**
 * The simulation screen.
 * <p>
 *     Reuses {@link GUI} for the title, the button helpers and the back button, then takes over the
 *     centre with the {@link SimulationView} canvas and moves the inherited button container to the
 *     right to act as a sidebar.
 * </p>
 */
public class GUISimulation extends GUI {
	private static final int SIDEBAR_WIDTH = MAX_BUTTON_SIZE;
	private static final float TITLE_SIZE = 28.0f;
	private static final int SPEED_SCALE = 100;


	private final SimulationView view;
	private final SimulationController controller;
	private final JButton playPauseButton;
	private final JLabel speedLabel;
	private final JLabel timeLabel;
	private final JLabel vehicleLabel;
	private final JLabel waitingLabel;
	private final JLabel onboardLabel;
	private final JLabel deliveredLabel;

	public GUISimulation(JFrame frame, GUI previous) {
		super(frame, previous);
		setTitleSize(TITLE_SIZE);

		this.view = new SimulationView();
		this.controller = new SimulationController(DemoScenario.build());

		this.playPauseButton = new JButton();
		this.playPauseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		lockButtonSize(this.playPauseButton, MAX_BUTTON_SIZE / 2);
		this.playPauseButton.addActionListener(event -> togglePlayPause());
		bind(this::updatePlayPauseText);
		this.speedLabel = createStatLabel();
		this.timeLabel = createStatLabel();
		this.vehicleLabel = createStatLabel();
		this.waitingLabel = createStatLabel();
		this.onboardLabel = createStatLabel();
		this.deliveredLabel = createStatLabel();

		buildSidebar();

		// GUI put buttonContainer in the centre, so claim the centre first and then re-home it
		remove(buttonContainer);
		add(view.getRoot(), BorderLayout.CENTER);
		buttonContainer.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
		buttonContainer.setPreferredSize(new Dimension(SIDEBAR_WIDTH + 50, 0));
		add(buttonContainer, BorderLayout.EAST);

		controller.setOnFrame(this::onFrame);
		controller.start();
		updatePlayPauseText();
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
	protected JButton createBackButton() {
		// called during super(), but the action only runs on click, by which point controller exists
		return createMenuButton("menu.button.back", () -> {
			controller.stop();
			openMenu(previous);
		});
	}

	private void buildSidebar() {
		int min = (int) Math.round(SimulationController.MIN_SPEED_MULTIPLIER * SPEED_SCALE);
		int max = (int) Math.round(SimulationController.MAX_SPEED_MULTIPLIER * SPEED_SCALE);
		int value = (int) Math.round(controller.getSpeedMultiplier() * SPEED_SCALE);
		JSlider speedSlider = new JSlider(min, max, value);
		speedSlider.setMaximumSize(new Dimension(SIDEBAR_WIDTH, speedSlider.getPreferredSize().height));
		speedSlider.addChangeListener(event ->
				controller.setSpeedMultiplier(speedSlider.getValue() / (double) SPEED_SCALE));

		addMenuItems(
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
		waitingLabel.setText(I18N.getString("sim.label.passengers.waiting", controller.getWaitingUnits()));
		onboardLabel.setText(I18N.getString("sim.label.passengers.onBoard", controller.getOnboardUnits()));
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
		String key = controller.isRunning() ? "sim.button.pause" : "sim.button.play";
		playPauseButton.setText(I18N.getString(key));
	}

	/**
	 * Starts the scenario over by replacing this screen with a fresh one.
	 */
	private void reset() {
		controller.stop();
		openMenu(new GUISimulation(frame, previous));
	}

	private JLabel createSectionLabel(String key) {
		JLabel label = new JLabel();
		label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
		bind(label, key);
		return label;
	}

	private JLabel createStatLabel() {
		JLabel label = new JLabel();
		label.setFont(label.getFont().deriveFont(13f));
		return label;
	}
}
