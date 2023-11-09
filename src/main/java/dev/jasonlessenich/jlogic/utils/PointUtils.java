package dev.jasonlessenich.jlogic.utils;

public class PointUtils {
	public static double step(double x, int step) {
		return x - x % step;
	}
}
