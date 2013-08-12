package edu.teco.dnd.graphiti;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.FunctionBlockFactory;
import edu.teco.dnd.eclipse.EclipseUtil;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.OptionModel;
import edu.teco.dnd.meeting.BeamerActorBlock;
import edu.teco.dnd.meeting.BeamerOperatorBlock;
import edu.teco.dnd.meeting.DisplayActorBlock;
import edu.teco.dnd.meeting.DisplayOperatorBlock;
import edu.teco.dnd.meeting.LightSensorBlock;
import edu.teco.dnd.meeting.MeetingOperatorBlock;
import edu.teco.dnd.meeting.OutletActorBlock;
import edu.teco.dnd.meeting.OutletSensorBlock;
import edu.teco.dnd.temperature.TemperatureActorBlock;
import edu.teco.dnd.temperature.TemperatureLogicBlock;
import edu.teco.dnd.temperature.TemperatureSensorBlock;
import edu.teco.dnd.util.ClassScanner;
import edu.teco.dnd.util.StringUtil;

import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

/**
 * Provides the features that are used by the editor.
 */
public class DNDFeatureProvider extends DefaultFeatureProvider {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DNDFeatureProvider.class);

	/**
	 * Default FunctionBlocks.
	 */
	private static final Class<?>[] DEFAULT_TYPES = new Class<?>[] { OutletActorBlock.class,
			BeamerOperatorBlock.class, DisplayActorBlock.class, DisplayOperatorBlock.class,
			LightSensorBlock.class, MeetingOperatorBlock.class, OutletSensorBlock.class,
			TemperatureActorBlock.class, TemperatureLogicBlock.class, TemperatureSensorBlock.class,
			BeamerActorBlock.class };

	/**
	 * Feature factory for block create features.
	 */
	private final DNDCreateFeatureFactory createFeatureFactory = new DNDCreateFeatureFactory();
	
	/**
	 * Used to inspect FunctionBlocks.
	 */
	private final FunctionBlockFactory blockFactory;

	/**
	 * Whether or not {@link #createFeatureFactory} was initialised.
	 */
	private boolean factoryInitialised = false;

	/**
	 * Passes the {@link IDiagramTypeProvider} to the super constructor.
	 * 
	 * @param dtp
	 *            the diagram type provider this feature provider belongs to
	 * @throws ClassNotFoundException 
	 */
	public DNDFeatureProvider(final IDiagramTypeProvider dtp) throws ClassNotFoundException {
		super(dtp);
		LOGGER.info("DNDFeatureProvider created successfully");
		blockFactory = new FunctionBlockFactory(StringUtil.joinIterable(getProjectClassPath(), ":"));
		registerDefaultTypes();
	}
	
	/**
	 * Returns the class path for the Eclipse project this diagram is part of.
	 * 
	 * @return the class path for the enclosing Eclipse project
	 */
	private Set<IPath> getProjectClassPath() {
		IProject project = EclipseUtil.getWorkspaceProject(URI.createURI(EcoreUtil.getURI(
				getDiagramTypeProvider().getDiagram()).toString()));
		Set<URL> urls = new HashSet<URL>();
		return EclipseUtil.getAbsoluteBinPaths(project);
	}

	/**
	 * Registers default create features at the factory.
	 */
	@SuppressWarnings("unchecked")
	private void registerDefaultTypes() {
		final FunctionBlockFactory factory;
		try {
			factory = new FunctionBlockFactory(SyntheticRepository.getInstance());
		} catch (final ClassNotFoundException e) {
			LOGGER.catching(Level.WARN, e);
			return;
		}
		for (Class<?> type : DEFAULT_TYPES) {
			try {
				createFeatureFactory.registerBlockType(factory.getFunctionBlockClass(type));
			} catch (final ClassNotFoundException e) {
				LOGGER.catching(Level.WARN, e);
			}
		}
	}

	/**
	 * Returns the CreateFeatures for all loaded blocks.
	 * 
	 * @return CreateFeatures for all loaded blocks
	 */
	public final synchronized ICreateFeature[] getCreateFeatures() {
		initialiseFactory();
		return createFeatureFactory.getCreateFeatures(this).toArray(new ICreateFeature[0]);
	}

	/**
	 * Used to initialize a Factory.
	 */
	@SuppressWarnings("unchecked")
	private synchronized void initialiseFactory() {
		if (!factoryInitialised) {
			// TODO: register block types
			factoryInitialised = true;
		}
	}

	/**
	 * Returns the ClassLoader.
	 * 
	 * @return class loader
	 */
	public final synchronized ClassLoader getClassLoader() {
		initialiseFactory();
		return ucl;
	}

	/**
	 * Returns the AddFeatures for FunctionBlocks and data connections.
	 * 
	 * @param context
	 *            the context for which to return a feature
	 * @return a matching AddFeature
	 */
	@Override
	public final IAddFeature getAddFeature(final IAddContext context) {
		LOGGER.entry(context);
		if (context instanceof IAddConnectionContext) {
			return new DNDAddDataConnectionFeature(this);
		} else if (context.getNewObject() instanceof FunctionBlockModel) {
			return new DNDAddBlockFeature(this);
		}
		return super.getAddFeature(context);
	}

	/**
	 * Returns the LayoutFeature for FunctionBlocks.
	 * 
	 * @param context
	 *            the context for which to return a feature
	 * @return the LayoutFeature for FunctionBlocks
	 */
	@Override
	public final ILayoutFeature getLayoutFeature(final ILayoutContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof FunctionBlockModel) {
			return new DNDLayoutBlockFeature(this);
		}
		return super.getLayoutFeature(context);
	}

	/**
	 * Returns the UpdateFeature for FunctionBlocks.
	 * 
	 * @param context
	 *            the context for which to return a feature
	 * @return the UpdateFeature for FunctionBlocks
	 */
	@Override
	public final IUpdateFeature getUpdateFeature(final IUpdateContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof OptionModel) {
			return new DNDUpdateOptionFeature(this);
		} else if (bo instanceof FunctionBlockModel) {
			GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
			if (TypePropertyUtil.isPositionText(ga)) {
				return new DNDUpdatePositionFeature(this);
			}
			else if (TypePropertyUtil.isBlockNameText(ga)){
				return new DNDUpdateBlockNameFeature(this);
			}
		}
		return super.getUpdateFeature(context);
	}

	/**
	 * Returns the CreateConnectionFeatures for data connections.
	 * 
	 * @return the CreateConnectionFeatures for data connections
	 */
	@Override
	public final ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] { new DNDCreateDataConnectionFeature(this) };
	}

	/**
	 * Returns a DirectEditFeature for Options and positions.
	 * 
	 * @param context
	 *            the context for which to return a feature
	 * @return a matching DirectEditFeature
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final IDirectEditingFeature getDirectEditingFeature(final IDirectEditingContext context) {
		LOGGER.entry(context);
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		IDirectEditingFeature feature = null;
		if (bo instanceof OptionModel) {
			OptionModel option = (OptionModel) bo;
			Class<? extends Serializable> type = null;
			try {
				type = (Class<? extends Serializable>) getClassLoader().loadClass(option.getType());
			} catch (ClassNotFoundException e) {
				LOGGER.catching(e);
			}
			if (type != null && type.isAssignableFrom(String.class)) {
				feature = new DNDEditStringOptionFeature(this);
			} else if (type != null && type.isAssignableFrom(Integer.class)) {
				feature = new DNDEditIntegerOptionFeature(this);
			}
		} else if (bo instanceof FunctionBlockModel) {
			GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
			if (TypePropertyUtil.isBlockNameText(ga)){
				feature = new DNDEditBlockNameFeature(this);
			}
			else if (TypePropertyUtil.isPositionText(ga)){
				feature = new DNDEditPositionFeature(this);
			}
		}
		if (feature == null) {
			feature = super.getDirectEditingFeature(context);
		}
		LOGGER.exit(feature);
		return feature;
	}

	@Override
	public final IRemoveFeature getRemoveFeature(final IRemoveContext context) {
		if (context.getPictogramElement() instanceof Connection) {
			return new DNDRemoveDataConnectionFeature(this);
		}
		return super.getRemoveFeature(context);
	}

	@Override
	public final ICustomFeature[] getCustomFeatures(final ICustomContext context) {
		return new ICustomFeature[] { new DNDDebugFeature(this) };
	}

	@Override
	public final IFeature[] getDragAndDropFeatures(final IPictogramElementContext context) {
		return getCreateConnectionFeatures();
	}
}
