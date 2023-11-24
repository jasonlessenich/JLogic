package dev.jasonlessenich.jlogic.utils;

import lombok.Data;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A class representing a point in 2D space.
 */
@Data
public class Point {
	private double x, y;

	/**
	 * Creates a new {@link Point} with the given coordinates.
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return The {@link Point}.
	 */
	public static @Nonnull Point of(double x, double y) {
		final Point point = new Point();
		point.setX(x);
		point.setY(y);
		return point;
	}

	/**
	 * "Steps" the point by rounding its coordinates to the nearest multiple of a step.
	 * This simply uses {@link PointUtils#step(double, int)}.
	 *
	 * @param step The step to round to.
	 * @return The stepped {@link Point}.
	 */
	public @Nonnull Point stepped(int step) {
		return Point.of(PointUtils.step(x, step), PointUtils.step(y, step));
	}

	/**
	 * Adds the given x value to the x coordinate of this {@link Point}.
	 *
	 * @param x The x value to add.
	 * @return This {@link Point}.
	 */
	public Point addX(double x) {
		this.x += x;
		return this;
	}

	/**
	 * Adds the given y value to the y coordinate of this {@link Point}.
	 *
	 * @param y The y value to add.
	 * @return This {@link Point}.
	 */
	public Point addY(double y) {
		this.y += y;
		return this;
	}

	/**
	 * Sets the x and y coordinates of this {@link Point} to the given values.
	 *
	 * @param point The {@link Point} to set this {@link Point} to.
	 * @return This {@link Point}.
	 */
	public Point set(@Nonnull Point point) {
		this.x = point.x;
		this.y = point.y;
		return this;
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
