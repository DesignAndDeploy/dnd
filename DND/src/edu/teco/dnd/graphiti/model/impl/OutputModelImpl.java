/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package edu.teco.dnd.graphiti.model.impl;

import java.util.Collection;

import org.apache.bcel.util.Repository;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.InputModel;
import edu.teco.dnd.graphiti.model.ModelPackage;
import edu.teco.dnd.graphiti.model.OutputModel;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Output Model</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link edu.teco.dnd.graphiti.model.impl.OutputModelImpl#getName <em>Name</em>}</li>
 * <li>{@link edu.teco.dnd.graphiti.model.impl.OutputModelImpl#getFunctionBlock <em>Function Block</em>}</li>
 * <li>{@link edu.teco.dnd.graphiti.model.impl.OutputModelImpl#getInputs <em>Inputs</em>}</li>
 * <li>{@link edu.teco.dnd.graphiti.model.impl.OutputModelImpl#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class OutputModelImpl extends EObjectImpl implements OutputModel {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getInputs() <em>Inputs</em>}' reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getInputs()
	 * @generated
	 * @ordered
	 */
	protected EList<InputModel> inputs;

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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected OutputModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.OUTPUT_MODEL;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.OUTPUT_MODEL__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public FunctionBlockModel getFunctionBlock() {
		if (eContainerFeatureID() != ModelPackage.OUTPUT_MODEL__FUNCTION_BLOCK)
			return null;
		return (FunctionBlockModel) eContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetFunctionBlock(FunctionBlockModel newFunctionBlock, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newFunctionBlock, ModelPackage.OUTPUT_MODEL__FUNCTION_BLOCK, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setFunctionBlock(FunctionBlockModel newFunctionBlock) {
		if (newFunctionBlock != eInternalContainer()
				|| (eContainerFeatureID() != ModelPackage.OUTPUT_MODEL__FUNCTION_BLOCK && newFunctionBlock != null)) {
			if (EcoreUtil.isAncestor(this, newFunctionBlock))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newFunctionBlock != null)
				msgs =
						((InternalEObject) newFunctionBlock).eInverseAdd(this,
								ModelPackage.FUNCTION_BLOCK_MODEL__OUTPUTS, FunctionBlockModel.class, msgs);
			msgs = basicSetFunctionBlock(newFunctionBlock, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.OUTPUT_MODEL__FUNCTION_BLOCK,
					newFunctionBlock, newFunctionBlock));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<InputModel> getInputs() {
		if (inputs == null) {
			inputs =
					new EObjectWithInverseResolvingEList<InputModel>(InputModel.class, this,
							ModelPackage.OUTPUT_MODEL__INPUTS, ModelPackage.INPUT_MODEL__OUTPUT);
		}
		return inputs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setType(String newType) {
		String oldType = type;
		type = newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.OUTPUT_MODEL__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ModelPackage.OUTPUT_MODEL__FUNCTION_BLOCK:
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			return basicSetFunctionBlock((FunctionBlockModel) otherEnd, msgs);
		case ModelPackage.OUTPUT_MODEL__INPUTS:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getInputs()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ModelPackage.OUTPUT_MODEL__FUNCTION_BLOCK:
			return basicSetFunctionBlock(null, msgs);
		case ModelPackage.OUTPUT_MODEL__INPUTS:
			return ((InternalEList<?>) getInputs()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
		case ModelPackage.OUTPUT_MODEL__FUNCTION_BLOCK:
			return eInternalContainer().eInverseRemove(this, ModelPackage.FUNCTION_BLOCK_MODEL__OUTPUTS,
					FunctionBlockModel.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ModelPackage.OUTPUT_MODEL__NAME:
			return getName();
		case ModelPackage.OUTPUT_MODEL__FUNCTION_BLOCK:
			return getFunctionBlock();
		case ModelPackage.OUTPUT_MODEL__INPUTS:
			return getInputs();
		case ModelPackage.OUTPUT_MODEL__TYPE:
			return getType();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ModelPackage.OUTPUT_MODEL__NAME:
			setName((String) newValue);
			return;
		case ModelPackage.OUTPUT_MODEL__FUNCTION_BLOCK:
			setFunctionBlock((FunctionBlockModel) newValue);
			return;
		case ModelPackage.OUTPUT_MODEL__INPUTS:
			getInputs().clear();
			getInputs().addAll((Collection<? extends InputModel>) newValue);
			return;
		case ModelPackage.OUTPUT_MODEL__TYPE:
			setType((String) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case ModelPackage.OUTPUT_MODEL__NAME:
			setName(NAME_EDEFAULT);
			return;
		case ModelPackage.OUTPUT_MODEL__FUNCTION_BLOCK:
			setFunctionBlock((FunctionBlockModel) null);
			return;
		case ModelPackage.OUTPUT_MODEL__INPUTS:
			getInputs().clear();
			return;
		case ModelPackage.OUTPUT_MODEL__TYPE:
			setType(TYPE_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case ModelPackage.OUTPUT_MODEL__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case ModelPackage.OUTPUT_MODEL__FUNCTION_BLOCK:
			return getFunctionBlock() != null;
		case ModelPackage.OUTPUT_MODEL__INPUTS:
			return inputs != null && !inputs.isEmpty();
		case ModelPackage.OUTPUT_MODEL__TYPE:
			return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(", type: ");
		result.append(type);
		result.append(')');
		return result.toString();
	}

	@Override
	public boolean isCompatible(Repository repository, InputModel input) {
		if (input == null) {
			return false;
		}
		return input.isCompatible(repository, this);
	}

} // OutputModelImpl
