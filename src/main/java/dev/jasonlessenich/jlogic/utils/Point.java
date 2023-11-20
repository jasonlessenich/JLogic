package dev.jasonlessenich.jlogic.utils;

import lombok.Data;

import javax.annotation.Nonnull;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Point point = (Point) o;
		return Double.compare(x, point.x) == 0 && Double.compare(y, point.y) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
