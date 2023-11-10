package dev.jasonlessenich.jlogic.nodes;

import dev.jasonlessenich.jlogic.controller.MainViewController;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import dev.jasonlessenich.jlogic.utils.PointUtils;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class DraggableNode extends StackPane {
	public static boolean alignToGrid = false;

	@Getter
	private final Point position;

	@Nonnull
	protected ContextMenu contextMenu;

	public DraggableNode(@Nonnull Point point) {
		this.position = point;
		this.contextMenu = buildDefaultContextMenu();
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
			if (me.isSecondaryButtonDown()) {
				this.contextMenu.show(this, me.getScreenX(), me.getScreenY());
				return;
			}
			this.contextMenu.hide();
			if (me.isPrimaryButtonDown()) {
				getScene().setCursor(Cursor.DEFAULT);
			}
			getScene().setCursor(Cursor.CLOSED_HAND);
			if (ConnectableNode.connectMode || MainViewController.simulationMode) return;
			dragDelta.setX(me.getX());
			dragDelta.setY(me.getY());
		});
		setOnMouseReleased(me -> {
			if (!me.isPrimaryButtonDown()) {
				getScene().setCursor(Cursor.DEFAULT);
			}
		});
		setOnMouseDragged(me -> {
			if (ConnectableNode.connectMode || MainViewController.simulationMode || !me.isPrimaryButtonDown()) return;
			final double layoutX = getLayoutX() + me.getX() - dragDelta.getX();
			final double layoutY = getLayoutY() + me.getY() - dragDelta.getY();
			position.setX(getLayoutX());
			position.setY(getLayoutY());
			setLayoutX(alignToGrid ? PointUtils.step(layoutX, Constants.NODE_SIZE) : layoutX);
			setLayoutY(alignToGrid ? PointUtils.step(layoutY, Constants.NODE_SIZE) : layoutY);
		});
	}

	private @Nonnull ContextMenu buildDefaultContextMenu() {
		final ContextMenu contextMenu = new ContextMenu();
		contextMenu.getItems().addAll(buildDeleteItem());
		return contextMenu;
	}

	public @Nonnull MenuItem buildDeleteItem() {
		final MenuItem deleteNode = new MenuItem("Delete Node");
		deleteNode.setOnAction(actionEvent -> {
			getChildren().clear();
			final AnchorPane pane = ((AnchorPane) getParent());
			pane.getChildren().remove(this); // remove from pane
			// delete all connections
			for (DraggableNode node : MainViewController.NODES.values()) {
				if (node instanceof ConnectableNode) {
					final ConnectableNode connectableNode = (ConnectableNode) node;
					connectableNode.connections.removeIf(c -> c.getConnectionFrom() == this || c.getConnectionTo() == this);
				}
			}
			// redraw all connections
			MainViewController.NODES.remove(position);
			ConnectableNode.redrawConnections(pane);
		});
		return deleteNode;
	}
}
