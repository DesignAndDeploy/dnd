package edu.teco.dnd.graphiti;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import edu.teco.dnd.blocks.FunctionBlock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.features.IFeatureProvider;

/**
 * A factory for function block create features.
 */
public final class DNDCreateFeatureFactory {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DNDCreateFeatureFactory.class);

	/**
	 * Registered block types.
	 */
	private Set<Class<? extends FunctionBlock>> types = new HashSet<>();

	/**
	 * Returns a create feature for the given block type. If the block has not been registered it will
	 * automatically add it to the list of known blocks.
	 * 
	 * @param fp
	 *            the IFeatureProvider to use
	 * @param type
	 *            the type of the FunctionBlock. Must not be null or an abstract class.
	 * @return a create feature for the FunctionBlock
	 */
	public DNDCreateBlockFeature getCreateFeature(final IFeatureProvider fp,
			final Class<? extends FunctionBlock> type) {
		if (!types.contains(type) && (type == null || Modifier.isAbstract(type.getModifiers()))) {
			LOGGER.warn("{} was passed to getCreateFeature", type);
			throw new IllegalArgumentException("type is invalid");
		}
		if (LOGGER.isDebugEnabled() && !types.contains(type)) {
			LOGGER.debug("adding type {}", type);
		}
		types.add(type);
		return new DNDCreateBlockFeature(fp, type);
	}

	/**
	 * Create features for all registered block types.
	 * 
	 * @param fp
	 *            the IFeatureProvider to use
	 * @return all create features
	 */
	public Set<DNDCreateBlockFeature> getCreateFeatures(final IFeatureProvider fp) {
		Set<DNDCreateBlockFeature> features = new HashSet<>(types.size());
		for (Class<? extends FunctionBlock> type : types) {
			features.add(new DNDCreateBlockFeature(fp, type));
		}
		return features;
	}

	/**
	 * Registers a new block type.
	 * 
	 * @param type
	 *            the new block type. If null or abstract, nothing is done.
	 */
	public void registerBlockType(final Class<? extends FunctionBlock> type) {
		if (type == null || Modifier.isAbstract(type.getModifiers())) {
			LOGGER.warn("tried to register invalid type");
			return;
		}
		if (LOGGER.isDebugEnabled() && !types.contains(type)) {
			LOGGER.debug("adding type {}", type);
		}
		types.add(type);
	}
}
