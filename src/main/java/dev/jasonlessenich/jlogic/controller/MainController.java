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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MainController {
	public static final List<ConnectablePin> PINS = new ArrayList<>();
	public static AnchorPane MAIN_PANE;
	public static QuickAccessAction SELECTED_ACTION = QuickAccessAction.NONE;
	public static boolean SIMULATION_MODE = false;

	@FXML
	public Button inputNodeButton;
	@FXML
	public Button outputNodeButton;
	@FXML
	public Button wireButton;
	@FXML
	public Button toggleSimulationButton;
	@FXML
	private AnchorPane contentPane;
	private Point lastContextMenuPoint = new Point();

	@FXML
	private void initialize() {
		MAIN_PANE = contentPane;
		initializeQuickAccessButtons();
		// draw grid
		contentPane.setStyle("""
					-fx-background-color: rgba(255,255,255,0.2),
					linear-gradient(from 0.5px 0.0px to 10.5px  0.0px, repeat, #f1f1f1 5%, transparent 5%),
					linear-gradient(from 0.0px 0.5px to  0.0px 10.5px, repeat, #f1f1f1 5%, transparent 5%);
				""");
		final ContextMenu contextMenu = buildPaneContextMenu();
		contentPane.setOnMouseClicked(mouseEvent -> {
			contextMenu.hide();
			if (mouseEvent.getTarget().getClass() != AnchorPane.class) return;
			if (mouseEvent.getButton() == MouseButton.SECONDARY) {
				lastContextMenuPoint = Point.of(mouseEvent.getX(), mouseEvent.getY()).stepped(Constants.GRID_STEP_SIZE);
				contextMenu.show(contentPane, mouseEvent.getScreenX(), mouseEvent.getScreenY());
			}
		});
	}

	// TODO: docs
	private void initializeQuickAccessButtons() {
		inputNodeButton.setGraphic(getIcon("input"));
		installTooltip(inputNodeButton, "Add Input Node");
		outputNodeButton.setGraphic(getIcon("output"));
		installTooltip(outputNodeButton, "Add Output Node");
		wireButton.setGraphic(getIcon("wire"));
		installTooltip(wireButton, "Add Wire");
		toggleSimulationButton.setGraphic(getIcon("simulate-start"));
		installTooltip(toggleSimulationButton, "Toggle Simulation");
	}

	@FXML
	public void onInputNodeButton(@Nonnull ActionEvent actionEvent) {
		setQuickAccessAction(QuickAccessAction.ADD_INPUT);
	}

	@FXML
	public void onOutputNodeButton(@Nonnull ActionEvent actionEvent) {
		setQuickAccessAction(QuickAccessAction.ADD_OUTPUT);
	}

	@FXML
	public void onWireButton(@Nonnull ActionEvent actionEvent) {
		setQuickAccessAction(QuickAccessAction.ADD_WIRE);
	}

	@FXML
	public void onToggleSimulation(@Nonnull ActionEvent actionEvent) {
		SELECTED_ACTION = QuickAccessAction.NONE;
		SIMULATION_MODE = !SIMULATION_MODE;
		toggleSimulationButton.setGraphic(getIcon(SIMULATION_MODE ? "simulate-stop" : "simulate-start"));
	}

	private void setQuickAccessAction(@Nonnull QuickAccessAction action) {
		SELECTED_ACTION = action;
		SIMULATION_MODE = false;
	}

	private void addConnectable(@Nonnull ConnectableNode node) {
		contentPane.getChildren().add(node);
		node.evaluate();
	}

	private @Nonnull ContextMenu buildPaneContextMenu() {
		final CheckMenuItem simulationMode = new CheckMenuItem("Simulation Mode");
		simulationMode.setSelected(MainController.SIMULATION_MODE);
		simulationMode.setOnAction(actionEvent -> {
			MainController.SIMULATION_MODE = simulationMode.isSelected();
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

	@Nonnull
	private ImageView getIcon(@Nonnull String name) {
		final ImageView view = new ImageView(new Image("icons/%s.png".formatted(name)));
		view.setFitWidth(25);
		view.setFitHeight(25);
		return view;
	}

	private void installTooltip(@Nonnull Node node, @Nonnull String text) {
		Tooltip.install(node, new Tooltip(text));
	}

	public enum QuickAccessAction {
		NONE,
		ADD_INPUT,
		ADD_OUTPUT,
		ADD_WIRE
	}
}
