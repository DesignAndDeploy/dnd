package edu.teco.dnd.graphiti;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

/**
 * Provides a FeatureProvider and a ToolBehaviorProvider.
 */
public class DNDDiagramTypeProvider extends AbstractDiagramTypeProvider {
	/**
	 * Sets the FeatureProvider to {@link DNDFeatureProvider}.
	 * @throws ClassNotFoundException 
	 */
	public DNDDiagramTypeProvider() throws ClassNotFoundException {
		super();
		setFeatureProvider(new DNDFeatureProvider(this));
	}

	/**
	 * Returns {@link DNDToolBehaviorProvider} as a ToolBehaviorProvider.
	 * 
	 * @return {@link DNDToolBehaviorProvider}
	 */
	@Override
	public final IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		return new IToolBehaviorProvider[] { new DNDToolBehaviorProvider(this) };
	}
}
