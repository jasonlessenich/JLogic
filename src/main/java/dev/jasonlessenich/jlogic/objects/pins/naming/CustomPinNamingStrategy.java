package dev.jasonlessenich.jlogic.objects.pins.naming;

import javax.annotation.Nonnull;
import java.util.List;

public class CustomPinNamingStrategy implements PinNamingStrategy {
	@Nonnull
	private final List<String> customNames;
	@Nonnull
	private final PinNamingStrategy fallbackStrategy;

	public CustomPinNamingStrategy(
			@Nonnull List<String> customNames,
			@Nonnull PinNamingStrategy fallbackStrategy
	) {
		this.customNames = customNames;
		this.fallbackStrategy = fallbackStrategy;
		if (fallbackStrategy.getClass() == getClass())
			throw new IllegalArgumentException("Fallback strategy may not be custom!");
	}

	@Override
	public String calculateName(int index) {
		return index < customNames.size()
				? customNames.get(index)
				: fallbackStrategy.calculateName(index);
	}
}
