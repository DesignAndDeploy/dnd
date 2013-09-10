package edu.teco.dnd.graphiti;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;

/**
 * Direct editing feature for {@link Option}s.
 */
public class DNDEditPositionFeature extends AbstractDirectEditingFeature {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DNDEditPositionFeature.class);

	/**
	 * Passes the feature provider to the super constructor.
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public DNDEditPositionFeature(final IFeatureProvider fp) {
		super(fp);
	}

	/**
	 * Whether or not the feature can direct edit a context.
	 * 
	 * @param context
	 *            the context
	 * @return whether or not the feature can be used
	 */
	@Override
	public boolean canDirectEdit(final IDirectEditingContext context) {
		LOGGER.entry(context);
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (!(bo instanceof FunctionBlockModel)) {
			LOGGER.exit(false);
			return false;
		}
		GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
		if (!TypePropertyUtil.isPositionText(ga)) {
			LOGGER.exit(false);
			return false;
		}
		LOGGER.exit(true);
		return true;
	}

	/**
	 * Returns the editing type.
	 * 
	 * @return the editing type
	 */
	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	/**
	 * Returns the initial value.
	 * 
	 * @param context
	 *            the context
	 * @return the initial value
	 */
	@Override
	public String getInitialValue(final IDirectEditingContext context) {
		LOGGER.entry(context);
		FunctionBlockModel block =
				(FunctionBlockModel) getBusinessObjectForPictogramElement(context.getPictogramElement());
		String value = block.getPosition();
		LOGGER.exit(value);
		return value;
	}

	/**
	 * Checks if the given value is valid for the context.
	 * 
	 * @param value
	 *            the value to check
	 * @param context
	 *            the context
	 * @return null if the value is valid or an error message otherwise
	 */
	@Override
	public String checkValueValid(final String value, final IDirectEditingContext context) {
		try {
			Pattern.compile(value);
		} catch (PatternSyntaxException e) {
			return Messages.NotARegex_Info;
		}
		return null;
	}

	/**
	 * Sets the new value.
	 * 
	 * @param value
	 *            the new value
	 * @param context
	 *            the context
	 */
	@Override
	public void setValue(final String value, final IDirectEditingContext context) {
		LOGGER.entry(value, context);
		PictogramElement pe = context.getPictogramElement();
		FunctionBlockModel block =
				(FunctionBlockModel) getBusinessObjectForPictogramElement(context.getPictogramElement());
		block.setPosition(value);
		updatePictogramElement(pe);

		DNDFeatureProvider provider = (DNDFeatureProvider) getFeatureProvider();
		provider.updateEMFResourcePosition(block.getID(), value);
		
		LOGGER.exit();
	}
}
