package dev.jasonlessenich.jlogic.nodes.pins;

import dev.jasonlessenich.jlogic.controller.MainController;
import dev.jasonlessenich.jlogic.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.nodes.DraggableNode;
import dev.jasonlessenich.jlogic.nodes.Evaluable;
import dev.jasonlessenich.jlogic.nodes.io.InputNode;
import dev.jasonlessenich.jlogic.nodes.io.OutputNode;
import dev.jasonlessenich.jlogic.utils.Connection;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.nodes.pins.wires.Wire;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
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
	private final ConnectableNode parentNode;
	@Getter
	private final boolean input;

	private final Circle model;

	public ConnectablePin(ConnectableNode parentNode, boolean input) {
		this.parentNode = parentNode;
		this.input = input;
		model = buildModel();
		getChildren().add(model);
		setOnMouseEntered(e -> model.setFill(HOVER_COLOR));
		setOnMouseExited(e -> model.setFill(DEFAULT_COLOR));

		final Point startDrag = new Point();
		final Point endDrag = new Point();
//		setOnMousePressed(me -> {
//			// get center of pin
//			startDrag.setX(me.getSceneX());
//			startDrag.setY(PointUtils.step(me.getSceneY(), Constants.GRID_STEP_SIZE));
//		});
//		setOnMouseDragged(me -> {
//			// remove all preview wires
//			MainViewController.MAIN_PANE.getChildren().removeIf(n -> n instanceof Wire && "WirePreview".equals(n.getId()));
//			endDrag.setX(me.getSceneX());
//			endDrag.setY(me.getSceneY());
//			// get node at mouse position
//			MainViewController.MAIN_PANE.getChildren().add(
//					new Wire(startDrag, endDrag.stepped(Constants.GRID_STEP_SIZE), true)
//			);
//		});
//		setOnMouseDragReleased(me -> {
//			// remove all preview wires
//			MainViewController.MAIN_PANE.getChildren().removeIf(n -> n instanceof Wire && "WirePreview".equals(n.getId()));
//			// get node at mouse position
//			MainViewController.MAIN_PANE.getChildren().add(
//					new Wire(startDrag, endDrag.stepped(Constants.GRID_STEP_SIZE), false)
//			);
//		});
	}

	public static void redrawConnections(@Nonnull Pane pane) {
		pane.getChildren().removeIf(n -> n instanceof Wire);
	}

	public static void evaluateConnections() {
		// 			Target			   Source Nodes
		final Map<ConnectableNode, List<ConnectableNode>> nodeTree = new HashMap<>();
		for (DraggableNode node : MainController.NODES.values()) {
			if (!(node instanceof ConnectableNode con)) continue;
			final List<ConnectableNode> sources = con.getConnections().stream()
					.filter(c -> c.getConnectionType() == Connection.Type.BACKWARD)
					.map(Connection::getConnectionTo)
					.map(ConnectablePin::getParentNode)
					.toList();
			nodeTree.put(con, sources);
		}
		// pretty print node tree
		for (Map.Entry<ConnectableNode, List<ConnectableNode>> entry : nodeTree.entrySet()) {
			if (!(entry.getKey() instanceof OutputNode out)) continue;
			final boolean result = out.evaluate(evaluateSources(out));
			out.setActivated(result);
		}
	}

	private static @Nonnull List<Boolean> evaluateSources(@Nonnull ConnectableNode node) {
		List<Boolean> input = new ArrayList<>();
		for (Connection source : node.getSourceConnections()) {
			final ConnectableNode sourceNode = source.getConnectionTo().getParentNode();
			if (sourceNode instanceof InputNode in) {
				input.add(in.isActivated());
			} else if (sourceNode instanceof Evaluable eval) {
				input.add(eval.evaluate(evaluateSources(sourceNode)));
			} else {
				throw new IllegalStateException("Source node " + sourceNode + " is not an InputNode or Evaluable");
			}
		}
		return input;
	}

	public boolean canConnectTo(@Nonnull ConnectablePin pin) {
		return parentNode.getConnections().stream().noneMatch(c -> Objects.equals(c, new Connection(this, pin, Connection.Type.FORWARD))) &&
				(parentNode.getTargetConnections().size() < parentNode.getOutputCount()) &&
				(parentNode.getSourceConnections().size() < pin.getParentNode().getInputCount());
	}

	public void connectTo(@Nonnull ConnectablePin pin) {
		final boolean canConnect = canConnectTo(pin);
		if (canConnect) {
			log.info("Connected " + this + " to " + pin);
			parentNode.getConnections().add(new Connection(this, pin, Connection.Type.FORWARD));
			pin.getParentNode().getConnections().add(new Connection(pin, this, Connection.Type.BACKWARD));
			// drawArrowLine(this, pin, (Pane) getParent());
			evaluateConnections();
		}
	}

	private @Nonnull Circle buildModel() {
		final Circle circle = new Circle();
		circle.setId("ConnectionPin");
		circle.setRadius(Constants.NODE_CONNECTION_SIZE);
		circle.setFill(DEFAULT_COLOR);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(2);
		return circle;
	}
}
