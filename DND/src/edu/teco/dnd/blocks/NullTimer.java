package edu.teco.dnd.blocks;

/**
 * A timer that never ticks. It is used as null object if no timer is needed.
 */
public class NullTimer implements Timer {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = -8563697393677953506L;
	/**
	 * The only instance of NullTimer that should be used.
	 */
	private static final NullTimer INSTANCE = new NullTimer();

	/**
	 * Returns the only instance of NullTimer that should be used.
	 * 
	 * @return the only instance of NullTimer that should be used
	 */
	public static NullTimer getInstance() {
		return INSTANCE;
	}

	/**
	 * Always returns false.
	 * 
	 * @return false
	 */
	@Override
	public final boolean check() {
		return false;
	}

	/**
	 * Returns a negative value as no tick is scheduled.
	 * 
	 * @return a negative value as no tick is scheduled
	 */
	@Override
	public final long getTimeToNextTick() {
		return -1L;
	}

	@Override
	public final void reset() {
	}
}
