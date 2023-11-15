package dev.jasonlessenich.jlogic.objects.pins;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface PinLayoutStrategy {
	void layoutPins(ConnectableNode node);

	default void removePins(@Nonnull ConnectableNode node) {
		node.getChildren().removeIf(n -> n instanceof ConnectablePin);
	}
}
