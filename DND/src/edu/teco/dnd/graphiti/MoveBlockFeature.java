package edu.teco.dnd.graphiti;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;

/**
 * Controls if a move is valid.
 */
public class MoveBlockFeature extends DefaultMoveShapeFeature {
	/**
	 * Passes the feature provider to the super constructor.
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public MoveBlockFeature(final IFeatureProvider fp) {
		super(fp);
	}

	/**
	 * Whether or not a move is valid.
	 * 
	 * @param context
	 *            the context
	 * @return whether or not the move is valid
	 */
	@Override
	public boolean canMoveShape(final IMoveShapeContext context) {
		return false;
	}
}
