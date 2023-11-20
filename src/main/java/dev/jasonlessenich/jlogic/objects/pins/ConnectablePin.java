package dev.jasonlessenich.jlogic.objects.pins;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.Connection;
import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.wires.Wire;
import dev.jasonlessenich.jlogic.utils.Constants;
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
	private final ConnectableNode node;
	@Getter
	@Setter
	private Point displacement;

	@Nullable
	private Wire wire;

	private final Circle model;

	public ConnectablePin(
			@Nonnull String name,
			@Nonnull ConnectableNode node,
			@Nonnull Point displacement
	) {
		setId("ConnectablePin");
		this.name = name;
		this.node = node;
		this.displacement = displacement;
		model = buildModel();
		getChildren().add(model);
		setOnMouseEntered(e -> model.setFill(HOVER_COLOR));
		setOnMouseExited(e -> model.setFill(DEFAULT_COLOR));
		setOnMouseDragged(me -> {
			// clear old wire
			MainController.MAIN_PANE.getChildren().removeIf(n -> n instanceof Wire && n == this.wire);
			final Point from = getPinPosition();
			final Point to = Point.of(me.getSceneX(), me.getSceneY()).stepped(Constants.GRID_STEP_SIZE);
			final Wire wire = new Wire(from, to, this, null);
			// final Optional<ConnectablePin> pinOptional = wire.checkConnection(node, to);
			// pinOptional.ifPresent(wire::connect);
			// add new wire at mouse pos
			this.wire = wire;
			MainController.MAIN_PANE.getChildren().add(wire);
		});
	}

	public Point getPinPosition() {
		return node.getPosition()
				.addX(displacement.getX() + node.getModel().getMaxWidth() / 2)
				.addY(displacement.getY() + node.getModel().getMaxHeight() / 2);
	}

	public void setConnectedWire(@Nullable Wire wire) {
		this.wire = wire;
	}

	public Optional<Wire> getConnectedWire() {
		return Optional.ofNullable(wire);
	}

	public boolean canConnectTo(@Nonnull ConnectablePin pin) {
		return node.getConnections().stream().noneMatch(c -> Objects.equals(c, new Connection(this, pin, Connection.Type.FORWARD))) &&
				(node.getTargetConnections().size() < node.getOutputCount()) &&
				(node.getSourceConnections().size() < pin.getNode().getInputCount());
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