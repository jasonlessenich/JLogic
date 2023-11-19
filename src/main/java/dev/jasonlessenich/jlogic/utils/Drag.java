package dev.jasonlessenich.jlogic.utils;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
	private Predicate<Node> canDrag;
	@Nullable
	private final Integer step;

	private Drag(
			@Nonnull Node node,
			@Nullable Point initialPosition,
			@Nullable ContextMenu contextMenu,
			@Nullable Consumer<Point> onDrag,
			@Nullable Predicate<Node> canDrag,
			@Nullable Integer step
	) {
		this.node = node;
		this.position = initialPosition == null
				? Point.of(node.getLayoutX(), node.getLayoutY())
				: initialPosition;
		this.contextMenu = contextMenu;
		this.onDrag = onDrag;
		this.canDrag = canDrag;
		this.step = step;
		node.setLayoutX(node.getLayoutX() + position.getX());
		node.setLayoutY(node.getLayoutY() + position.getY());
		makeDraggable();
	}

	private void makeDraggable() {
		final Point dragDelta = new Point();
		node.setOnMouseEntered(me -> {
			if (!checkCanDrag(me)) return;
			setCursor(node, me, Cursor.HAND);
		});
		node.setOnMouseExited(me -> {
			if (!checkCanDrag(me)) return;
			setCursor(node, me, Cursor.DEFAULT);
		});
		node.setOnMousePressed(me -> {
			if (!checkCanDrag(me)) return;
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
		node.setOnMouseReleased(me -> {
			if (!checkCanDrag(me)) return;
			setCursor(node, me, Cursor.DEFAULT);
		});
		node.setOnMouseDragged(me -> {
			if (!checkCanDrag(me)) return;
			Point newLayout = Point.of(
					node.getLayoutX() + me.getX() - dragDelta.getX(),
					node.getLayoutY() + me.getY() - dragDelta.getY()
			);
			position.setX(node.getLayoutX());
			position.setY(node.getLayoutY());
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

	private boolean checkCanDrag(MouseEvent event) {
		return canDrag == null || !(event.getTarget() instanceof Node n) || canDrag.test(n);
	}

	public static class Builder {
		private final Node node;
		@Nullable
		private Point initialPosition;
		@Nullable
		private ContextMenu contextMenu;
		@Nullable
		private Consumer<Point> onDrag;
		@Nullable
		private Predicate<Node> canDrag;
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

		public Builder setCanDrag(@Nullable Predicate<Node> canDrag) {
			this.canDrag = canDrag;
			return this;
		}

		public Builder setStep(int step) {
			this.step = step;
			return this;
		}

		public Drag build() {
			return new Drag(node, initialPosition, contextMenu, onDrag, canDrag, step);
		}
	}
}
