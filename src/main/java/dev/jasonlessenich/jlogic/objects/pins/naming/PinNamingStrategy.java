package dev.jasonlessenich.jlogic.objects.pins.naming;

public interface PinNamingStrategy {
	PinNamingStrategy INDEX = new IndexNamingStrategy();

	String calculateName(int index);
}
