package dev.jasonlessenich.jlogic.utils;

public class PointUtils {
	private static final int STEP = 20;

	public static double step(double x) {
		return x - x % STEP;
	}
}
