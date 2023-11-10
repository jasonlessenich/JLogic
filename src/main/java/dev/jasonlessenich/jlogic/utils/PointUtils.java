package dev.jasonlessenich.jlogic.utils;

public class PointUtils {
	public static double step(double num, int step) {
		return num - num % step;
	}
}
