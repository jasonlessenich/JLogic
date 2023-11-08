package dev.jasonlessenich.jlogic.controller;

import dev.jasonlessenich.jlogic.utils.Point;
import dev.jasonlessenich.jlogic.utils.PointUtils;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class MainViewController {
	@FXML
	private Pane mainStackPane;

	private Point dragDelta = new Point();
	private Circle selectedCircle;

	private Map<Point, Circle> nodes = new HashMap<>();

	@FXML
	private void initialize() {
		mainStackPane.setOnMouseReleased(e -> {
			final Point point = Point.of(e.getX(), e.getY()).stepped();
			drawNode(point);
		});
	}

	private void drawNode(@Nonnull Point point) {
		if (selectedCircle != null) {
			selectedCircle = null;
			return;
		}
		final Circle circle = new Circle(point.x, point.y, 20, Color.RED);
		circle.setOnMousePressed(mouseEvent -> {
			dragDelta.x = circle.getCenterX() - mouseEvent.getX();
			dragDelta.y = circle.getCenterY() - mouseEvent.getY();
			selectedCircle = circle;
		});
		circle.setOnMouseDragged(mouseEvent -> {
			circle.setCenterX(PointUtils.step(mouseEvent.getX() + dragDelta.x));
			circle.setCenterY(PointUtils.step((mouseEvent.getY() + dragDelta.y)));
			dragDelta = new Point();
		});
		nodes.put(point, circle);
		mainStackPane.getChildren().addAll(circle);
	}
}
