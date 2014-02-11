package edu.teco.dnd.graphiti.model;

import org.apache.bcel.util.Repository;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Output;

/**
 * Represents an {@link Output} of a {@link FunctionBlock}.
 * 
 * @model
 */
public interface OutputModel extends EObject {
	/**
	 * @model
	 */
	String getName();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.OutputModel#getName <em>Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * @model opposite="outputs"
	 */
	FunctionBlockModel getFunctionBlock();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.OutputModel#getFunctionBlock <em>Function Block</em>}'
	 * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Function Block</em>' container reference.
	 * @see #getFunctionBlock()
	 * @generated
	 */
	void setFunctionBlock(FunctionBlockModel value);

	/**
	 * @model opposite="output"
	 */
	EList<InputModel> getInputs();

	/**
	 * @model
	 */
	String getType();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.OutputModel#getType <em>Type</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

	/**
	 * Checks if an {@link Input} is compatible with this Output. An Input is compatible if it takes either the type
	 * this Output sends or any supertype of it.
	 * 
	 * @param repository
	 *            the Repository used for inspecting the classes
	 * @param input
	 *            the Input to check
	 * @return true if the input is compatible
	 */
	boolean isCompatible(Repository repository, InputModel input);
}
