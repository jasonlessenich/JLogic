package dev.jasonlessenich.jlogic.model.io;

import dev.jasonlessenich.jlogic.model.ConnectableNode;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public abstract class IONode extends ConnectableNode {
	public IONode(@Nonnull Point point, @Nonnull Consumer<Circle> circleConsumer, int inputCount, int outputCount) {
		super(point, inputCount, outputCount);
		final Circle circle = new Circle((double) Constants.NODE_SIZE / 2, Color.RED);
		circleConsumer.accept(circle);
		getChildren().add(circle);
	}
}
