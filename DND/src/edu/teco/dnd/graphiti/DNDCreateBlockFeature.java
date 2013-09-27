package edu.teco.dnd.graphiti;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
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

		Resource resource = ((DNDFeatureProvider) getFeatureProvider()).getEMFResource();
		resource.getContents().add(newBlock);

		addGraphicalRepresentation(context, newBlock);

		/**
		 * Links the block to the diagram. Found this on the Internet, not really sure what it does. Still not done with
		 * this part.
		 */
		Diagram diagram = getDiagram();
		TransactionalEditingDomain domain = createEditingDomain();
		;
		Assert.isNotNull(diagram.getDiagramTypeId());
		String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
		Assert.isNotNull(providerId);
		domain.getCommandStack().execute(new LinkCoreModelCommand(domain, diagram, newBlock, providerId));

		return new Object[] { newBlock };
	}

	/**
	 * Tries to create a TransactionalEditingDomain. This is a workaround to support both versions of Graphiti &lt;0.9.0
	 * and &gt;=0.9.0.
	 * 
	 * @return a TransactionalEditingDomain or null if creating one failed
	 */
	private static final TransactionalEditingDomain createEditingDomain() {
		final ClassLoader loader = DNDCreateBlockFeature.class.getClassLoader();

		// version for Graphiti <0.9.0. Calls DiagramEditorFactory.createResourceSetAndEditingDomain()
		Class<?> diagramEditorFactoryClass = null;
		try {
			diagramEditorFactoryClass = loader.loadClass("org.eclipse.graphiti.ui.editor.DiagramEditorFactory"); //$NON-NLS-1$
		} catch (final ClassNotFoundException e) {
		}
		if (diagramEditorFactoryClass != null) {
			Method createResourceSetAndEditingDomainMethod = null;
			try {
				createResourceSetAndEditingDomainMethod =
						diagramEditorFactoryClass.getDeclaredMethod("createResourceSetAndEditingDomain"); //$NON-NLS-1$
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			}
			if (createResourceSetAndEditingDomainMethod != null) {
				try {
					return (TransactionalEditingDomain) createResourceSetAndEditingDomainMethod.invoke(null);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				}
			}
		}

		// we only get here if we failed to create an EditingDomain via DiagramEditorFactory
		// version for Graphiti >=0.9.0. Calls GraphitiUiInternal.getEmfService().createResourceSetAndEditingDomain()
		Class<?> graphitiUiInternalClass = null;
		try {
			graphitiUiInternalClass = loader.loadClass("org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal"); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
		}
		if (graphitiUiInternalClass != null) {
			Method getEmfServiceMethod = null;
			try {
				getEmfServiceMethod = graphitiUiInternalClass.getDeclaredMethod("getEmfService"); //$NON-NLS-1$
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			}
			Object emfService = null;
			if (getEmfServiceMethod != null) {
				try {
					emfService = getEmfServiceMethod.invoke(null);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				}
			}
			Method createResourceSetAndEditingDomainMethod = null;
			if (emfService != null) {
				try {
					createResourceSetAndEditingDomainMethod =
							emfService.getClass().getDeclaredMethod("createResourceSetAndEditingDomain"); //$NON-NLS-1$
				} catch (SecurityException e) {
				} catch (NoSuchMethodException e) {
				}
			}
			if (createResourceSetAndEditingDomainMethod != null) {
				try {
					return (TransactionalEditingDomain) createResourceSetAndEditingDomainMethod.invoke(emfService);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				}
			}
		}

		return null;
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
