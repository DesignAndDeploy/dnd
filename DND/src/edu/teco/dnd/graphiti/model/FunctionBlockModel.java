package edu.teco.dnd.graphiti.model;

import java.util.UUID;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.InvalidFunctionBlockException;
import edu.teco.dnd.module.RemoteConnectionTarget;

/**
 * @model
 */
public interface FunctionBlockModel extends EObject {
	/**
	 * @model
	 */
	String getType();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

	/**
	 * @model containment="true" opposite="functionBlock"
	 */
	EList getInputs();

	/**
	 * @model containment="true" opposite="functionBlock"
	 */
	EList getOutputs();

	/**
	 * @model containment="true" opposite="functionBlock"
	 */
	EList getOptions();

	/**
	 * @model
	 */
	UUID getID();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getID <em>ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>ID</em>' attribute.
	 * @see #getID()
	 * @generated
	 */
	void setID(UUID value);

	/**
	 * @model
	 */
	String getPosition();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getPosition <em>Position</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Position</em>' attribute.
	 * @see #getPosition()
	 * @generated
	 */
	void setPosition(String value);
	
	/**
	 * @model
	 */
	String getBlockName();

	/**
	 * Sets the value of the '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getBlockName <em>BlockNam</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>BlockName</em>' attribute.
	 * @see #getBlockName()
	 * @generated
	 */
	void setBlockName(String value);

	/**
	 * Whether or not this FunctionBlock is a sensor. A FunctionBlock is a sensor if it has no inputs and at
	 * least one output.
	 * 
	 * @return true if this FunctionBlock is a sensor
	 */
	boolean isSensor();

	/**
	 * Whether or not this FunctionBlock is an actor. A FunctionBlock is a sensor if it has no outputs and at
	 * least one input.
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

	/**
	 * Creates a FunctionBlock matching this model. It is of the type set in the model, the options are set
	 * and RemoteConnectionTargets are created for all connected inputs. The default class loader for
	 * this class is used.
	 * 
	 * @return the created block
	 * @throws InvalidFunctionBlockException
	 *             if loading the block fails or setting the options/outputs fails
	 */
	FunctionBlock createBlock() throws InvalidFunctionBlockException;

	/**
	 * Creates a FunctionBlock matching this model. It is of the type set in the model, the options are set
	 * and {@link RemoteConnectionTarget}s are created for all connected inputs.
	 * 
	 * @param cl
	 *            the classloader to use
	 * @return the created block
	 * @throws InvalidFunctionBlockException
	 *             if loading the block fails or setting the options/outputs fails
	 */
	FunctionBlock createBlock(ClassLoader cl) throws InvalidFunctionBlockException;
}
