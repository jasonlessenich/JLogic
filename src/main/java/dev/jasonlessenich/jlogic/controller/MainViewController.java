package dev.jasonlessenich.jlogic.controller;

import com.sun.tools.javac.Main;
import dev.jasonlessenich.jlogic.JLogicApplication;
import dev.jasonlessenich.jlogic.data.JGate;
import dev.jasonlessenich.jlogic.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.nodes.DraggableNode;
import dev.jasonlessenich.jlogic.nodes.GateNode;
import dev.jasonlessenich.jlogic.nodes.io.InputNode;
import dev.jasonlessenich.jlogic.nodes.io.OutputNode;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MainViewController {
	public static boolean simulationMode = false;

	@FXML
	private AnchorPane mainStackPane;

	private Point lastContextMenuPoint = new Point();

	public static final Map<Point, DraggableNode> NODES = new HashMap<>();

	@FXML
	private void initialize() {
		final ContextMenu contextMenu = buildPaneContextMenu();
		mainStackPane.setOnMouseClicked(mouseEvent -> {
			contextMenu.hide();
			if (mouseEvent.getTarget().getClass() != AnchorPane.class) return;
			if (mouseEvent.getButton() == MouseButton.SECONDARY) {
				lastContextMenuPoint = Point.of(mouseEvent.getX(), mouseEvent.getY()).stepped(Constants.NODE_SIZE);
				contextMenu.show(mainStackPane, mouseEvent.getScreenX(), mouseEvent.getScreenY());
			}
		});
	}

	private @Nonnull ContextMenu buildPaneContextMenu() {
		final CheckMenuItem connectMode = new CheckMenuItem("Connect Mode");
		connectMode.setSelected(ConnectableNode.connectMode);
		connectMode.setOnAction(actionEvent -> {
			MainViewController.simulationMode = false;
			ConnectableNode.connectMode = connectMode.isSelected();
		});
		final CheckMenuItem alignMode = new CheckMenuItem("Align Mode");
		alignMode.setSelected(DraggableNode.alignToGrid);
		alignMode.setOnAction(actionEvent -> DraggableNode.alignToGrid = alignMode.isSelected());
		final CheckMenuItem simulationMode = new CheckMenuItem("Simulation Mode");
		simulationMode.setSelected(MainViewController.simulationMode);
		simulationMode.setOnAction(actionEvent -> {
			ConnectableNode.connectMode = false;
			MainViewController.simulationMode = simulationMode.isSelected();
		});
		final MenuItem addInput = new MenuItem("Add Input");
		addInput.setOnAction(actionEvent -> drawDraggableNode(lastContextMenuPoint, InputNode::new));
		final MenuItem addOutput = new MenuItem("Add Output");
		addOutput.setOnAction(actionEvent -> drawDraggableNode(lastContextMenuPoint, OutputNode::new));
		final Menu addGate = new Menu("Add Gate...");
		// register gates
		for (JGate gate : JLogicApplication.getGateManager().getGates()) {
			final Menu gateMenu = new Menu("%s (%s)".formatted(gate.getName(), gate.getSymbol()));
			for (JGate.Table table : gate.getTableDefinition()) {
				final MenuItem subMenu = new MenuItem("%s IN / %s OUT".formatted(table.getInputCount(), table.getOutputCount()));
				subMenu.setOnAction(actionEvent -> drawDraggableNode(lastContextMenuPoint, point -> new GateNode(point, gate, table)));
				gateMenu.getItems().add(subMenu);
			}
			addGate.getItems().add(gateMenu);
		}
		final ContextMenu contextMenu = new ContextMenu();
		contextMenu.getItems().addAll(connectMode, alignMode, simulationMode, new SeparatorMenuItem(), addInput, addOutput, new SeparatorMenuItem(), addGate);
		return contextMenu;
	}

	private void drawDraggableNode(@Nonnull Point point, @Nonnull Function<Point, DraggableNode> nodeFunction) {
		final DraggableNode node = nodeFunction.apply(point);
		NODES.put(point, node);
		mainStackPane.getChildren().add(node);
	}
}
