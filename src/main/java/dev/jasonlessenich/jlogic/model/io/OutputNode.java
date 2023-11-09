package dev.jasonlessenich.jlogic.model.io;

import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;

import javax.annotation.Nonnull;

public class OutputNode extends IONode {
	public OutputNode(@Nonnull Point point) {
		super(point, circle -> {
			circle.setStroke(Color.BLACK);
			circle.setStrokeWidth(2);
			circle.getStrokeDashArray().addAll(5d, 5d);
		}, 1, 0);
	}
}
