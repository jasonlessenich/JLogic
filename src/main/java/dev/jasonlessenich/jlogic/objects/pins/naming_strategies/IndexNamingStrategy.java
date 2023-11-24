package dev.jasonlessenich.jlogic.objects.pins.naming_strategies;

public class IndexNamingStrategy implements PinNamingStrategy {
	protected IndexNamingStrategy() {
	}

	@Override
	public String calculateName(int index) {
		return String.valueOf(index);
	}
}
