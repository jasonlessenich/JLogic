package dev.jasonlessenich.jlogic.objects.wires.layout;

import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * An implementation of {@link WireLayoutStrategy} that lays out a wire only in straight lines.
 */
public class StraightWireLayoutStrategy implements WireLayoutStrategy {
	/**
	 * Whether to use the full width between the start and end points.
	 * Normally, the wire is laid out in three lines, with the first and last lines being half the width.
	 */
	private final boolean firstYThenX;

	protected StraightWireLayoutStrategy(boolean firstYThenX) {
		this.firstYThenX = firstYThenX;
	}

	@Override
	public Path layoutWire(Point start, Point end) {
		final double xLength = end.getX() - start.getX();
		final double yLength = end.getY() - start.getY();
		final Path path = new Path();
		path.getElements().add(new MoveTo(start.getX(), start.getY()));
		if (xLength == 0 || yLength == 0) {
			path.getElements().add(new LineTo(end.getX(), end.getY()));
			return path;
		}
		if (firstYThenX) {
			final Point first = Point.of(start.getX(), start.getY() + yLength);
			path.getElements().addAll(
					new LineTo(start.getX(), start.getY()),
					new LineTo(first.getX(), first.getY()),
					new LineTo(end.getX(), end.getY())
			);
			return path;
		}
		if (Math.abs(xLength) < Constants.GRID_STEP_SIZE * 3) {
			final Point first = Point.of(start.getX() + xLength, start.getY());
			path.getElements().addAll(
					new LineTo(start.getX(), start.getY()),
					new LineTo(first.getX(), first.getY()),
					new LineTo(end.getX(), end.getY())
			);
			return path;
		}
		path.getElements().addAll(
				new LineTo(start.getX(), start.getY()),
				new LineTo(start.getX() + xLength / 2, start.getY()),
				new LineTo(start.getX() + xLength / 2, start.getY() + yLength),
				new LineTo(end.getX(), end.getY())
		);
		return path;
	}
}
