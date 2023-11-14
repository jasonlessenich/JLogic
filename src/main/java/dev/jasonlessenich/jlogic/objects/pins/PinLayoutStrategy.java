package dev.jasonlessenich.jlogic.objects.pins;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;

@FunctionalInterface
public interface PinLayoutStrategy {
	void layoutPins(ConnectableNode node);
}
