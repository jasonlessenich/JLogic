package dev.jasonlessenich.jlogic.utils;

import javafx.scene.Parent;
import javafx.scene.layout.Region;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;

public class NodeUtils {
	private NodeUtils() {}

	public static Region setSize(@Nonnull Region node, double width, double height) {
		node.setMaxWidth(width);
		node.setMinWidth(width);
		node.setMaxHeight(height);
		node.setMinHeight(height);
		return node;
	}

	public static Region setSize(Region node, double size) {
		return setSize(node, size, size);
	}
}
