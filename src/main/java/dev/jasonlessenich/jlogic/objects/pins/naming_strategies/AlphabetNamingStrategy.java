package dev.jasonlessenich.jlogic.objects.pins.naming_strategies;

public class AlphabetNamingStrategy implements PinNamingStrategy {
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

	protected AlphabetNamingStrategy() {
	}

	@Override
	public String calculateName(int index) {
		if (index >= ALPHABET.length()) {
			return ALPHABET.charAt(index / ALPHABET.length() - 1) + "" + ALPHABET.charAt(index % ALPHABET.length());
		} else {
			return ALPHABET.charAt(index) + "";
		}
	}
}
