package edu.teco.dnd.module;

import edu.teco.dnd.blocks.FunctionBlock;

/**
 * This exception is thrown when user code (mostly {@link FunctionBlock}s) throw an {@link Exception}.
 * 
 * @see FunctionBlockSecurityDecorator
 * @see UsercodeWrapper
 */
public class UserSuppliedCodeException extends Exception {
	private static final long serialVersionUID = -851321417787303985L;

	public UserSuppliedCodeException(String message) {
		super(message);
	}

	public UserSuppliedCodeException() {
		super();
	}
}
