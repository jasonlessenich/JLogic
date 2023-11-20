package dev.jasonlessenich.jlogic.objects.nodes.io;

import dev.jasonlessenich.jlogic.objects.nodes.Evaluable;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.NodeUtils;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.annotation.Nonnull;
import java.util.List;

public class OutputNode extends IONode implements Evaluable {
	private Circle circleModel;

	public OutputNode(@Nonnull Point point) {
		super(point, 1, 0);
	}

	@Override
	protected void setFill(Color color) {
		if (circleModel != null) {
			circleModel.setFill(color);
		}
	}

	@Override
	public Region buildModel() {
		final double width = Constants.NODE_SIZE;
		final Circle circle = new Circle(width / 2, Color.RED);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(2);
		circle.getStrokeDashArray().addAll(5d, 5d);
		this.circleModel = circle;
		return NodeUtils.setSize(new StackPane(circle), width);
	}

	@Override
	public boolean evaluate(@Nonnull List<Boolean> inputs) {
		for (boolean input : inputs) {
			if (input) return true;
		}
		return false;
	}
}
