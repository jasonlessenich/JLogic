package dev.jasonlessenich.jlogic.objects.nodes.gates.loader;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JGate {
	private String name;
	private String symbol;
	private List<Table> tableDefinition;

	@Data
	public static class Table {
		private int inputCount;
		private int outputCount;
		private Map<String, Boolean> map;
	}
}
