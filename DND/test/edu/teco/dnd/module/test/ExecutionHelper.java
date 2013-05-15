package edu.teco.dnd.module.test;

import java.io.Serializable;

import lime.LimeTupleSpace;

/**
 * Class inherited by testcode to be executed in the utilagent.
 */
public abstract class ExecutionHelper implements Serializable {
	/**
	 * the id to which the returnvalue will be written upon completion of run.
	 */
	public int returnId;
	private LimeTupleSpace moduleSpace = null;
	private LimeTupleSpace appSpace = null;

	/**
	 * the main run method.
	 * 
	 * @return boolean to communicate back. interpretation is up to user.
	 */
	public abstract boolean run();

	/**
	 * called by utilAgent to hand over a reference to the owned moduleSpace.
	 * 
	 * @param moduleSpace
	 *            the moduleSpace
	 */
	public void setup(final LimeTupleSpace moduleSpace) {
		this.moduleSpace = moduleSpace;
	}

	/**
	 * called by utilAgent to hand over a reference to the owned module-/appSpace.
	 * 
	 * @param moduleSpace
	 *            the moduleSpace
	 * @param appSpace
	 *            the AppSpace
	 */
	public void setup(final LimeTupleSpace moduleSpace, final LimeTupleSpace appSpace) {
		this.moduleSpace = moduleSpace;
		this.appSpace = appSpace;
	}

	/**
	 * get the moduleSpace from setup.
	 * 
	 * @return The moduleSpace from setup
	 */
	protected LimeTupleSpace getModuleSpace() {
		return this.moduleSpace;
	}

	/**
	 * get the appSpace from setup.
	 * 
	 * @return The appSpace from setup
	 */
	protected LimeTupleSpace getAppSpace() {
		return this.appSpace;
	}

}
