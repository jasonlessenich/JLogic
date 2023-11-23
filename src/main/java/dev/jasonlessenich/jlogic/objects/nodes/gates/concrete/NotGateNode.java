package dev.jasonlessenich.jlogic.objects.nodes.gates.concrete;

import dev.jasonlessenich.jlogic.objects.nodes.gates.GateNode;
import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.PinNamingStrategy;
import dev.jasonlessenich.jlogic.utils.Point;

import javax.annotation.Nonnull;
import java.util.List;

public class NotGateNode extends GateNode {
	public NotGateNode(@Nonnull Point point) {
		super(point, PinNamingStrategy.INDEX, 1, 1, "-1");
	}

	@Override
	public boolean[] evaluate(@Nonnull List<Boolean> inputs) {
		return new boolean[]{inputs.isEmpty() || !inputs.get(0)};
	}
}
