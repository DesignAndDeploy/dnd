package edu.teco.dnd.graphiti.model;

import java.util.UUID;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.deploy.Deploy;
import edu.teco.dnd.graphiti.DiagramTypeProvider;
import edu.teco.dnd.module.Application;

/**
 * Represents all information of a {@link FunctionBlock} needed by the {@link DiagramTypeProvider graph editor} and
 * {@link Deploy}. This is used in the Eclipse plugin to build {@link Application}s from FunctionBlocks.
 * 
 * @model
 */
public interface FunctionBlockModel extends EObject {
	/**
	 * The type of the FunctionBlock. This is used to group FunctionBlocks.
	 * 
	 * @return the type of this FunctionBlock
	 * @model
	 */
	String getType();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

	/**
	 * @model containment="true" opposite="functionBlock"
	 */
	EList<InputModel> getInputs();

	/**
	 * @model containment="true" opposite="functionBlock"
	 */
	EList<OutputModel> getOutputs();

	/**
	 * @model containment="true" opposite="functionBlock"
	 */
	EList<OptionModel> getOptions();

	/**
	 * Returns the <code>UUID</code> identifying the FunctionBlock.
	 * 
	 * @model
	 */
	UUID getID();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getID <em>ID</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>ID</em>' attribute.
	 * @see #getID()
	 * @generated
	 */
	void setID(UUID value);

	/**
	 * @model
	 */
	String getPosition();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getPosition <em>Position</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Position</em>' attribute.
	 * @see #getPosition()
	 * @generated
	 */
	void setPosition(String value);

	/**
	 * @model
	 */
	String getBlockName();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getBlockName <em>Block Name</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Block Name</em>' attribute.
	 * @see #getBlockName()
	 * @generated
	 */
	void setBlockName(String value);

	/**
	 * @model
	 */
	String getBlockClass();

	/**
	 * @generated
	 */
	void setBlockClass(String value);

	/**
	 * Whether or not this FunctionBlock is a sensor. A FunctionBlock is a sensor if it has no inputs and at least one
	 * output.
	 * 
	 * @return true if this FunctionBlock is a sensor
	 */
	boolean isSensor();

	/**
	 * Whether or not this FunctionBlock is an actor. A FunctionBlock is a sensor if it has no outputs and at least one
	 * input.
	 * 
	 * @return true if this FunctionBlock is an actor
	 */
	boolean isActor();

	/**
	 * Returns the name of the type of the FunctionBlock represented by this model object.
	 * 
	 * @return the name of the type of the FunctionBlock
	 */
	String getTypeName();
}
