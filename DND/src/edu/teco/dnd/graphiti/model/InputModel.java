package edu.teco.dnd.graphiti.model;

import org.apache.bcel.util.Repository;
import org.eclipse.emf.ecore.EObject;

/**
 * @model
 */
public interface InputModel extends EObject {
	/**
	 * @model
	 */
	String getName();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.InputModel#getName <em>Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * @model opposite="inputs"
	 */
	FunctionBlockModel getFunctionBlock();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.InputModel#getFunctionBlock <em>Function Block</em>}'
	 * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Function Block</em>' container reference.
	 * @see #getFunctionBlock()
	 * @generated
	 */
	void setFunctionBlock(FunctionBlockModel value);

	/**
	 * @model opposite="inputs"
	 */
	OutputModel getOutput();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.InputModel#getOutput <em>Output</em>}' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Output</em>' reference.
	 * @see #getOutput()
	 * @generated
	 */
	void setOutput(OutputModel value);

	/**
	 * @model
	 */
	String getType();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.InputModel#getType <em>Type</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

	boolean isCompatible(Repository repository, OutputModel output);
}
