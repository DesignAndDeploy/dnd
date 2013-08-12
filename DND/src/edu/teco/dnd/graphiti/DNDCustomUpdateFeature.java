package edu.teco.dnd.graphiti;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.impl.RoundedRectangleImpl;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.impl.ContainerShapeImpl;
import org.eclipse.graphiti.mm.pictograms.impl.PictogramLinkImpl;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;

public class DNDCustomUpdateFeature extends AbstractCustomFeature {

	IFeatureProvider featureProv;

	public DNDCustomUpdateFeature(IFeatureProvider fp) {
		super(fp);
		featureProv = fp;
		// TODO Auto-generated constructor stub
	}

	private boolean hasDoneChanges = false;

	@Override
	public String getName() {
		return "Update Block";
	}

	@Override
	public String getDescription() {
		return "Update information (name and position) of this block.";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof FunctionBlockModel) {
				PictogramElement pe = context.getInnerPictogramElement();
				if (pe.getGraphicsAlgorithm() instanceof Text) {
					Text text = (Text) pe.getGraphicsAlgorithm();
					if (TypePropertyUtil.isBlockNameText(text) || TypePropertyUtil.isPositionText(text)){
						ret = true;
					}
				}
			}
		}
		return ret;
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof FunctionBlockModel) {
				FunctionBlockModel model = (FunctionBlockModel) bo;
				String newName = model.getBlockName();
				String newPosition = model.getPosition();
				PictogramElement pe = context.getInnerPictogramElement();
				if (pe.getGraphicsAlgorithm() instanceof Text) {
					Text text = (Text) pe.getGraphicsAlgorithm();
					if (TypePropertyUtil.isBlockNameText(text)
							&& newName != null) {
						DNDUpdateBlockNameFeature updateBlockName = new DNDUpdateBlockNameFeature(
								featureProv);
						updateBlockName.update(context);
						this.hasDoneChanges = true;
					} else if (TypePropertyUtil.isPositionText(text)
							&& newPosition != null) {
						DNDUpdatePositionFeature updatePosition = new DNDUpdatePositionFeature(
								featureProv);
						updatePosition.update(context);
						this.hasDoneChanges = true;
					}
				}
			}
		}
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;

	}

}
