package com.trains.ui;

import com.trains.cargo.CargoHold;
import com.trains.network.Network;
import com.trains.network.Node;
import com.trains.network.PathwaySegment;
import com.trains.network.Station;
import com.trains.sim.Simulation;
import com.trains.utils.GridPos;
import com.trains.utils.Point2D;
import com.trains.utils.lang.I18N;
import com.trains.vehicles.Vehicle;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.util.Objects;

/**
 * Draws a {@link Simulation} onto a Canvas.
 * <p>
 * Purely a renderer - it never advances the simulation, it only reads it.
 * {@link SimulationController} decides when a frame happens and calls {@link #render(Simulation)}.
 * </p>
 * <p>
 * World coordinates are the {@link GridPos} coordinates of the network, screen coordinates are
 * canvas pixels. The two are related by a scale and an offset, which zooming and panning change.
 * </p>
 */
public final class SimulationView extends JPanel {
	/**
	 * Canvas size used before the pane has been laid out, and the fallback world extent.
	 */
	private static final double DEFAULT_SIZE = 200.0;
	/**
	 * Screen pixels left clear around the network when fitting.
	 */
	private static final double FIT_PADDING = 60.0;
	private static final double MIN_SCALE = 0.5;
	private static final double MAX_SCALE = 40.0;
	private static final double ZOOM_STEP = 1.1;
	/**
	 * World units between grid lines, matching the multiple of 5 that GridPos enforces.
	 */
	private static final double GRID_SPACING = 5.0;
	/**
	 * Grid lines closer together than this on-screen get thinned out.
	 */
	private static final double MIN_GRID_PIXELS = 14.0;

	private static final Color BACKGROUND = Color.decode("#1b2130");
	private static final Color GRID = Color.decode("#252d3f");
	private static final Color PATHWAY = Color.decode("#7d8799");
	private static final Color NODE = Color.decode("#5c6b82");
	private static final Color STATION = Color.decode("#e8b339");
	private static final Color STATION_FULL = Color.decode("#d95f5f");
	private static final Color VEHICLE_MOVING = Color.decode("#4bab6a");
	private static final Color VEHICLE_STOPPED = Color.decode("#c9d1d9");
	private static final Color TEXT = Color.decode("#e6edf3");
	private static final Color TEXT_DIM = Color.decode("#9aa5b1");

	private final double worldSize;
	private Simulation simulation;
	private double scale = 4.0;
	/**
	 * World coordinate drawn at the left edge of the canvas.
	 */
	private double offsetX;
	/**
	 * World coordinate drawn at the top edge of the canvas.
	 */
	private double offsetY;
	/**
	 * Set while a fit is owed, deferred because fitting needs a laid out canvas.
	 */
	private boolean fitPending = true;
	private double dragAnchorX;
	private double dragAnchorY;
	private Simulation lastSim;
	private Object hovered;

	/**
	 * Creates a view at the default world size
	 */
	public SimulationView() {
		this(DEFAULT_SIZE);
	}

	/**
	 * Creates a view
	 *
	 * @param worldSize the starting canvas size, and the extent assumed for an empty network
	 * @throws IllegalArgumentException if worldSize is not greater than 0
	 */
	public SimulationView(double worldSize) {
		if (worldSize <= 0) {
			throw new IllegalArgumentException("World size must be greater than 0");
		}
		this.worldSize = worldSize;
		setBackground(BACKGROUND);
        setOpaque(true);
		installPanAndZoom();
	}

	/**
	 * The node to place in a layout, the canvas fills it
	 *
	 * @return the root pane
	 */
	public JPanel getRoot() {
		return this;
	}

	/**
	 * Asks for the view to be re-framed around the network on the next render.
	 * <p>
	 * Deferred rather than immediate so it works before the canvas has been laid out.
	 * </p>
	 */
	public void requestFit() {
		fitPending = true;
	}

	/**
	 * Scales and centres the view so the whole network is visible
	 *
	 * @param network the network to frame
	 * @throws NullPointerException if network is null
	 */
	public void fitToNetwork(Network network) {
		Objects.requireNonNull(network);

		double width = getWidth();
		double height = getHeight();
		if (width <= 0 || height <= 0) {
			fitPending = true; // not laid out yet, try again next frame
			return;
		}

		double minX = 0;
		double minY = 0;
		double maxX = worldSize;
		double maxY = worldSize;

		if (!network.getNodes().isEmpty()) {
			minX = Double.POSITIVE_INFINITY;
			minY = Double.POSITIVE_INFINITY;
			maxX = Double.NEGATIVE_INFINITY;
			maxY = Double.NEGATIVE_INFINITY;
			for (GridPos pos : network.getNodes().keySet()) {
				minX = Math.min(minX, pos.x());
				minY = Math.min(minY, pos.y());
				maxX = Math.max(maxX, pos.x());
				maxY = Math.max(maxY, pos.y());
			}
		}

		// a single node, or a network in a straight line, has no extent on one axis
		double spanX = Math.max(maxX - minX, 1.0);
		double spanY = Math.max(maxY - minY, 1.0);
		double usableWidth = Math.max(width - FIT_PADDING * 2, 1.0);
		double usableHeight = Math.max(height - FIT_PADDING * 2, 1.0);

		scale = Math.clamp(Math.min(usableWidth / spanX, usableHeight / spanY), MIN_SCALE, MAX_SCALE);
		offsetX = (minX + maxX) / 2.0 - width / (2 * scale);
		offsetY = (minY + maxY) / 2.0 - height / (2 * scale);
		fitPending = false;
	}

	/**
	 * Draws one frame of the simulation
	 *
	 * @param sim the simulation to draw
	 * @throws NullPointerException if sim is null
	 */
	public void render(Simulation sim) {
		Objects.requireNonNull(sim);
		Network network = sim.getNetwork();
		this.lastSim = sim;

		if (fitPending) {
			fitToNetwork(network);
		}

		GraphicsContext gc = canvas.getGraphicsContext2D();
		double width = canvas.getWidth();
		double height = canvas.getHeight();

		gc.setFill(BACKGROUND);
		gc.fillRect(0, 0, width, height);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFont(Font.font(12));

		drawGrid(gc, width, height);
		drawPathways(gc, network);
		drawNodes(gc, network);

		for (Vehicle vehicle : sim.getVehicles()) {
			drawVehicle(gc, vehicle);
		}
	}

	private void drawGrid(GraphicsContext gc, double width, double height) {
		double spacing = GRID_SPACING;
		// thin the grid out when zoomed far enough out that every line would be a smear
		while (spacing * scale < MIN_GRID_PIXELS) {
			spacing *= 2;
		}

		gc.setStroke(GRID);
		gc.setLineWidth(1);

		double firstX = Math.floor(offsetX / spacing) * spacing;
		for (double x = firstX; toScreenX(x) <= width; x += spacing) {
			double screenX = Math.floor(toScreenX(x)) + 0.5; // half pixel offset keeps the line crisp
			gc.strokeLine(screenX, 0, screenX, height);
		}

		double firstY = Math.floor(offsetY / spacing) * spacing;
		for (double y = firstY; toScreenY(y) <= height; y += spacing) {
			double screenY = Math.floor(toScreenY(y)) + 0.5;
			gc.strokeLine(0, screenY, width, screenY);
		}
	}

	private void drawPathways(GraphicsContext gc, Network network) {
		gc.setStroke(PATHWAY);
		gc.setLineWidth(Math.max(2.0, scale * 0.45));
		gc.setLineCap(StrokeLineCap.ROUND);

		for (PathwaySegment segment : network.getPathways()) {
			GridPos start = segment.getStart().getPos();
			GridPos end = segment.getEnd().getPos();
			gc.strokeLine(
					toScreenX(start.x()), toScreenY(start.y()),
					toScreenX(end.x()), toScreenY(end.y()));
		}
	}

	private void drawNodes(GraphicsContext gc, Network network) {
		for (Node node : network.getNodes().values()) {
			double screenX = toScreenX(node.getX());
			double screenY = toScreenY(node.getY());

			if (node instanceof Station station) {
				drawStation(gc, station, screenX, screenY);
			} else {
				double radius = Math.max(3.0, scale * 0.5);
				gc.setFill(NODE);
				gc.fillOval(screenX - radius, screenY - radius, radius * 2, radius * 2);
			}
		}
	}

	private void drawStation(GraphicsContext gc, Station station, double screenX, double screenY) {
		CargoHold hold = station.getCargoHold();
		double size = Math.max(12.0, scale * 2.0);
		double corner = size / 3;

		gc.setFill(hold.getRemainingCapacity() <= 0 ? STATION_FULL : STATION);
		gc.fillRoundRect(screenX - size / 2, screenY - size / 2, size, size, corner, corner);

		if (station.getName() != null) {
			gc.setFill(TEXT);
			gc.fillText(station.getName(), screenX, screenY - size / 2 - 6);
		}
		// gc.setFill(TEXT_DIM);
		// gc.fillText(hold.getUsedUnits() + "/" + hold.getCapacity(), screenX, screenY + size / 2 + 14);
	}

	private void drawVehicle(GraphicsContext gc, Vehicle vehicle) {
		Point2D pos = vehicle.getPos();
		double screenX = toScreenX(pos.x);
		double screenY = toScreenY(pos.y);
		double length = Math.max(14.0, scale * 2.4);
		double width = Math.max(7.0, scale * 1.1);

		double angle = 0;
		// getTargetNode() goes through Itinerary.getCurrentSegment(), which throws once the route is done
		if (!vehicle.isRouteComplete()) {
			Node from = vehicle.getEntryNode();
			Node to = vehicle.getTargetNode();
			angle = Math.toDegrees(Math.atan2(to.getY() - from.getY(), to.getX() - from.getX()));
		}

		gc.save();
		gc.translate(screenX, screenY);
		gc.rotate(angle);
		gc.setFill(vehicle.isStopped() ? VEHICLE_STOPPED : VEHICLE_MOVING);
		gc.fillRoundRect(-length / 2, -width / 2, length, width, width / 2, width / 2);
		gc.restore();

		// only label a train that is actually carrying something, empty labels just collide
		// with each other when trains bunch up at a junction
		CargoHold hold = vehicle.getCargoHold();
//		if (hold.getUsedUnits() > 0) {
//			/*
//			 * Beside the train rather than above it. A stopped train sits exactly on its station, so
//			 * a label above would land on top of the station name.
//			 */
//			gc.setFill(TEXT_DIM);
//			gc.setTextAlign(TextAlignment.LEFT);
//			gc.fillText(hold.getUsedUnits() + "/" + hold.getCapacity(),
//					screenX + length / 2 + 6, screenY + 4);
//			gc.setTextAlign(TextAlignment.CENTER);
//		}
	}

	/*
	 * Generates a hitbox for the objects on-screen to summon a tooltip
	 * when the mouse is hovered over.
	 */
	private Object pickObject(double screenX, double screenY) {
		for (Vehicle vehicle : lastSim.getVehicles()) {
			Point2D pos = vehicle.getPos();
			double vx = toScreenX(pos.x);
			double vy = toScreenY(pos.y);

			double half = Math.max(12.0, scale * 2.0) / 2;
			if (Math.abs(screenX - vx) <= half && Math.abs(screenY - vy) <= half) {
				return vehicle;
			}
		}

		for (Node node : lastSim.getNetwork().getNodes().values()) {
			double nx = toScreenX(node.getX());
			double ny = toScreenY(node.getY());

			if (node instanceof Station || node.getClass() == Node.class) {
				double half = Math.max(12.0, scale * 2.0) / 2;
				if (Math.abs(screenX - nx) <= half && Math.abs(screenY - ny) <= half) {
					return node;
				}
			}
		}

		return null;
	}

	/*
	 * Create the text to be displayed in the tooltip box for
	 * each type of object (train, Station, Junction).
	 */
	private String hitDescription(Object entity) {
		switch (entity) {
			case Vehicle vehicle -> {
				CargoHold hold = vehicle.getCargoHold();
				String routeStatus = vehicle.isRouteComplete()
						? I18N.getString("sim.tooltip.train.pendingDispatch")
						: I18N.getString("sim.tooltip.train.nextStop", label(vehicle.getTargetNode()));
				return I18N.getString("sim.tooltip.train")
						+ "\n" + I18N.getString("sim.tooltip.train.cargo", hold.getUsedUnits(), hold.getCapacity())
						+ "\n" + I18N.getString("sim.tooltip.train.speed", vehicle.getSpeed())
						+ "\n" + routeStatus;
			}
			case Station station -> {
				CargoHold hold = station.getCargoHold();
				return label(station)
						+ "\n" + I18N.getString("sim.tooltip.station.passengers.waiting", hold.getUsedUnits(), hold.getCapacity())
						+ "\n" + I18N.getString("sim.tooltip.tracks.connections", station.getConnections().size());
			}
			case Node node -> {
				return label(node)
						+ "\n" + I18N.getString("sim.tooltip.tracks.connections", node.getConnections().size());
			}
			default -> throw new IllegalStateException("Unexpected value: " + entity);
		}
	}

	private String label(Node node) {
		if (node instanceof Station station && station.getName() != null) {
			return station.getName();
		}

		return I18N.getString("sim.tooltip.junction", node.getX(), node.getY());
	}

	private void installPanAndZoom() {
		root.setOnScroll(event -> {
			double factor = event.getDeltaY() > 0 ? ZOOM_STEP : 1 / ZOOM_STEP;
			double newScale = Math.clamp(scale * factor, MIN_SCALE, MAX_SCALE);
			if (newScale == scale) {
				return;
			}
			// pin the world point under the cursor so zooming feels anchored to it
			double worldX = toWorldX(event.getX());
			double worldY = toWorldY(event.getY());
			scale = newScale;
			offsetX = worldX - event.getX() / scale;
			offsetY = worldY - event.getY() / scale;
			event.consume();
		});

		root.setOnMousePressed(event -> {
			dragAnchorX = event.getX();
			dragAnchorY = event.getY();
		});

		root.setOnMouseDragged(event -> {
			offsetX -= (event.getX() - dragAnchorX) / scale;
			offsetY -= (event.getY() - dragAnchorY) / scale;
			dragAnchorX = event.getX();
			dragAnchorY = event.getY();
		});
	}

	/*
	 * Check if mouse is hovering over an object that can
	 * generate a tooltip.
	 */
	private void installHover() {
		tooltip.setShowDuration(Duration.INDEFINITE);

		root.setOnMouseMoved(event -> {
			if (lastSim == null) {
				return;
			}

			Object hit = pickObject(event.getX(), event.getY());
			if (hit == null) {
				clearHover();
				return;
			}

			double x = event.getScreenX() + 15;
			double y = event.getScreenY() + 15;

			if (hit != hovered) {
				hovered = hit;
				tooltip.setText(hitDescription(hit));
				tooltip.show(root, x, y);
			} else {
				tooltip.setText(hitDescription(hit));
				tooltip.setAnchorX(x);
				tooltip.setAnchorY(y);
			}
		});

		root.setOnMouseExited(event -> clearHover());
	}

	private void clearHover() {
		hovered = null;
		tooltip.hide();
	}

	private double toScreenX(double worldX) {
		return (worldX - offsetX) * scale;
	}

	private double toScreenY(double worldY) {
		return (worldY - offsetY) * scale;
	}

	private double toWorldX(double screenX) {
		return screenX / scale + offsetX;
	}

	private double toWorldY(double screenY) {
		return screenY / scale + offsetY;
	}
}
