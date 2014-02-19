package edu.teco.dnd.util;

public class ValueWithHash<T> {
	private final T value;
	private final Hash hash;

	public ValueWithHash(final T value, final Hash hash) {
		this.value = value;
		this.hash = hash;
	}

	public T getValue() {
		return value;
	}

	public Hash getHash() {
		return hash;
	}
}
