package dev.jasonlessenich.jlogic.nodes;

import dev.jasonlessenich.jlogic.controller.MainViewController;
import dev.jasonlessenich.jlogic.nodes.ConnectableNode;
import dev.jasonlessenich.jlogic.nodes.DraggableNode;
import dev.jasonlessenich.jlogic.utils.Constants;
import dev.jasonlessenich.jlogic.utils.Point;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public abstract class IONode extends ConnectableNode {
	private final Circle circle;

	@Getter
	private boolean activated = false;

	public IONode(@Nonnull Point point, @Nonnull Consumer<Circle> circleConsumer, int inputCount, int outputCount) {
		super(point, inputCount, outputCount);
		this.circle = new Circle((double) Constants.NODE_SIZE / 2, Color.RED);
		circleConsumer.accept(circle);
		getChildren().add(circle);
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
		circle.setFill(activated ? Color.LAWNGREEN : Color.ORANGERED);
	}
}
