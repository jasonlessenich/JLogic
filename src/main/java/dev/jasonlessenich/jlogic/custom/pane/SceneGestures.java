package dev.jasonlessenich.jlogic.custom.pane;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

// TODO: docs
public class SceneGestures {
	private final DragContext sceneDragContext = new DragContext();
	private PanAndZoomPane panAndZoomPane;

	public SceneGestures(PanAndZoomPane panAndZoomPane) {
		this.panAndZoomPane = panAndZoomPane;
	}

	@Getter
	private final EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<>() {
		public void handle(@Nonnull MouseEvent event) {
			if (event.getButton() == MouseButton.MIDDLE) {
				sceneDragContext.setMouseAnchorX(event.getX());
				sceneDragContext.setMouseAnchorY(event.getY());
				sceneDragContext.setTranslateAnchorX(panAndZoomPane.getTranslateX());
				sceneDragContext.setTranslateAnchorY(panAndZoomPane.getTranslateY());
			}
		}
	};
	@Getter
	private final EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<>() {
		public void handle(@Nonnull MouseEvent event) {
			if (event.getButton() == MouseButton.MIDDLE) {
				panAndZoomPane.setTranslateX(sceneDragContext.getTranslateAnchorX() + event.getX() - sceneDragContext.getMouseAnchorX());
				panAndZoomPane.setTranslateY(sceneDragContext.getTranslateAnchorY() + event.getY() - sceneDragContext.getMouseAnchorY());
				event.consume();
			}
		}
	};
	@Getter
	private final EventHandler<ScrollEvent> onScrollEventHandler = event -> {
		double delta = PanAndZoomPane.DEFAULT_DELTA;
		double scale = panAndZoomPane.getScale();
		double oldScale = scale;
		panAndZoomPane.setDeltaY(event.getDeltaY());
		if (panAndZoomPane.getDeltaY().get() < 0) {
			scale /= delta;
		} else {
			scale *= delta;
		}
		// add "min" size
		scale = Math.max(.1, scale);
		double f = (scale / oldScale) - 1;
		double dx = event.getX() - panAndZoomPane.getBoundsInParent().getWidth() / 2 + panAndZoomPane.getBoundsInParent().getMinX();
		double dy = event.getY() - panAndZoomPane.getBoundsInParent().getHeight() / 2 + panAndZoomPane.getBoundsInParent().getMinY();
		panAndZoomPane.setPivot(f * dx, f * dy, scale);
		event.consume();
	};

	@Getter
	private final EventHandler<MouseEvent> onMouseClickedEventHandler = event -> {
		if (event.getButton() == MouseButton.MIDDLE && event.getClickCount() == 2) {
			reset();
		}
	};

	public void reset() {
		panAndZoomPane.fitWidth();
	}
}
