package dev.jasonlessenich.jlogic.objects.nodes.gates.concrete;

import dev.jasonlessenich.jlogic.objects.nodes.gates.GateNode;
import dev.jasonlessenich.jlogic.utils.Point;

import javax.annotation.Nonnull;
import java.util.List;

public class NotGateNode extends GateNode {
	public NotGateNode(@Nonnull Point point) {
		super(point, 1, 1, "-1");
	}

	@Override
	public boolean evaluate(@Nonnull List<Boolean> inputs) {
		if (inputs.size() != 1)
			throw new IllegalArgumentException("Not Gate can only have one input!");
		return !inputs.get(0);
	}
}
