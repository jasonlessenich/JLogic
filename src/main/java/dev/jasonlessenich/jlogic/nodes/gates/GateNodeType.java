package dev.jasonlessenich.jlogic.nodes.gates;

public enum GateNodeType {
	AND("&"),
	NOT("-1");

	private final String symbol;

	GateNodeType(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}
}
