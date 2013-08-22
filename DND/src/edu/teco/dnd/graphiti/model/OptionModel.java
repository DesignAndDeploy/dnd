package edu.teco.dnd.graphiti.model;

import org.eclipse.emf.ecore.EObject;

// TODO: regenerate model
/**
 * @model
 */
public interface OptionModel extends EObject {
	/**
	 * @model
	 */
	String getName();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.OptionModel#getName <em>Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * @model
	 */
	String getType();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.OptionModel#getType <em>Type</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

	/**
	 * @model
	 */
	String getValue();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.OptionModel#getValue <em>Value</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(String value);

	/**
	 * @model opposite="options"
	 */
	FunctionBlockModel getFunctionBlock();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.OptionModel#getFunctionBlock <em>Function Block</em>}'
	 * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Function Block</em>' container reference.
	 * @see #getFunctionBlock()
	 * @generated
	 */
	void setFunctionBlock(FunctionBlockModel value);
}
