package dev.jasonlessenich.jlogic.utils;

import dev.jasonlessenich.jlogic.nodes.ConnectableNode;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class Connection {
	private final ConnectableNode connectionFrom;
	private final ConnectableNode connectionTo;
	private final Type connectionType;

	public Connection(@Nonnull ConnectableNode connectionFrom, @Nonnull ConnectableNode connectionTo, @Nonnull Type connectionType) {
		this.connectionFrom = connectionFrom;
		this.connectionTo = connectionTo;
		this.connectionType = connectionType;
	}

	public enum Type {
		INPUT, OUTPUT
	}
}

