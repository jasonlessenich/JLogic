package dev.jasonlessenich.jlogic.objects.pins.naming;

public interface PinNamingStrategy {
	PinNamingStrategy INDEX = new IndexPinNamingStrategy();
	PinNamingStrategy ALPHABET = new AlphabetPinNamingStrategy();

	String calculateName(int index);
}
