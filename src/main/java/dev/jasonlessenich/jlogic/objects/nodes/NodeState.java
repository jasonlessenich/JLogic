package dev.jasonlessenich.jlogic.objects.nodes;

import javax.annotation.Nonnull;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NodeState implements PropertyChangeListener {
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private final int outputCount;
	private final Consumer<boolean[]> activeChangedListener;
	private boolean[] active;

	public NodeState(int outputCount, @Nonnull Consumer<boolean[]> activeChangedListener) {
		this.outputCount = outputCount;
		this.activeChangedListener = activeChangedListener;
		pcs.addPropertyChangeListener(this);
	}

	public boolean[] getActive() {
		if (active == null)
			return new boolean[outputCount];
		return active;
	}

	public void setActive(boolean[] active) {
		final boolean[] old = this.active;
		this.active = active;
		pcs.firePropertyChange("active", old, active);
	}

	public List<Boolean> getActiveAsList() {
		final List<Boolean> active = new ArrayList<>();
		for (boolean b : getActive())
			active.add(b);
		return active;
	}

	@Override
	public void propertyChange(@Nonnull PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("active"))
			activeChangedListener.accept((boolean[]) evt.getNewValue());
	}
}
