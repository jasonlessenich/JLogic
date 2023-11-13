package dev.jasonlessenich.jlogic.objects.nodes.io;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import javax.annotation.Nonnull;

public class InputNode extends IONode {
	public InputNode(@Nonnull Point point) {
		super(point, circle -> {
			circle.setStroke(Color.BLACK);
			circle.setStrokeWidth(2);
		}, 0, 1);
		final EventHandler<? super MouseEvent> onMousePressed = getOnMousePressed();
		setOnMousePressed(me -> {
			onMousePressed.handle(me);
			if (MainController.simulationMode && me.isPrimaryButtonDown()) {
				toggleActivated();
				ConnectablePin.evaluateConnections();
			}
		});
	}
}
