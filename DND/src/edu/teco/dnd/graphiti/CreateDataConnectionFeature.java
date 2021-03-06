package edu.teco.dnd.graphiti;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;

import edu.teco.dnd.graphiti.model.InputModel;
import edu.teco.dnd.graphiti.model.OutputModel;

/**
 * Creates a connection between an Output and an Input.
 * 
 * @see AddDataConnectionFeature
 */
public class CreateDataConnectionFeature extends AbstractCreateConnectionFeature {
	private static final Logger LOGGER = LogManager.getLogger(CreateDataConnectionFeature.class);

	public CreateDataConnectionFeature(final FeatureProvider fp) {
		super(fp, Messages.Graphiti_CREATE_CONNECTION, Messages.Graphiti_CREATE_CONNECTION_DESCRIPTION);
	}

	@Override
	public boolean canCreate(final ICreateConnectionContext context) {
		Anchor sourceAnchor = context.getSourceAnchor();
		Anchor targetAnchor = context.getTargetAnchor();
		if (!(sourceAnchor instanceof FixPointAnchor) || !(targetAnchor instanceof FixPointAnchor)) {
			return false;
		}
		Object sourceBo = getBusinessObjectForPictogramElement(sourceAnchor);
		Object targetBo = getBusinessObjectForPictogramElement(targetAnchor);
		if (sourceBo instanceof OutputModel && targetBo instanceof InputModel) {
			return ((OutputModel) sourceBo).isCompatible(((FeatureProvider) getFeatureProvider()).getRepository(),
					(InputModel) targetBo);
		} else if (sourceBo instanceof InputModel && targetBo instanceof OutputModel) {
			return ((InputModel) sourceBo).isCompatible(((FeatureProvider) getFeatureProvider()).getRepository(),
					(OutputModel) targetBo);
		}
		return false;
	}

	@Override
	public boolean canStartConnection(final ICreateConnectionContext context) {
		if (!(context.getSourceAnchor() instanceof FixPointAnchor)) {
			return false;
		}
		Object bo = getBusinessObjectForPictogramElement(context.getSourceAnchor());
		return bo instanceof InputModel || bo instanceof OutputModel;
	}

	@Override
	public Connection create(final ICreateConnectionContext context) {
		Anchor sourceAnchor = context.getSourceAnchor();
		Anchor targetAnchor = context.getTargetAnchor();
		if (getBusinessObjectForPictogramElement(sourceAnchor) instanceof InputModel) {
			Anchor temp = sourceAnchor;
			sourceAnchor = targetAnchor;
			targetAnchor = temp;
		}
		OutputModel output = (OutputModel) getBusinessObjectForPictogramElement(sourceAnchor);
		InputModel input = (InputModel) getBusinessObjectForPictogramElement(targetAnchor);
		if (input.getOutput() != null) {
			LOGGER.info("Removing old connection"); //$NON-NLS-1$
			IRemoveContext removeContext = new RemoveContext(targetAnchor.getIncomingConnections().get(0));
			IRemoveFeature removeFeature = new RemoveDataConnectionFeature(getFeatureProvider());
			if (removeFeature.canExecute(removeContext)) {
				removeFeature.execute(removeContext);
			} else {
				LOGGER.warn("could not remove"); //$NON-NLS-1$
			}
		}
		input.setOutput(output);
		AddConnectionContext addContext = new AddConnectionContext(sourceAnchor, targetAnchor);
		return (Connection) getFeatureProvider().addIfPossible(addContext);
	}

	public String getName() {
		return Messages.Graphiti_CREATE_CONNECTION;
	}

	public String getDescription() {
		return Messages.Graphiti_CREATE_CONNECTION_DESCRIPTION;
	}
}
