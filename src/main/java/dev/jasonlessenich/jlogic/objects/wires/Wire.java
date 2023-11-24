package dev.jasonlessenich.jlogic.objects.wires;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.Connection;
import dev.jasonlessenich.jlogic.objects.nodes.Evaluable;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.objects.wires.layout.WireLayoutStrategy;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * A {@link Wire} that represents a connection between two {@link ConnectablePin}s.
 * This is a {@link Parent} that contains the lines that make up the wire, as well as
 * the start and end {@link Circle}s if there is no {@link ConnectablePin}.
 * The layout of this wire is determined by the specified {@link WireLayoutStrategy}.
 */
@Slf4j
public class Wire extends Parent implements Evaluable {
	private static final Color PIN_FILL = Color.WHITE;

	/**
	 * The strategy used to layout the wire
	 */
	private final WireLayoutStrategy layoutStrategy;

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
	 * Constructs a new {@link Wire} with the given {@link WireLayoutStrategy},
	 * start and end points, and start and end {@link ConnectablePin}s.
	 *
	 * @param layoutStrategy The {@link WireLayoutStrategy} to use to lay out the wire.
	 * @param start The start point of the wire.
	 * @param end The end point of the wire.
	 * @param startPin A nullable {@link ConnectablePin} that this wire is connected to.
	 * @param endPin A nullable {@link ConnectablePin} that this wire is connected to.
	 */
	public Wire(
			@Nonnull WireLayoutStrategy layoutStrategy,
			@Nonnull Point start,
			@Nonnull Point end,
			@Nullable ConnectablePin startPin,
			@Nullable ConnectablePin endPin
	) {
		setId("Wire");
		this.layoutStrategy = layoutStrategy;
		this.start = start;
		this.end = end;
		this.lines = redrawLines(start, end);
		// build start pin
		this.startPin = startPin;
		if (startPin == null) {
			this.startCircle = buildWirePin(start, true);
			getChildren().add(startCircle);
		} else {
			this.startCircle = null;
		}
		// build end pin
		this.endPin = endPin;
		if (endPin == null) {
			this.endCircle = buildWirePin(end, false);
			getChildren().add(endCircle);
		} else {
			this.endCircle = null;
		}
		final ContextMenu contextMenu = buildContextMenu();
		setOnMousePressed(me -> {
			if (me.isSecondaryButtonDown()) {
				contextMenu.show(this, me.getScreenX(), me.getScreenY());
			} else {
				contextMenu.hide();
			}
		});
	}

	/**
	 * Constructs a new {@link Wire} with the given start and end points,
	 * and start and end {@link ConnectablePin}s.
	 * This defaults to using the {@link WireLayoutStrategy#STRAIGHT} layout strategy.
	 *
	 * @param start The start point of the wire.
	 * @param end The end point of the wire.
	 * @param startPin A nullable {@link ConnectablePin} that this wire is connected to.
	 * @param endPin A nullable {@link ConnectablePin} that this wire is connected to.
	 */
	public Wire(
			@Nonnull Point start,
			@Nonnull Point end,
			@Nullable ConnectablePin startPin,
			@Nullable ConnectablePin endPin
	) {
		this(WireLayoutStrategy.STRAIGHT, start, end, startPin, endPin);
	}

	@Override
	public boolean[] evaluate(@Nonnull List<Boolean> inputs) {
		return new boolean[]{inputs.get(0)};
	}

	/**
	 * Sets the activation state of this wire.
	 * This simply changes the color of the wire.
	 *
	 * @param activated The new activation state.
	 */
	public void setActivated(boolean activated) {
		lines.forEach(l -> l.setStroke(activated ? Color.LIMEGREEN : Color.BLACK));
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
	 * Checks whether there is a {@link ConnectablePin} at the given point.
	 *
	 * @param to The point to check.
	 * @return An {@link Optional} containing the {@link ConnectablePin} if there is one.
	 */
	public Optional<ConnectablePin> isPinAtPoint(Point to) {
		return MainController.PINS.stream()
				.filter(p -> p.getPinPosition().equals(to))
				.findFirst();
	}

	/**
	 * Creates a new {@link Wire} connecting the given {@link ConnectablePin}s.
	 * This will remove the start and end {@link Circle}s from this wire, as well
	 * as lock the start and end points to the {@link ConnectablePin}s.
	 *
	 * @param from The {@link ConnectablePin} to connect from.
	 * @param to The {@link ConnectablePin} to connect to.
	 */
	public void connect(@Nonnull ConnectablePin from, @Nonnull ConnectablePin to) {
		if (!from.canConnectTo(to)) return;
		// remove end pin
		getChildren().removeIf(n -> n == startCircle || n == endCircle);
		setStart(from.getPinPosition());
		setEnd(to.getPinPosition());
		from.setConnectedWire(this);
		to.setConnectedWire(this);
		from.getNode().getConnections().add(new Connection(from, to, Connection.Type.FORWARD));
		to.getNode().getConnections().add(new Connection(to, from, Connection.Type.BACKWARD));
		this.startPin = from;
		this.endPin = to;
		log.info("Connected {} ConnectablePin ({}) to {} ConnectablePin ({})",
				from.getType(), from.getNode(), to.getType(), to.getNode()
		);
		from.getNode().evaluate(0);
		to.getNode().evaluate(0);
	}

	/**
	 * Disconnects this wire from its {@link ConnectablePin}s and removes it from the pane.
	 */
	public void disconnect() {
		if (startPin != null) {
			startPin.setConnectedWire(null);
			startPin.getNode().getConnections().removeIf(c -> c.getConnectionTo() == startPin || c.getConnectionFrom() == startPin);
			log.info("Disconnected {} ConnectablePin (start, {})", startPin.getType(), startPin.getNode());
		}
		if (endPin != null) {
			endPin.setConnectedWire(null);
			endPin.getNode().getConnections().removeIf(c -> c.getConnectionTo() == endPin || c.getConnectionFrom() == endPin);
			log.info("Disconnected {} ConnectablePin (end, {})", endPin.getType(), endPin.getNode());
		}
		MainController.MAIN_PANE.getChildren().remove(this);
	}

	/**
	 * Redraws the lines of this wire using the given {@link WireLayoutStrategy}.
	 *
	 * @param start The start point of the wire.
	 * @param end The end point of the wire.
	 * @return The new lines.
	 */
	private List<Line> redrawLines(Point start, Point end) {
		getChildren().removeIf(n -> n instanceof Line);
		this.lines = layoutStrategy.layoutWire(start, end);
		getChildren().addAll(lines);
		return lines;
	}

	/**
	 * Builds a single, draggable {@link Circle} that acts as a placeholder for a {@link ConnectablePin}.
	 *
	 * @param p The point to build the {@link Circle} at.
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
			final Optional<ConnectablePin> pinOptional = isPinAtPoint(newPos);
			pinOptional.ifPresent(pin -> {
				if (isStart && endPin != null) connect(pin, endPin);
				if (!isStart && startPin != null) connect(startPin, pin);
			});
		});
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
