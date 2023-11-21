package dev.jasonlessenich.jlogic.objects.nodes;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.Connection;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.objects.pins.layout_strategies.PinLayoutStrategy;
import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.PinNamingStrategy;
import dev.jasonlessenich.jlogic.objects.wires.Wire;
import dev.jasonlessenich.jlogic.utils.Drag;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Slf4j
public abstract class ConnectableNode extends StackPane {
	private final PinLayoutStrategy layoutStrategy;
	private final PinNamingStrategy namingStrategy;

	private final NodeState state;

	private final List<Connection> connections;
	private final Map<ConnectablePin.Type, List<ConnectablePin>> pins;
	private final int inputCount;
	private final int outputCount;
	private final Drag drag;
	private final Region model;

	public ConnectableNode(
			@Nonnull Point point,
			@Nonnull PinLayoutStrategy layoutStrategy,
			@Nonnull PinNamingStrategy namingStrategy,
			int inputCount,
			int outputCount
	) {
		this.layoutStrategy = layoutStrategy;
		this.namingStrategy = namingStrategy;
		this.state = new NodeState(this::handleStateChange);
		this.connections = new ArrayList<>();
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.drag = new Drag.Builder(this)
				.setInitialPosition(point)
				.setCanDrag(node -> !"ConnectablePinModel".equals(node.getId()))
				.setContextMenu(buildDefaultContextMenu())
				.setOnDrag(p -> {
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
		this.pins = layoutStrategy.layoutPins(this, namingStrategy);
		pins.values().forEach(MainController.PINS::addAll);
	}

	public abstract Region buildModel();

	public Point getPosition() {
		return Point.of(getLayoutX(), getLayoutY());
	}

	public boolean isActivated() {
		return getState().isActive();
	}

	public void setActivated(boolean activated) {
		getState().setActive(activated);
	}

	@Nonnull
	public List<ConnectablePin> getInputPins() {
		return getPins().getOrDefault(ConnectablePin.Type.INPUT, List.of());
	}

	@Nonnull
	public List<ConnectablePin> getOutputPins() {
		return getPins().getOrDefault(ConnectablePin.Type.OUTPUT, List.of());
	}

	public List<Connection> getSourceConnections() {
		return connections.stream().filter(c -> c.getConnectionType() == Connection.Type.BACKWARD).toList();
	}

	public List<Connection> getTargetConnections() {
		return connections.stream().filter(c -> c.getConnectionType() == Connection.Type.FORWARD).toList();
	}

	private void handleStateChange(boolean state) {
		for (Connection con : getTargetConnections()) {
			final ConnectableNode node = con.getConnectionTo().getNode();
			if (node instanceof Evaluable eval) {
				final List<Boolean> booleans = node.getSourceConnections()
						.stream()
						.map(c -> c.getConnectionTo().getNode().isActivated())
						.toList();
				final boolean result = eval.evaluate(booleans);
				log.info("Notified {} of state change with input: {} ({} -> {})", node, booleans, node.isActivated(), result);
				node.setActivated(result);
			}
		}
		for (ConnectablePin pin : getOutputPins()) {
			pin.getConnectedWire().ifPresent(w -> w.setActivated(state));
		}
	}

	@Nonnull
	private ContextMenu buildDefaultContextMenu() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem delete = new MenuItem("Delete Node");
		delete.setOnAction(e -> {
					getPins().values().stream()
							.flatMap(List::stream)
							.forEach(p -> p.getConnectedWire().ifPresent(Wire::disconnect));
					MainController.MAIN_PANE.getChildren().remove(this);
				}
		);
		contextMenu.getItems().add(delete);
		return contextMenu;
	}
}
