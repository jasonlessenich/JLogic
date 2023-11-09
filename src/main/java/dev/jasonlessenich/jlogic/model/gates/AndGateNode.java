package dev.jasonlessenich.jlogic.model.gates;

import dev.jasonlessenich.jlogic.utils.Point;

import javax.annotation.Nonnull;

public class AndGateNode extends GateNode {
	public AndGateNode(@Nonnull Point point) {
		super(point, GateNodeType.AND, -1, 1);
	}
}
