package dev.jasonlessenich.jlogic.model;

import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import dev.jasonlessenich.jlogic.utils.PointUtils;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;

import javax.annotation.Nonnull;

public abstract class DraggableNode extends StackPane {
	public static boolean alignToGrid = false;

	private final Point position;

	public DraggableNode(@Nonnull Point point) {
		this.position = point;
		setLayoutX(point.getX() + getLayoutX());
		setLayoutY(point.getY() + getLayoutY());
		final Point dragDelta = new Point();
		setOnMouseEntered(me -> {
			if (!me.isPrimaryButtonDown()) {
				getScene().setCursor(Cursor.HAND);
			}
		});
		setOnMouseExited(me -> {
			if (!me.isPrimaryButtonDown()) {
				getScene().setCursor(Cursor.DEFAULT);
			}
		});
		setOnMousePressed(me -> {
			if (me.isPrimaryButtonDown()) {
				getScene().setCursor(Cursor.DEFAULT);
			}
			getScene().setCursor(Cursor.CLOSED_HAND);
			if (ConnectableNode.connectMode) return;
			dragDelta.setX(me.getX());
			dragDelta.setY(me.getY());
		});
		setOnMouseReleased(me -> {
			if (!me.isPrimaryButtonDown()) {
				getScene().setCursor(Cursor.DEFAULT);
			}
		});
		setOnMouseDragged(me -> {
			if (ConnectableNode.connectMode) return;
			final double layoutX = getLayoutX() + me.getX() - dragDelta.getX();
			final double layoutY = getLayoutY() + me.getY() - dragDelta.getY();
			position.setX(getLayoutX());
			position.setY(getLayoutY());
			setLayoutX(alignToGrid ? PointUtils.step(layoutX, Constants.NODE_SIZE) : layoutX);
			setLayoutY(alignToGrid ? PointUtils.step(layoutY, Constants.NODE_SIZE) : layoutY);
		});
	}

	public Point getPosition() {
		return position;
	}
}
