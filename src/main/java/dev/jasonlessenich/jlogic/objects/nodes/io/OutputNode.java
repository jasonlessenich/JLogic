package dev.jasonlessenich.jlogic.objects.nodes.io;

import dev.jasonlessenich.jlogic.objects.nodes.Evaluable;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;

import javax.annotation.Nonnull;
import java.util.List;

public class OutputNode extends IONode implements Evaluable {
	public OutputNode(@Nonnull Point point) {
		super(point, circle -> {
			circle.setStroke(Color.BLACK);
			circle.setStrokeWidth(2);
			circle.getStrokeDashArray().addAll(5d, 5d);
		}, 1, 0);
	}

	@Override
	public boolean evaluate(@Nonnull List<Boolean> inputs) {
		for (boolean input : inputs) {
			if (input) return true;
		}
		return false;
	}
}
