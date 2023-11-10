package dev.jasonlessenich.jlogic.nodes;

import dev.jasonlessenich.jlogic.data.JGate;
import dev.jasonlessenich.jlogic.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javax.annotation.Nonnull;

public class GateNode extends ConnectableNode {
	public GateNode(@Nonnull Point point, @Nonnull JGate gate, @Nonnull JGate.Table table) {
		super(point, table.getInputCount(), table.getOutputCount());
		final Rectangle rect = new Rectangle();
		rect.setWidth(Constants.NODE_SIZE);
		rect.setHeight(Constants.NODE_SIZE);
		rect.setFill(Color.WHITE);
		rect.setStroke(Color.BLACK);
		rect.setStrokeWidth(2);
		final Text text = new Text(gate.getSymbol());
		text.setStyle("-fx-font-weight: bold");
		getChildren().addAll(rect, text);
	}
}
