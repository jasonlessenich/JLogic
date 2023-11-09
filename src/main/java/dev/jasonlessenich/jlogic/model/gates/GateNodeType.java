package dev.jasonlessenich.jlogic.model.gates;

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
