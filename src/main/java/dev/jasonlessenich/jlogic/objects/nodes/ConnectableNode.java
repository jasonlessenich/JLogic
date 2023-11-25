package dev.jasonlessenich.jlogic.objects.nodes;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.nodes.io.OutputNode;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import dev.jasonlessenich.jlogic.objects.pins.layout.PinLayoutStrategy;
import dev.jasonlessenich.jlogic.objects.pins.naming.PinNamingStrategy;
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
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
public abstract class ConnectableNode extends StackPane {
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

	@Nonnull
	public List<ConnectablePin> getInputPins() {
		return getPins().getOrDefault(ConnectablePin.Type.INPUT, List.of());
	}

	@Nonnull
	public List<ConnectablePin> getOutputPins() {
		return getPins().getOrDefault(ConnectablePin.Type.OUTPUT, List.of());
	}

	public void evaluate() {
		if (!(this instanceof Evaluable eval)) return;
		final List<Boolean> input = getInputPins().stream()
				.map(ConnectablePin::isActive)
				.toList();
		final boolean[] result = eval.evaluate(input);
		log.info("Evaluated {}: {} => {}", this, input, result);
		if (this instanceof OutputNode out) {
			out.setActive(result[0]);
		} else {
			final List<ConnectablePin> outputPins = getOutputPins();
			for (int i = 0; i < outputPins.size(); i++) {
				outputPins.get(i).setState(result[i]);
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
							.forEach(p -> {
								p.getConnectedWire().ifPresent(Wire::disconnect);
								MainController.PINS.remove(p);
							});
					MainController.MAIN_PANE.getChildren().remove(this);
				}
		);
		contextMenu.getItems().add(delete);
		return contextMenu;
	}
}
