package dev.jasonlessenich.jlogic.objects.pins.layout_strategies;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.PinNamingStrategy;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface PinLayoutStrategy {
	PinLayoutStrategy GATE = new GateLayoutStrategy();
	PinLayoutStrategy IO = new IOLayoutStrategy();

	Map<ConnectablePin.Type, List<ConnectablePin>> layoutPins(ConnectableNode node, PinNamingStrategy namingStrategy);

	default void removePins(@Nonnull ConnectableNode node) {
		node.getChildren().removeIf(n -> n instanceof ConnectablePin);
	}
}
