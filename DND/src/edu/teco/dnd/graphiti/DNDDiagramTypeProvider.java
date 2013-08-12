package edu.teco.dnd.graphiti;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

/**
 * Provides a FeatureProvider and a ToolBehaviorProvider.
 */
public class DNDDiagramTypeProvider extends AbstractDiagramTypeProvider {
	private static final Logger LOGGER = LogManager.getLogger(DNDDiagramTypeProvider.class);
	
	/**
	 * Sets the FeatureProvider to {@link DNDFeatureProvider}.
	 * @throws ClassNotFoundException 
	 */
	public DNDDiagramTypeProvider() {
		super();
		try {
			setFeatureProvider(new DNDFeatureProvider(this));
		} catch (final ClassNotFoundException e) {
			LOGGER.catching(e);
		}
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
