package edu.teco.dnd.graphiti;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

/**
 * Main class of a Graphiti diagram. Basically it just tells Graphiti which FeatureProvider and ToolBehaviorProviders to
 * use.
 * 
 * @see FeatureProvider
 * @see ToolBehaviorProvider
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
