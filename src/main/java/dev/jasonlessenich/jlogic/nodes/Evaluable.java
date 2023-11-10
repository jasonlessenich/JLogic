package dev.jasonlessenich.jlogic.nodes;

import java.util.List;

public interface Evaluable {
	boolean evaluate(List<Boolean> inputs);
}
