/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package edu.teco.dnd.graphiti.model.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.InputModel;
import edu.teco.dnd.graphiti.model.ModelPackage;
import edu.teco.dnd.graphiti.model.OptionModel;
import edu.teco.dnd.graphiti.model.OutputModel;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 * 
 * @see edu.teco.dnd.graphiti.model.ModelPackage
 * @generated
 */
public class ModelAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected static ModelPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ModelAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = ModelPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This
	 * implementation returns <code>true</code> if the object is either the model's package or is an instance object of
	 * the model. <!-- end-user-doc -->
	 * 
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject) object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ModelSwitch<Adapter> modelSwitch = new ModelSwitch<Adapter>() {
		@Override
		public Adapter caseFunctionBlockModel(FunctionBlockModel object) {
			return createFunctionBlockModelAdapter();
		}

		@Override
		public Adapter caseInputModel(InputModel object) {
			return createInputModelAdapter();
		}

		@Override
		public Adapter caseOptionModel(OptionModel object) {
			return createOptionModelAdapter();
		}

		@Override
		public Adapter caseOutputModel(OutputModel object) {
			return createOutputModelAdapter();
		}

		@Override
		public Adapter defaultCase(EObject object) {
			return createEObjectAdapter();
		}
	};

	/**
	 * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param target
	 *            the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject) target);
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel
	 * <em>Function Block Model</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
	 * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
	 * end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see edu.teco.dnd.graphiti.model.FunctionBlockModel
	 * @generated
	 */
	public Adapter createFunctionBlockModelAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.teco.dnd.graphiti.model.InputModel <em>Input Model</em>}
	 * '. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's
	 * useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see edu.teco.dnd.graphiti.model.InputModel
	 * @generated
	 */
	public Adapter createInputModelAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.teco.dnd.graphiti.model.OptionModel
	 * <em>Option Model</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see edu.teco.dnd.graphiti.model.OptionModel
	 * @generated
	 */
	public Adapter createOptionModelAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.teco.dnd.graphiti.model.OutputModel
	 * <em>Output Model</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see edu.teco.dnd.graphiti.model.OutputModel
	 * @generated
	 */
	public Adapter createOutputModelAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null.
	 * <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} // ModelAdapterFactory
