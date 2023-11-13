package dev.jasonlessenich.jlogic.utils;

import dev.jasonlessenich.jlogic.objects.pins.ConnectablePin;
import lombok.Data;

import javax.annotation.Nonnull;

@Data
public class Connection {
	private final ConnectablePin connectionFrom;
	private final ConnectablePin connectionTo;
	private final Type connectionType;

	public Connection(@Nonnull ConnectablePin connectionFrom, @Nonnull ConnectablePin connectionTo, @Nonnull Type connectionType) {
		this.connectionFrom = connectionFrom;
		this.connectionTo = connectionTo;
		this.connectionType = connectionType;
	}

	public enum Type {
		BACKWARD, FORWARD
	}
}

