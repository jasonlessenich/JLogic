package dev.jasonlessenich.jlogic.objects.nodes;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.function.Consumer;

public class NodeState implements PropertyChangeListener {
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private final Consumer<Boolean> activeChangedListener;

	public NodeState(@Nonnull Consumer<Boolean> activeChangedListener) {
		this.activeChangedListener = activeChangedListener;
		pcs.addPropertyChangeListener(this);
	}

	@Getter
	private boolean active;

	public void setActive(boolean active) {
		final boolean old = this.active;
		this.active = active;
		pcs.firePropertyChange("active", old, active);
	}

	@Override
	public void propertyChange(@Nonnull PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("active"))
			activeChangedListener.accept((Boolean) evt.getNewValue());
	}
}
