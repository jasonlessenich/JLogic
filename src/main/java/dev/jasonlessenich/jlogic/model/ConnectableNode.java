package dev.jasonlessenich.jlogic.model;

import dev.jasonlessenich.jlogic.controller.MainViewController;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ConnectableNode extends DraggableNode {
	public static boolean connectMode = false;

	private final int inputCount;
	private final int outputCount;
	private final List<Connection> connections;

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
			((Pane) getParent()).getChildren().removeIf(n -> n instanceof Line);
			// re-draw arrows for all connections
			for (DraggableNode node : MainViewController.NODES.values()) {
				if (node instanceof ConnectableNode) {
					final ConnectableNode connectableNode = (ConnectableNode) node;
					for (Connection c : connectableNode.connections) {
						if (c.getConnectionType() == Connection.Type.INPUT) continue;
						drawArrowLine(
								c.getConnectionType() == Connection.Type.OUTPUT ? connectableNode : c.getConnectionTo(),
								c.getConnectionType() == Connection.Type.OUTPUT ? c.getConnectionTo() : connectableNode,
								(Pane) getParent()
						);
					}
				}
			}
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

	public boolean canConnectTo(@Nonnull ConnectableNode node) {
		return connections.stream().filter(c -> c.getConnectionType() == Connection.Type.OUTPUT).count() < outputCount &&
				((node.connections.stream().filter(c -> c.getConnectionType() == Connection.Type.INPUT).count() < node.inputCount) || node.inputCount == -1);
	}

	public void connectTo(@Nonnull ConnectableNode node) {
		final boolean canConnect = canConnectTo(node);
		if (canConnect) {
			System.out.println("Connected " + this + " to " + node);
			connections.add(new Connection(node, Connection.Type.OUTPUT));
			node.connections.add(new Connection(this, Connection.Type.INPUT));
			drawArrowLine(this, node, (Pane) getParent());
		}
	}

	public void drawArrowLine(@Nonnull DraggableNode from, @Nonnull DraggableNode to, @Nonnull Pane pane) {
		drawArrowLine(
				from.getPosition().getX() + Constants.NODE_SIZE + 2, // + 2 to avoid clipping with the circle's stroke
				from.getPosition().getY() + ((double) Constants.NODE_SIZE / 2),
				to.getPosition().getX(),
				to.getPosition().getY() + ((double) Constants.NODE_SIZE / 2),
				pane
		);
	}

	private void drawArrowLine(double startX, double startY, double endX, double endY, @Nonnull Pane pane) {
		double slope = (startY - endY) / (startX - endX);
		double lineAngle = Math.atan(slope);
		double arrowAngle = startX > endX ? Math.toRadians(45) : -Math.toRadians(225);

		Line line = new Line(startX, startY, endX, endY);
		line.setStrokeWidth(3);

		final double arrowLength = 10;

		// create the arrow legs
		final Line arrow1 = new Line();
		arrow1.setStrokeWidth(3);
		arrow1.setStartX(line.getEndX());
		arrow1.setStartY(line.getEndY());
		arrow1.setEndX(line.getEndX() + arrowLength * Math.cos(lineAngle - arrowAngle));
		arrow1.setEndY(line.getEndY() + arrowLength * Math.sin(lineAngle - arrowAngle));

		final Line arrow2 = new Line();
		arrow2.setStrokeWidth(3);
		arrow2.setStartX(line.getEndX());
		arrow2.setStartY(line.getEndY());
		arrow2.setEndX(line.getEndX() + arrowLength * Math.cos(lineAngle + arrowAngle));
		arrow2.setEndY(line.getEndY() + arrowLength * Math.sin(lineAngle + arrowAngle));

		pane.getChildren().addAll(line, arrow1, arrow2);
	}
}
