package dev.jasonlessenich.jlogic.objects.wires.layout;

import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.shape.Path;

/**
 * An interface representing a strategy for laying out a wire.
 */
@FunctionalInterface
public interface WireLayoutStrategy {
	WireLayoutStrategy STRAIGHT = new StraightWireLayoutStrategy(false);
	WireLayoutStrategy STRAIGHT_FIRST_Y = new StraightWireLayoutStrategy(true);

	/**
	 * Lays out a {@link Path} that makes up the model of a wire.
	 *
	 * @param start The start {@link Point}.
	 * @param end   The end {@link Point}.
	 * @return The {@link Path}.
	 */
	Path layoutWire(Point start, Point end);
}
