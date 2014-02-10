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
	private static final Logger LOGGER = LogManager.getLogger(EditOptionFeature.class);

	public EditOptionFeature(final FeatureProvider fp) {
		super(fp);
	}

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

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public String getInitialValue(final IDirectEditingContext context) {
		LOGGER.entry(context);
		OptionModel option = (OptionModel) getBusinessObjectForPictogramElement(context.getPictogramElement());
		String value = (String) option.getValue();
		LOGGER.exit(value);
		return value;
	}

	@Override
	public String checkValueValid(final String value, final IDirectEditingContext context) {
		return null;
	}

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
