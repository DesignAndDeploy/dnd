/**
 */
package edu.teco.dnd.graphiti.model;

import edu.teco.dnd.blocks.FunctionBlock;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract
 * class of the model. <!-- end-user-doc -->
 * @see edu.teco.dnd.graphiti.model.ModelPackage
 * @generated
 */
public interface ModelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	ModelFactory eINSTANCE = edu.teco.dnd.graphiti.model.impl.ModelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Function Block Model</em>'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return a new object of class '<em>Function Block Model</em>'.
	 * @generated
	 */
	FunctionBlockModel createFunctionBlockModel();

	FunctionBlockModel createFunctionBlockModel(Class<? extends FunctionBlock> cls);

	/**
	 * Returns a new object of class '<em>Input Model</em>'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return a new object of class '<em>Input Model</em>'.
	 * @generated
	 */
	InputModel createInputModel();

	/**
	 * Returns a new object of class '<em>Option Model</em>'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return a new object of class '<em>Option Model</em>'.
	 * @generated
	 */
	OptionModel createOptionModel();

	/**
	 * Returns a new object of class '<em>Output Model</em>'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return a new object of class '<em>Output Model</em>'.
	 * @generated
	 */
	OutputModel createOutputModel();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ModelPackage getModelPackage();

} // ModelFactory
