package dev.jasonlessenich.jlogic.utils;

public class Point {
	public double x, y;

	public Point() {}

	private Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public static Point of(double x, double y) {
		return new Point(x, y);
	}

	public Point stepped() {
		return new Point(PointUtils.step(x), PointUtils.step((y)));
	}
}
