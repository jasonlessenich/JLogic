package dev.jasonlessenich.jlogic.objects.pins.layout_strategies;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.PinNamingStrategy;
import dev.jasonlessenich.jlogic.utils.Point;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOLayoutStrategy implements PinLayoutStrategy {
	protected IOLayoutStrategy() {}

	@Override
	public Map<ConnectablePin.Type, List<ConnectablePin>> layoutPins(
			@Nonnull ConnectableNode node,
			@Nonnull PinNamingStrategy namingStrategy
	) {
		this.removePins(node);
		final Map<ConnectablePin.Type, List<ConnectablePin>> pinMap = new HashMap<>();
		if (node.getInputCount() > 0)
			pinMap.put(ConnectablePin.Type.INPUT, layoutNodePins(node, namingStrategy, true));
		if (node.getOutputCount() > 0)
			pinMap.put(ConnectablePin.Type.OUTPUT, layoutNodePins(node, namingStrategy, false));
		return pinMap;
	}

	@Nonnull
	private List<ConnectablePin> layoutNodePins(
			@Nonnull ConnectableNode node,
			@Nonnull PinNamingStrategy namingStrategy,
			boolean isInput
	) {
		final String name = namingStrategy.calculateName(1);
		double x = (node.getModel().getMaxWidth() / 2);
		if (isInput) x *= -1;
		final ConnectablePin pin = new ConnectablePin(
				name, isInput ? ConnectablePin.Type.INPUT : ConnectablePin.Type.OUTPUT, node, Point.of(x, 0)
		);
		pin.setTranslateX(x);
		node.getChildren().add(pin);
		return List.of(pin);
	}
}
