package dev.jasonlessenich.jlogic.utils;

import javax.annotation.Nonnull;

public class Point {
	private double x, y;

	public Point() {}

	private Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public static @Nonnull Point of(double x, double y) {
		return new Point(x, y);
	}

	public @Nonnull Point stepped(int step) {
		return new Point(PointUtils.step(x, step), PointUtils.step(y, step));
	}
}
