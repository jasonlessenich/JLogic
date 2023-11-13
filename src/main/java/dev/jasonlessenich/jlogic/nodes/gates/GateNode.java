package dev.jasonlessenich.jlogic.nodes.gates;

import dev.jasonlessenich.jlogic.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.nodes.Evaluable;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javax.annotation.Nonnull;

public abstract class GateNode extends ConnectableNode implements Evaluable {
	public GateNode(@Nonnull Point point, int inputCount, int outputCount, @Nonnull String symbol) {
		super(point, inputCount, outputCount);
		final Rectangle rect = new Rectangle(Constants.NODE_SIZE, Constants.NODE_SIZE, Color.WHITE);
		rect.setStroke(Color.BLACK);
		rect.setStrokeWidth(2);
		final Text text = new Text(symbol);
		text.setStyle("-fx-font-weight: bold");
		getChildren().addAll(rect, text);
		ConnectableNode.redrawPins(this);
	}
}
