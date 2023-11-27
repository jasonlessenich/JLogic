package dev.jasonlessenich.jlogic.objects.wires;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.objects.wires.layout.WireLayoutStrategy;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.NodeUtils;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A {@link Wire} that represents a connection between two {@link ConnectablePin}s.
 * This is a {@link Parent} that contains the lines that make up the wire, as well as
 * the start and end {@link Circle}s if there is no {@link ConnectablePin}.
 * The layout of this wire is determined by the specified {@link WireLayoutStrategy}.
 */
@Slf4j
public class Wire extends Parent {
	private static final Color PIN_FILL = Color.WHITE;

	/**
	 * The strategy used to layout the wire
	 */
	private final WireLayoutStrategy layoutStrategy;
	/**
	 * Other wires attached to this wire.
	 */
	@Getter
	private final List<Wire> wires;
	// TODO: docs
	@Nullable
	private final Circle intersectionCircle;
	/**
	 * The circle at the start of the wire if there is no {@link ConnectablePin}.
	 */
	@Nullable
	private final Circle startCircle;
	/**
	 * The circle at the end of the wire if there is no {@link ConnectablePin}.
	 */
	@Nullable
	private final Circle endCircle;
	/**
	 * The wire's start point.
	 */
	@Getter
	private Point start;
	/**
	 * The wire's end point.
	 */
	@Getter
	private Point end;
	/**
	 * The nullable {@link ConnectablePin} (start) that this wire is connected to.
	 */
	@Nullable
	@Getter
	private ConnectablePin startPin;
	/**
	 * The nullable {@link ConnectablePin} (end) that this wire is connected to.
	 */
	@Nullable
	@Getter
	private ConnectablePin endPin;
	/**
	 * The lines that make up this wire.
	 */
	private List<Line> lines;
	/**
	 * Whether this wire is activated.
	 */
	@Getter
	private boolean activated;

	// TODO: docs
	private Wire(
			@Nonnull WireLayoutStrategy layoutStrategy,
			@Nonnull Point start,
			@Nonnull Point end,
			@Nullable Wire wire,
			@Nullable ConnectablePin startPin,
			@Nullable ConnectablePin endPin
	) {
		if (wire != null && startPin != null) {
			throw new IllegalArgumentException("Cannot have both a wire and a start pin!");
		}
		setId("Wire");
		this.layoutStrategy = layoutStrategy;
		this.start = start;
		this.end = end;
		this.wires = new ArrayList<>();
		this.lines = redrawLines(start, end);
		// build intersection circle
		this.intersectionCircle = wire != null ? add(buildIntersection(start)) : null;
		// build start pin
		this.startPin = startPin;
		this.startCircle = startPin == null && intersectionCircle == null ? add(buildWirePin(start, true)) : null;
		// build end pin
		this.endPin = endPin;
		this.endCircle = endPin == null ? add(buildWirePin(end, false)) : null;
		// build context menu
		final ContextMenu contextMenu = buildContextMenu();
		setOnMousePressed(me -> {
			if (me.isSecondaryButtonDown()) {
				contextMenu.show(this, me.getScreenX(), me.getScreenY());
			} else {
				contextMenu.hide();
			}
		});
	}

	// TODO: docs
	public Wire(
			@Nonnull Point start,
			@Nonnull Point end,
			@Nonnull ConnectablePin startPin,
			@Nullable ConnectablePin endPin
	) {
		this(WireLayoutStrategy.STRAIGHT, start, end, null, startPin, endPin);
	}

	// TODO: docs
	public Wire(
			@Nonnull WireLayoutStrategy layoutStrategy,
			@Nonnull Point start,
			@Nonnull Point end,
			@Nonnull Wire wire,
			@Nullable ConnectablePin endPin
	) {
		this(layoutStrategy, start, end, wire, null, endPin);
	}

	/**
	 * Sets the activation state of this wire.
	 * This simply changes the color of the wire.
	 *
	 * @param activated The new activation state.
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
		lines.forEach(l -> l.setStroke(activated ? Color.LIMEGREEN : Color.BLACK));
		for (Wire wire : wires) {
			if (wire == this || wire.isActivated() == activated) continue;
			wire.setActivated(activated);
		}
		if (getEndPin() != null) {
			getEndPin().setState(activated);
		}
		if (intersectionCircle != null) {
			intersectionCircle.setFill(activated ? Color.LIMEGREEN : Color.BLACK);
		}
	}

	/**
	 * Sets the start point of this wire.
	 *
	 * @param start The new start point.
	 */
	public void setStart(@Nonnull Point start) {
		this.start = start;
		if (startCircle != null) {
			startCircle.setCenterX(start.getX());
			startCircle.setCenterY(start.getY());
		}
		redrawLines(start, end);
	}

	/**
	 * Sets the end point of this wire.
	 *
	 * @param end The new end point.
	 */
	public void setEnd(@Nonnull Point end) {
		this.end = end;
		if (endCircle != null) {
			endCircle.setCenterX(end.getX());
			endCircle.setCenterY(end.getY());
		}
		redrawLines(start, end);
	}

	/**
	 * Creates a new {@link Wire} connecting the given {@link ConnectablePin}s.
	 * This will remove the start and end {@link Circle}s from this wire, as well
	 * as lock the start and end points to the {@link ConnectablePin}s.
	 *
	 * @param from The {@link ConnectablePin} to connect from.
	 * @param to   The {@link ConnectablePin} to connect to.
	 */
	public void connect(@Nonnull ConnectablePin from, @Nonnull ConnectablePin to) {
		if (!from.canConnectTo(to)) return;
		// remove end pin
		getChildren().removeIf(n -> n == startCircle || n == endCircle);
		setStart(from.getPinPosition());
		setEnd(to.getPinPosition());
		from.setConnectedWire(this);
		to.setConnectedWire(this);
		this.startPin = from;
		this.endPin = to;
		log.info("Connected {} ConnectablePin ({}) to {} ConnectablePin ({})",
				from.getType(), from.getNode(), to.getType(), to.getNode()
		);
		from.getNode().evaluate();
		to.getNode().evaluate();
	}

	/**
	 * Disconnects this wire from its {@link ConnectablePin}s and removes it from the pane.
	 */
	public void disconnect() {
		if (startPin != null) {
			startPin.setConnectedWire(null);
			log.info("Disconnected {} ConnectablePin (start, {})", startPin.getType(), startPin.getNode());
		}
		if (endPin != null) {
			endPin.setConnectedWire(null);
			log.info("Disconnected {} ConnectablePin (end, {})", endPin.getType(), endPin.getNode());
		}
		MainController.MAIN_PANE.getChildren().remove(this);
	}

	/**
	 * Redraws the lines of this wire using the given {@link WireLayoutStrategy}.
	 *
	 * @param start The start point of the wire.
	 * @param end   The end point of the wire.
	 * @return The new lines.
	 */
	private List<Line> redrawLines(Point start, Point end) {
		getChildren().removeIf(n -> n instanceof Line);
		this.lines = layoutStrategy.layoutWire(start, end, (from, to) -> {
			final Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
			line.setStrokeWidth(3);
			line.setStroke(Color.BLACK);
			return line;
		});
		lines.forEach(this::addEventListeners);
		setActivated(this.activated);
		getChildren().addAll(lines);
		return lines;
	}

	// TODO: docs
	private void addEventListeners(@Nonnull Line line) {
		final Point start = new Point();
		line.setOnMousePressed(me -> {
			start.setX(me.getSceneX());
			start.setY(me.getSceneY());
		});
		line.setOnMouseDragged(me -> {
			if (!me.isPrimaryButtonDown()) return;
			// clear old wire
			MainController.MAIN_PANE.getChildren().removeIf(n -> n instanceof PreviewWire);
			end.set(Point.of(me.getSceneX(), me.getSceneY()).stepped(Constants.GRID_STEP_SIZE));
			final PreviewWire wire = new PreviewWire(WireLayoutStrategy.STRAIGHT_FIRST_Y, start, end);
			wire.setCanConnect(NodeUtils.isPinAtPoint(end).isPresent());
			// add new wire at mouse pos
			MainController.MAIN_PANE.getChildren().add(wire);
		});
		line.setOnMouseReleased(me -> {
			MainController.MAIN_PANE.getChildren().removeIf(n -> n instanceof PreviewWire);
			final ConnectablePin endPin = NodeUtils.isPinAtPoint(end)
					.orElse(null);
			final Wire wire = new Wire(WireLayoutStrategy.STRAIGHT_FIRST_Y, start, end, this, endPin);
			wires.add(wire);
			if (endPin != null) {
				endPin.setConnectedWire(wire);
			}
			log.info("Connected {} Wire to {} Wire", this, wire);
			// this.wire = wire;
			MainController.MAIN_PANE.getChildren().add(wire);
		});
		setOnDragDetected(e -> startFullDrag());
	}

	private <T extends Shape> T add(T node) {
		getChildren().add(node);
		return node;
	}

	/**
	 * Builds a single, draggable {@link Circle} that acts as a placeholder for a {@link ConnectablePin}.
	 *
	 * @param p       The point to build the {@link Circle} at.
	 * @param isStart Whether this is the start or end {@link Circle}.
	 * @return The built {@link Circle} model.
	 */
	private @Nonnull Circle buildWirePin(@Nonnull Point p, boolean isStart) {
		final Circle circle = new Circle();
		circle.setCenterX(p.getX());
		circle.setCenterY(p.getY());
		circle.setRadius((double) Constants.PIN_SIZE / 2);
		circle.setFill(PIN_FILL);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(2);

		final Point dragDelta = new Point();
		circle.setOnMousePressed(me -> {
			dragDelta.setX(me.getSceneX());
			dragDelta.setY(me.getSceneY());
		});
		circle.setOnMouseDragged(me -> {
			final Point newPos = Point.of(me.getSceneX(), me.getSceneY()).stepped(Constants.GRID_STEP_SIZE);
			if (isStart) setStart(newPos);
			else setEnd(newPos);
			final Optional<ConnectablePin> pinOptional = NodeUtils.isPinAtPoint(newPos);
			pinOptional.ifPresent(pin -> {
				if (isStart && endPin != null) connect(pin, endPin);
				if (!isStart && startPin != null) connect(startPin, pin);
			});
		});
		return circle;
	}

	private @Nonnull Circle buildIntersection(@Nonnull Point p) {
		final Circle circle = new Circle();
		circle.setCenterX(p.getX());
		circle.setCenterY(p.getY());
		circle.setRadius((double) Constants.PIN_SIZE / 3);
		circle.setFill(activated ? Color.LIMEGREEN : Color.BLACK);
		return circle;
	}

	/**
	 * Builds the {@link ContextMenu} for this wire.
	 *
	 * @return The built {@link ContextMenu}.
	 */
	@Nonnull
	private ContextMenu buildContextMenu() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem deleteItem = new MenuItem("Delete Wire");
		deleteItem.setOnAction(e -> disconnect());
		contextMenu.getItems().addAll(deleteItem);
		return contextMenu;
	}
}
