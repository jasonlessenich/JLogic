package dev.jasonlessenich.jlogic.objects.pins.naming_strategies;

public interface PinNamingStrategy {
	PinNamingStrategy INDEX = new IndexNamingStrategy();
	PinNamingStrategy ALPHABET = new AlphabetNamingStrategy();

	String calculateName(int index);
}
