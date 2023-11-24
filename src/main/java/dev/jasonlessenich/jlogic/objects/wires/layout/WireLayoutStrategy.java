package dev.jasonlessenich.jlogic.objects.wires.layout;

import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.shape.Line;

import java.util.List;

/**
 * An interface representing a strategy for laying out a wire.
 */
@FunctionalInterface
public interface WireLayoutStrategy {
	WireLayoutStrategy STEPPED = new SteppedWireLayoutStrategy();

	/**
	 * Lays out a set of {@link Line lines} that make up the model of a wire.
	 *
	 * @param start The start {@link Point}.
	 * @param end The end {@link Point}.
	 * @return A {@link List} of {@link Line}s.
	 */
	List<Line> layoutWire(Point start, Point end);
}
