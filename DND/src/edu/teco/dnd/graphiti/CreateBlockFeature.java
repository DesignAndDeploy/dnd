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
 * This feature is used to create new FunctionBlocks.
 */
public class CreateBlockFeature extends AbstractCreateFeature {
	/**
	 * The type of blocks created by this feature.
	 */
	private final FunctionBlockClass blockClass;

	/**
	 * Initializes a new create feature.
	 * 
	 * @param fp
	 *            the feature provider
	 * @param blockClass
	 *            the type of FunctionBlocks to create
	 */
	public CreateBlockFeature(final IFeatureProvider fp, final FunctionBlockClass blockClass) {
		super(fp, blockClass == null ? "null" : blockClass.getSimplifiedClassName(), Messages.Graphiti_createBlock_CREATE_DESCRIPTION
				+ (blockClass == null ? "null" : blockClass.getClassName())); //$NON-NLS-1$
		
		this.blockClass = blockClass;
	}

	/**
	 * Whether or not the create feature can be used in the given context.
	 * 
	 * @param context
	 *            the context
	 * @return whether or not the feature can be used
	 */
	@Override
	public final boolean canCreate(final ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	/**
	 * Creates a new FunctionBlock.
	 * 
	 * @param context
	 *            the context
	 * @return a new FunctionBlock
	 */
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
