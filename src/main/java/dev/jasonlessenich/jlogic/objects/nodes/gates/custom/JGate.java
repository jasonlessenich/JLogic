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
	private String inputNamingStrategy = "INDEX";
	private String outputNamingStrategy = "INDEX";
	private List<String> inputNames = List.of();
	private List<String> outputNames = List.of();
	private List<Table> tables;

	public PinNamingStrategy getInputNamingStrategy() {
		return getNamingStrategy(inputNamingStrategy);
	}

	public PinNamingStrategy getOutputNamingStrategy() {
		return getNamingStrategy(outputNamingStrategy);
	}

	private PinNamingStrategy getNamingStrategy(String strategy) {
		if (!STRATEGY_MAP.containsKey(strategy))
			throw new IllegalArgumentException("No naming strategy for " + strategy);
		return STRATEGY_MAP.get(strategy);
	}

	@Data
	public static class Table {
		private int inputCount;
		private int outputCount;
		private Map<String, boolean[]> definition;
	}
}
