package dev.jasonlessenich.jlogic.objects.wires;

import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.objects.wires.layout.WireLayoutStrategy;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.List;

@Slf4j
public class PreviewWire extends Parent {
	/**
	 * The strategy used to lay out the wire
	 */
	private final WireLayoutStrategy layoutStrategy;

	/**
	 * The wire's start point.
	 */
	@Getter
	private final Point start;

	/**
	 * The wire's end point.
	 */
	@Getter
	private final Point end;

	/**
	 * The lines that make up this wire.
	 */
	private List<Line> lines;

	/**
	 * Constructs a new {@link PreviewWire} with the given {@link WireLayoutStrategy},
	 * start and end points, and start and end {@link ConnectablePin}s.
	 *
	 * @param layoutStrategy The {@link WireLayoutStrategy} to use to lay out the wire.
	 * @param start          The start point of the wire.
	 * @param end            The end point of the wire.
	 */
	public PreviewWire(
			@Nonnull WireLayoutStrategy layoutStrategy,
			@Nonnull Point start,
			@Nonnull Point end
	) {
		setId("Wire");
		this.layoutStrategy = layoutStrategy;
		this.start = start;
		this.end = end;
		this.lines = redrawLines(start, end);
	}

	/**
	 * Sets whether the wire can, when released, connect to a {@link ConnectablePin}.
	 *
	 * @param canConnect True if the wire can connect, false otherwise.
	 */
	public void setCanConnect(boolean canConnect) {
		lines.forEach(l -> {
			if (canConnect) l.getStrokeDashArray().clear();
			else l.getStrokeDashArray().addAll(5d, 5d);
		});
	}

	/**
	 * Redraws the lines of this wire using the given {@link WireLayoutStrategy}.
	 *
	 * @param start The start point of the wire.
	 * @param end   The end point of the wire.
	 * @return The new lines.
	 */
	private List<Line> redrawLines(Point start, Point end) {
		getChildren().removeIf(n -> n instanceof Line);
		this.lines = layoutStrategy.layoutWire(start, end, (from, to) -> {
			final Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
			line.setStrokeWidth(2);
			line.setStroke(Color.BLUE);
			return line;
		});
		getChildren().addAll(lines);
		return lines;
	}
}
