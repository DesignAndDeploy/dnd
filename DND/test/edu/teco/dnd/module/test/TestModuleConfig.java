package edu.teco.dnd.module.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.teco.dnd.module.ModuleConfig;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link ModuleConfig#setAndReadConfig(edu.teco.dnd.module.ConfigFile)}.
 */
public class TestModuleConfig {
	public static final String TEST_NAME = "test";

	public static final String TEST_LOCATION = "location";

	public static final int TEST_BLOCK_NUMBER = 42;

	public static final int TEST_MEMORY = 36;

	public static final int TEST_MHZ = 21;

	public static final List<String> TEST_BLOCKS;

	public static final String TEST_BLOCKS_STRING = "block1,block2,block2,block3";

	static {
		List<String> testBlocks = new ArrayList<>();
		testBlocks.add("block1");
		testBlocks.add("block2");
		testBlocks.add("block2");
		testBlocks.add("block3");
		TEST_BLOCKS = Collections.unmodifiableList(testBlocks);
	}

	private HashMapConfigFile configFile;

	private ModuleConfig moduleConfig;

	@Before
	public void init() {
		configFile = new HashMapConfigFile();
		configFile.setProperty(ModuleConfig.NAME_ID, TEST_NAME);
		configFile.setProperty(ModuleConfig.LOCATION_ID, TEST_LOCATION);
		configFile.setProperty(ModuleConfig.BLOCK_NUMBER_ID, Integer.toString(TEST_BLOCK_NUMBER));
		configFile.setProperty(ModuleConfig.MEMORY_ID, Integer.toString(TEST_MEMORY));
		configFile.setProperty(ModuleConfig.MHZ_ID, Integer.toString(TEST_MHZ));
		configFile.setProperty(ModuleConfig.BLOCKS_ID, TEST_BLOCKS_STRING);
		moduleConfig = new ModuleConfig();
		moduleConfig.setAndReadConfig(configFile);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitNull() {
		new ModuleConfig(null);
	}

	@Test
	public void testReadName() {
		assertEquals(TEST_NAME, moduleConfig.getName());
	}

	@Test
	public void testReadLocation() {
		assertEquals(TEST_LOCATION, moduleConfig.getLocation());
	}

	@Test
	public void testReadBlockNumber() {
		assertEquals(TEST_BLOCK_NUMBER, moduleConfig.getMaxNumberOfBlocks());
	}

	@Test
	public void testReadMemory() {
		assertEquals(TEST_MEMORY, moduleConfig.getMemory());
	}

	@Test
	public void testReadMHz() {
		assertEquals(TEST_MHZ, moduleConfig.getCpuMHz());
	}

	@Test
	public void testReadBlocks() {
		List<String> expected = new ArrayList<>(TEST_BLOCKS);
		List<String> actual = moduleConfig.getSupportedBlockId();
		Collections.sort(expected);
		Collections.sort(actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testSave() throws IOException {
		configFile.resetSavedState();
		moduleConfig.save();
		assertTrue(configFile.hasBeenSaved());
	}

	@Test
	public void testSaveName() throws IOException {
		moduleConfig.save();
		assertEquals(TEST_NAME, configFile.getProperty(ModuleConfig.NAME_ID));
	}

	@Test
	public void testSaveLocation() throws IOException {
		moduleConfig.save();
		assertEquals(TEST_LOCATION, configFile.getProperty(ModuleConfig.LOCATION_ID));
	}

	@Test
	public void testSaveBlockNumber() throws IOException {
		moduleConfig.save();
		String blockNumberString = configFile.getProperty(ModuleConfig.BLOCK_NUMBER_ID);
		if (blockNumberString == null) {
			fail("number of blocks is null");
		}
		int blockNumber = 0;
		try {
			blockNumber = Integer.valueOf(blockNumberString);
		} catch (NumberFormatException e) {
			fail("number of blocks is not a number");
		}
		assertEquals(TEST_BLOCK_NUMBER, blockNumber);
	}

	@Test
	public void testSaveMemory() throws IOException {
		moduleConfig.save();
		String memoryString = configFile.getProperty(ModuleConfig.MEMORY_ID);
		if (memoryString == null) {
			fail("memory is null");
		}
		int memoryNumber = 0;
		try {
			memoryNumber = Integer.valueOf(memoryString);
		} catch (NumberFormatException e) {
			fail("memory is not a number");
		}
		assertEquals(TEST_MEMORY, memoryNumber);
	}

	@Test
	public void testSaveMHz() throws IOException {
		moduleConfig.save();
		String mhzString = configFile.getProperty(ModuleConfig.MHZ_ID);
		if (mhzString == null) {
			fail("MHz is null");
		}
		int mhzNumber = 0;
		try {
			mhzNumber = Integer.valueOf(mhzString);
		} catch (NumberFormatException e) {
			fail("mhz is not a number");
		}
		assertEquals(TEST_MHZ, mhzNumber);
	}

	@Test
	public void testSaveBlocks() throws IOException {
		moduleConfig.save();
		String actualString = configFile.getProperty(ModuleConfig.BLOCKS_ID);
		if (actualString == null) {
			fail("blocks is null");
		}
		List<String> expected = new ArrayList<>(TEST_BLOCKS);
		List<String> actual = new ArrayList<>();
		for (String str : actualString.split(",")) {
			actual.add(str);
		}
		Collections.sort(expected);
		Collections.sort(actual);
		assertEquals(expected, actual);
	}
}
