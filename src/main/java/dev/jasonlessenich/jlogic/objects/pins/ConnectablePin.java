package dev.jasonlessenich.jlogic.objects.pins;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.nodes.gates.GateNode;
import dev.jasonlessenich.jlogic.objects.wires.PreviewWire;
import dev.jasonlessenich.jlogic.objects.wires.Wire;
import dev.jasonlessenich.jlogic.objects.wires.layout.WireLayoutStrategy;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.NodeUtils;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConnectablePin extends Parent {
	private static final Color DEFAULT_COLOR = Color.LIGHTGRAY;
	private static final Color HOVER_COLOR = Color.DEEPSKYBLUE;

	@Getter
	private final String name;
	@Getter
	private final Type type;
	@Getter
	private final ConnectableNode node;
	private final Circle model;
	@Getter
	@Setter
	private Point displacement;
	@Getter
	private boolean active;
	@Nullable
	private Wire wire;

	public ConnectablePin(
			@Nonnull String name,
			@Nonnull Type type,
			@Nonnull ConnectableNode node,
			@Nonnull Point displacement
	) {
		setId("ConnectablePin");
		this.name = name;
		this.type = type;
		this.node = node;
		this.displacement = displacement;
		model = buildModel();
		getChildren().add(model);
		setOnMouseEntered(e -> model.setFill(HOVER_COLOR));
		setOnMouseExited(e -> model.setFill(isActive() ? Color.LAWNGREEN : DEFAULT_COLOR));
		Point end = new Point();
		setOnMouseDragged(me -> {
			if (!me.isPrimaryButtonDown()) return;
			// clear old wire
			MainController.MAIN_PANE.getChildren().removeIf(n -> n instanceof PreviewWire);
			final Point start = getPinPosition();
			end.set(Point.of(me.getSceneX(), me.getSceneY()).stepped(Constants.GRID_STEP_SIZE));
			final PreviewWire wire = type == Type.OUTPUT
					? new PreviewWire(WireLayoutStrategy.STRAIGHT, start, end)
					: new PreviewWire(WireLayoutStrategy.STRAIGHT, end, start);
			wire.setCanConnect(NodeUtils.isPinAtPoint(end).isPresent());
			// add new wire at mouse pos
			MainController.MAIN_PANE.getChildren().add(wire);
		});
		setOnMouseReleased(me -> {
			MainController.MAIN_PANE.getChildren().removeIf(n -> n instanceof PreviewWire || n == this.wire);
			final Point start = getPinPosition();
			final ConnectablePin endPin = NodeUtils.isPinAtPoint(end)
					.orElse(null);
			final Wire wire = type == Type.OUTPUT
					? new Wire(start, end, this, endPin)
					: new Wire(end, start, endPin, this);
			if (endPin != null) {
				wire.connect(this, endPin);
			}
			this.wire = wire;
			MainController.MAIN_PANE.getChildren().add(wire);
		});
		setOnDragDetected(e -> startFullDrag());
	}

	/**
	 * Sets the {@link ConnectablePin}s state.
	 * This updates the pins color, the connected {@link Wire}s state all other connected {@link ConnectablePin}s.
	 *
	 * @param active Whether the pin should be active or not.
	 */
	public void setState(boolean active) {
		this.active = active;
		model.setFill(active ? Color.LAWNGREEN : DEFAULT_COLOR);
		if (getConnectedWire().isEmpty()) return;
		switch (type) {
			case INPUT -> CompletableFuture
					.delayedExecutor(getNode() instanceof GateNode gate ? gate.getSpecificGateDelay() : 0, TimeUnit.MILLISECONDS)
					.execute(node::evaluate);
			case OUTPUT -> getConnectedWire().get().setActivated(active);
		}
	}

	/**
	 * Gets the {@link ConnectablePin}s position in the {@link MainController#MAIN_PANE}.
	 * This simply gets the parent's position and adds the {@link ConnectablePin}s displacement.
	 * (Calculated by {@link dev.jasonlessenich.jlogic.objects.pins.layout.PinLayoutStrategy})
	 *
	 * @return The {@link ConnectablePin}s position, as a {@link Point}.
	 */
	public Point getPinPosition() {
		return node.getPosition()
				.addX(displacement.getX() + node.getModel().getMaxWidth() / 2)
				.addY(displacement.getY() + node.getModel().getMaxHeight() / 2);
	}

	public Optional<Wire> getConnectedWire() {
		return Optional.ofNullable(wire);
	}

	public void setConnectedWire(@Nullable Wire wire) {
		this.wire = wire;
	}

	public boolean canConnectTo(@Nonnull ConnectablePin pin) {
		return true;
	}

	private @Nonnull Circle buildModel() {
		final Circle circle = new Circle();
		circle.setId("ConnectablePinModel");
		circle.setRadius((double) Constants.PIN_SIZE / 2);
		circle.setFill(DEFAULT_COLOR);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(2);
		return circle;
	}

	public enum Type {
		INPUT,
		OUTPUT
	}
}
