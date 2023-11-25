package dev.jasonlessenich.jlogic.utils;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import javafx.scene.layout.Region;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * A utility class for interacting with {@link javafx.scene.Node}s.
 */
public class NodeUtils {
	private NodeUtils() {
	}

	/**
	 * Sets min and max width and height of a {@link Region}.
	 *
	 * @param node   The {@link Region}.
	 * @param width  The width.
	 * @param height The height.
	 * @return The {@link Region}.
	 */
	public static Region setSize(@Nonnull Region node, double width, double height) {
		node.setMaxWidth(width);
		node.setMinWidth(width);
		node.setMaxHeight(height);
		node.setMinHeight(height);
		return node;
	}

	/**
	 * Sets min and max width and height of a {@link Region}.
	 *
	 * @param node The {@link Region}.
	 * @param size The size.
	 * @return The {@link Region}.
	 */
	public static Region setSize(Region node, double size) {
		return setSize(node, size, size);
	}

	/**
	 * Checks whether there is a {@link ConnectablePin} at the given point.
	 *
	 * @param to The point to check.
	 * @return An {@link Optional} containing the {@link ConnectablePin} if there is one.
	 */
	@Nonnull
	public static Optional<ConnectablePin> isPinAtPoint(Point to) {
		return MainController.PINS.stream()
				.filter(p -> p.getPinPosition().equals(to))
				.findFirst();
	}
}
