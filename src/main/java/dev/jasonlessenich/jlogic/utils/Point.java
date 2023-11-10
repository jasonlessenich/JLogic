package dev.jasonlessenich.jlogic.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nonnull;

@Getter
@Setter
@NoArgsConstructor
public class Point {
	private double x, y;

	private Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public static @Nonnull Point of(double x, double y) {
		return new Point(x, y);
	}

	public @Nonnull Point stepped(int step) {
		return new Point(PointUtils.step(x, step), PointUtils.step(y, step));
	}
}
