package dev.jasonlessenich.jlogic.objects.nodes;

import dev.jasonlessenich.jlogic.objects.Connection;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.objects.pins.PinLayoutStrategy;
import dev.jasonlessenich.jlogic.utils.Drag;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
public abstract class ConnectableNode extends StackPane {
	private final PinLayoutStrategy layout;
	private final List<Connection> connections;
	private final Map<ConnectablePin.Type, List<ConnectablePin>> pins;
	private final int inputCount;
	private final int outputCount;
	private final Drag drag;
	private final Region model;

	public ConnectableNode(
			@Nonnull Point point,
			@Nonnull PinLayoutStrategy layout,
			int inputCount,
			int outputCount
	) {
		this.layout = layout;
		this.connections = new ArrayList<>();
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.drag = new Drag.Builder(this)
				.setInitialPosition(point)
				.setCanDrag(node -> !"ConnectablePinModel".equals(node.getId()))
				.setOnDrag(p -> {
					// TODO: fix bug where wire is not updated when node is dragged slowly
					for (ConnectablePin inputPin : getInputPins()) {
						inputPin.getConnectedWire().ifPresent(w -> w.setEnd(inputPin.getPinPosition()));
					}
					for (ConnectablePin outputPin : getOutputPins()) {
						outputPin.getConnectedWire().ifPresent(w -> w.setStart(outputPin.getPinPosition()));
					}
				})
				.build();
		this.model = buildModel();
		getChildren().add(model);
		this.pins = layout.layoutPins(this);
	}

	public abstract Region buildModel();

	public Point getPosition() {
		return Point.of(getLayoutX(), getLayoutY());
	}

	@Nonnull
	public List<ConnectablePin> getInputPins() {
		final List<ConnectablePin> pins = getPins().get(ConnectablePin.Type.INPUT);
		return pins == null ? List.of() : pins;
	}

	@Nonnull
	public List<ConnectablePin> getOutputPins() {
		final List<ConnectablePin> pins = getPins().get(ConnectablePin.Type.OUTPUT);
		return pins == null ? List.of() : pins;
	}

	public List<Connection> getSourceConnections() {
		return connections.stream().filter(c -> c.getConnectionType() == Connection.Type.BACKWARD).toList();
	}

	public List<Connection> getTargetConnections() {
		return connections.stream().filter(c -> c.getConnectionType() == Connection.Type.FORWARD).toList();
	}
}
