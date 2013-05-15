package edu.teco.dnd.blocks.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite contains all tests for FunctionBlock.
 * 
 * @author philipp
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ TestFunctionBlockStatic.class, TestFunctionBlockInputs.class, TestFunctionBlockOutputs.class,
		TestFunctionBlock.class, TestFunctionBlockOptions.class })
public class FunctionBlockTestSuite {

}
