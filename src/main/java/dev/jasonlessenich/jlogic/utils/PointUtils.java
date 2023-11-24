package dev.jasonlessenich.jlogic.utils;

import javafx.util.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class PointUtils {
	public static double step(double num, int step) {
		return num - num % step;
	}

	@Nonnull
	public static List<Pair<Point, Point>> straightLines(@Nonnull Point start, @Nonnull Point end) {
		final double xLength = end.getX() - start.getX();
		final double yLength = end.getY() - start.getY();
		if (xLength == 0 || yLength == 0) {
			return List.of(new Pair<>(start, end));
		}
		final Point first = Point.of(start.getX() + xLength / 2, start.getY());
		final Point second = Point.of(first.getX(), start.getY() + yLength);
		return List.of(new Pair<>(start, first), new Pair<>(first, second), new Pair<>(second, end));
	}
}
