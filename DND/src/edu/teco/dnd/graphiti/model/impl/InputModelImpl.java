/**
 */
package edu.teco.dnd.graphiti.model.impl;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.InputModel;
import edu.teco.dnd.graphiti.model.ModelPackage;
import edu.teco.dnd.graphiti.model.OutputModel;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Input Model</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link edu.teco.dnd.graphiti.model.impl.InputModelImpl#getName <em>Name</em>}</li>
 * <li>{@link edu.teco.dnd.graphiti.model.impl.InputModelImpl#getFunctionBlock <em>Function Block</em>}</li>
 * <li>{@link edu.teco.dnd.graphiti.model.impl.InputModelImpl#getOutput <em>Output</em>}</li>
 * <li>{@link edu.teco.dnd.graphiti.model.impl.InputModelImpl#getType <em>Type</em>}</li>
 * <li>{@link edu.teco.dnd.graphiti.model.impl.InputModelImpl#isQueued <em>Queued</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class InputModelImpl extends EObjectImpl implements InputModel {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOutput() <em>Output</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getOutput()
	 * @generated
	 * @ordered
	 */
	protected OutputModel output;

	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected String type = TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #isQueued() <em>Queued</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #isQueued()
	 * @generated
	 * @ordered
	 */
	protected static final boolean QUEUED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isQueued() <em>Queued</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #isQueued()
	 * @generated
	 * @ordered
	 */
	protected boolean queued = QUEUED_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected InputModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.INPUT_MODEL;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.INPUT_MODEL__NAME, oldName,
					name));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public FunctionBlockModel getFunctionBlock() {
		if (eContainerFeatureID() != ModelPackage.INPUT_MODEL__FUNCTION_BLOCK)
			return null;
		return (FunctionBlockModel) eContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetFunctionBlock(FunctionBlockModel newFunctionBlock, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newFunctionBlock,
				ModelPackage.INPUT_MODEL__FUNCTION_BLOCK, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setFunctionBlock(FunctionBlockModel newFunctionBlock) {
		if (newFunctionBlock != eInternalContainer()
				|| (eContainerFeatureID() != ModelPackage.INPUT_MODEL__FUNCTION_BLOCK && newFunctionBlock != null)) {
			if (EcoreUtil.isAncestor(this, newFunctionBlock))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newFunctionBlock != null)
				msgs = ((InternalEObject) newFunctionBlock).eInverseAdd(this,
						ModelPackage.FUNCTION_BLOCK_MODEL__INPUTS, FunctionBlockModel.class, msgs);
			msgs = basicSetFunctionBlock(newFunctionBlock, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.INPUT_MODEL__FUNCTION_BLOCK,
					newFunctionBlock, newFunctionBlock));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public OutputModel getOutput() {
		if (output != null && output.eIsProxy()) {
			InternalEObject oldOutput = (InternalEObject) output;
			output = (OutputModel) eResolveProxy(oldOutput);
			if (output != oldOutput) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							ModelPackage.INPUT_MODEL__OUTPUT, oldOutput, output));
			}
		}
		return output;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public OutputModel basicGetOutput() {
		return output;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetOutput(OutputModel newOutput, NotificationChain msgs) {
		OutputModel oldOutput = output;
		output = newOutput;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					ModelPackage.INPUT_MODEL__OUTPUT, oldOutput, newOutput);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setOutput(OutputModel newOutput) {
		if (newOutput != output) {
			NotificationChain msgs = null;
			if (output != null)
				msgs = ((InternalEObject) output).eInverseRemove(this, ModelPackage.OUTPUT_MODEL__INPUTS,
						OutputModel.class, msgs);
			if (newOutput != null)
				msgs = ((InternalEObject) newOutput).eInverseAdd(this, ModelPackage.OUTPUT_MODEL__INPUTS,
						OutputModel.class, msgs);
			msgs = basicSetOutput(newOutput, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.INPUT_MODEL__OUTPUT,
					newOutput, newOutput));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.INPUT_MODEL__TYPE, oldType,
					type));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public boolean isQueued() {
		return queued;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setQueued(boolean newQueued) {
		boolean oldQueued = queued;
		queued = newQueued;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.INPUT_MODEL__QUEUED,
					oldQueued, queued));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ModelPackage.INPUT_MODEL__FUNCTION_BLOCK:
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			return basicSetFunctionBlock((FunctionBlockModel) otherEnd, msgs);
		case ModelPackage.INPUT_MODEL__OUTPUT:
			if (output != null)
				msgs = ((InternalEObject) output).eInverseRemove(this, ModelPackage.OUTPUT_MODEL__INPUTS,
						OutputModel.class, msgs);
			return basicSetOutput((OutputModel) otherEnd, msgs);
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
		case ModelPackage.INPUT_MODEL__FUNCTION_BLOCK:
			return basicSetFunctionBlock(null, msgs);
		case ModelPackage.INPUT_MODEL__OUTPUT:
			return basicSetOutput(null, msgs);
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
		case ModelPackage.INPUT_MODEL__FUNCTION_BLOCK:
			return eInternalContainer().eInverseRemove(this, ModelPackage.FUNCTION_BLOCK_MODEL__INPUTS,
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
		case ModelPackage.INPUT_MODEL__NAME:
			return getName();
		case ModelPackage.INPUT_MODEL__FUNCTION_BLOCK:
			return getFunctionBlock();
		case ModelPackage.INPUT_MODEL__OUTPUT:
			if (resolve)
				return getOutput();
			return basicGetOutput();
		case ModelPackage.INPUT_MODEL__TYPE:
			return getType();
		case ModelPackage.INPUT_MODEL__QUEUED:
			return isQueued();
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
		case ModelPackage.INPUT_MODEL__NAME:
			setName((String) newValue);
			return;
		case ModelPackage.INPUT_MODEL__FUNCTION_BLOCK:
			setFunctionBlock((FunctionBlockModel) newValue);
			return;
		case ModelPackage.INPUT_MODEL__OUTPUT:
			setOutput((OutputModel) newValue);
			return;
		case ModelPackage.INPUT_MODEL__TYPE:
			setType((String) newValue);
			return;
		case ModelPackage.INPUT_MODEL__QUEUED:
			setQueued((Boolean) newValue);
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
		case ModelPackage.INPUT_MODEL__NAME:
			setName(NAME_EDEFAULT);
			return;
		case ModelPackage.INPUT_MODEL__FUNCTION_BLOCK:
			setFunctionBlock((FunctionBlockModel) null);
			return;
		case ModelPackage.INPUT_MODEL__OUTPUT:
			setOutput((OutputModel) null);
			return;
		case ModelPackage.INPUT_MODEL__TYPE:
			setType(TYPE_EDEFAULT);
			return;
		case ModelPackage.INPUT_MODEL__QUEUED:
			setQueued(QUEUED_EDEFAULT);
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
		case ModelPackage.INPUT_MODEL__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case ModelPackage.INPUT_MODEL__FUNCTION_BLOCK:
			return getFunctionBlock() != null;
		case ModelPackage.INPUT_MODEL__OUTPUT:
			return output != null;
		case ModelPackage.INPUT_MODEL__TYPE:
			return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
		case ModelPackage.INPUT_MODEL__QUEUED:
			return queued != QUEUED_EDEFAULT;
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
		result.append(", queued: ");
		result.append(queued);
		result.append(')');
		return result.toString();
	}

	@Override
	public boolean isCompatible(ClassLoader cl, OutputModel output) {
		if (output == null) {
			return false;
		}
		Class<?> myType;
		Class<?> otherType;
		try {
			myType = cl.loadClass(getType());
			otherType = cl.loadClass(output.getType());
		} catch (ClassNotFoundException e) {
			return false;
		}
		return myType.isAssignableFrom(otherType);
	}
} // InputModelImpl
