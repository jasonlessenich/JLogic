package dev.jasonlessenich.jlogic.utils;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;

import javax.annotation.Nonnull;

/**
 * A utility class for interacting with {@link ContextMenu}s.
 */
public class ContextMenuUtils {
	private ContextMenuUtils() {
	}

	/**
	 * Registers the {@link Node#setOnMousePressed(EventHandler)} event handler to
	 * show the given {@link ContextMenu} when the node is right-clicked.
	 *
	 * @param node 	  The node to register the event handler to.
	 * @param contextMenu The context menu to show.
	 */
	public static void register(@Nonnull Node node, @Nonnull ContextMenu contextMenu) {
		node.setOnMousePressed(me -> {
			if (me.isSecondaryButtonDown()) {
				contextMenu.show(node, me.getScreenX(), me.getScreenY());
			} else contextMenu.hide();
		});
	}
}
