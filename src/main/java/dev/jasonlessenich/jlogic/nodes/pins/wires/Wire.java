package dev.jasonlessenich.jlogic.nodes.pins.wires;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import dev.jasonlessenich.jlogic.utils.PointUtils;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import javax.annotation.Nonnull;
import java.util.Optional;

public class Wire extends Parent {
	private static final Color PIN_FILL = Color.WHITE;
	private static final Color PIN_CONNECTED = Color.LIGHTBLUE;

	private final Line line;
	private final Circle startPin, endPin;

	public Wire(@Nonnull Point start, @Nonnull Point end) {
		setId("Wire");
		this.line = buildLine(start.getX(), start.getY(), end.getX(), end.getY());
		this.startPin = buildWirePin(start, true);
		this.endPin = buildWirePin(end, false);
		getChildren().add(line);
		getChildren().addAll(startPin, endPin);
		final ContextMenu contextMenu = buildDefaultContextMenu();
		setOnMousePressed(me -> {
			if (me.isSecondaryButtonDown()) {
				contextMenu.show(this, me.getScreenX(), me.getScreenY());
			} else {
				contextMenu.hide();
			}
		});
	}

	public void setActivated(boolean activated) {
		line.setStroke(activated ? Color.LIMEGREEN : Color.BLACK);
	}

	public void removeStartPin() {
		getChildren().removeIf(n -> n == startPin);
	}

	public void removeEndPin() {
		getChildren().removeIf(n -> n == endPin);
	}

	public Optional<Wire> intersectsWire(Point p) {
		return MainController.WIRES.stream()
				.filter(wire -> wire != this)
				.filter(wire -> PointUtils.isOnLine(p, Point.of(wire.line.getStartX(), wire.line.getStartY()), Point.of(wire.line.getEndX(), wire.line.getEndY())))
				.findFirst();
	}

	private @Nonnull Circle buildWirePin(@Nonnull Point p, boolean isStart) {
		final Circle circle = new Circle();
		circle.setCenterX(p.getX());
		circle.setCenterY(p.getY());
		circle.setRadius(Constants.NODE_CONNECTION_SIZE);
		circle.setFill(PIN_FILL);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(2);

		final Point dragDelta = new Point();
		circle.setOnMousePressed(me -> {
			dragDelta.setX(me.getSceneX());
			dragDelta.setY(me.getSceneY());
		});
		circle.setOnMouseDragged(me -> {
			final double newX = PointUtils.step(me.getSceneX(), Constants.GRID_STEP_SIZE);
			final double newY = PointUtils.step(me.getSceneY(), Constants.GRID_STEP_SIZE);
			circle.setCenterX(newX);
			circle.setCenterY(newY);
			if (isStart) {
				line.setStartX(newX);
				line.setStartY(newY);
			} else {
				line.setEndX(newX);
				line.setEndY(newY);
			}
			// final Optional<Wire> wireOptional = intersectsWire(Point.of(newX, newY));
			// circle.setFill(wireOptional.isPresent() ? PIN_CONNECTED : PIN_FILL);
		});
		return circle;
	}

	@Nonnull
	private Line buildLine(double startX, double startY, double endX, double endY) {
		final Line line = new Line(startX, startY, endX, endY);
		line.setStrokeWidth(3);
		line.setStroke(Color.BLACK);
		line.setOnMouseMoved(me -> {
			getChildren().removeIf(n -> n instanceof Circle && "WireIntersectionIndicator".equals(n.getId()));
			final double x = PointUtils.step(me.getSceneX(), Constants.GRID_STEP_SIZE);
			final double y = PointUtils.step(me.getSceneY(), Constants.GRID_STEP_SIZE);
			final Circle circle = new Circle(x, y, Constants.NODE_CONNECTION_SIZE);
			circle.setId("WireIntersectionIndicator");
			getChildren().add(circle);
		});
		line.setOnMouseExited(me -> {
			//getChildren().removeIf(n -> n instanceof Circle && "WireIntersectionIndicator".equals(n.getId()));
		});
		return line;
	}

	@Nonnull
	private ContextMenu buildDefaultContextMenu() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem deleteItem = new MenuItem("Delete Wire");
		deleteItem.setOnAction(actionEvent -> MainController.MAIN_PANE.getChildren().remove(this));
		contextMenu.getItems().addAll(deleteItem);
		return contextMenu;
	}
}
