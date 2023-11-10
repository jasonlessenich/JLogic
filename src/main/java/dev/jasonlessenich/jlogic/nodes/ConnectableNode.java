package dev.jasonlessenich.jlogic.nodes;

import dev.jasonlessenich.jlogic.controller.MainViewController;
import dev.jasonlessenich.jlogic.nodes.io.InputNode;
import dev.jasonlessenich.jlogic.nodes.io.OutputNode;
import dev.jasonlessenich.jlogic.utils.Connection;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.NodeArrow;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class ConnectableNode extends DraggableNode {
	public static boolean connectMode = false;

	private final int inputCount;
	private final int outputCount;
	protected final List<Connection> connections;

	public ConnectableNode(@Nonnull Point point, int inputCount, int outputCount) {
		super(point);
		this.connections = new ArrayList<>();
		this.inputCount = inputCount;
		this.outputCount = outputCount;

		final Point dragDelta = new Point();
		final EventHandler<? super MouseEvent> onMousePressed = getOnMousePressed();
		setOnMousePressed(me -> {
			onMousePressed.handle(me);
			if (!ConnectableNode.connectMode) return;
			dragDelta.setX(me.getX());
			dragDelta.setY(me.getY());
		});
		final EventHandler<? super MouseEvent> onMouseDragged = getOnMouseDragged();
		setOnMouseDragged(me -> {
			onMouseDragged.handle(me);
			// re-draw arrows for all connections
			ConnectableNode.redrawConnections((Pane) getParent());
			if (!ConnectableNode.connectMode) return;
			final double layoutX = getLayoutX() + me.getX() - dragDelta.getX();
			final double layoutY = getLayoutY() + me.getY() - dragDelta.getY();
			// get node at mouse position
			final Map<Point, DraggableNode> nodes = MainViewController.NODES;
			final Optional<ConnectableNode> nodeAtMouse = nodes.values().stream()
					.filter(n -> n != this && n instanceof ConnectableNode)
					.map(n -> (ConnectableNode) n)
					.filter(n -> {
						final double posX = n.getPosition().getX();
						final double posY = n.getPosition().getY();
						return posX <= layoutX && layoutX <= posX + Constants.NODE_SIZE &&
								posY <= layoutY && layoutY <= posY + Constants.NODE_SIZE;
					}) // check bounds
					.filter(this::canConnectTo)
					.findFirst();
			nodeAtMouse.ifPresent(this::connectTo);
		});
	}

	public List<Connection> getSources() {
		return connections.stream().filter(c -> c.getConnectionType() == Connection.Type.BACKWARD).toList();
	}

	public List<Connection> getTargets() {
		return connections.stream().filter(c -> c.getConnectionType() == Connection.Type.FORWARD).toList();
	}

	public static void redrawConnections(@Nonnull Pane pane) {
		pane.getChildren().removeIf(n -> n instanceof NodeArrow);
		for (DraggableNode node : MainViewController.NODES.values()) {
			if (node instanceof ConnectableNode connectableNode) {
				for (Connection c : connectableNode.connections) {
					if (c.getConnectionType() == Connection.Type.BACKWARD) continue;
					drawArrowLine(
							c.getConnectionType() == Connection.Type.FORWARD ? connectableNode : c.getConnectionTo(),
							c.getConnectionType() == Connection.Type.FORWARD ? c.getConnectionTo() : connectableNode,
							pane
					);
				}
			}
		}
	}

	public boolean canConnectTo(@Nonnull ConnectableNode node) {
		return connections.stream().filter(c -> c.getConnectionType() == Connection.Type.FORWARD).count() < outputCount &&
				((node.connections.stream().filter(c -> c.getConnectionType() == Connection.Type.BACKWARD).count() < node.inputCount) || node.inputCount == -1);
	}

	public void connectTo(@Nonnull ConnectableNode node) {
		final boolean canConnect = canConnectTo(node);
		if (canConnect) {
			log.info("Connected " + this + " to " + node);
			connections.add(new Connection(this, node, Connection.Type.FORWARD));
			node.connections.add(new Connection(node, this, Connection.Type.BACKWARD));
			drawArrowLine(this, node, (Pane) getParent());
			evaluateConnections();
		}
	}

	public static void evaluateConnections() {
		// 			Target			   Source Nodes
		final Map<ConnectableNode, List<ConnectableNode>> nodeTree = new HashMap<>();
		for (DraggableNode node : MainViewController.NODES.values()) {
			if (!(node instanceof ConnectableNode con)) continue;
			final List<ConnectableNode> sources = con.connections.stream()
					.filter(c -> c.getConnectionType() == Connection.Type.BACKWARD)
					.map(Connection::getConnectionTo)
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
		for (Connection source : node.getSources()) {
			final ConnectableNode sourceNode = source.getConnectionTo();
			if (sourceNode instanceof InputNode in) {
				input.add(in.isActivated());
			} else if (sourceNode instanceof Evaluable eval){
				input.add(eval.evaluate(evaluateSources(sourceNode)));
			} else {
				throw new IllegalStateException("Source node " + sourceNode + " is not an InputNode or Evaluable");
			}
		}
		return input;
	}

	public static void drawArrowLine(@Nonnull DraggableNode from, @Nonnull DraggableNode to, @Nonnull Pane pane) {
		drawArrowLine(
				from.getPosition().getX() + Constants.NODE_SIZE + 2, // + 2 to avoid clipping with the circle's stroke
				from.getPosition().getY() + ((double) Constants.NODE_SIZE / 2),
				to.getPosition().getX() - 2, // - 2 to avoid clipping with the circle's stroke
				to.getPosition().getY() + ((double) Constants.NODE_SIZE / 2),
				pane
		);
	}

	private static void drawArrowLine(double startX, double startY, double endX, double endY, @Nonnull Pane pane) {
		pane.getChildren().add(new NodeArrow(startX, startY, endX, endY));
	}
}
