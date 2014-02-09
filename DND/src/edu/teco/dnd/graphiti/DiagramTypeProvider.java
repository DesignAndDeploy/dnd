package edu.teco.dnd.graphiti;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

/**
 * Provides a FeatureProvider and a ToolBehaviorProvider.
 */
public class DiagramTypeProvider extends AbstractDiagramTypeProvider {
	/**
	 * Sets the FeatureProvider to {@link FeatureProvider}.
	 * 
	 * @throws ClassNotFoundException
	 */
	public DiagramTypeProvider() {
		super();
		setFeatureProvider(new FeatureProvider(this));
	}

	/**
	 * Returns {@link ToolBehaviorProvider} as a ToolBehaviorProvider.
	 * 
	 * @return {@link ToolBehaviorProvider}
	 */
	@Override
	public final IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		return new IToolBehaviorProvider[] { new ToolBehaviorProvider(this) };
	}
}
