package dev.jasonlessenich.jlogic.objects.pins.naming;

import javax.annotation.Nonnull;

public class IndexNamingStrategy implements PinNamingStrategy {
	protected IndexNamingStrategy() {
	}

	@Override
	public String calculateName(int index) {
		return String.valueOf(index);
	}
}
