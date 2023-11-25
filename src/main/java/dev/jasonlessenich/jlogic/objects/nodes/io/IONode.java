package dev.jasonlessenich.jlogic.objects.nodes.io;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.pins.layout.PinLayoutStrategy;
import dev.jasonlessenich.jlogic.objects.pins.naming.PinNamingStrategy;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public abstract class IONode extends ConnectableNode {
	protected boolean active = false;

	public IONode(@Nonnull Point point, int inputCount, int outputCount) {
		super(point, PinLayoutStrategy.IO, PinNamingStrategy.INDEX, inputCount, outputCount);
	}

	protected abstract void setFill(Color color);

	public void toggleActivated() {
		active = !active;
		setFill(active ? Color.LAWNGREEN : Color.RED);
	}
}
