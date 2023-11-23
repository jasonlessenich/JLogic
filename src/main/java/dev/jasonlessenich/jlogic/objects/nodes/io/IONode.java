package dev.jasonlessenich.jlogic.objects.nodes.io;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.objects.pins.layout_strategies.PinLayoutStrategy;
import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.PinNamingStrategy;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Arrays;

@Getter
public abstract class IONode extends ConnectableNode {
	public IONode(@Nonnull Point point, int inputCount, int outputCount) {
		super(point, PinLayoutStrategy.IO, PinNamingStrategy.INDEX, inputCount, outputCount);
	}

	protected abstract void setFill(Color color);

	@Override
	public void setActive(boolean[] activated) {
		super.setActive(activated);
		System.out.println(Arrays.toString(activated));
		// get pin index
		int index = 0;
		if (activated.length > 1) {
			for (ConnectablePin pin : getInputPins()) {
				if (pin.getConnectedWire().isPresent()) {
					final ConnectablePin startPin = pin.getConnectedWire().get().getStartPin();
					if (startPin == null) continue;
					index = startPin.getNode().getOutputPins().indexOf(startPin);
				}
			}
		}
		setFill(activated[index] ? Color.LAWNGREEN : Color.ORANGERED);
	}

	public void toggleActivated() {
		setActive(new boolean[]{!getState().getActive()[0]});
	}
}
