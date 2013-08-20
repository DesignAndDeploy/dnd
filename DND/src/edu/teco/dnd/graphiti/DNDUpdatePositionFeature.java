package edu.teco.dnd.graphiti;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;

/**
 * This class is used to update options if they are changed in the underlying model.
 * 
 * @author philipp
 */
public class DNDUpdatePositionFeature extends AbstractUpdateFeature {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DNDUpdatePositionFeature.class);

	/**
	 * Initializes a new DNDUpdateOptionFeature.
	 * 
	 * @param fp
	 *            the FeatureProvider
	 */
	public DNDUpdatePositionFeature(final IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(final IUpdateContext context) {
		LOGGER.entry(context);
		PictogramElement pe = context.getPictogramElement();
		GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (TypePropertyUtil.isPositionText(ga) && bo instanceof FunctionBlockModel) {
			LOGGER.exit(true);
			return true;
		}
		LOGGER.exit(false);
		return false;
	}

	@Override
	public IReason updateNeeded(final IUpdateContext context) {
		LOGGER.entry(context);
		PictogramElement pe = context.getPictogramElement();
		Text text = (Text) pe.getGraphicsAlgorithm();
		FunctionBlockModel block = (FunctionBlockModel) getBusinessObjectForPictogramElement(pe);
		String value = block.getPosition();
		if (value == null) {
			value = "";
		}
		String current = text.getValue();
		IReason reason = null;
		if (value.equals(current)) {
			reason = Reason.createFalseReason();
		} else {
			reason = Reason.createTrueReason("position is out of date");
		}
		LOGGER.exit(reason);
		return reason;
	}

	@Override
	public boolean update(final IUpdateContext context) {
		LOGGER.entry(context);
		PictogramElement pe = context.getPictogramElement();
		FunctionBlockModel block = (FunctionBlockModel) getBusinessObjectForPictogramElement(pe);
		Text text = (Text) pe.getGraphicsAlgorithm();
		String value = block.getPosition();
		if (value == null) {
			value = "";
		}
		text.setValue(value);
		LOGGER.exit(true);
		return true;
	}

	/**
	 * This update-method is called by the DNDCustomUpdateFeature. It offers an alternative to the not-working update
	 * button, that should appear in graphiti by default.
	 * 
	 * @param context
	 *            Context offered by the DNDCustomUpdateFeature
	 * @return if completed successfully.
	 */
	protected boolean update(final ICustomContext context) {
		LOGGER.entry(context);
		PictogramElement pe = context.getInnerPictogramElement();
		FunctionBlockModel block = (FunctionBlockModel) getBusinessObjectForPictogramElement(pe);
		Text text = (Text) pe.getGraphicsAlgorithm();
		String value = block.getPosition();
		if (value == null) {
			value = "";
		}
		text.setValue(value);
		LOGGER.exit(true);
		return true;
	}
}
