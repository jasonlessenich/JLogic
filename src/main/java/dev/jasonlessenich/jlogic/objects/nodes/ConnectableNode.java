package dev.jasonlessenich.jlogic.objects.nodes;

import dev.jasonlessenich.jlogic.objects.Connection;
import dev.jasonlessenich.jlogic.objects.pins.PinLayoutStrategy;
import dev.jasonlessenich.jlogic.utils.Drag;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public abstract class ConnectableNode extends StackPane {
	private final PinLayoutStrategy layout;
	private final List<Connection> connections;
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
				.build();
		this.model = buildModel();
		getChildren().add(model);
		layout.layoutPins(this);
	}

	public abstract Region buildModel();

	public Point getPosition() {
		return getDrag().getPosition();
	}

	public List<Connection> getSourceConnections() {
		return connections.stream().filter(c -> c.getConnectionType() == Connection.Type.BACKWARD).toList();
	}

	public List<Connection> getTargetConnections() {
		return connections.stream().filter(c -> c.getConnectionType() == Connection.Type.FORWARD).toList();
	}
}
