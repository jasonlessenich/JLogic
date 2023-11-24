package dev.jasonlessenich.jlogic.objects.wires.layout;

import dev.jasonlessenich.jlogic.utils.Point;
import dev.jasonlessenich.jlogic.utils.PointUtils;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Pair;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An implementation of {@link WireLayoutStrategy} that lays out a wire only in straight lines.
 */
public class SteppedWireLayoutStrategy implements WireLayoutStrategy {
	protected SteppedWireLayoutStrategy() {}

	@Override
	public List<Line> layoutWire(Point start, Point end) {
		final List<Pair<Point, Point>> points = PointUtils.straightLines(start, end);
		return points.stream().map(p -> buildLine(p.getKey(), p.getValue())).toList();
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
