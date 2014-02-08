package edu.teco.dnd.graphiti;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.features.IFeatureProvider;

import edu.teco.dnd.blocks.FunctionBlockClass;

/**
 * A factory for function block create features.
 */
public final class CreateFeatureFactory {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(CreateFeatureFactory.class);

	/**
	 * Registered block types.
	 */
	private Set<FunctionBlockClass> blockClasses = new HashSet<FunctionBlockClass>();

	/**
	 * Returns a create feature for the given block class. If the block has not been registered it will automatically
	 * add it to the list of known blocks.
	 * 
	 * @param fp
	 *            the IFeatureProvider to use
	 * @param blockClass
	 *            the class of the FunctionBlock. Must not be null.
	 * @return a create feature for the FunctionBlock
	 */
	public CreateBlockFeature getCreateFeature(final IFeatureProvider fp, final FunctionBlockClass blockClass) {
		if (blockClass == null) {
			LOGGER.warn("{} was passed to getCreateFeature", blockClass);
			throw new IllegalArgumentException("type is invalid");
		}
		if (LOGGER.isDebugEnabled() && !blockClasses.contains(blockClass)) {
			LOGGER.debug("adding type {}", blockClass);
		}
		blockClasses.add(blockClass);
		return new CreateBlockFeature(fp, blockClass);
	}

	/**
	 * Create features for all registered block types.
	 * 
	 * @param fp
	 *            the IFeatureProvider to use
	 * @return all create features
	 */
	public Set<CreateBlockFeature> getCreateFeatures(final IFeatureProvider fp) {
		Set<CreateBlockFeature> features = new HashSet<CreateBlockFeature>(blockClasses.size());
		for (final FunctionBlockClass blockClass : blockClasses) {
			features.add(new CreateBlockFeature(fp, blockClass));
		}
		return features;
	}

	/**
	 * Registers a new block class.
	 * 
	 * @param blockClass
	 *            the new block class. If null or abstract, nothing is done.
	 */
	public void registerBlockType(final FunctionBlockClass blockClass) {
		if (blockClass == null) {
			LOGGER.warn("tried to register invalid type");
			return;
		}
		if (LOGGER.isDebugEnabled() && !blockClasses.contains(blockClass)) {
			LOGGER.debug("adding type {}", blockClass);
		}
		blockClasses.add(blockClass);
	}
}
