package edu.teco.dnd.graphiti;

import edu.teco.dnd.graphiti.model.InputModel;
import edu.teco.dnd.graphiti.model.OutputModel;

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

/**
 * Creates a new data connection.
 */
public class DNDCreateDataConnectionFeature extends AbstractCreateConnectionFeature {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DNDCreateDataConnectionFeature.class);

	/**
	 * Passes the feature provider to the super constructor.
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public DNDCreateDataConnectionFeature(final DNDFeatureProvider fp) {
		super(fp, Messages.DNDCreateDataConnectionFeature_CreatConnection_Short_Info,
				Messages.DNDCreateDataConnectionFeature_CreateConnection_Long_Info);
	}

	/**
	 * Whether or not a connection can be created.
	 * 
	 * @param context
	 *            the context
	 * @return whether or not the connection can be created
	 */
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
			return ((OutputModel) sourceBo).isCompatible(((DNDFeatureProvider) getFeatureProvider()).getClassLoader(),
					(InputModel) targetBo);
		} else if (sourceBo instanceof InputModel && targetBo instanceof OutputModel) {
			return ((InputModel) sourceBo).isCompatible(((DNDFeatureProvider) getFeatureProvider()).getClassLoader(),
					(OutputModel) targetBo);
		}
		return false;
	}

	/**
	 * Whether or not a connection can be started.
	 * 
	 * @param context
	 *            the context
	 * @return whether or not a connection can be started
	 */
	@Override
	public boolean canStartConnection(final ICreateConnectionContext context) {
		if (!(context.getSourceAnchor() instanceof FixPointAnchor)) {
			return false;
		}
		Object bo = getBusinessObjectForPictogramElement(context.getSourceAnchor());
		return bo instanceof InputModel || bo instanceof OutputModel;
	}

	/**
	 * Creates a new data connection.
	 * 
	 * @param context
	 *            the context
	 * @return the new connection
	 */
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
			LOGGER.info("Removing old connection");
			IRemoveContext removeContext = new RemoveContext(targetAnchor.getIncomingConnections().get(0));
			IRemoveFeature removeFeature = new DNDRemoveDataConnectionFeature(getFeatureProvider());
			if (removeFeature.canExecute(removeContext)) {
				removeFeature.execute(removeContext);
			} else {
				LOGGER.warn("could not remove");
			}
		}
		input.setOutput(output);
		AddConnectionContext addContext = new AddConnectionContext(sourceAnchor, targetAnchor);
		return (Connection) getFeatureProvider().addIfPossible(addContext);
	}
}
