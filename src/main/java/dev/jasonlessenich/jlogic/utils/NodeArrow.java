package dev.jasonlessenich.jlogic.utils;

import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class NodeArrow extends Path {
	public NodeArrow(double startX, double startY, double endX, double endY) {
		final double arrowHeadSize = 5;
		setFill(Color.BLACK);
		setStrokeWidth(3);
		// draw line
		getElements().add(new MoveTo(startX, startY));
		getElements().add(new LineTo(endX, endY));
		// draw head
		double angle = Math.atan2((endY - startY), (endX - startX)) - Math.PI / 2.0;
		double sin = Math.sin(angle);
		double cos = Math.cos(angle);
		double x1 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
		double y1 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;
		double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
		double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;

		getElements().add(new LineTo(x1, y1));
		getElements().add(new LineTo(x2, y2));
		getElements().add(new LineTo(endX, endY));
	}
}
