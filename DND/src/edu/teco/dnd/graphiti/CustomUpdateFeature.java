package edu.teco.dnd.graphiti;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;

/**
 * This class is responsible for importing changes from the DeployView to the graphiti diagram editor. After reloading a
 * diagram to graphiti, the user can manually update all edited names and positions to the values set in DeployView.
 * 
 * @author jung
 * 
 */
public class CustomUpdateFeature extends AbstractCustomFeature {
	private boolean hasDoneChanges = false;

	public CustomUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement pe = context.getInnerPictogramElement();
		if (pe != null) {
			Object bo = getBusinessObjectForPictogramElement(pe);
			if (bo instanceof FunctionBlockModel && pe.getGraphicsAlgorithm() instanceof Text) {
				Text text = (Text) pe.getGraphicsAlgorithm();
				if (TypePropertyUtil.isBlockNameText(text) || TypePropertyUtil.isPositionText(text)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof FunctionBlockModel) {
				FunctionBlockModel model = (FunctionBlockModel) bo;
				PictogramElement pe = context.getInnerPictogramElement();
				if (needsUpdate(model, pe)) {
					final IUpdateContext updateContext = new UpdateContext(pe);
					IUpdateFeature updateFeature = getFeatureProvider().getUpdateFeature(updateContext);
					updateFeature.update(updateContext);
					this.hasDoneChanges |= updateFeature.hasDoneChanges();
				}
			}
		}
	}

	private static boolean needsUpdate(final FunctionBlockModel model, final PictogramElement pe) {
		assert model != null;
		assert pe != null;

		final GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
		if (ga instanceof Text) {
			final Text text = (Text) ga;
			final String value = text.getValue();
			if (TypePropertyUtil.isBlockNameText(text)) {
				return value != model.getBlockName();
			} else if (TypePropertyUtil.isPositionText(text)) {
				return value != model.getPosition();
			}
		}

		return false;
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

	@Override
	public String getName() {
		return Messages.Graphiti_UPDATE_CUSTOM;
	}

	@Override
	public String getDescription() {
		return Messages.Graphiti_UPDATE_CUSTOM_DESCRIPTION;
	}
}
