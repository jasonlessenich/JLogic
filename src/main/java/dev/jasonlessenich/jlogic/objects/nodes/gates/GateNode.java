package dev.jasonlessenich.jlogic.objects.nodes.gates;

import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.nodes.Evaluable;
import dev.jasonlessenich.jlogic.objects.pins.layout_strategies.PinLayoutStrategy;
import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.PinNamingStrategy;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.NodeUtils;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public abstract class GateNode extends ConnectableNode implements Evaluable {
	private final String symbol;
	private final int specificGateDelay;

	public GateNode(
			@Nonnull Point point,
			@Nonnull PinNamingStrategy inputNamingStrategy,
			@Nonnull PinNamingStrategy outputNamingStrategy,
			int inputCount,
			int outputCount,
			@Nonnull String symbol
	) {
		super(point, PinLayoutStrategy.GATE, inputNamingStrategy, outputNamingStrategy, inputCount, outputCount);
		this.symbol = symbol;
		this.specificGateDelay = ThreadLocalRandom.current().nextInt(100);
		((StackPane) getModel()).getChildren().add(buildSymbolText());
	}

	public GateNode(
			@Nonnull Point point,
			@Nonnull PinNamingStrategy namingStrategy,
			int inputCount,
			int outputCount,
			@Nonnull String symbol
	) {
		this(point, namingStrategy, namingStrategy, inputCount, outputCount, symbol);
	}

	@Override
	public Region buildModel() {
		final StackPane stackPane = new StackPane();
		final double height = Constants.PIN_SIZE * (2 * Math.max(getInputCount(), getOutputCount()));
		final double width = Math.min(Constants.NODE_SIZE * 2, height);
		final Rectangle rect = new Rectangle(width, height, Color.WHITE);
		rect.setStroke(Color.BLACK);
		rect.setStrokeWidth(2);
		stackPane.getChildren().add(rect);
		return NodeUtils.setSize(stackPane, width, height);
	}

	@Nonnull
	private Text buildSymbolText() {
		final Text text = new Text(getSymbol());
		if (Math.max(getInputCount(), getOutputCount()) == 1) {
			text.setStyle("-fx-font-weight: bold; -fx-font-size: 8");
		} else {
			text.setStyle("-fx-font-weight: bold");
		}
		return text;
	}
}
