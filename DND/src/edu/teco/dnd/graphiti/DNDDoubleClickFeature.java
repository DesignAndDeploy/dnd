package edu.teco.dnd.graphiti;

import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.impl.DirectEditingContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.internal.parts.IAnchorContainerDelegate;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;

/**
 * Feature to handle double clicks in Graphiti. Only handles double clicks on the Name and the Place Text Field. Double
 * clicks at other locations are ignored.
 * 
 * @author jung
 * 
 */
public class DNDDoubleClickFeature extends AbstractCustomFeature {

	public DNDDoubleClickFeature(IFeatureProvider fp) {
		super(fp);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		if (context instanceof IDoubleClickContext) {
			IDoubleClickContext dcon = (IDoubleClickContext) context;
			PictogramElement pe = dcon.getInnerPictogramElement();
			if (pe != null && getBusinessObjectForPictogramElement(pe) instanceof FunctionBlockModel
					&& pe.getGraphicsAlgorithm() instanceof Text) {
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
		if (context instanceof IDoubleClickContext) {
			IDoubleClickContext dcon = (IDoubleClickContext) context;
			PictogramElement pe = dcon.getInnerPictogramElement();
			if (pe != null) {
				GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
				if (getBusinessObjectForPictogramElement(pe) instanceof FunctionBlockModel && ga instanceof Text) {
					Text text = (Text) pe.getGraphicsAlgorithm();
					if (TypePropertyUtil.isBlockNameText(text)) {
						//TODO: Open text field for block name.
					} else if (TypePropertyUtil.isPositionText(text)) {
						//And for location
					}
				}
			}
		}
	}
}
