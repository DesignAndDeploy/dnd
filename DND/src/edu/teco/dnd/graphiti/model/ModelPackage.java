/**
 */
package edu.teco.dnd.graphiti.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see edu.teco.dnd.graphiti.model.ModelFactory
 * @model kind="package"
 * @generated
 */
public interface ModelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "model";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http:///edu/teco/dnd/graphiti/model.ecore";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "edu.teco.dnd.graphiti.model";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ModelPackage eINSTANCE = edu.teco.dnd.graphiti.model.impl.ModelPackageImpl.init();

	/**
	 * The meta object id for the '{@link edu.teco.dnd.graphiti.model.impl.FunctionBlockModelImpl <em>Function Block Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see edu.teco.dnd.graphiti.model.impl.FunctionBlockModelImpl
	 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getFunctionBlockModel()
	 * @generated
	 */
	int FUNCTION_BLOCK_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_BLOCK_MODEL__TYPE = 0;

	/**
	 * The feature id for the '<em><b>Inputs</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_BLOCK_MODEL__INPUTS = 1;

	/**
	 * The feature id for the '<em><b>Outputs</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_BLOCK_MODEL__OUTPUTS = 2;

	/**
	 * The feature id for the '<em><b>Options</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_BLOCK_MODEL__OPTIONS = 3;

	/**
	 * The feature id for the '<em><b>ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_BLOCK_MODEL__ID = 4;

	/**
	 * The feature id for the '<em><b>Position</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_BLOCK_MODEL__POSITION = 5;

	/**
	 * The feature id for the '<em><b>Block Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_BLOCK_MODEL__BLOCK_NAME = 6;

	/**
	 * The feature id for the '<em><b>Block Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_BLOCK_MODEL__BLOCK_CLASS = 7;

	/**
	 * The number of structural features of the '<em>Function Block Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_BLOCK_MODEL_FEATURE_COUNT = 8;

	/**
	 * The meta object id for the '{@link edu.teco.dnd.graphiti.model.impl.InputModelImpl <em>Input Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see edu.teco.dnd.graphiti.model.impl.InputModelImpl
	 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getInputModel()
	 * @generated
	 */
	int INPUT_MODEL = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_MODEL__NAME = 0;

	/**
	 * The feature id for the '<em><b>Function Block</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_MODEL__FUNCTION_BLOCK = 1;

	/**
	 * The feature id for the '<em><b>Output</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_MODEL__OUTPUT = 2;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_MODEL__TYPE = 3;

	/**
	 * The number of structural features of the '<em>Input Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_MODEL_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link edu.teco.dnd.graphiti.model.impl.OptionModelImpl <em>Option Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see edu.teco.dnd.graphiti.model.impl.OptionModelImpl
	 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getOptionModel()
	 * @generated
	 */
	int OPTION_MODEL = 2;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPTION_MODEL__TYPE = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPTION_MODEL__VALUE = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPTION_MODEL__NAME = 2;

	/**
	 * The feature id for the '<em><b>Function Block</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPTION_MODEL__FUNCTION_BLOCK = 3;

	/**
	 * The number of structural features of the '<em>Option Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPTION_MODEL_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link edu.teco.dnd.graphiti.model.impl.OutputModelImpl <em>Output Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see edu.teco.dnd.graphiti.model.impl.OutputModelImpl
	 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getOutputModel()
	 * @generated
	 */
	int OUTPUT_MODEL = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_MODEL__NAME = 0;

	/**
	 * The feature id for the '<em><b>Function Block</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_MODEL__FUNCTION_BLOCK = 1;

	/**
	 * The feature id for the '<em><b>Inputs</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_MODEL__INPUTS = 2;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_MODEL__TYPE = 3;

	/**
	 * The number of structural features of the '<em>Output Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_MODEL_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '<em>Serializable</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.io.Serializable
	 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getSerializable()
	 * @generated
	 */
	int SERIALIZABLE = 4;


	/**
	 * The meta object id for the '<em>Function Block</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see edu.teco.dnd.blocks.FunctionBlock
	 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getFunctionBlock()
	 * @generated
	 */
	int FUNCTION_BLOCK = 5;


	/**
	 * The meta object id for the '<em>UUID</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.UUID
	 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getUUID()
	 * @generated
	 */
	int UUID = 6;


	/**
	 * Returns the meta object for class '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel <em>Function Block Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Function Block Model</em>'.
	 * @see edu.teco.dnd.graphiti.model.FunctionBlockModel
	 * @generated
	 */
	EClass getFunctionBlockModel();

	/**
	 * Returns the meta object for the attribute '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see edu.teco.dnd.graphiti.model.FunctionBlockModel#getType()
	 * @see #getFunctionBlockModel()
	 * @generated
	 */
	EAttribute getFunctionBlockModel_Type();

	/**
	 * Returns the meta object for the containment reference list '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getInputs <em>Inputs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Inputs</em>'.
	 * @see edu.teco.dnd.graphiti.model.FunctionBlockModel#getInputs()
	 * @see #getFunctionBlockModel()
	 * @generated
	 */
	EReference getFunctionBlockModel_Inputs();

	/**
	 * Returns the meta object for the containment reference list '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getOutputs <em>Outputs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Outputs</em>'.
	 * @see edu.teco.dnd.graphiti.model.FunctionBlockModel#getOutputs()
	 * @see #getFunctionBlockModel()
	 * @generated
	 */
	EReference getFunctionBlockModel_Outputs();

	/**
	 * Returns the meta object for the containment reference list '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getOptions <em>Options</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Options</em>'.
	 * @see edu.teco.dnd.graphiti.model.FunctionBlockModel#getOptions()
	 * @see #getFunctionBlockModel()
	 * @generated
	 */
	EReference getFunctionBlockModel_Options();

	/**
	 * Returns the meta object for the attribute '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getID <em>ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>ID</em>'.
	 * @see edu.teco.dnd.graphiti.model.FunctionBlockModel#getID()
	 * @see #getFunctionBlockModel()
	 * @generated
	 */
	EAttribute getFunctionBlockModel_ID();

	/**
	 * Returns the meta object for the attribute '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getPosition <em>Position</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Position</em>'.
	 * @see edu.teco.dnd.graphiti.model.FunctionBlockModel#getPosition()
	 * @see #getFunctionBlockModel()
	 * @generated
	 */
	EAttribute getFunctionBlockModel_Position();

	/**
	 * Returns the meta object for the attribute '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getBlockName <em>Block Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Block Name</em>'.
	 * @see edu.teco.dnd.graphiti.model.FunctionBlockModel#getBlockName()
	 * @see #getFunctionBlockModel()
	 * @generated
	 */
	EAttribute getFunctionBlockModel_BlockName();

	/**
	 * Returns the meta object for the attribute '{@link edu.teco.dnd.graphiti.model.FunctionBlockModel#getBlockClass <em>Block Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Block Class</em>'.
	 * @see edu.teco.dnd.graphiti.model.FunctionBlockModel#getBlockClass()
	 * @see #getFunctionBlockModel()
	 * @generated
	 */
	EAttribute getFunctionBlockModel_BlockClass();

	/**
	 * Returns the meta object for class '{@link edu.teco.dnd.graphiti.model.InputModel <em>Input Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Input Model</em>'.
	 * @see edu.teco.dnd.graphiti.model.InputModel
	 * @generated
	 */
	EClass getInputModel();

	/**
	 * Returns the meta object for the attribute '{@link edu.teco.dnd.graphiti.model.InputModel#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see edu.teco.dnd.graphiti.model.InputModel#getName()
	 * @see #getInputModel()
	 * @generated
	 */
	EAttribute getInputModel_Name();

	/**
	 * Returns the meta object for the container reference '{@link edu.teco.dnd.graphiti.model.InputModel#getFunctionBlock <em>Function Block</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Function Block</em>'.
	 * @see edu.teco.dnd.graphiti.model.InputModel#getFunctionBlock()
	 * @see #getInputModel()
	 * @generated
	 */
	EReference getInputModel_FunctionBlock();

	/**
	 * Returns the meta object for the reference '{@link edu.teco.dnd.graphiti.model.InputModel#getOutput <em>Output</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Output</em>'.
	 * @see edu.teco.dnd.graphiti.model.InputModel#getOutput()
	 * @see #getInputModel()
	 * @generated
	 */
	EReference getInputModel_Output();

	/**
	 * Returns the meta object for the attribute '{@link edu.teco.dnd.graphiti.model.InputModel#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see edu.teco.dnd.graphiti.model.InputModel#getType()
	 * @see #getInputModel()
	 * @generated
	 */
	EAttribute getInputModel_Type();

	/**
	 * Returns the meta object for class '{@link edu.teco.dnd.graphiti.model.OptionModel <em>Option Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Option Model</em>'.
	 * @see edu.teco.dnd.graphiti.model.OptionModel
	 * @generated
	 */
	EClass getOptionModel();

	/**
	 * Returns the meta object for the attribute '{@link edu.teco.dnd.graphiti.model.OptionModel#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see edu.teco.dnd.graphiti.model.OptionModel#getType()
	 * @see #getOptionModel()
	 * @generated
	 */
	EAttribute getOptionModel_Type();

	/**
	 * Returns the meta object for the attribute '{@link edu.teco.dnd.graphiti.model.OptionModel#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see edu.teco.dnd.graphiti.model.OptionModel#getValue()
	 * @see #getOptionModel()
	 * @generated
	 */
	EAttribute getOptionModel_Value();

	/**
	 * Returns the meta object for the attribute '{@link edu.teco.dnd.graphiti.model.OptionModel#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see edu.teco.dnd.graphiti.model.OptionModel#getName()
	 * @see #getOptionModel()
	 * @generated
	 */
	EAttribute getOptionModel_Name();

	/**
	 * Returns the meta object for the container reference '{@link edu.teco.dnd.graphiti.model.OptionModel#getFunctionBlock <em>Function Block</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Function Block</em>'.
	 * @see edu.teco.dnd.graphiti.model.OptionModel#getFunctionBlock()
	 * @see #getOptionModel()
	 * @generated
	 */
	EReference getOptionModel_FunctionBlock();

	/**
	 * Returns the meta object for class '{@link edu.teco.dnd.graphiti.model.OutputModel <em>Output Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Output Model</em>'.
	 * @see edu.teco.dnd.graphiti.model.OutputModel
	 * @generated
	 */
	EClass getOutputModel();

	/**
	 * Returns the meta object for the attribute '{@link edu.teco.dnd.graphiti.model.OutputModel#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see edu.teco.dnd.graphiti.model.OutputModel#getName()
	 * @see #getOutputModel()
	 * @generated
	 */
	EAttribute getOutputModel_Name();

	/**
	 * Returns the meta object for the container reference '{@link edu.teco.dnd.graphiti.model.OutputModel#getFunctionBlock <em>Function Block</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Function Block</em>'.
	 * @see edu.teco.dnd.graphiti.model.OutputModel#getFunctionBlock()
	 * @see #getOutputModel()
	 * @generated
	 */
	EReference getOutputModel_FunctionBlock();

	/**
	 * Returns the meta object for the reference list '{@link edu.teco.dnd.graphiti.model.OutputModel#getInputs <em>Inputs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Inputs</em>'.
	 * @see edu.teco.dnd.graphiti.model.OutputModel#getInputs()
	 * @see #getOutputModel()
	 * @generated
	 */
	EReference getOutputModel_Inputs();

	/**
	 * Returns the meta object for the attribute '{@link edu.teco.dnd.graphiti.model.OutputModel#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see edu.teco.dnd.graphiti.model.OutputModel#getType()
	 * @see #getOutputModel()
	 * @generated
	 */
	EAttribute getOutputModel_Type();

	/**
	 * Returns the meta object for data type '{@link java.io.Serializable <em>Serializable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Serializable</em>'.
	 * @see java.io.Serializable
	 * @model instanceClass="java.io.Serializable"
	 * @generated
	 */
	EDataType getSerializable();

	/**
	 * Returns the meta object for data type '{@link edu.teco.dnd.blocks.FunctionBlock <em>Function Block</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Function Block</em>'.
	 * @see edu.teco.dnd.blocks.FunctionBlock
	 * @model instanceClass="edu.teco.dnd.blocks.FunctionBlock"
	 * @generated
	 */
	EDataType getFunctionBlock();

	/**
	 * Returns the meta object for data type '{@link java.util.UUID <em>UUID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>UUID</em>'.
	 * @see java.util.UUID
	 * @model instanceClass="java.util.UUID"
	 * @generated
	 */
	EDataType getUUID();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ModelFactory getModelFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link edu.teco.dnd.graphiti.model.impl.FunctionBlockModelImpl <em>Function Block Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see edu.teco.dnd.graphiti.model.impl.FunctionBlockModelImpl
		 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getFunctionBlockModel()
		 * @generated
		 */
		EClass FUNCTION_BLOCK_MODEL = eINSTANCE.getFunctionBlockModel();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION_BLOCK_MODEL__TYPE = eINSTANCE.getFunctionBlockModel_Type();

		/**
		 * The meta object literal for the '<em><b>Inputs</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FUNCTION_BLOCK_MODEL__INPUTS = eINSTANCE.getFunctionBlockModel_Inputs();

		/**
		 * The meta object literal for the '<em><b>Outputs</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FUNCTION_BLOCK_MODEL__OUTPUTS = eINSTANCE.getFunctionBlockModel_Outputs();

		/**
		 * The meta object literal for the '<em><b>Options</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FUNCTION_BLOCK_MODEL__OPTIONS = eINSTANCE.getFunctionBlockModel_Options();

		/**
		 * The meta object literal for the '<em><b>ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION_BLOCK_MODEL__ID = eINSTANCE.getFunctionBlockModel_ID();

		/**
		 * The meta object literal for the '<em><b>Position</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION_BLOCK_MODEL__POSITION = eINSTANCE.getFunctionBlockModel_Position();

		/**
		 * The meta object literal for the '<em><b>Block Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION_BLOCK_MODEL__BLOCK_NAME = eINSTANCE.getFunctionBlockModel_BlockName();

		/**
		 * The meta object literal for the '<em><b>Block Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION_BLOCK_MODEL__BLOCK_CLASS = eINSTANCE.getFunctionBlockModel_BlockClass();

		/**
		 * The meta object literal for the '{@link edu.teco.dnd.graphiti.model.impl.InputModelImpl <em>Input Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see edu.teco.dnd.graphiti.model.impl.InputModelImpl
		 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getInputModel()
		 * @generated
		 */
		EClass INPUT_MODEL = eINSTANCE.getInputModel();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INPUT_MODEL__NAME = eINSTANCE.getInputModel_Name();

		/**
		 * The meta object literal for the '<em><b>Function Block</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INPUT_MODEL__FUNCTION_BLOCK = eINSTANCE.getInputModel_FunctionBlock();

		/**
		 * The meta object literal for the '<em><b>Output</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INPUT_MODEL__OUTPUT = eINSTANCE.getInputModel_Output();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INPUT_MODEL__TYPE = eINSTANCE.getInputModel_Type();

		/**
		 * The meta object literal for the '{@link edu.teco.dnd.graphiti.model.impl.OptionModelImpl <em>Option Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see edu.teco.dnd.graphiti.model.impl.OptionModelImpl
		 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getOptionModel()
		 * @generated
		 */
		EClass OPTION_MODEL = eINSTANCE.getOptionModel();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPTION_MODEL__TYPE = eINSTANCE.getOptionModel_Type();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPTION_MODEL__VALUE = eINSTANCE.getOptionModel_Value();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPTION_MODEL__NAME = eINSTANCE.getOptionModel_Name();

		/**
		 * The meta object literal for the '<em><b>Function Block</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPTION_MODEL__FUNCTION_BLOCK = eINSTANCE.getOptionModel_FunctionBlock();

		/**
		 * The meta object literal for the '{@link edu.teco.dnd.graphiti.model.impl.OutputModelImpl <em>Output Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see edu.teco.dnd.graphiti.model.impl.OutputModelImpl
		 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getOutputModel()
		 * @generated
		 */
		EClass OUTPUT_MODEL = eINSTANCE.getOutputModel();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OUTPUT_MODEL__NAME = eINSTANCE.getOutputModel_Name();

		/**
		 * The meta object literal for the '<em><b>Function Block</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OUTPUT_MODEL__FUNCTION_BLOCK = eINSTANCE.getOutputModel_FunctionBlock();

		/**
		 * The meta object literal for the '<em><b>Inputs</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OUTPUT_MODEL__INPUTS = eINSTANCE.getOutputModel_Inputs();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OUTPUT_MODEL__TYPE = eINSTANCE.getOutputModel_Type();

		/**
		 * The meta object literal for the '<em>Serializable</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.io.Serializable
		 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getSerializable()
		 * @generated
		 */
		EDataType SERIALIZABLE = eINSTANCE.getSerializable();

		/**
		 * The meta object literal for the '<em>Function Block</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see edu.teco.dnd.blocks.FunctionBlock
		 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getFunctionBlock()
		 * @generated
		 */
		EDataType FUNCTION_BLOCK = eINSTANCE.getFunctionBlock();

		/**
		 * The meta object literal for the '<em>UUID</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.UUID
		 * @see edu.teco.dnd.graphiti.model.impl.ModelPackageImpl#getUUID()
		 * @generated
		 */
		EDataType UUID = eINSTANCE.getUUID();

	}

} //ModelPackage
