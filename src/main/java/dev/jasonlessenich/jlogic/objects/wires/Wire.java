package dev.jasonlessenich.jlogic.objects.wires;

import dev.jasonlessenich.jlogic.controller.MainController;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class Wire extends Parent {
	private static final Color PIN_FILL = Color.WHITE;

	@Nonnull
	@Getter
	private Point start;
	@Nonnull
	@Getter
	private Point end;

	@Nullable
	private ConnectablePin startPin;
	@Nullable
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

	public Optional<ConnectablePin> checkConnection(Point to) {
		return MainController.PINS.stream()
				.filter(p -> p.getPinPosition().equals(to))
				.findFirst();
	}

	public void connect(@Nonnull ConnectablePin from, @Nonnull ConnectablePin to) {
		// remove end pin
		getChildren().removeIf(n -> n == startCircle || n == endCircle);
		setStart(from.getPinPosition());
		setEnd(to.getPinPosition());
		from.setConnectedWire(this);
		to.setConnectedWire(this);
		this.startPin = from;
		this.endPin = to;
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
			final Optional<ConnectablePin> pinOptional = checkConnection(newPos);
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
		deleteItem.setOnAction(e -> {
			if (startPin != null)
				startPin.setConnectedWire(null);
			if (endPin != null)
				endPin.setConnectedWire(null);
			MainController.MAIN_PANE.getChildren().remove(this);
		});
		contextMenu.getItems().addAll(deleteItem);
		return contextMenu;
	}
}
