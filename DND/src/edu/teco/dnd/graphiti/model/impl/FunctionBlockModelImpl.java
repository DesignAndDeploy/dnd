/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package edu.teco.dnd.graphiti.model.impl;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.Repository;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.FunctionBlockClass;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.InvalidFunctionBlockException;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.blocks.RetrievementException;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.InputModel;
import edu.teco.dnd.graphiti.model.ModelPackage;
import edu.teco.dnd.graphiti.model.OptionModel;
import edu.teco.dnd.graphiti.model.OutputModel;
import edu.teco.dnd.module.RemoteConnectionTarget;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Function Block Model</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link edu.teco.dnd.graphiti.model.impl.FunctionBlockModelImpl#getType <em>Type</em>}</li>
 *   <li>{@link edu.teco.dnd.graphiti.model.impl.FunctionBlockModelImpl#getInputs <em>Inputs</em>}</li>
 *   <li>{@link edu.teco.dnd.graphiti.model.impl.FunctionBlockModelImpl#getOutputs <em>Outputs</em>}</li>
 *   <li>{@link edu.teco.dnd.graphiti.model.impl.FunctionBlockModelImpl#getOptions <em>Options</em>}</li>
 *   <li>{@link edu.teco.dnd.graphiti.model.impl.FunctionBlockModelImpl#getID <em>ID</em>}</li>
 *   <li>{@link edu.teco.dnd.graphiti.model.impl.FunctionBlockModelImpl#getPosition <em>Position</em>}</li>
 *   <li>{@link edu.teco.dnd.graphiti.model.impl.FunctionBlockModelImpl#getBlockName <em>Block Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FunctionBlockModelImpl extends EObjectImpl implements FunctionBlockModel {
	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected String type = TYPE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getInputs() <em>Inputs</em>}' containment reference list.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #getInputs()
	 * @generated
	 * @ordered
	 */
	protected EList<InputModel> inputs;

	/**
	 * The cached value of the '{@link #getOutputs() <em>Outputs</em>}' containment reference list.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #getOutputs()
	 * @generated
	 * @ordered
	 */
	protected EList<OutputModel> outputs;

	/**
	 * The cached value of the '{@link #getOptions() <em>Options</em>}' containment reference list.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #getOptions()
	 * @generated
	 * @ordered
	 */
	protected EList<OptionModel> options;

	/**
	 * The default value of the '{@link #getID() <em>ID</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getID()
	 * @generated
	 * @ordered
	 */
	protected static final UUID ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getID() <em>ID</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getID()
	 * @generated
	 * @ordered
	 */
	protected UUID iD = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getPosition() <em>Position</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getPosition()
	 * @generated
	 * @ordered
	 */
	protected static final String POSITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPosition() <em>Position</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getPosition()
	 * @generated
	 * @ordered
	 */
	protected String position = POSITION_EDEFAULT;
	
	/**
	 * The default value of the '{@link #getBlockName() <em>Block Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBlockName()
	 * @generated
	 * @ordered
	 */
	protected static final String BLOCK_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBlockName() <em>Block Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBlockName()
	 * @generated
	 * @ordered
	 */
	protected String blockName = BLOCK_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected FunctionBlockModelImpl() {
		super();
	}

	protected FunctionBlockModelImpl(FunctionBlockClass cls) {
		super();
		setID(UUID.randomUUID());
		if (cls != null) {
			setBlockName(cls.getSimpleClassName());
			setType(cls.getClassName());
			for (final Entry<String, JavaClass> input : cls.getInputs().entrySet()) {
				final InputModel inputModel = ModelFactoryImpl.eINSTANCE.createInputModel();
				inputModel.setFunctionBlock(this);
				inputModel.setName(input.getKey());
				// TODO: remove queued attribute
				inputModel.setQueued(false);
				inputModel.setType(input.getValue().getClassName());
			}
			for (final Entry<String, JavaClass> output : cls.getOutputs().entrySet()) {
				final OutputModel outputModel = ModelFactoryImpl.eINSTANCE.createOutputModel();
				outputModel.setFunctionBlock(this);
				outputModel.setName(output.getKey());
				outputModel.setType(output.getValue().getClassName());
			}
			for (final String option : cls.getOptions()) {
				final OptionModel optionModel = ModelFactoryImpl.eINSTANCE.createOptionModel();
				optionModel.setFunctionBlock(this);
				optionModel.setName(option);
				// TODO: remove type attribute
				optionModel.setType("java.lang.String");
			}
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.FUNCTION_BLOCK_MODEL;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(String newType) {
		String oldType = type;
		type = newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.FUNCTION_BLOCK_MODEL__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList<InputModel> getInputs() {
		if (inputs == null) {
			inputs = new EObjectContainmentWithInverseEList<InputModel>(InputModel.class, this, ModelPackage.FUNCTION_BLOCK_MODEL__INPUTS, ModelPackage.INPUT_MODEL__FUNCTION_BLOCK);
		}
		return inputs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList<OutputModel> getOutputs() {
		if (outputs == null) {
			outputs = new EObjectContainmentWithInverseEList<OutputModel>(OutputModel.class, this, ModelPackage.FUNCTION_BLOCK_MODEL__OUTPUTS, ModelPackage.OUTPUT_MODEL__FUNCTION_BLOCK);
		}
		return outputs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList<OptionModel> getOptions() {
		if (options == null) {
			options = new EObjectContainmentWithInverseEList<OptionModel>(OptionModel.class, this, ModelPackage.FUNCTION_BLOCK_MODEL__OPTIONS, ModelPackage.OPTION_MODEL__FUNCTION_BLOCK);
		}
		return options;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public UUID getID() {
		return iD;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setID(UUID newID) {
		UUID oldID = iD;
		iD = newID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.FUNCTION_BLOCK_MODEL__ID, oldID, iD));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setPosition(String newPosition) {
		String oldPosition = position;
		position = newPosition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.FUNCTION_BLOCK_MODEL__POSITION, oldPosition, position));
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBlockName() {
		return blockName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBlockName(String newBlockName) {
		String oldBlockName = blockName;
		blockName = newBlockName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.FUNCTION_BLOCK_MODEL__BLOCK_NAME, oldBlockName, blockName));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
		@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ModelPackage.FUNCTION_BLOCK_MODEL__INPUTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getInputs()).basicAdd(otherEnd, msgs);
			case ModelPackage.FUNCTION_BLOCK_MODEL__OUTPUTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutputs()).basicAdd(otherEnd, msgs);
			case ModelPackage.FUNCTION_BLOCK_MODEL__OPTIONS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOptions()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ModelPackage.FUNCTION_BLOCK_MODEL__INPUTS:
				return ((InternalEList<?>)getInputs()).basicRemove(otherEnd, msgs);
			case ModelPackage.FUNCTION_BLOCK_MODEL__OUTPUTS:
				return ((InternalEList<?>)getOutputs()).basicRemove(otherEnd, msgs);
			case ModelPackage.FUNCTION_BLOCK_MODEL__OPTIONS:
				return ((InternalEList<?>)getOptions()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ModelPackage.FUNCTION_BLOCK_MODEL__TYPE:
				return getType();
			case ModelPackage.FUNCTION_BLOCK_MODEL__INPUTS:
				return getInputs();
			case ModelPackage.FUNCTION_BLOCK_MODEL__OUTPUTS:
				return getOutputs();
			case ModelPackage.FUNCTION_BLOCK_MODEL__OPTIONS:
				return getOptions();
			case ModelPackage.FUNCTION_BLOCK_MODEL__ID:
				return getID();
			case ModelPackage.FUNCTION_BLOCK_MODEL__POSITION:
				return getPosition();
			case ModelPackage.FUNCTION_BLOCK_MODEL__BLOCK_NAME:
				return getBlockName();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ModelPackage.FUNCTION_BLOCK_MODEL__TYPE:
				setType((String)newValue);
				return;
			case ModelPackage.FUNCTION_BLOCK_MODEL__INPUTS:
				getInputs().clear();
				getInputs().addAll((Collection<? extends InputModel>)newValue);
				return;
			case ModelPackage.FUNCTION_BLOCK_MODEL__OUTPUTS:
				getOutputs().clear();
				getOutputs().addAll((Collection<? extends OutputModel>)newValue);
				return;
			case ModelPackage.FUNCTION_BLOCK_MODEL__OPTIONS:
				getOptions().clear();
				getOptions().addAll((Collection<? extends OptionModel>)newValue);
				return;
			case ModelPackage.FUNCTION_BLOCK_MODEL__ID:
				setID((UUID)newValue);
				return;
			case ModelPackage.FUNCTION_BLOCK_MODEL__POSITION:
				setPosition((String)newValue);
				return;
			case ModelPackage.FUNCTION_BLOCK_MODEL__BLOCK_NAME:
				setBlockName((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ModelPackage.FUNCTION_BLOCK_MODEL__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case ModelPackage.FUNCTION_BLOCK_MODEL__INPUTS:
				getInputs().clear();
				return;
			case ModelPackage.FUNCTION_BLOCK_MODEL__OUTPUTS:
				getOutputs().clear();
				return;
			case ModelPackage.FUNCTION_BLOCK_MODEL__OPTIONS:
				getOptions().clear();
				return;
			case ModelPackage.FUNCTION_BLOCK_MODEL__ID:
				setID(ID_EDEFAULT);
				return;
			case ModelPackage.FUNCTION_BLOCK_MODEL__POSITION:
				setPosition(POSITION_EDEFAULT);
				return;
			case ModelPackage.FUNCTION_BLOCK_MODEL__BLOCK_NAME:
				setBlockName(BLOCK_NAME_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ModelPackage.FUNCTION_BLOCK_MODEL__TYPE:
				return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
			case ModelPackage.FUNCTION_BLOCK_MODEL__INPUTS:
				return inputs != null && !inputs.isEmpty();
			case ModelPackage.FUNCTION_BLOCK_MODEL__OUTPUTS:
				return outputs != null && !outputs.isEmpty();
			case ModelPackage.FUNCTION_BLOCK_MODEL__OPTIONS:
				return options != null && !options.isEmpty();
			case ModelPackage.FUNCTION_BLOCK_MODEL__ID:
				return ID_EDEFAULT == null ? iD != null : !ID_EDEFAULT.equals(iD);
			case ModelPackage.FUNCTION_BLOCK_MODEL__POSITION:
				return POSITION_EDEFAULT == null ? position != null : !POSITION_EDEFAULT.equals(position);
			case ModelPackage.FUNCTION_BLOCK_MODEL__BLOCK_NAME:
				return BLOCK_NAME_EDEFAULT == null ? blockName != null : !BLOCK_NAME_EDEFAULT.equals(blockName);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (type: ");
		result.append(type);
		result.append(", iD: ");
		result.append(iD);
		result.append(", position: ");
		result.append(position);
		result.append(", blockName: ");
		result.append(blockName);
		result.append(')');
		return result.toString();
	}

	@Override
	public String getTypeName() {
		if (type != null && !type.isEmpty()) {
			return type.substring(type.lastIndexOf('.') + 1);
		}
		return type;
	}

	@Override
	public boolean isSensor() {
		return getInputs().isEmpty() && !getOutputs().isEmpty();
	}

	@Override
	public boolean isActor() {
		return !getInputs().isEmpty() && getOutputs().isEmpty();
	}
} // FunctionBlockModelImpl
