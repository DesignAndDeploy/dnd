package edu.teco.dnd.graphiti;

import java.lang.reflect.Modifier;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.util.ClassFilter;

/**
 * A class filter that only accepts non-abstract classes that extend FunctionBlock.
 * 
 * @author philipp
 */
public class FunctionBlockFilter implements ClassFilter {
	@Override
	public boolean acceptClass(final Class<?> cls) {
		return cls != null && !Modifier.isAbstract(cls.getModifiers())
				&& FunctionBlock.class.isAssignableFrom(cls);
	}
}
