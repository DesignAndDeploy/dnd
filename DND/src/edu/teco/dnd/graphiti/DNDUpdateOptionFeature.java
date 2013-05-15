package edu.teco.dnd.graphiti;

import edu.teco.dnd.graphiti.model.OptionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * This class is used to update options if they are changed in the underlying model.
 * 
 * @author philipp
 */
public class DNDUpdateOptionFeature extends AbstractUpdateFeature {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DNDUpdateOptionFeature.class);

	/**
	 * Initializes a new DNDUpdateOptionFeature.
	 * 
	 * @param fp
	 *            the FeatureProvider
	 */
	public DNDUpdateOptionFeature(final IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(final IUpdateContext context) {
		LOGGER.entry(context);
		PictogramElement pe = context.getPictogramElement();
		GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (ga instanceof Text && bo instanceof OptionModel) {
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
		OptionModel option = (OptionModel) getBusinessObjectForPictogramElement(pe);
		String value = "" + option.getValue();
		String current = text.getValue();
		IReason reason = null;
		if (value.equals(current)) {
			reason = Reason.createFalseReason();
		} else {
			reason = Reason.createTrueReason("value is out of date");
		}
		LOGGER.exit(reason);
		return reason;
	}

	@Override
	public boolean update(final IUpdateContext context) {
		LOGGER.entry(context);
		PictogramElement pe = context.getPictogramElement();
		OptionModel option = (OptionModel) getBusinessObjectForPictogramElement(pe);
		Text text = (Text) pe.getGraphicsAlgorithm();
		text.setValue("" + option.getValue());
		LOGGER.exit(true);
		return true;
	}
}
