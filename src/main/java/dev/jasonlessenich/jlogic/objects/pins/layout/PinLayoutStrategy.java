package dev.jasonlessenich.jlogic.objects.pins.layout;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.objects.pins.naming.PinNamingStrategy;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface PinLayoutStrategy {
	PinLayoutStrategy GATE = new GatePinLayoutStrategy();
	PinLayoutStrategy IO = new IOPinLayoutStrategy();

	Map<ConnectablePin.Type, List<ConnectablePin>> layoutPins(ConnectableNode node, PinNamingStrategy inputNamingStrategy, PinNamingStrategy outputNamingStrategy);

	default void removePins(@Nonnull ConnectableNode node) {
		node.getChildren().removeIf(n -> n instanceof ConnectablePin);
	}
}
