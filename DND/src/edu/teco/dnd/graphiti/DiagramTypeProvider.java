package edu.teco.dnd.graphiti;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

/**
 * Provides a FeatureProvider and a ToolBehaviorProvider.
 */
public class DiagramTypeProvider extends AbstractDiagramTypeProvider {
	private static final Logger LOGGER = LogManager.getLogger(DiagramTypeProvider.class);

	/**
	 * Sets the FeatureProvider to {@link FeatureProvider}.
	 * 
	 * @throws ClassNotFoundException
	 */
	public DiagramTypeProvider() {
		super();
		try {
			setFeatureProvider(new FeatureProvider(this));
		} catch (final ClassNotFoundException e) {
			LOGGER.catching(e);
		}
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
