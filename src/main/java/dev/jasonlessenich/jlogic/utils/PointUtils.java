package dev.jasonlessenich.jlogic.utils;

import javax.annotation.Nonnull;

public class PointUtils {
	public static double step(double num, int step) {
		return num - num % step;
	}

	public static boolean isOnLine(@Nonnull Point a, @Nonnull Point lineStart, @Nonnull Point lineEnd) {
		return (a.getX() - lineStart.getX()) * (lineEnd.getY() - lineStart.getY()) ==
				(a.getY() - lineStart.getY()) * (lineEnd.getX() - lineStart.getX());
	}
}
