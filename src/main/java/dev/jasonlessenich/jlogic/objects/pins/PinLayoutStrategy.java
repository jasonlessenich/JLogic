package dev.jasonlessenich.jlogic.objects.pins;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface PinLayoutStrategy {
	Map<ConnectablePin.Type, List<ConnectablePin>> layoutPins(ConnectableNode node);

	default void removePins(@Nonnull ConnectableNode node) {
		node.getChildren().removeIf(n -> n instanceof ConnectablePin);
	}
}
