package dev.jasonlessenich.jlogic.objects.pins.naming;

public class IndexPinNamingStrategy implements PinNamingStrategy {
	protected IndexPinNamingStrategy() {
	}

	@Override
	public String calculateName(int index) {
		return String.valueOf(index);
	}
}
