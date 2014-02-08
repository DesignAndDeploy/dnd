package edu.teco.dnd.graphiti;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;

/**
 * Feature to handle double clicks in Graphiti. Only handles double clicks on the Name and the Place Text Field. Double
 * clicks at other locations are ignored.
 * 
 * @author jung
 * 
 */
public class DoubleClickFeature extends AbstractCustomFeature {

	public DoubleClickFeature(IFeatureProvider fp) {
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
			PictogramElement pe = context.getInnerPictogramElement();
			if (pe != null) {
				GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
				if (getBusinessObjectForPictogramElement(pe) instanceof FunctionBlockModel && ga instanceof Text) {
					Text text = (Text) ga;
					if (TypePropertyUtil.isBlockNameText(ga) || TypePropertyUtil.isPositionText(text)) {
						IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
						EObject containingObject = pe.eContainer();
						if (containingObject instanceof ContainerShape) {
							directEditingInfo.setMainPictogramElement((ContainerShape) containingObject);
							directEditingInfo.setPictogramElement(pe);
							directEditingInfo.setGraphicsAlgorithm(text);
							directEditingInfo.setActive(true);
							getDiagramEditor().refresh();
						}
					}
				}
			}
		}
	}

}
