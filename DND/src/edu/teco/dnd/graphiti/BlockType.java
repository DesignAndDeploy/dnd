package edu.teco.dnd.graphiti;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify where a block should be shown in the editor.
 * 
 * @author philipp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BlockType {
	/**
	 * The name of the category the block should be in.
	 */
	String value();
}
