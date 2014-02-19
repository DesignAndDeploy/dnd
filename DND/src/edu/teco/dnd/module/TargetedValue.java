package edu.teco.dnd.module;

import java.io.Serializable;

import edu.teco.dnd.blocks.Input;

/**
 * A value together with the name of an {@link Input} it should be sent to.
 */
public class TargetedValue {
	private final String inputName;
	private final Serializable value;

	/**
	 * Initializes a new TargetedValue.
	 * 
	 * @param inputName
	 *            the name of the Input the value should be send to
	 * @param value
	 *            the value that should be send
	 */
	public TargetedValue(final String inputName, final Serializable value) {
		this.inputName = inputName;
		this.value = value;
	}

	public String getInputName() {
		return this.inputName;
	}

	public Serializable getValue() {
		return this.value;
	}
}
