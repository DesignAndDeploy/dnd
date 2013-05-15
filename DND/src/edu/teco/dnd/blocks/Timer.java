package edu.teco.dnd.blocks;

import java.io.Serializable;

/**
 * A timer is used to update {@link FunctionBlock}s at specified intervals. How acurate this updates are
 * depends on the scheduler used. Updates between two ticks may happen if inputs change.
 */
public interface Timer extends Serializable {
	/**
	 * Returns true and resets the timer if the specified interval has elapsed. Returns false otherwise.
	 * 
	 * @return true if an update is needed, false otherwise
	 */
	boolean check();

	/**
	 * Returns the time in milliseconds until the next tick is scheduled. Returns a negative number if the
	 * time is unknown or no tick is scheduled.
	 * 
	 * @return the time in milliseconds until the next tick is scheduled. Negative if unknown.
	 */
	long getTimeToNextTick();

	/**
	 * Resets the time to the next tick.
	 */
	void reset();
}
