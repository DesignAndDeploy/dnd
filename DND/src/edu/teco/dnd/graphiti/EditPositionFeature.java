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
 * Direct editing feature for a {@link FunctionBlockModel}s {@link FunctionBlockModel#getPosition() position}.
 */
public class EditPositionFeature extends AbstractDirectEditingFeature {
	private static final Logger LOGGER = LogManager.getLogger(EditPositionFeature.class);

	public EditPositionFeature(final IFeatureProvider fp) {
		super(fp);
	}

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

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public String getInitialValue(final IDirectEditingContext context) {
		LOGGER.entry(context);
		FunctionBlockModel block =
				(FunctionBlockModel) getBusinessObjectForPictogramElement(context.getPictogramElement());
		String value = block.getPosition();
		LOGGER.exit(value);
		return value;
	}

	@Override
	public String checkValueValid(final String value, final IDirectEditingContext context) {
		try {
			Pattern.compile(value);
		} catch (PatternSyntaxException e) {
			return Messages.Graphiti_NOT_A_REGEX;
		}
		return null;
	}

	@Override
	public void setValue(final String value, final IDirectEditingContext context) {
		LOGGER.entry(value, context);
		PictogramElement pe = context.getPictogramElement();
		FunctionBlockModel block =
				(FunctionBlockModel) getBusinessObjectForPictogramElement(context.getPictogramElement());
		block.setPosition(value);
		updatePictogramElement(pe);

		FeatureProvider provider = (FeatureProvider) getFeatureProvider();
		provider.updateEMFResourcePosition(block.getID(), value);

		LOGGER.exit();
	}
}
