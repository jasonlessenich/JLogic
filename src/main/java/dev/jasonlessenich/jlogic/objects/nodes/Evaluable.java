package dev.jasonlessenich.jlogic.objects.nodes;

import java.util.List;

@FunctionalInterface
public interface Evaluable {
	boolean[] evaluate(List<Boolean> inputs);
}
