package edu.teco.dnd.deploy;

public class BlockNotAcceptedException extends Exception {
	private static final long serialVersionUID = 6875214832597689333L;

	public BlockNotAcceptedException(final String msg) {
		super(msg);
	}
	
	public BlockNotAcceptedException() {
		super();
	}
}
