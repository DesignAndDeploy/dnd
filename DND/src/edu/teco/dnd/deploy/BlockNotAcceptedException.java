package edu.teco.dnd.deploy;

import edu.teco.dnd.blocks.FunctionBlock;

/**
 * This exception is used to signal that a Module denied running a {@link FunctionBlock}.
 */
public class BlockNotAcceptedException extends Exception {
	private static final long serialVersionUID = 6875214832597689333L;

	public BlockNotAcceptedException(final String msg) {
		super(msg);
	}

	public BlockNotAcceptedException() {
		super();
	}
}
