package dev.jasonlessenich.jlogic.objects.pins;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.Connection;
import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
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
import java.util.Objects;
import java.util.Optional;

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
			if (endPin != null)
				wire.connect(this, endPin);
			this.wire = wire;
			MainController.MAIN_PANE.getChildren().add(wire);
		});
		setOnDragDetected(e -> startFullDrag());
	}

	public void setState(boolean active) {
		this.active = active;
		model.setFill(active ? Color.LAWNGREEN : DEFAULT_COLOR);
		if (getConnectedWire().isPresent()) {
			final Wire w = getConnectedWire().get();
			// update wire state
			w.setActivated(active);
			// update wire pins
			if (type == Type.INPUT && w.getStartPin() != null
					&& w.getStartPin().isActive() != active)
				w.getStartPin().setState(active);
			if (type == Type.OUTPUT && w.getEndPin() != null
					&& w.getEndPin().isActive() != active)
				w.getEndPin().setState(active);
		}
	}

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
		return node.getConnections().stream().noneMatch(c -> Objects.equals(c, new Connection(this, pin, Connection.Type.FORWARD))) &&
				(node.getTargetConnections().size() < node.getOutputCount()) &&
				(pin.getNode().getSourceConnections().size() < pin.getNode().getInputCount());
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
