package dev.jasonlessenich.jlogic.objects.pins.layout_strategies;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.PinNamingStrategy;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.text.Text;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GateLayoutStrategy implements PinLayoutStrategy {
	protected GateLayoutStrategy() {
	}

	@Override
	public Map<ConnectablePin.Type, List<ConnectablePin>> layoutPins(
			@Nonnull ConnectableNode node,
			@Nonnull PinNamingStrategy namingStrategy
	) {
		this.removePins(node);
		final Map<ConnectablePin.Type, List<ConnectablePin>> pinMap = new HashMap<>();
		if (node.getInputCount() > 0)
			pinMap.put(ConnectablePin.Type.INPUT, layoutNodePins(node, namingStrategy, node.getInputCount(), true));
		if (node.getOutputCount() > 0)
			pinMap.put(ConnectablePin.Type.OUTPUT, layoutNodePins(node, namingStrategy, node.getOutputCount(), false));
		return pinMap;
	}

	@Nonnull
	private List<ConnectablePin> layoutNodePins(
			@Nonnull ConnectableNode node,
			@Nonnull PinNamingStrategy namingStrategy,
			int count,
			boolean isInput
	) {
		final double requiredHeight = Constants.PIN_SIZE * (2 * count);
		final List<ConnectablePin> pins = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			final String label = namingStrategy.calculateName(i);
			double x = (node.getModel().getMaxWidth() / 2);
			if (isInput) x *= -1;
			double y = (((i * Constants.PIN_SIZE) * 2) - requiredHeight / 2) + Constants.PIN_SIZE;
			final ConnectablePin pin = new ConnectablePin(
					label, isInput ? ConnectablePin.Type.INPUT : ConnectablePin.Type.OUTPUT, node, Point.of(x, y)
			);
			pin.setTranslateX(x);
			pin.setTranslateY(y);
			node.getChildren().add(pin);
			if (count > 1) {
				final Text text = new Text(label);
				text.setStyle("-fx-font-size: 10");
				text.setTranslateX(x - (isInput ? 10 : -10));
				text.setTranslateY(y - 10);
				node.getChildren().add(text);
			}
			pins.add(pin);
		}
		return pins;
	}
}
