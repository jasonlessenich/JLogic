package dev.jasonlessenich.jlogic.objects.pins;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.nodes.Evaluable;
import dev.jasonlessenich.jlogic.objects.nodes.io.InputNode;
import dev.jasonlessenich.jlogic.objects.nodes.io.OutputNode;
import dev.jasonlessenich.jlogic.objects.Connection;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.objects.wires.Wire;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class ConnectablePin extends Parent {
	private static final Color DEFAULT_COLOR = Color.LIGHTGRAY;
	private static final Color HOVER_COLOR = Color.DEEPSKYBLUE;

	@Getter
	private final ConnectableNode node;
	@Getter
	@Setter
	private Point displacement;

	private final Circle model;

	public ConnectablePin(
			@Nonnull ConnectableNode node,
			@Nonnull Point displacement
	) {
		setId("ConnectablePin");
		this.node = node;
		this.displacement = displacement;
		model = buildModel();
		getChildren().add(model);
		setOnMouseEntered(e -> model.setFill(HOVER_COLOR));
		setOnMouseExited(e -> model.setFill(DEFAULT_COLOR));
		setOnMouseDragged(me -> {
			MainController.MAIN_PANE.getChildren().removeIf(n -> n instanceof Wire);
			final Point from = getPosition();
			final Point to = Point.of(me.getSceneX(), me.getSceneY()).stepped(Constants.GRID_STEP_SIZE);
			final Wire wire = new Wire(from, to, false, true);
			MainController.MAIN_PANE.getChildren().add(wire);
		});
	}

	public Point getPosition() {
		return node.getPosition()
				.addX(displacement.getX() + getNode().getModel().getMaxWidth() / 2)
				.addY(displacement.getY() + getNode().getModel().getMaxHeight() / 2);
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
