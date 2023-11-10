package dev.jasonlessenich.jlogic.nodes.gates;

import dev.jasonlessenich.jlogic.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.nodes.Evaluable;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javax.annotation.Nonnull;

public abstract class GateNode extends ConnectableNode implements Evaluable {
	public GateNode(@Nonnull Point point, int inputCount, int outputCount, @Nonnull String symbol) {
		super(point, inputCount, outputCount);
		final Rectangle rect = new Rectangle();
		rect.setWidth(Constants.NODE_SIZE);
		rect.setHeight(Constants.NODE_SIZE);
		rect.setFill(Color.WHITE);
		rect.setStroke(Color.BLACK);
		rect.setStrokeWidth(2);
		final Text text = new Text(symbol);
		text.setStyle("-fx-font-weight: bold");
		getChildren().addAll(rect, text);
		// TODO: revisit this & fix placement of connection circles
		for (int i = 0; i < inputCount; i++) {
			getChildren().add(buildConnectionCircle(i, true, inputCount));
		}
		if (inputCount == -1) {
			getChildren().add(buildConnectionCircle(0, true, inputCount));
		}
		for (int i = 0; i < outputCount; i++) {
			getChildren().add(buildConnectionCircle(i, false, outputCount));
		}
		if (outputCount == -1) {
			getChildren().add(buildConnectionCircle(0, false, outputCount));
		}
	}

	private @Nonnull Circle buildConnectionCircle(int i, boolean input, int total) {
		final Circle circle = new Circle();
		circle.setRadius(Constants.NODE_CONNECTION_SIZE);
		circle.setFill(Color.WHITE);
		circle.setStroke(total == -1 ? Color.DEEPSKYBLUE : Color.BLACK);
		circle.setStrokeWidth(2);
		// layout
		circle.setTranslateY((i * Constants.NODE_CONNECTION_SIZE) * 3 - (total >= 2 ? total + 1 : 0));
		circle.setTranslateX((input ? -1 : 1) * (double) Constants.NODE_CONNECTION_SIZE);
		StackPane.setAlignment(circle, input ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
		return circle;
	}
}
