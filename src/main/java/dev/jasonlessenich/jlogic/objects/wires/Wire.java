package dev.jasonlessenich.jlogic.objects.wires;

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
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class Wire extends Parent {
	private static final Color PIN_FILL = Color.WHITE;
	private static final Color PIN_CONNECTED = Color.LIGHTBLUE;

	@Nonnull
	@Getter
	private final Point start;
	@Nonnull
	@Getter
	private final Point end;
	private final Line line;
	@Nullable
	private final Circle startPin;
	@Nullable
	private final Circle endPin;

	public Wire(@Nonnull Point start, @Nonnull Point end) {
		this(start, end, true, true);
	}

	public Wire(@Nonnull Point start, @Nonnull Point end, boolean hasStart, boolean hasEnd) {
		setId("Wire");
		this.start = start;
		this.end = end;
		this.line = buildLine(start, end);
		getChildren().add(line);
		this.startPin = hasStart ? buildWirePin(start, true) : null;
		if (startPin != null) {
			getChildren().add(startPin);
		}
		this.endPin = hasEnd ? buildWirePin(end, false) : null;
		if (endPin != null) {
			getChildren().add(endPin);
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

	public void setActivated(boolean activated) {
		line.setStroke(activated ? Color.LIMEGREEN : Color.BLACK);
	}

	public void removeStartPin() {
		getChildren().removeIf(n -> n == startPin);
	}

	public void removeEndPin() {
		getChildren().removeIf(n -> n == endPin);
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
		deleteItem.setOnAction(actionEvent -> MainController.MAIN_PANE.getChildren().remove(this));
		contextMenu.getItems().addAll(deleteItem);
		return contextMenu;
	}
}
