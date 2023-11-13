package dev.jasonlessenich.jlogic.controller;

import dev.jasonlessenich.jlogic.JLogicApplication;
import dev.jasonlessenich.jlogic.nodes.DraggableNode;
import dev.jasonlessenich.jlogic.nodes.gates.CustomGateNode;
import dev.jasonlessenich.jlogic.nodes.gates.concrete.AndGateNode;
import dev.jasonlessenich.jlogic.nodes.gates.concrete.NotGateNode;
import dev.jasonlessenich.jlogic.nodes.gates.loader.JGate;
import dev.jasonlessenich.jlogic.nodes.io.InputNode;
import dev.jasonlessenich.jlogic.nodes.io.OutputNode;
import dev.jasonlessenich.jlogic.nodes.pins.wires.Wire;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MainController {
	public static final Map<Point, DraggableNode> NODES = new HashMap<>();
	public static final List<Wire> WIRES = new ArrayList<>();

	public static AnchorPane MAIN_PANE;

	public static boolean simulationMode = false;
	@FXML
	private AnchorPane mainPane;
	private Point lastContextMenuPoint = new Point();

	@FXML
	private void initialize() {
		MAIN_PANE = mainPane;
		// draw grid
		mainPane.setStyle("""
					-fx-background-color: rgba(255,255,255,0.2),
					linear-gradient(from 0.5px 0.0px to 10.5px  0.0px, repeat, #f1f1f1 5%, transparent 5%),
					linear-gradient(from 0.0px 0.5px to  0.0px 10.5px, repeat, #f1f1f1 5%, transparent 5%);
				""");
		final ContextMenu contextMenu = buildPaneContextMenu();
		mainPane.setOnMouseClicked(mouseEvent -> {
			contextMenu.hide();
			if (mouseEvent.getTarget().getClass() != AnchorPane.class) return;
			if (mouseEvent.getButton() == MouseButton.SECONDARY) {
				lastContextMenuPoint = Point.of(mouseEvent.getX(), mouseEvent.getY()).stepped(Constants.GRID_STEP_SIZE);
				contextMenu.show(mainPane, mouseEvent.getScreenX(), mouseEvent.getScreenY());
			}
		});
	}

	private void addWire(@Nonnull Point start, @Nonnull Point end) {
		final Wire wire = new Wire(start, end);
		WIRES.add(wire);
		mainPane.getChildren().add(wire);
	}

	private void addDraggable(@Nonnull Point point, @Nonnull Function<Point, DraggableNode> nodeFunction) {
		final DraggableNode node = nodeFunction.apply(point);
		NODES.put(point, node);
		mainPane.getChildren().add(node);
	}

	private @Nonnull ContextMenu buildPaneContextMenu() {
		final CheckMenuItem simulationMode = new CheckMenuItem("Simulation Mode");
		simulationMode.setSelected(MainController.simulationMode);
		simulationMode.setOnAction(actionEvent -> {
			MainController.simulationMode = simulationMode.isSelected();
		});
		final MenuItem addInput = new MenuItem("Add Input");
		addInput.setOnAction(actionEvent -> addDraggable(lastContextMenuPoint, InputNode::new));
		final MenuItem addOutput = new MenuItem("Add Output");
		addOutput.setOnAction(actionEvent -> addDraggable(lastContextMenuPoint, OutputNode::new));
		final MenuItem addWire = new MenuItem("Add Wire");
		addWire.setOnAction(actionEvent -> addWire(lastContextMenuPoint, lastContextMenuPoint.addX(Constants.GRID_STEP_SIZE * 3)));
		final Menu addGate = new Menu("Add Gate...");
		final MenuItem addAndGate = new MenuItem("AND Gate");
		addAndGate.setOnAction(e -> addDraggable(lastContextMenuPoint, AndGateNode::new));
		final MenuItem addOrGate = new MenuItem("NOT Gate");
		addOrGate.setOnAction(e -> addDraggable(lastContextMenuPoint, NotGateNode::new));
		addGate.getItems().addAll(addAndGate, addOrGate, new SeparatorMenuItem());
		// register gates
		for (JGate gate : JLogicApplication.getGateManager().getGates()) {
			final Menu gateMenu = new Menu("%s (%s)".formatted(gate.getName(), gate.getSymbol()));
			for (JGate.Table table : gate.getTableDefinition()) {
				final MenuItem subMenu = new MenuItem("%s IN / %s OUT".formatted(table.getInputCount(), table.getOutputCount()));
				subMenu.setOnAction(actionEvent -> addDraggable(lastContextMenuPoint, point -> new CustomGateNode(point, gate, table)));
				gateMenu.getItems().add(subMenu);
			}
			addGate.getItems().add(gateMenu);
		}
		final ContextMenu contextMenu = new ContextMenu();
		contextMenu.getItems().addAll(simulationMode, new SeparatorMenuItem(), addInput, addOutput, addWire, new SeparatorMenuItem(), addGate);
		return contextMenu;
	}
}
