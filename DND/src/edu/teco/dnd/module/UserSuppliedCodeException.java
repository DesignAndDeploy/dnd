package edu.teco.dnd.module;

public class UserSuppliedCodeException extends Exception {

	private static final long serialVersionUID = -851321417787303985L;

	public UserSuppliedCodeException() {
			super();
			}

	public UserSuppliedCodeException(String message) {
		super(message);
	}

	public UserSuppliedCodeException(Throwable cause) {
		super(cause);
	}

	public UserSuppliedCodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserSuppliedCodeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
