package edu.teco.dnd.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.ThrowStatement;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.InvalidFunctionBlockException;
import edu.teco.dnd.blocks.Output;

/**
 * This is a marker class. It's only purpose is so every call to a usersupplied FunctionBlock can be channeled through
 * it and as thus can be identified by the stacktrace and if necessary a certain amount of setup can be done.
 * 
 * @author Marvin Marx
 * 
 */
public class BlockRunner implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(BlockRunner.class);

	/** whether we are an initializer or an updater. */
	private final boolean isInit;
	private final FunctionBlock block;
	private final Application associatedApp;

	/**
	 * get a Runnable we can schedule to initialize a FunctionBlock.
	 * 
	 * @param block
	 *            the FunctionBlock to be initialize
	 * @param associatedApp
	 *            the App we should associate with the Block.
	 * @return a Runnable we can schedule to initialize a FunctionBlock
	 */
	public static Runnable getBlockInitializer(FunctionBlock block, Application associatedApp) {
		return new BlockRunner(true, block, associatedApp);
	}

	/**
	 * get a Runnable we can schedule to call doUpdate on a FunctionBlock.
	 * 
	 * @param block
	 *            the FunctionBlock to be updated
	 * @return a Runnable we can schedule to call doUpdate on a FunctionBlock
	 */
	public static Runnable getBlockUpdater(FunctionBlock block) {
		return new BlockRunner(false, block, null);
	}

	public static String getToString(Object obj) throws UserSuppliedCodeException {
		String toStr;
		try {
			toStr = obj.toString();
		} catch (Throwable t) {
			throw new UserSuppliedCodeException(t);
		}
		if (toStr == null) {
			throw new UserSuppliedCodeException("Blocktype must not be null!");
		} else {
			return toStr;
		}
	}

	public static int getHashCode(Object obj) throws UserSuppliedCodeException {
		int hashCode;
		try {
			hashCode = obj.hashCode();
		} catch (Throwable t) {
			throw new UserSuppliedCodeException(t);
		}
		return hashCode;
	}

	/**
	 * security wrapper for the equals method. Wraps the call which would usually be one.equals(two);
	 * 
	 * @param one
	 *            first argument
	 * @param two
	 *            second argument to equals
	 * @return the result of one.equals(two);
	 * @throws UserSuppliedCodeException
	 *             if there was an unexpected exception.
	 * @throws NullPointerException
	 *             if one ==null;
	 */
	public static boolean getEquals(Object one, Object two) throws UserSuppliedCodeException {
		if (one == null) {
			throw new IllegalArgumentException("one must not be null");
		}
		boolean equal;
		try {
			equal = one.equals(two);
		} catch (Throwable t) {
			throw new UserSuppliedCodeException(t);
		}
		return equal;
	}

	@Override
	public void run() {
		try {
			if (isInit) {
				doInit();
			} else {
				doUpdate();
			}
		} catch (UserSuppliedCodeException e) {
			e.printStackTrace();
			// ignoring. Don't kill the rest of the application.
		}
	}

	private BlockRunner(boolean isInit, FunctionBlock block, Application associatedApp) {
		this.isInit = isInit;
		this.block = block;
		this.associatedApp = associatedApp;
	}

	private void doInit() throws UserSuppliedCodeException {
		try {
			block.init();
		} catch (Throwable t) {
			throw new UserSuppliedCodeException(t);
		}
	}

	private void doUpdate() throws UserSuppliedCodeException {
		try {
			// TODO: update inputs if this will be needed
			block.update();
		} catch (Throwable t) {
			throw new UserSuppliedCodeException(t);
		}
	}

}
