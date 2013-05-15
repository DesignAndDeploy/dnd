package edu.teco.dnd.blocks;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field of a {@link FunctionBlock} as an input.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Input {
	/**
	 * If set to true all values will be passed to the FunctionBlock. If set to false values will be discarded
	 * if newer ones arrive before the block is updated.
	 */
	boolean value() default false;

	/**
	 * If set to true only a change in a value will cause an update.
	 */
	boolean newOnly() default false;
}
