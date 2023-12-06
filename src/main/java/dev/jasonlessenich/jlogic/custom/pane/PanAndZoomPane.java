package dev.jasonlessenich.jlogic.custom.pane;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import lombok.Getter;

/**
 * Pane that allows elements inside to be panned and zoomed at. HIGHLY inspired and MOSTLY COPIED from
 * <a href="https://stackoverflow.com/a/51201219">this StackOverflow post</a>
 */
public class PanAndZoomPane extends Pane {

	/**
	 * The default delta value.
	 */
	public static final double DEFAULT_DELTA = 1.3d;

	@Getter
	private final DoubleProperty deltaY = new SimpleDoubleProperty(0.0);

	@Getter
	private final DoubleProperty myScale = new SimpleDoubleProperty(1.0);

	private final Timeline timeline;

	/**
	 * The constructor, which initializes the {@link Timeline} and bindes the scale-properties.
	 */
	public PanAndZoomPane() {
		timeline = new Timeline(60);
		// add scale transform
		scaleXProperty().bind(myScale);
		scaleYProperty().bind(myScale);
	}

	public double getScale() {
		return myScale.get();
	}

	public void setScale(double scale) {
		myScale.set(scale);
	}

	/**
	 * Sets the pivot point.
	 *
	 * @param x The x-value.
	 * @param y The y-value.
	 * @param scale The scale.
	 */
	public void setPivot(double x, double y, double scale) {
		// note: pivot value must be untransformed, i.e. without scaling
		// timeline that scales and moves the node
		timeline.getKeyFrames().clear();
		timeline.getKeyFrames().addAll(
				new KeyFrame(Duration.millis(200), new KeyValue(translateXProperty(), getTranslateX() - x)),
				new KeyFrame(Duration.millis(200), new KeyValue(translateYProperty(), getTranslateY() - y)),
				new KeyFrame(Duration.millis(200), new KeyValue(myScale, scale))
		);
		timeline.play();
	}

	/**
	 * Fits the view to the pans' width.
	 */
	public void fitWidth() {
		double scale = getParent().getLayoutBounds().getMaxX() / getLayoutBounds().getMaxX();
		double oldScale = getScale();
		double f = scale - oldScale;
		double dx = getTranslateX() - getBoundsInParent().getMinX() - getBoundsInParent().getWidth() / 2;
		double dy = getTranslateY() - getBoundsInParent().getMinY() - getBoundsInParent().getHeight() / 2;
		setPivot(f * dx + getBoundsInParent().getMinX(), f * dy + getBoundsInParent().getMinY(), scale);
	}

	/**
	 * Resets the views' zoom.
	 */
	public void resetZoom() {
		setPivot(getTranslateX(), getTranslateY(), 1.0d);
	}

	public void setDeltaY(double dY) {
		deltaY.set(dY);
	}
}
