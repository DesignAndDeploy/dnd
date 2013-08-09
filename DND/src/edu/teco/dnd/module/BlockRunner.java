package edu.teco.dnd.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.ConnectionTarget;
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

	/**
	 * Returns the type of the given functionBlock. (Marking it for the security manager.
	 * 
	 * @param block
	 *            the FunctionBlock to get the type from.
	 * @return the type of the block
	 * @throws UserSuppliedCodeException
	 *             if the code produces any Exceptions.
	 */
	public static String getBlockType(FunctionBlock block) throws UserSuppliedCodeException {
		String type;
		try {
			type = block.getType();
			
		} catch (Throwable t) {
			throw new UserSuppliedCodeException(t);
		}
		if(type == null) {
			throw new UserSuppliedCodeException("Blocktype must not be null!");
		} else {
			return type;
		}

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

			for (Output<?> output : block.getOutputs().values()) {
				for (ConnectionTarget ct : output.getConnectedTargets()) {
					if (ct instanceof RemoteConnectionTarget) {
						RemoteConnectionTarget rct = (RemoteConnectionTarget) ct;
						rct.setApplication(associatedApp);
					}
				}
			}
		} catch (InvalidFunctionBlockException e) {
			LOGGER.warn("User supplied block {} initialization failed.", block.getID());
			LOGGER.catching(e);
		} catch (Throwable t) {
			throw new UserSuppliedCodeException(t);
		}
	}

	private void doUpdate() throws UserSuppliedCodeException {
		try {
			block.doUpdate();
		} catch (AssignmentException e) {
			LOGGER.catching(e);
		} catch (Throwable t) {
			throw new UserSuppliedCodeException(t);
		}
	}
}
