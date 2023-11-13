package dev.jasonlessenich.jlogic.objects.nodes;

import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.utils.Connection;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Drag;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public abstract class ConnectableNode extends Parent {
	private final List<Connection> connections;
	private final int inputCount;
	private final int outputCount;
	private final List<ConnectablePin> pins;

	private final Drag drag;

	public ConnectableNode(@Nonnull Point point, int inputCount, int outputCount) {
		this.connections = new ArrayList<>();
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.pins = new ArrayList<>();

		this.drag = new Drag.Builder(this)
				.setInitialPosition(point)
				.build();
	}

	public List<Connection> getSourceConnections() {
		return connections.stream().filter(c -> c.getConnectionType() == Connection.Type.BACKWARD).toList();
	}

	public List<Connection> getTargetConnections() {
		return connections.stream().filter(c -> c.getConnectionType() == Connection.Type.FORWARD).toList();
	}

	public List<ConnectablePin> getInputPins() {
		return pins.stream().filter(ConnectablePin::isInput).toList();
	}

	public List<ConnectablePin> getOutputPins() {
		return pins.stream().filter(c -> !c.isInput()).toList();
	}

	public static void redrawPins(@Nonnull ConnectableNode node) {
		redrawPins(node, true, true);
	}

	public static void redrawPins(@Nonnull ConnectableNode node, boolean drawInput, boolean drawOutput) {
		node.pins.clear();
		node.getChildren().removeIf(c -> c instanceof VBox v && "PinBox".equals(v.getId()));
		if (drawInput)
			node.getChildren().add(node.buildPinBox(node.getInputCount(), true));
		if (drawOutput)
			node.getChildren().add(node.buildPinBox(node.getOutputCount(), false));
	}


	private @Nonnull VBox buildPinBox(int count, boolean isInput) {
		final VBox vBox = new VBox();
		vBox.setId("PinBox");
		vBox.setMaxWidth(Constants.NODE_CONNECTION_SIZE);
		vBox.setAlignment(Pos.CENTER);
		vBox.setSpacing(Constants.NODE_CONNECTION_SIZE);
		for (int i = 0; i < count; i++) {
			final ConnectablePin pin = new ConnectablePin(this, isInput);
			pins.add(pin);
			vBox.getChildren().add(pin);
		}
		StackPane.setAlignment(vBox, isInput ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
		vBox.setTranslateX((isInput ? -1 : 1) * (double) Constants.NODE_CONNECTION_SIZE);
		return vBox;
	}
}
