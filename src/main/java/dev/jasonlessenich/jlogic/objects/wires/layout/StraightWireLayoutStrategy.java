package dev.jasonlessenich.jlogic.objects.wires.layout;

import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.shape.Line;
import javafx.util.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

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
	public List<Line> layoutWire(Point start, Point end, BiFunction<Point, Point, Line> modelFunction) {
		final List<Pair<Point, Point>> points = straightLines(start, end);
		return points.stream().map(p -> modelFunction.apply(p.getKey(), p.getValue())).toList();
	}

	/**
	 * Lays out a set of {@link Point points} only in straight, one-dimensional, lines.
	 * If either the x or y length of the original (diagonal) line is 0, then only one line is returned,
	 * as it is already one-dimensional.
	 *
	 * @param start The start {@link Point}.
	 * @param end   The end {@link Point}.
	 * @return An unmodifiable {@link List} of {@link Pair}s that contain the start and end {@link Point}s of each line.
	 */
	@Nonnull
	private List<Pair<Point, Point>> straightLines(@Nonnull Point start, @Nonnull Point end) {
		final double xLength = end.getX() - start.getX();
		final double yLength = end.getY() - start.getY();
		if (xLength == 0 || yLength == 0) {
			return List.of(new Pair<>(start, end));
		}
		if (firstYThenX) {
			final Point first = Point.of(start.getX(), start.getY() + yLength);
			return List.of(new Pair<>(start, first), new Pair<>(first, end));
		}
		if (Math.abs(xLength) < Constants.GRID_STEP_SIZE * 3) {
			final Point first = Point.of(start.getX() + xLength, start.getY());
			return List.of(new Pair<>(start, first), new Pair<>(first, end));
		}
		final Point first = Point.of(start.getX() + xLength / 2, start.getY());
		final Point second = Point.of(first.getX(), start.getY() + yLength);
		return List.of(new Pair<>(start, first), new Pair<>(first, second), new Pair<>(second, end));
	}
}
