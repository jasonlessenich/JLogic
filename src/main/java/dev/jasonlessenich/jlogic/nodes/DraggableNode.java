package dev.jasonlessenich.jlogic.nodes;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.nodes.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import dev.jasonlessenich.jlogic.utils.PointUtils;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import lombok.Getter;

import javax.annotation.Nonnull;

public abstract class DraggableNode extends StackPane {
	public static boolean alignToGrid = true;

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
			if (MainController.simulationMode) return;
			dragDelta.setX(me.getX());
			dragDelta.setY(me.getY());
		});
		setOnMouseReleased(me -> {
			if (!me.isPrimaryButtonDown()) {
				getScene().setCursor(Cursor.DEFAULT);
			}
		});
		setOnMouseDragged(me -> {
			if (MainController.simulationMode || !me.isPrimaryButtonDown()) return;
			if (me.getTarget() instanceof Circle circle && circle.getId() != null && circle.getId().equals("ConnectionPin")) return;
			final double layoutX = getLayoutX() + me.getX() - dragDelta.getX();
			final double layoutY = getLayoutY() + me.getY() - dragDelta.getY();
			position.setX(getLayoutX());
			position.setY(getLayoutY());
			setLayoutX(alignToGrid ? PointUtils.step(layoutX, Constants.GRID_STEP_SIZE) : layoutX);
			setLayoutY(alignToGrid ? PointUtils.step(layoutY, Constants.GRID_STEP_SIZE) : layoutY);
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
			for (DraggableNode node : MainController.NODES.values()) {
				if (node instanceof ConnectableNode connectableNode) {
					connectableNode.getConnections().removeIf(c -> c.getConnectionFrom().getParentNode() == this || c.getConnectionTo().getParentNode() == this);
				}
			}
			// redraw all connections
			MainController.NODES.remove(position);
			ConnectablePin.redrawConnections(pane);
			ConnectablePin.evaluateConnections();
		});
		return deleteNode;
	}
}
