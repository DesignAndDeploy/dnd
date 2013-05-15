package edu.teco.dnd.module.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import lime.LimeServer;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.CommunicationAgent;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleConfig;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Module.
 */
@SuppressWarnings("static-method")
public class TestModule {

	/**
	 * Test function Block to hand to Module.
	 */
	private class TestBlock extends FunctionBlock {
		/**
		 * type the block is supposed to claim to have.
		 */
		private final String type;

		/**
		 * Constructor.
		 * 
		 * @param type
		 *            the type of the block
		 * @param id
		 *            the id
		 * @param posRegEx
		 *            the positionRegEx where this block wishes to be.
		 */
		TestBlock(final String type, final String id, final String posRegEx) {
			super(id);
			this.type = type;
			setPosition(posRegEx);
		}

		@Override
		public String getType() {
			return type;
		}

		@Override
		public void init() {
		}

		@Override
		protected void update() {
		}

	}

	public static final String TEST_NAME = "test";
	public static final String TEST_LOCATION = "location";
	public static final int TEST_BLOCK_NUMBER = 42;
	public static final int TEST_MEMORY = 36;
	public static final int TEST_MHZ = 21;
	public static final String TEST_BLOCKS_STRING = "block1,block2,block2,block3";
	public static final int TEST_APPLICATION_ID = 1;

	private static HashMapConfigFile configFile = new HashMapConfigFile();
	private static ModuleConfig moduleConfig;

	private static Module mod;

	static {

		configFile.setProperty(ModuleConfig.NAME_ID, TEST_NAME);
		configFile.setProperty(ModuleConfig.LOCATION_ID, TEST_LOCATION);
		configFile.setProperty(ModuleConfig.BLOCK_NUMBER_ID, Integer.toString(TEST_BLOCK_NUMBER));
		configFile.setProperty(ModuleConfig.MEMORY_ID, Integer.toString(TEST_MEMORY));
		configFile.setProperty(ModuleConfig.MHZ_ID, Integer.toString(TEST_MHZ));
		configFile.setProperty(ModuleConfig.BLOCKS_ID, TEST_BLOCKS_STRING);
		moduleConfig = new ModuleConfig();
		moduleConfig.setAndReadConfig(configFile);
	}

	/**
	 * Setup before each test.
	 */
	@Before
	public void setUpModule() {
		mod = new Module(moduleConfig);
	}

	/**
	 * Test constructor with nullpointer.
	 * 
	 * @throws IOException
	 *             not handled, and should not appear.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInitNull() throws IOException {
		new Module((String) null);
	}

	/**
	 * Test constructor with invalid file.
	 * 
	 * @throws IOException
	 *             expected
	 */
	@Test(expected = IOException.class)
	public void testInitDirectory() throws IOException {
		new Module("./");
	}

	/**
	 * test correct setting of config file.
	 */
	@Test
	public void testConfig() {
		assertEquals(moduleConfig, mod.getModuleConfig());
	}

	/**
	 * test whether getting of commAgent works properly.
	 * 
	 * @throws Exception
	 *             the usual not handled reflection exceptions.
	 */
	@Test
	public void testGetCommAgent() throws Exception {

		ModuleConfig moduleConfig = new ModuleConfig();
		Field tmpfield = Module.class.getDeclaredField("localModule");
		tmpfield.setAccessible(true);
		tmpfield.set(null, new Module(moduleConfig));

		CommunicationAgent commAgg = new CommunicationAgent();
		tmpfield = Module.class.getDeclaredField("communicationAgent");
		tmpfield.setAccessible(true);
		tmpfield.set(Module.getLocalModule(), commAgg);

		assertEquals(commAgg, Module.getLocalModule().getCommunicationAgent());
	}

	/**
	 * test whether canRun is set properly from config file.
	 */
	@Test
	public void testGetCanRun() {

		List<String> actual = mod.getCanRun();
		List<String> expected = new LinkedList<>();

		for (String str : TEST_BLOCKS_STRING.split(",")) {
			expected.add(str);
		}

		assertEquals(expected, actual);
	}

	/**
	 * test whether a single block is correctly processed as (not) runnable.
	 */
	@Test
	public void testCanRunSingleBl1() {
		FunctionBlock block1 = new TestBlock("block1", "testBl1", null);
		assertTrue(mod.canRun(block1));
	}

	/**
	 * test whether a single block is correctly processed as (not) runnable.
	 */
	@Test
	public void testCanRunSingleBl2() {
		FunctionBlock block2 = new TestBlock("block2", "testBl2", null);
		assertTrue(mod.canRun(block2));
	}

	/**
	 * test whether a single block is correctly processed as (not) runnable.
	 */
	@Test
	public void testCanRunSingleBl3() {
		FunctionBlock block3 = new TestBlock("blockNOT3", "testBl3", null);
		assertFalse(mod.canRun(block3));
	}

	/**
	 * test whether a single block is correctly processed as (not) runnable.
	 */
	@Test
	public void testCanRunSingleBlPos1() {
		FunctionBlock block1 = new TestBlock("block1", "testBl1", "location");
		assertTrue(mod.canRun(block1));
	}

	/**
	 * test whether a single block is correctly processed as (not) runnable.
	 */
	@Test
	public void testCanRunSingleBlPos2() {
		FunctionBlock block2 = new TestBlock("block2", "testBl2", "lo?c.*n");
		assertTrue(mod.canRun(block2));
	}

	/**
	 * test whether a single block is correctly processed as (not) runnable.
	 */
	@Test
	public void testCanRunSingleBlPos3() {
		FunctionBlock block2 = new TestBlock("block3", "testBl3", "locNOT.*");
		assertFalse(mod.canRun(block2));
	}

	/**
	 * test whether a null block is correctly marked as always runnable.
	 */
	@Test
	public void testCanRunNull() {
		assertTrue(mod.canRun((Collection<FunctionBlock>) null));
	}

	/**
	 * test whether a list of too many blocks is correctly marked as to big to run.
	 */
	@Test
	public void testCanRunTooBig() {
		Collection<FunctionBlock> list = new LinkedList<FunctionBlock>();
		for (int i = 0; i < TEST_BLOCK_NUMBER + 1; i++) {
			list.add(new TestBlock("block1", "testblock" + i, null));
		}
		assertFalse(mod.canRun(list));
	}

	/**
	 * test whether a group of blocks is correctly processed as (not) runnable.
	 */
	@Test
	public void testCanRunGroup1() {
		Collection<FunctionBlock> list = new LinkedList<FunctionBlock>();
		list.add(new TestBlock("block1", "testBl1", null));
		list.add(new TestBlock("block2", "testBl2", null));
		list.add(null);
		list.add(new TestBlock(null, "testBl0", null));
		list.add(new TestBlock("block2", "testBl2", "lo?c.*n"));

		assertTrue(mod.canRun(list));
	}

	/**
	 * test whether a group of blocks is correctly processed as (not) runnable.
	 */
	@Test
	public void testCanRunGroup2() {
		Collection<FunctionBlock> list = new LinkedList<FunctionBlock>();
		list.add(new TestBlock("block2", "testBl2", "lo?c.*n"));

		assertTrue(mod.canRun(list));
	}

	/**
	 * test whether a group of blocks is correctly processed as (not) runnable.
	 */
	@Test
	public void testCanRunGroup3() {
		Collection<FunctionBlock> list = new LinkedList<FunctionBlock>();
		list.add(new TestBlock("block1", "testBl1", null));
		list.add(new TestBlock("block2", "testBl2", null));
		list.add(new TestBlock("block2", "testBl2", "?*\\"));

		assertFalse(mod.canRun(list));
	}

	/**
	 * test whether a group of blocks is correctly processed as (not) runnable.
	 */
	@Test
	public void testCanRunGroup4() {
		Collection<FunctionBlock> list = new LinkedList<FunctionBlock>();
		list.add(new TestBlock("block1", "testBl1", null));
		list.add(new TestBlock("block1", "testBl1", null));
		list.add(new TestBlock("block2", "testBl2", "lo?c.*n"));

		assertFalse(mod.canRun(list));
	}

	/**
	 * clean up lime.
	 */
	@AfterClass
	public static void exitClass() {
		LimeServer server = LimeServer.getServer();
		if (server != null) {
			server.disengage();
			server.shutdown(false);
		}
	}

}
