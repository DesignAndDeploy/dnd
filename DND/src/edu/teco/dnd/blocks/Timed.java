package edu.teco.dnd.blocks;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify an interval at which a {@link FunctionBlock} should be updated regardless of changes to the inputs.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Timed {
	/**
	 * The number of milliseconds between two updates. No updates will happen if this is 0 or less.
	 */
	long value();
}
