package edu.teco.dnd.graphiti;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;


public class DNDUpdateBlockFeature extends AbstractUpdateFeature {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DNDUpdateBlockNameFeature.class);

	/**
	 * Initializes a new DNDUpdateOptionFeature.
	 * 
	 * @param fp
	 *            the FeatureProvider
	 */
	public DNDUpdateBlockFeature(final IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public boolean canUpdate(IUpdateContext context) {
		LOGGER.entry(context);
		PictogramElement pe = context.getPictogramElement();
		GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
		Object bo = getBusinessObjectForPictogramElement(pe);
		LOGGER.debug("ga: {}, bo: {}", ga, bo);
		if (bo instanceof FunctionBlockModel) {
			LOGGER.exit(true);
			return true;
		}
		LOGGER.exit(false);
		return false;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		LOGGER.entry(context);
		PictogramElement pe = context.getPictogramElement();
		IFeatureProvider provider = getFeatureProvider();
		ContainerShape shape = (ContainerShape) pe;
		IReason reason = null;
		for (Shape child : shape.getChildren()){
			UpdateContext childContext = new UpdateContext(child);
			IUpdateFeature feature = provider.getUpdateFeature(childContext);
			if (feature != null){
				reason = feature.updateNeeded(childContext);
				if (reason.toBoolean()){
					reason = Reason.createTrueReason("Update needed for at least one block value");
					return reason;
				}
			}
		}
		reason = Reason.createFalseReason();
		LOGGER.exit(reason);
		return reason;
	}

	@Override
	public boolean update(IUpdateContext context) {
		
		LOGGER.entry(context);
		PictogramElement pe = context.getPictogramElement();
		IFeatureProvider provider = getFeatureProvider();
		ContainerShape shape = (ContainerShape) pe;
		IReason reason = null;
		for (Shape child : shape.getChildren()){
			UpdateContext childContext = new UpdateContext(child);
			IUpdateFeature feature = provider.getUpdateFeature(childContext);
			if (feature != null){
				reason = feature.updateNeeded(childContext);
				if (reason.toBoolean()){
					feature.update(childContext);
				}
			}
		}
		LOGGER.exit(true);
		return true;
	}

}
