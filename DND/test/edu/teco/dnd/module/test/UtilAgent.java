package edu.teco.dnd.module.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import lime.IllegalTupleSpaceNameException;
import lime.LimeServer;
import lime.LimeTupleSpace;
import lime.StationaryAgent;
import lime.TupleSpaceEngineException;

/** Helper used to execute things that need to run inside a StationaryAgent in tests. */
public class UtilAgent extends StationaryAgent {
	/** map whether the test identified by a certain ID was run successfully. */
	private Map<Integer, Boolean> success = new HashMap<Integer, Boolean>();

	/** our limeTupleSpaces. */
	private LimeTupleSpace moduleSpace;
	/** our limeTupleSpaces. */
	private LimeTupleSpace appSpace;

	/**
	 * Used to mark the end.
	 */
	private class EndMarker extends ExecutionHelper {
		@Override
		public boolean run() {
			assert false;
			return false;
		}
	};

	/**
	 * Stores the jobs that should be run.
	 */
	private final LinkedBlockingQueue<ExecutionHelper> queue = new LinkedBlockingQueue<>();

	@Override
	public final void run() {

		try {
			moduleSpace = new LimeTupleSpace("ModuleSpace");
			appSpace = new LimeTupleSpace("ApplicationSpace" + TestModule.TEST_APPLICATION_ID);

		} catch (TupleSpaceEngineException e) {
			throw new IllegalStateException("Lime not working properly.", e);
		} catch (IllegalTupleSpaceNameException e) {
			assert false;
			e.printStackTrace();
		}
		moduleSpace.setShared(true);
		appSpace.setShared(true);
		LimeServer.getServer().engage();

		boolean running = true;
		while (running) {
			ExecutionHelper runnable;
			try {
				runnable = queue.take();
			} catch (InterruptedException e) {
				continue;
			}
			if (runnable instanceof EndMarker) {
				running = false;
			} else {
				runnable.setup(moduleSpace, appSpace);
				success.put(runnable.returnId, runnable.run());
			}
		}
	}

	/**
	 * adds a runnable to be run at a later time.
	 * 
	 * @param runnable
	 *            the runnable, <br>
	 *            when null is inserted RunnerAgent terminates after finishing the jobs scheduled before the
	 *            null.
	 */
	public final synchronized void addRunnable(final ExecutionHelper runnable) {
		queue.offer((runnable != null) ? runnable : new EndMarker());
	}

	/**
	 * whether running of app with ID returnId succeeded.
	 * 
	 * @param returnId
	 *            the ID of the executionHelper
	 * @return whether running succeeded. Null if not yet finished.
	 */
	public final synchronized Boolean getSuccess(final int returnId) {

		return success.get(returnId);
	}

}
