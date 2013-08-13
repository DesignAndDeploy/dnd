package edu.teco.dnd.blocks;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An Option of a {@link FunctionBlock} can be set before the FunctionBlock is deployed. It will not change while the
 * FunctionBlock is running.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Option {
}
