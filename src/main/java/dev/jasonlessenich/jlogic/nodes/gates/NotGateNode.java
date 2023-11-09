package dev.jasonlessenich.jlogic.nodes.gates;

import dev.jasonlessenich.jlogic.utils.Point;

import javax.annotation.Nonnull;

public class NotGateNode extends GateNode {
	public NotGateNode(@Nonnull Point point) {
		super(point, GateNodeType.NOT, 1, 1);
		// TODO: Implement this again
		// final Circle circle = new Circle((double) Constants.NODE_SIZE / 8, Color.WHITE);
		// circle.setStroke(Color.BLACK);
		// circle.setStrokeWidth(2);
		// StackPane.setAlignment(circle, Pos.CENTER_RIGHT);
		// getChildren().add(circle);
	}
}
