package dev.jasonlessenich.jlogic.objects.nodes.io;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.NodeUtils;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;

public class InputNode extends IONode {
	public InputNode(@Nonnull Point point) {
		super(point, 0, 1);
		final EventHandler<? super MouseEvent> onMousePressed = getOnMousePressed();
		setOnMousePressed(me -> {
			onMousePressed.handle(me);
			if (MainController.simulationMode && me.isPrimaryButtonDown()) {
				toggleActivated();
				// TODO: evaluate circuit
			}
		});
	}

	@Override
	public Region buildModel() {
		final double width = Constants.NODE_SIZE;
		final Circle circle = new Circle(width / 2, Color.RED);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(2);
		return NodeUtils.setSize(new StackPane(circle), width);
	}
}
