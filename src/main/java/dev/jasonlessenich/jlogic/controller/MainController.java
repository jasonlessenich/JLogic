package dev.jasonlessenich.jlogic.controller;

import dev.jasonlessenich.jlogic.JLogicApplication;
import dev.jasonlessenich.jlogic.custom.pane.PanAndZoomPane;
import dev.jasonlessenich.jlogic.custom.pane.SceneGestures;
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
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MainController {
	public static final List<ConnectableNode> NODES = new ArrayList<>();
	public static final List<ConnectablePin> PINS = new ArrayList<>();

	public static AnchorPane NODE_PANE;

	public static boolean simulationMode = false;
	@FXML
	public TabPane nodeTabPane;
	@FXML
	public PanAndZoomPane mainPanPane;
	@FXML
	public StackPane mainStackPane;
	@FXML
	private AnchorPane nodePane;
	@FXML
	public MenuBar menuBar;
	@FXML
	public TreeItem<String> rootTreeItem;
	private Point lastContextMenuPoint = new Point();

	@FXML
	private void initialize() {
		NODE_PANE = nodePane;
		final SceneGestures sceneGestures = new SceneGestures(mainPanPane);
		mainPanPane.toBack();
		// add gesture event filters
		mainStackPane.addEventFilter(MouseEvent.MOUSE_CLICKED, sceneGestures.getOnMouseClickedEventHandler());
		mainStackPane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
		mainStackPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
		mainStackPane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
		nodeTabPane.getStyleClass().add("floating");
		// draw grid
		nodePane.setStyle("""
					-fx-background-color: rgba(255,255,255,0.2),
					linear-gradient(from 0.5px 0.0px to 10.5px  0.0px, repeat, #BDBDBD 5%, transparent 5%),
					linear-gradient(from 0.0px 0.5px to  0.0px 10.5px, repeat, #BDBDBD 5%, transparent 5%);
				""");
		final ContextMenu contextMenu = buildPaneContextMenu();
		nodePane.setOnMouseClicked(mouseEvent -> {
			contextMenu.hide();
			if (mouseEvent.getTarget().getClass() != AnchorPane.class) return;
			if (mouseEvent.getButton() == MouseButton.SECONDARY) {
				lastContextMenuPoint = Point.of(mouseEvent.getX(), mouseEvent.getY()).stepped(Constants.GRID_STEP_SIZE);
				contextMenu.show(nodePane, mouseEvent.getScreenX(), mouseEvent.getScreenY());
			}
		});
	}

	private void addConnectable(@Nonnull ConnectableNode node) {
		NODES.add(node);
		nodePane.getChildren().add(node);
		node.evaluate();
		updateNodeTreeView();
	}

	private void updateNodeTreeView() {
		rootTreeItem.getChildren().clear();
		for (ConnectableNode n : NODES) {
			final TreeItem<String> i = new TreeItem<>(n.getClass().getSimpleName());
			rootTreeItem.getChildren().add(i);
		}
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
