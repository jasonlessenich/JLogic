package dev.jasonlessenich.jlogic.nodes;

import javax.annotation.Nonnull;

public class Connection {
	private final ConnectableNode connectionFrom;
	private final ConnectableNode connectionTo;
	private final Type connectionType;

	public Connection(@Nonnull ConnectableNode connectionFrom, @Nonnull ConnectableNode connectionTo, @Nonnull Type connectionType) {
		this.connectionFrom = connectionFrom;
		this.connectionTo = connectionTo;
		this.connectionType = connectionType;
	}

	public ConnectableNode getConnectionFrom() {
		return connectionFrom;
	}

	public ConnectableNode getConnectionTo() {
		return connectionTo;
	}

	public Type getConnectionType() {
		return connectionType;
	}

	public enum Type {
		INPUT, OUTPUT
	}
}

