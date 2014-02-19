package edu.teco.dnd.graphiti;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.services.GraphitiUi;

import edu.teco.dnd.blocks.FunctionBlockClass;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.impl.ModelFactoryImpl;

/**
 * Creates the model for a new {@link FunctionBlockModel}.
 * 
 * @see AddBlockFeature
 */
public class CreateBlockFeature extends AbstractCreateFeature {
	private final FunctionBlockClass blockClass;

	public CreateBlockFeature(final IFeatureProvider fp, final FunctionBlockClass blockClass) {
		super(fp, blockClass == null ? "null" : blockClass.getSimplifiedClassName(),
				Messages.Graphiti_createBlock_CREATE_DESCRIPTION
						+ (blockClass == null ? "null" : blockClass.getClassName())); //$NON-NLS-1$

		this.blockClass = blockClass;
	}

	@Override
	public final boolean canCreate(final ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public final Object[] create(final ICreateContext context) {
		FunctionBlockModel newBlock;
		try {
			newBlock = ModelFactoryImpl.eINSTANCE.createFunctionBlockModel(blockClass);
		} catch (final ClassNotFoundException e) {
			return null;
		}

		Resource resource = ((FeatureProvider) getFeatureProvider()).getEMFResource();
		resource.getContents().add(newBlock);

		addGraphicalRepresentation(context, newBlock);

		Diagram diagram = getDiagram();
		Assert.isNotNull(diagram.getDiagramTypeId());
		String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
		Assert.isNotNull(providerId);

		return new Object[] { newBlock };
	}

	/**
	 * Returns the block class created by this feature.
	 * 
	 * @return the block class created by this feature
	 */
	public final FunctionBlockClass getBlockClass() {
		return blockClass;
	}
}
