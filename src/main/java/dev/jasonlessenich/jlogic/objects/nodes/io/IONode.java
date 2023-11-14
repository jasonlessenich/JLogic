package dev.jasonlessenich.jlogic.objects.nodes.io;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.nodes.gates.GatePinLayoutStrategy;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Consumer;

public abstract class IONode extends ConnectableNode {
	@Getter
	private boolean activated = false;

	public IONode(@Nonnull Point point, int inputCount, int outputCount) {
		// TODO: add separate, simpler strategy
		super(point, new GatePinLayoutStrategy(), inputCount, outputCount);
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
		getModel().getShape().setFill(activated ? Color.LAWNGREEN : Color.ORANGERED);
	}

	public void toggleActivated() {
		setActivated(!isActivated());
	}
}
