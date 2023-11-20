package dev.jasonlessenich.jlogic.utils;

import javafx.scene.layout.Region;

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
