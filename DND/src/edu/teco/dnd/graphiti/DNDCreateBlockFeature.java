package edu.teco.dnd.graphiti;

import java.lang.reflect.Modifier;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.impl.ModelFactoryImpl;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * This feature is used to create new FunctionBlocks.
 */
public class DNDCreateBlockFeature extends AbstractCreateFeature {
	/**
	 * The type of blocks created by this feature.
	 */
	private final Class<? extends edu.teco.dnd.blocks.FunctionBlock> blockType;

	/**
	 * Initializes a new create feature.
	 * 
	 * @param fp
	 *            the feature provider
	 * @param blockType
	 *            the type of FunctionBlocks to create
	 */
	public DNDCreateBlockFeature(final IFeatureProvider fp,
			final Class<? extends edu.teco.dnd.blocks.FunctionBlock> blockType) {
		super(fp, blockType == null ? "null" : blockType.getSimpleName(),
				Messages.DNDCreateBlockFeature_CreatesFunBlockOfTpe_Info
						+ (blockType == null ? "null" : blockType.getSimpleName()));
		if (blockType == null) {
			throw new IllegalArgumentException("blockType must not be null");
		}
		if (Modifier.isAbstract(blockType.getModifiers())) {
			throw new IllegalArgumentException("blockType must not be abstract");
		}
		this.blockType = blockType;
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
		FunctionBlockModel newBlock = ModelFactoryImpl.eINSTANCE.createFunctionBlockModel(blockType);
		getDiagram().eResource().getContents().add(newBlock);

		addGraphicalRepresentation(context, newBlock);

		return new Object[] { newBlock };
	}

	/**
	 * Returns the block type created by this feature.
	 * 
	 * @return the block type created by this feature
	 */
	public final Class<? extends FunctionBlock> getBlockType() {
		return blockType;
	}
}
