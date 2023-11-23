package dev.jasonlessenich.jlogic.objects.nodes;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.Connection;
import dev.jasonlessenich.jlogic.objects.nodes.gates.CustomGateNode;
import dev.jasonlessenich.jlogic.objects.nodes.gates.GateNode;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
public abstract class ConnectableNode extends StackPane {
	private final PinLayoutStrategy layoutStrategy;
	private final PinNamingStrategy inputNamingStrategy;
	private final PinNamingStrategy outputNamingStrategy;

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
			@Nonnull PinNamingStrategy inputNamingStrategy,
			@Nonnull PinNamingStrategy outputNamingStrategy,
			int inputCount,
			int outputCount
	) {
		this.layoutStrategy = layoutStrategy;
		this.inputNamingStrategy = inputNamingStrategy;
		this.outputNamingStrategy = outputNamingStrategy;
		this.state = new NodeState(outputCount, this::handleStateChange);
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
		this.pins = layoutStrategy.layoutPins(this, inputNamingStrategy, outputNamingStrategy);
		pins.values().forEach(MainController.PINS::addAll);
	}

	public ConnectableNode(
			@Nonnull Point point,
			@Nonnull PinLayoutStrategy layoutStrategy,
			@Nonnull PinNamingStrategy namingStrategy,
			int inputCount,
			int outputCount
	) {
		this(point, layoutStrategy, namingStrategy, namingStrategy, inputCount, outputCount);
	}


	public abstract Region buildModel();

	public Point getPosition() {
		return Point.of(getLayoutX(), getLayoutY());
	}

	public void setState(boolean... active) {
		getState().setActive(active);
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

	private void handleStateChange(boolean[] state) {
		final List<ConnectablePin> outputPins = getOutputPins();
		for (int i = 0; i < outputPins.size(); i++) {
			final ConnectablePin pin = outputPins.get(i);
			// update current pin state
			pin.setState(state[i]);
		}
		for (Connection con : getTargetConnections()) {
			final ConnectableNode node = con.getConnectionTo().getNode();
			if (node instanceof Evaluable eval) {
				// simply get state of all input pins
				final List<Boolean> booleans = node.getInputPins()
						.stream()
						.map(ConnectablePin::isActive)
						.toList();
				final boolean[] result = eval.evaluate(booleans);
				log.debug("Notified {} of state change with input: {} ({} -> {})", node, booleans, node.getState().getActive(), result);
				// add random delay to prevent race conditions
				final int delay = node instanceof GateNode gate &&
						!(node instanceof CustomGateNode c && c.getSymbol().equals("*")) ? gate.getSpecificGateDelay() : 0;
				CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS)
						.execute(() -> node.setState(result));
			}
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
