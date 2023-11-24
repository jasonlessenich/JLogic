package dev.jasonlessenich.jlogic.utils;

/**
 * A utility class for interacting with {@link Point}s.
 */
public class PointUtils {
	private PointUtils() {}

	/**
	 * Rounds a number to the nearest multiple of a step.
	 *
	 * @param num The number to round.
	 * @param step The step to round to.
	 * @return The rounded number.
	 */
	public static double step(double num, int step) {
		return num - num % step;
	}
}
