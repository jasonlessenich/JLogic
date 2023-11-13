package dev.jasonlessenich.jlogic.utils;

import lombok.Data;

import javax.annotation.Nonnull;

@Data
public class Point {
	private double x, y;

	public static @Nonnull Point of(double x, double y) {
		final Point point = new Point();
		point.setX(x);
		point.setY(y);
		return point;
	}

	public @Nonnull Point stepped(int step) {
		return Point.of(PointUtils.step(x, step), PointUtils.step(y, step));
	}

	public Point addX(double x) {
		return Point.of(this.x + x, y);
	}

	public Point addY(double y) {
		return Point.of(x, this.y + y);
	}
}
