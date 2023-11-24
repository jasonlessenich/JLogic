package dev.jasonlessenich.jlogic.objects.wires;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.Connection;
import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.nodes.Evaluable;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
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

@Slf4j
public class Wire extends Parent implements Evaluable {
	private static final Color PIN_FILL = Color.WHITE;

	@Nonnull
	@Getter
	private Point start;
	@Nonnull
	@Getter
	private Point end;

	@Nullable
	@Getter
	private ConnectablePin startPin;
	@Nullable
	@Getter
	private ConnectablePin endPin;

	/* MODEL */
	private final Line line;
	@Nullable
	private final Circle startCircle;
	@Nullable
	private final Circle endCircle;

	public Wire(
			@Nonnull Point start,
			@Nonnull Point end,
			@Nullable ConnectablePin startPin,
			@Nullable ConnectablePin endPin
	) {
		setId("Wire");
		this.start = start;
		this.end = end;
		this.line = buildLine(start, end);
		getChildren().add(line);
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
		final ContextMenu contextMenu = buildDefaultContextMenu();
		setOnMousePressed(me -> {
			if (me.isSecondaryButtonDown()) {
				contextMenu.show(this, me.getScreenX(), me.getScreenY());
			} else {
				contextMenu.hide();
			}
		});
	}

	@Override
	public boolean[] evaluate(@Nonnull List<Boolean> inputs) {
		return new boolean[]{inputs.get(0)};
	}

	public void setStart(@Nonnull Point start) {
		this.start = start;
		if (startCircle != null) {
			startCircle.setCenterX(start.getX());
			startCircle.setCenterY(start.getY());
		}
		line.setStartX(start.getX());
		line.setStartY(start.getY());
	}

	public void setEnd(@Nonnull Point end) {
		this.end = end;
		if (endCircle != null) {
			endCircle.setCenterX(end.getX());
			endCircle.setCenterY(end.getY());
		}
		line.setEndX(end.getX());
		line.setEndY(end.getY());
	}

	public void setActivated(boolean activated) {
		line.setStroke(activated ? Color.LIMEGREEN : Color.BLACK);
	}

	public Optional<ConnectablePin> isPinAtPoint(Point to) {
		return MainController.PINS.stream()
				.filter(p -> p.getPinPosition().equals(to))
				.findFirst();
	}

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
			circle.setCenterX(newPos.getX());
			circle.setCenterY(newPos.getY());
			if (isStart) {
				line.setStartX(newPos.getX());
				line.setStartY(newPos.getY());
			} else {
				line.setEndX(newPos.getX());
				line.setEndY(newPos.getY());
			}
			final Optional<ConnectablePin> pinOptional = isPinAtPoint(newPos);
			pinOptional.ifPresent(pin -> {
				if (isStart && endPin != null) connect(pin, endPin);
				if (!isStart && startPin != null) connect(startPin, pin);
			});
		});
		return circle;
	}

	@Nonnull
	private Line buildLine(@Nonnull Point start, @Nonnull Point end) {
		final Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
		line.setStrokeWidth(3);
		line.setStroke(Color.BLACK);
		return line;
	}

	@Nonnull
	private ContextMenu buildDefaultContextMenu() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem deleteItem = new MenuItem("Delete Wire");
		deleteItem.setOnAction(e -> disconnect());
		contextMenu.getItems().addAll(deleteItem);
		return contextMenu;
	}
}
