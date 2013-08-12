package edu.teco.dnd.graphiti;

import java.lang.reflect.Modifier;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.FunctionBlockClass;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.impl.ModelFactoryImpl;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditorFactory;
import org.eclipse.graphiti.ui.services.GraphitiUi;

/**
 * This feature is used to create new FunctionBlocks.
 */
public class DNDCreateBlockFeature extends AbstractCreateFeature {
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
	public DNDCreateBlockFeature(final IFeatureProvider fp, final FunctionBlockClass blockClass) {
		super(fp, blockClass == null ? "null" : blockClass.getSimpleClassName(),
				Messages.DNDCreateBlockFeature_CreatesFunBlockOfTpe_Info
						+ (blockClass == null ? "null" : blockClass
								.getSimpleClassName()));
		if (blockClass == null) {
			throw new IllegalArgumentException("blockClass must not be null");
		}
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
		FunctionBlockModel newBlock = ModelFactoryImpl.eINSTANCE
				.createFunctionBlockModel(blockClass);
		getDiagram().eResource().getContents().add(newBlock);

		addGraphicalRepresentation(context, newBlock);

		/**
		 * Links the block to the diagram. Found this on the Internet, not
		 * really sure what it does. Still not done with this part.
		 */
		Diagram diagram = getDiagram();
		TransactionalEditingDomain domain = DiagramEditorFactory
				.createResourceSetAndEditingDomain();
		;
		Assert.isNotNull(diagram.getDiagramTypeId());
		String providerId = GraphitiUi.getExtensionManager()
				.getDiagramTypeProviderId(diagram.getDiagramTypeId());
		Assert.isNotNull(providerId);
		domain.getCommandStack()
				.execute(
						new LinkCoreModelCommand(domain, diagram, newBlock,
								providerId));

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
