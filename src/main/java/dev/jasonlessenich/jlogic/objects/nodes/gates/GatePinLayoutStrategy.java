package dev.jasonlessenich.jlogic.objects.nodes.gates;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.objects.pins.PinLayoutStrategy;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;

import javax.annotation.Nonnull;

public class GatePinLayoutStrategy implements PinLayoutStrategy {
	@Override
	public void layoutPins(@Nonnull ConnectableNode node) {
		removePins(node);
		if (node.getInputCount() > 0)
			layoutNodePins(node, node.getInputCount(), true);
		if (node.getOutputCount() > 0)
			layoutNodePins(node, node.getOutputCount(), false);
	}

	private void layoutNodePins(@Nonnull ConnectableNode node, int count, boolean isInput) {
		final double requiredHeight = Constants.PIN_SIZE * (2 * count);
		for (int i = 0; i < count; i++) {
			double x = (node.getModel().getMaxWidth() / 2);
			if (isInput) x *= -1;
			double y = (((i * Constants.PIN_SIZE) * 2) - requiredHeight / 2) + Constants.PIN_SIZE;
			final ConnectablePin pin = new ConnectablePin(node, Point.of(x, y));
			pin.setTranslateX(x);
			pin.setTranslateY(y);
			node.getChildren().add(pin);
		}
	}
}
