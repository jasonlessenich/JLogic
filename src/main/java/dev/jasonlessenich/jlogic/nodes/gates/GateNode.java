package dev.jasonlessenich.jlogic.nodes.gates;

import dev.jasonlessenich.jlogic.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javax.annotation.Nonnull;

public abstract class GateNode extends ConnectableNode {
	protected GateNode(@Nonnull Point point, @Nonnull GateNodeType type, int inputCount, int outputCount) {
		super(point, inputCount, outputCount);
		final Rectangle rect = new Rectangle();
		rect.setWidth(Constants.NODE_SIZE);
		rect.setHeight(Constants.NODE_SIZE);
		rect.setFill(Color.WHITE);
		rect.setStroke(Color.BLACK);
		rect.setStrokeWidth(2);
		final Text text = new Text(type.getSymbol());
		text.setStyle("-fx-font-weight: bold");
		getChildren().addAll(rect, text);
	}
}
