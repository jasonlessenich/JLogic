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
	public IONode(@Nonnull Point point, int inputCount, int outputCount) {
		super(point, PinLayoutStrategy.IO, PinNamingStrategy.INDEX, inputCount, outputCount);
	}

	protected abstract void setFill(Color color);

	@Override
	public void setState(boolean[] activated) {
		super.setState(activated);
		setFill(activated[0] ? Color.LAWNGREEN : Color.RED);
	}

	public void toggleActivated() {
		setState(new boolean[]{!getState().getActive()[0]});
	}
}
