package edu.teco.dnd.graphiti;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import edu.teco.dnd.graphiti.model.OptionModel;

/**
 * Direct editing feature for {@link Option}s.
 */
public class EditOptionFeature extends AbstractDirectEditingFeature {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(EditOptionFeature.class);

	/**
	 * Passes the feature provider to the super constructor.
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public EditOptionFeature(final FeatureProvider fp) {
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
		String value = (String) option.getValue();
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
		option.setValue(value);
		updatePictogramElement(pe);
		LOGGER.exit();
	}
}
