package edu.teco.dnd.module;

/**
 * Exception thrown when a method/some other code in a functionBlock was bogus or malicious and would otherwise have
 * caused things to go wrong badly. That is e.g. a "throw new Error()" inside a FunctionBlocks init(). To prevent this
 * error from propagating outside the securityManager... protected area the "Error()" above should/will be silently
 * disregarded and this exception thrown instead. See tests..EvilBlock for an example of how this might be done.
 * 
 * @author Marvin Marx
 * 
 */
public class UserSuppliedCodeException extends Exception {

	private static final long serialVersionUID = -851321417787303985L;

	/**
	 * construct a new UserSuppliedCode exception.
	 */
	public UserSuppliedCodeException() {
		super();
	}

	/**
	 * 
	 * @param message
	 *            the message to use for this error. DO NOT use UserSuppliedCodeException(oldError.getMessage()) without
	 *            properly wrapping that for newly thrown errors again!
	 */
	public UserSuppliedCodeException(String message) {
		super(message);
	}
}
