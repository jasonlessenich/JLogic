package dev.jasonlessenich.jlogic.nodes.io;

import dev.jasonlessenich.jlogic.controller.MainViewController;
import dev.jasonlessenich.jlogic.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.nodes.IONode;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import javax.annotation.Nonnull;

public class InputNode extends IONode {
	public InputNode(@Nonnull Point point) {
		super(point, circle -> {
			circle.setStroke(Color.BLACK);
			circle.setStrokeWidth(2);
		}, 0, 9999);
		this.contextMenu = buildContextMenu();
		final EventHandler<? super MouseEvent> onMousePressed = getOnMousePressed();
		setOnMousePressed(me -> {
			onMousePressed.handle(me);
			if (MainViewController.simulationMode && me.isPrimaryButtonDown()) {
				toggleActivated();
				ConnectableNode.evaluateConnections();
			}
		});
	}

	private @Nonnull ContextMenu buildContextMenu() {
		final ContextMenu contextMenu = new ContextMenu();
		contextMenu.getItems().addAll(buildDeleteItem(), buildToggleItem());
		return contextMenu;
	}

	private @Nonnull MenuItem buildToggleItem() {
		final MenuItem setActivatedItem = new MenuItem("Toggle State...");
		setActivatedItem.setOnAction(actionEvent -> toggleActivated());
		return setActivatedItem;
	}
}
