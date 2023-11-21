package dev.jasonlessenich.jlogic.objects.nodes.io;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.pins.layout_strategies.PinLayoutStrategy;
import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.PinNamingStrategy;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public abstract class IONode extends ConnectableNode {
	public IONode(@Nonnull Point point, int inputCount, int outputCount) {
		super(point, PinLayoutStrategy.IO, PinNamingStrategy.INDEX, inputCount, outputCount);
	}

	protected abstract void setFill(Color color);

	@Override
	public void setActivated(boolean activated) {
		super.setActivated(activated);
		setFill(activated ? Color.LAWNGREEN : Color.ORANGERED);
	}

	public void toggleActivated() {
		setActivated(!isActivated());
	}
}
