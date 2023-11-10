package dev.jasonlessenich.jlogic.nodes;

import dev.jasonlessenich.jlogic.data.JGate;
import dev.jasonlessenich.jlogic.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GateNode extends ConnectableNode implements Evaluable {
	private final JGate.Table table;

	public GateNode(@Nonnull Point point, @Nonnull JGate gate, @Nonnull JGate.Table table) {
		super(point, table.getInputCount(), table.getOutputCount());
		this.table = table;
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

	@Override
	public boolean evaluate(@Nonnull List<Boolean> inputs) {
		if (inputs.size() < table.getInputCount()) {
			inputs = new ArrayList<>(inputs);
			while (inputs.size() < table.getInputCount()) {
				inputs.add(true);
			}
		}
		final String binaryString = inputs.stream().map(b -> b ? "1" : "0").collect(Collectors.joining(", "));
		System.out.println("Evaluating " + binaryString + " with table " + table);
		final Boolean result = table.getMap().get(binaryString);
		if (result == null)
			throw new IllegalStateException("No result for [" + binaryString + "] in table map: " + table.getMap());
		return result;
	}
}
