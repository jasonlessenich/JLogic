package dev.jasonlessenich.jlogic.nodes.io;

import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;

import javax.annotation.Nonnull;

public class InputNode extends IONode {
	public InputNode(@Nonnull Point point) {
		super(point, circle -> {
			circle.setStroke(Color.BLACK);
			circle.setStrokeWidth(2);
		}, 0, 1);
	}
}
