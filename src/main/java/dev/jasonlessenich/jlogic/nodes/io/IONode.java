package dev.jasonlessenich.jlogic.nodes.io;

import dev.jasonlessenich.jlogic.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public abstract class IONode extends ConnectableNode {
	private final Circle circle;

	@Getter
	private boolean activated = false;

	public IONode(@Nonnull Point point, @Nonnull Consumer<Circle> circleConsumer, int inputCount, int outputCount) {
		super(point, inputCount, outputCount);
		this.circle = new Circle((double) Constants.NODE_SIZE / 2, Color.RED);
		circleConsumer.accept(circle);
		getChildren().add(circle);
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
		circle.setFill(activated ? Color.LAWNGREEN : Color.ORANGERED);
	}

	public void toggleActivated() {
		setActivated(!isActivated());
	}
}
