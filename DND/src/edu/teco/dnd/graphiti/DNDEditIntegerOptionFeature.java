package edu.teco.dnd.graphiti;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.graphiti.model.OptionModel;

/**
 * Direct editing feature for {@link Option}s.
 */
public class DNDEditIntegerOptionFeature extends AbstractDirectEditingFeature {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DNDEditIntegerOptionFeature.class);

	/**
	 * Passes the feature provider to the super constructor.
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public DNDEditIntegerOptionFeature(final DNDFeatureProvider fp) {
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
		if (!(bo instanceof OptionModel)) {
			LOGGER.exit(false);
			return false;
		}
		OptionModel option = (OptionModel) bo;
		Class<?> type = null;
		try {
			type = ((DNDFeatureProvider) getFeatureProvider()).getClassLoader().loadClass(option.getType());
		} catch (ClassNotFoundException e) {
		}
		if (type == null || !type.isAssignableFrom(Integer.class)) {
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
		OptionModel option = (OptionModel) getBusinessObjectForPictogramElement(context.getPictogramElement());
		String value = "" + option.getValue();
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
			Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return Messages.DNDEditIntegerOptionFeature_NotANumber_Info;
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
		OptionModel option = (OptionModel) getBusinessObjectForPictogramElement(context.getPictogramElement());
		int intVal;
		try {
			intVal = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			LOGGER.warn("not a number in setValue: {}", value);
			LOGGER.exit();
			return;
		}
		option.setValue(intVal);
		updatePictogramElement(pe);
		LOGGER.exit();
	}
}
