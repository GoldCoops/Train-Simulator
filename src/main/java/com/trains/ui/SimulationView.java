package com.trains.ui;

import com.trains.cargo.CargoHold;
import com.trains.network.Network;
import com.trains.network.Node;
import com.trains.network.PathwaySegment;
import com.trains.network.Station;
import com.trains.sim.Simulation;
import com.trains.utils.GridPos;
import com.trains.utils.Point2D;
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
		this.simulation = sim;

		if (fitPending) {
			fitToNetwork(sim.getNetwork());
		}

		repaint();
    }

    @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
        if (simulation == null) {
            return;
        }

        Graphics2D gc = (Graphics2D) g.create();
        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gc.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        double width = getWidth();
        double height = getHeight();
        Network network = simulation.getNetwork();

        gc.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		
		drawGrid(gc, width, height);
		drawPathways(gc, network);
		drawNodes(gc, network);

		for (Vehicle vehicle : simulation.getVehicles()) {
            drawVehicle(gc, vehicle);
        }

        gc.dispose();
    }

	private void drawGrid(Graphics2D gc, double width, double height) {
		double spacing = GRID_SPACING;
		// thin the grid out when zoomed far enough out that every line would be a smear
		while (spacing * scale < MIN_GRID_PIXELS) {
			spacing *= 2;
		}

		gc.setColor(GRID);
        gc.setStroke(new BasicStroke(1f));

		double firstX = Math.floor(offsetX / spacing) * spacing;
		for (double x = firstX; toScreenX(x) <= width; x += spacing) {
			double screenX = Math.floor(toScreenX(x)) + 0.5; // half pixel offset keeps the line crisp
			gc.drawLine((int) screenX, 0, (int) screenX, (int) height);
		}

		double firstY = Math.floor(offsetY / spacing) * spacing;
		for (double y = firstY; toScreenY(y) <= height; y += spacing) {
			double screenY = Math.floor(toScreenY(y)) + 0.5;
			gc.drawLine(0, (int) screenY, (int) width, (int) screenY);
		}
	}

	private void drawPathways(Graphics2D gc, Network network) {
		gc.setColor(PATHWAY);
        gc.setStroke(new BasicStroke((float) Math.max(2.0, scale * 0.45),
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		for (PathwaySegment segment : network.getPathways()) {
			GridPos start = segment.getStart().getPos();
			GridPos end = segment.getEnd().getPos();
			gc.drawLine(
                    (int) Math.round(toScreenX(start.x())), (int) Math.round(toScreenY(start.y())),
                    (int) Math.round(toScreenX(end.x())), (int) Math.round(toScreenY(end.y())));
        }
	}

	private void drawNodes(Graphics2D gc, Network network) {
		for (Node node : network.getNodes().values()) {
			double screenX = toScreenX(node.getX());
			double screenY = toScreenY(node.getY());

			if (node instanceof Station station) {
				drawStation(gc, station, screenX, screenY);
			} else {
				double radius = Math.max(3.0, scale * 0.5);
				gc.setColor(NODE);
                fillOval(gc, screenX, screenY, radius);
			}
		}
	}

	private void drawStation(Graphics2D gc, Station station, double screenX, double screenY) {
		CargoHold hold = station.getCargoHold();
		double size = Math.max(12.0, scale * 2.0);
		double corner = size / 3;

		gc.setColor(hold.getRemainingCapacity() <= 0 ? STATION_FULL : STATION);
        gc.fillRoundRect(
                (int) Math.round(screenX - size / 2),
                (int) Math.round(screenY - size / 2),
                (int) Math.round(size),
                (int) Math.round(size),
                (int) Math.round(corner),
                (int) Math.round(corner));

		if (station.getName() != null) {
			gc.setColor(TEXT);
            fillTextCentered(gc, station.getName(), screenX, screenY - size / 2 - 6);
		}

		gc.setColor(TEXT_DIM);
        fillTextCentered(gc, hold.getUsedUnits() + "/" + hold.getCapacity(),
                screenX, screenY + size / 2 + 14);
	}

	private void drawVehicle(Graphics2D gc, Vehicle vehicle) {
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

		AffineTransform previous = gc.getTransform();
        gc.translate(screenX, screenY);
        gc.rotate(Math.toRadians(angle));
        gc.setColor(vehicle.isStopped() ? VEHICLE_STOPPED : VEHICLE_MOVING);
        gc.fillRoundRect(
                (int) Math.round(-length / 2),
                (int) Math.round(-width / 2),
                (int) Math.round(length),
                (int) Math.round(width),
                (int) Math.round(width / 2),
                (int) Math.round(width / 2));
        gc.setTransform(previous);

		// only label a train that is actually carrying something, empty labels just collide
		// with each other when trains bunch up at a junction
		CargoHold hold = vehicle.getCargoHold();
        if (hold.getUsedUnits() > 0) {
//			/*
//			 * Beside the train rather than above it. A stopped train sits exactly on its station, so
//			 * a label above would land on top of the station name.
//			 */
			gc.setColor(TEXT_DIM);
			fillTextLeft(gc, hold.getUsedUnits() + "/" + hold.getCapacity(),
            screenX + length / 2 + 6, screenY + 4);
		}
	}

	private void installPanAndZoom() {
        addMouseWheelListener(this::onScroll);

        MouseAdapter mouse = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				dragAnchorX = event.getX();
				dragAnchorY = event.getY();
			}

			@Override
			public void mouseDragged(MouseEvent event) {
				offsetX -= (event.getX() - dragAnchorX) / scale;
				offsetY -= (event.getY() - dragAnchorY) / scale;
				dragAnchorX = event.getX();
				dragAnchorY = event.getY();
				repaint();
			}
        };

        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }
	
	private void onScroll(MouseWheelEvent event) {
        double factor = event.getPreciseWheelRotation() < 0 ? ZOOM_STEP : 1 / ZOOM_STEP;
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
        repaint();
    }

    private static void fillOval(Graphics2D gc, double screenX, double screenY, double radius) {
        int size = (int) Math.round(radius * 2);
        gc.fillOval(
                (int) Math.round(screenX - radius),
                (int) Math.round(screenY - radius),
                size,
                size);
    }

    private static void fillTextCentered(Graphics2D gc, String text, double x, double y) {
		FontMetrics metrics = gc.getFontMetrics();
        gc.drawString(text, (float) (x - metrics.stringWidth(text) / 2.0), (float) y);
    }

    private static void fillTextLeft(Graphics2D gc, String text, double x, double y) {
        gc.drawString(text, (float) x, (float) y);
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
