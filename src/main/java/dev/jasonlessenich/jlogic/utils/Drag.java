package dev.jasonlessenich.jlogic.utils;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Simple class to make any {@link Node} draggable.
 */
public class Drag {
	private final Node node;
	@Getter
	private final Point position;
	@Nullable
	private final ContextMenu contextMenu;
	@Nullable
	private final Consumer<Point> onDrag;
	@Nullable
	private final Integer step;

	private Drag(@Nonnull Node node, @Nullable Point initialPosition, @Nullable ContextMenu contextMenu, @Nullable Consumer<Point> onDrag, @Nullable Integer step) {
		this.node = node;
		this.position = initialPosition == null
				? Point.of(node.getLayoutX(), node.getLayoutY())
				: initialPosition;
		this.contextMenu = contextMenu;
		this.onDrag = onDrag;
		this.step = step;
		node.setLayoutX(node.getLayoutX() + position.getX());
		node.setLayoutY(node.getLayoutY() + position.getY());
		makeDraggable();
	}

	private void makeDraggable() {
		final Point dragDelta = new Point();
		node.setOnMouseEntered(me -> setCursor(node, me, Cursor.HAND));
		node.setOnMouseExited(me -> setCursor(node, me, Cursor.DEFAULT));
		node.setOnMousePressed(me -> {
			if (contextMenu != null) {
				if (me.isSecondaryButtonDown()) {
					contextMenu.show(node, me.getScreenX(), me.getScreenY());
					return;
				}
				contextMenu.hide();
			}
			dragDelta.setX(me.getX());
			dragDelta.setY(me.getY());
		});
		node.setOnMouseReleased(me -> setCursor(node, me, Cursor.DEFAULT));
		node.setOnMouseDragged(me -> {
			Point newLayout = Point.of(
					node.getLayoutX() + me.getX() - dragDelta.getX(),
					node.getLayoutY() + me.getY() - dragDelta.getY()
			);
			position.setX(newLayout.getX());
			position.setY(newLayout.getY());
			if (step != null) newLayout = newLayout.stepped(step);
			if (onDrag != null) onDrag.accept(newLayout);
			node.relocate(newLayout.getX(), newLayout.getY());
		});
	}

	private void setCursor(@Nonnull Node node, @Nonnull MouseEvent me, @Nonnull Cursor cursor) {
		if (!me.isPrimaryButtonDown()) {
			node.getScene().setCursor(cursor);
		}
	}

	public static class Builder {
		private final Node node;
		@Nullable
		private Point initialPosition;
		@Nullable
		private ContextMenu contextMenu;
		@Nullable
		private Consumer<Point> onDrag;
		private Integer step = Constants.GRID_STEP_SIZE;

		public Builder(@Nonnull Node node) {
			this.node = node;
		}

		public Builder setInitialPosition(@Nonnull Point initialPosition) {
			this.initialPosition = initialPosition;
			return this;
		}

		public Builder setContextMenu(@Nonnull ContextMenu contextMenu) {
			this.contextMenu = contextMenu;
			return this;
		}

		public Builder setOnDrag(@Nonnull Consumer<Point> onDrag) {
			this.onDrag = onDrag;
			return this;
		}

		public Builder setStep(int step) {
			this.step = step;
			return this;
		}

		public Drag build() {
			return new Drag(node, initialPosition, contextMenu, onDrag, step);
		}
	}
}
