package dev.jasonlessenich.jlogic.objects.nodes.io;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.NodeUtils;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.annotation.Nonnull;

public class InputNode extends IONode {
	private Circle circleModel;

	public InputNode(@Nonnull Point point) {
		super(point, 0, 1);
		final EventHandler<? super MouseEvent> onMousePressed = getOnMousePressed();
		setOnMousePressed(me -> {
			onMousePressed.handle(me);
			if (MainController.SIMULATION_MODE && me.isPrimaryButtonDown())
				toggleActivated();
		});
	}

	@Override
	protected void setFill(Color color) {
		if (circleModel != null) {
			circleModel.setFill(color);
		}
	}

	@Override
	public void toggleActivated() {
		super.toggleActivated();
		getOutputPins().forEach(p -> p.setState(isActive()));
	}

	@Override
	public Region buildModel() {
		final double width = Constants.NODE_SIZE;
		final Circle circle = new Circle(width / 2, Color.RED);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(2);
		this.circleModel = circle;
		return NodeUtils.setSize(new StackPane(circle), width);
	}
}
