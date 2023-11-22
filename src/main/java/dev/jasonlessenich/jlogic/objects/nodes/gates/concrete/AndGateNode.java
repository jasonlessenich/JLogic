package dev.jasonlessenich.jlogic.objects.nodes.gates.concrete;

import dev.jasonlessenich.jlogic.objects.nodes.gates.GateNode;
import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.PinNamingStrategy;
import dev.jasonlessenich.jlogic.utils.Point;

import javax.annotation.Nonnull;
import java.util.List;

public class AndGateNode extends GateNode {
	public AndGateNode(@Nonnull Point point) {
		super(point, PinNamingStrategy.INDEX, PinNamingStrategy.INDEX, 2, 1, "&");
	}

	@Override
	public boolean evaluate(@Nonnull List<Boolean> inputs) {
		return inputs.stream().allMatch(i -> i);
	}
}
