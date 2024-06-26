package dev.jasonlessenich.jlogic.controller;

import dev.jasonlessenich.jlogic.JLogicApplication;
import dev.jasonlessenich.jlogic.objects.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.objects.nodes.gates.CustomGateNode;
import dev.jasonlessenich.jlogic.objects.nodes.gates.concrete.AndGateNode;
import dev.jasonlessenich.jlogic.objects.nodes.gates.concrete.NotGateNode;
import dev.jasonlessenich.jlogic.objects.nodes.gates.custom.JGate;
import dev.jasonlessenich.jlogic.objects.nodes.io.InputNode;
import dev.jasonlessenich.jlogic.objects.nodes.io.OutputNode;
import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
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
import java.util.List;

public class MainController {
	public static final List<ConnectablePin> PINS = new ArrayList<>();

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

	private void addConnectable(@Nonnull ConnectableNode node) {
		mainPane.getChildren().add(node);
		node.evaluate(0);
	}

	private @Nonnull ContextMenu buildPaneContextMenu() {
		final CheckMenuItem simulationMode = new CheckMenuItem("Simulation Mode");
		simulationMode.setSelected(MainController.simulationMode);
		simulationMode.setOnAction(actionEvent -> {
			MainController.simulationMode = simulationMode.isSelected();
		});
		final MenuItem addInput = new MenuItem("Add Input");
		addInput.setOnAction(actionEvent -> addConnectable(new InputNode(lastContextMenuPoint)));
		final MenuItem addOutput = new MenuItem("Add Output");
		addOutput.setOnAction(actionEvent -> addConnectable(new OutputNode(lastContextMenuPoint)));
		final Menu addGate = new Menu("Add Gate...");
		final MenuItem addAndGate = new MenuItem("AND Gate");
		addAndGate.setOnAction(e -> addConnectable(new AndGateNode(lastContextMenuPoint)));
		final MenuItem addOrGate = new MenuItem("NOT Gate");
		addOrGate.setOnAction(e -> addConnectable(new NotGateNode(lastContextMenuPoint)));
		addGate.getItems().addAll(addAndGate, addOrGate, new SeparatorMenuItem());
		// register gates
		for (JGate gate : JLogicApplication.getGateManager().getGates()) {
			final Menu gateMenu = new Menu("%s (%s)".formatted(gate.getName(), gate.getSymbol()));
			for (JGate.Table table : gate.getTables()) {
				final MenuItem subMenu = new MenuItem("%s IN / %s OUT".formatted(table.getInputCount(), table.getOutputCount()));
				subMenu.setOnAction(actionEvent -> addConnectable(new CustomGateNode(lastContextMenuPoint, gate, table)));
				gateMenu.getItems().add(subMenu);
			}
			addGate.getItems().add(gateMenu);
		}
		final ContextMenu contextMenu = new ContextMenu();
		contextMenu.getItems().addAll(simulationMode, new SeparatorMenuItem(), addInput, addOutput, new SeparatorMenuItem(), addGate);
		return contextMenu;
	}
}
