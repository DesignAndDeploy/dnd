package edu.teco.dnd.module;

import java.io.Serializable;

/**
 * A value together with the name of the Input it should be sent to.
 * 
 * @author Philipp Adolf
 */
public class TargetedValue {
	/**
	 * The name of the Input the value should be send to.
	 */
	private final String inputName;

	/**
	 * The value to be send.
	 */
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

	/**
	 * Returns the name of the Input the value should be send to.
	 * 
	 * @return the name of the Input the value should be send to
	 */
	public String getInputName() {
		return this.inputName;
	}

	/**
	 * Returns the value that should be send.
	 * 
	 * @return the value that should be send
	 */
	public Serializable getValue() {
		return this.value;
	}
}
