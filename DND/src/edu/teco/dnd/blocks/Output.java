package edu.teco.dnd.blocks;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * An Output for a {@link FunctionBlock}. A {@link FunctionBlock} can use this to provide data for other FunctionBlock’s
 * {@link Input}s. To define an Output simply define a non-static field of this type. It will be initialized by the
 * wrapper code, so you should never assign the field directly. You should also never call
 * {@link #setTarget(OutputTarget)} manually.
 * </p>
 * 
 * <p>
 * To send a value simply call {@link #setValue(Serializable)} with the value.
 * </p>
 * 
 * <p>
 * Output is thread-safe.
 * </p>
 */
public class Output<T extends Serializable> implements Serializable {
	private static final Logger LOGGER = LogManager.getLogger(Output.class);

	private static final long serialVersionUID = 8652366978996728530L;

	private OutputTarget<? super T> target = null;

	/**
	 * Sends a value using this output.
	 * 
	 * @param value
	 *            the value to send
	 */
	public void setValue(final T value) {
		if (target == null) {
			LOGGER.warn("called setValue before any targets were set up.");
		} else {
			target.setValue(value);
		}
	}

	/**
	 * Sets the target for this Output. The target can only be set once.
	 * 
	 * @param target
	 *            the new target for this Output
	 * @return <code>true</code> if the target was set successfully, <code>false</code> otherwise
	 */
	public synchronized boolean setTarget(final OutputTarget<? super T> target) {
		if (this.target != null) {
			return false;
		}
		this.target = target;
		return true;
	}
}
