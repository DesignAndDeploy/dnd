package edu.teco.dnd.blocks;

/**
 * A timer that uses {@link System#currentTimeMillis()} to track time.
 */
public class SystemTimer implements Timer {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = -9064161847687002819L;

	/**
	 * The interval to tick at.
	 */
	private final long interval;

	/**
	 * The last time a tick was triggered.
	 */
	private long lastTick = 0L;

	/**
	 * Initializes a new SystemTimer.
	 * 
	 * @param interval
	 *            the interval this SystemTimer should tick at.
	 */
	public SystemTimer(final long interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("interval must be greater than 0");
		}
		this.interval = interval;
		reset();
	}

	/**
	 * If at least {@code interval} seconds have passed, a new tick is generated and the timer is reset.
	 * Otherwise, nothing happens.
	 * 
	 * @return true if an update is needed, false otherwise
	 */
	@Override
	public final boolean check() {
		long now = System.currentTimeMillis();
		if (now - lastTick >= interval) {
			lastTick = now;
			return true;
		}
		return false;
	}

	/**
	 * Returns the time to the next tick.
	 * 
	 * @return the time to the next tick
	 */
	@Override
	public final long getTimeToNextTick() {
		long time = interval - (System.currentTimeMillis() - lastTick);
		if (time < 0) {
			time = 0;
		}
		return time;
	}

	@Override
	public void reset() {
		this.lastTick = System.currentTimeMillis();
	}
}
