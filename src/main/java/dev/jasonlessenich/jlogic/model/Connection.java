package dev.jasonlessenich.jlogic.model;

import javax.annotation.Nonnull;

public class Connection {
	private final ConnectableNode connectionTo;
	private final Type connectionType;

	public Connection(@Nonnull ConnectableNode connectionTo, @Nonnull Type connectionType) {
		this.connectionTo = connectionTo;
		this.connectionType = connectionType;
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

