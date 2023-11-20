package dev.jasonlessenich.jlogic.objects.nodes.gates.custom;

import dev.jasonlessenich.jlogic.objects.pins.naming_strategies.PinNamingStrategy;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JGate {
	private static final Map<String, PinNamingStrategy> STRATEGY_MAP = Map.of(
			"INDEX", PinNamingStrategy.INDEX,
			"ALPHABET", PinNamingStrategy.ALPHABET
	);

	private String name;
	private String symbol;
	private String namingStrategy = "INDEX";
	private List<String> customNames = List.of();
	private List<Table> tableDefinition;

	public PinNamingStrategy getNamingStrategy() {
		if (!STRATEGY_MAP.containsKey(namingStrategy))
			throw new IllegalArgumentException("No naming strategy for " + namingStrategy);
		return STRATEGY_MAP.get(namingStrategy);
	}

	@Data
	public static class Table {
		private int inputCount;
		private int outputCount;
		private Map<String, Boolean> map;
	}
}
