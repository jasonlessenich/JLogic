package dev.jasonlessenich.jlogic.custom.pane;

import lombok.Data;

/**
 * Mouse drag context used for scene and nodes.
 */
@Data
public class DragContext {
	private double mouseAnchorX;
	private double mouseAnchorY;
	private double translateAnchorX;
	private double translateAnchorY;
}