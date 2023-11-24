package dev.jasonlessenich.jlogic.objects.wires.layout;

import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Pair;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An implementation of {@link WireLayoutStrategy} that lays out a wire only in straight lines.
 */
public class StraightWireLayoutStrategy implements WireLayoutStrategy {
	protected StraightWireLayoutStrategy() {}

	@Override
	public List<Line> layoutWire(Point start, Point end) {
		final List<Pair<Point, Point>> points = straightLines(start, end);
		return points.stream().map(p -> buildLine(p.getKey(), p.getValue())).toList();
	}

	/**
	 * Lays out a set of {@link Point points} only in straight, one-dimensional, lines.
	 * If either the x or y length of the original (diagonal) line is 0, then only one line is returned,
	 * as it is already one-dimensional.
	 *
	 * @param start The start {@link Point}.
	 * @param end The end {@link Point}.
	 * @return An unmodifiable {@link List} of {@link Pair}s that contain the start and end {@link Point}s of each line.
	 */
	@Nonnull
	private List<Pair<Point, Point>> straightLines(@Nonnull Point start, @Nonnull Point end) {
		final double xLength = end.getX() - start.getX();
		final double yLength = end.getY() - start.getY();
		if (xLength == 0 || yLength == 0) {
			return List.of(new Pair<>(start, end));
		}
		final Point first = Point.of(start.getX() + xLength / 2, start.getY());
		final Point second = Point.of(first.getX(), start.getY() + yLength);
		return List.of(new Pair<>(start, first), new Pair<>(first, second), new Pair<>(second, end));
	}

	/**
	 * Builds a {@link Line} from the given start and end {@link Point}s.
	 *
	 * @param start The start {@link Point}.
	 * @param end The end {@link Point}.
	 * @return The {@link Line}.
	 */
	@Nonnull
	private Line buildLine(@Nonnull Point start, @Nonnull Point end) {
		final Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
		line.setStrokeWidth(3);
		line.setStroke(Color.BLACK);
		return line;
	}
}
