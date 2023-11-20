package dev.jasonlessenich.jlogic.objects.pins.naming_strategies;

public interface PinNamingStrategy {
	PinNamingStrategy INDEX = new IndexNamingStrategy();

	String calculateName(int index);
}
