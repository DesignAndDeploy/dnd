package edu.teco.dnd.module.test;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import lights.interfaces.ITuple;
import lime.AgentLocation;
import lime.HostLocation;
import lime.LimeServer;
import lime.LimeTupleSpace;

import edu.teco.dnd.messages.ApplicationModuleMessage;
import edu.teco.dnd.messages.GlobalJoinMessage;
import edu.teco.dnd.messages.GlobalModuleMessage;
import edu.teco.dnd.module.CommunicationAgent;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleConfig;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for the communicationAgent.
 */
@SuppressWarnings("static-method")
public class TestCommAgent {

	private static LimeServer server = null;
	private static CommunicationAgent commAgg = null;
	private static UtilAgent utilAgent = null;
	private static final long ONE_SECOND = 1000L;
	private static final long USUAL_TIMEOUT = 3L * ONE_SECOND;
	private static final Random randomGen = new Random();

	/**
	 * prepare the agents/lime.
	 * 
	 * @throws Exception
	 *             exceptions not handled any further.
	 */
	@BeforeClass
	public static void init() throws Exception {

		Field tmpfield;
		HashMapConfigFile configFile = new HashMapConfigFile();
		ModuleConfig moduleConfig = new ModuleConfig();

		configFile.setProperty(ModuleConfig.NAME_ID, TestModule.TEST_NAME);
		configFile.setProperty(ModuleConfig.LOCATION_ID, TestModule.TEST_LOCATION);
		configFile.setProperty(ModuleConfig.BLOCK_NUMBER_ID, Integer.toString(TestModule.TEST_BLOCK_NUMBER));
		configFile.setProperty(ModuleConfig.MEMORY_ID, Integer.toString(TestModule.TEST_MEMORY));
		configFile.setProperty(ModuleConfig.MHZ_ID, Integer.toString(TestModule.TEST_MHZ));
		configFile.setProperty(ModuleConfig.BLOCKS_ID, TestModule.TEST_BLOCKS_STRING);
		moduleConfig.setAndReadConfig(configFile);
		tmpfield = Module.class.getDeclaredField("localModule");
		tmpfield.setAccessible(true);
		tmpfield.set(null, new Module(moduleConfig));

		server = LimeServer.getServer();
		if (server.getServerID() == null) {
			server.boot();
		}
		commAgg = (CommunicationAgent) server.loadAgent(CommunicationAgent.class.getCanonicalName(), null);
		// above does actually execute doRun. See testDoRun for further tests.
		tmpfield = Module.class.getDeclaredField("communicationAgent");
		tmpfield.setAccessible(true);
		tmpfield.set(Module.getLocalModule(), commAgg);
		utilAgent = (UtilAgent) server.loadAgent(UtilAgent.class.getCanonicalName(), null);

		try {
			Thread.sleep(ONE_SECOND); // this is not nice, but as this runs asynchrounous, we might run into
										// strange
			// raceconditions.
		} catch (InterruptedException e) {
		}

	}

	/**
	 * Helper used to setup an execution helper to be send to utilAgent.
	 * 
	 * @param toRun
	 *            the runnable
	 * @return when the executionHelper returns a value, or never.
	 */
	public boolean utilAgentSetupHelper(final ExecutionHelper toRun) {
		toRun.returnId = randomGen.nextInt();

		utilAgent.addRunnable(toRun);

		Boolean result = utilAgent.getSuccess(toRun.returnId);
		while (result == null) {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			result = utilAgent.getSuccess(toRun.returnId);
		}

		return result;

	}

	/**
	 * check whether doRun was run successfully.
	 */
	@Test(timeout = USUAL_TIMEOUT)
	public void testDoRunCheckup() {
		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {

					// actual testcode.
					for (ITuple tup : getModuleSpace().rdg(new AgentLocation(commAgg.getMgr().getID()),
							AgentLocation.UNSPECIFIED, new GlobalModuleMessage().getTemplate())) {
						GlobalModuleMessage msg = new GlobalModuleMessage(tup);

						Field tmpfield = Module.class.getDeclaredField("localModule");
						tmpfield.setAccessible(true);

						Module ourModule = (Module) tmpfield.get(null);
						ModuleConfig msgConfig = msg.getModule().getModuleConfig();

						if (!(msg.getModule().getID() == ourModule.getID())
								|| !(msgConfig.getCpuMHz() == TestModule.TEST_MHZ)
								|| !(msgConfig.getLocation().equals(TestModule.TEST_LOCATION))
								|| !(msgConfig.getMaxNumberOfBlocks() == TestModule.TEST_BLOCK_NUMBER)
								|| !(msgConfig.getMemory() == TestModule.TEST_MEMORY)
								|| !(msgConfig.getName().equals(TestModule.TEST_NAME))) {
							continue;
						}

						List<String> blocks = new LinkedList<>();
						blocks.add("block1");
						blocks.add("block2");
						blocks.add("block2");
						blocks.add("block3");
						for (String blk : msg.getModule().getModuleConfig().getSupportedBlockId()) {
							if (!blocks.remove(blk)) {
								continue;
							}
						}
						if (!blocks.isEmpty()) {
							continue;
						}
						return true;
					}

					return false;
					//
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return false;
			}
		};

		assertTrue(utilAgentSetupHelper(toRun));

	}

	/**
	 * Test joining an application.
	 */
	@Test(timeout = USUAL_TIMEOUT * 2)
	public void testJoinApplication() {
		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {

					// actual testcode.

					GlobalJoinMessage msg = new GlobalJoinMessage(TestModule.TEST_APPLICATION_ID, utilAgent
							.getMgr().getID());
					getModuleSpace().out(new AgentLocation(commAgg.getMgr().getID()), msg.getTuple());
					Thread.sleep(ONE_SECOND);

					LimeTupleSpace applicationSpace = null;
					applicationSpace = new LimeTupleSpace("ApplicationSpace" + TestModule.TEST_APPLICATION_ID);
					applicationSpace.setShared(true);
					LimeServer.getServer().engage();

					ITuple tup = applicationSpace.rd(HostLocation.UNSPECIFIED, AgentLocation.UNSPECIFIED,
							new ApplicationModuleMessage().getTemplate());
					// fails on timeout
					return true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return false;
			}
		};

		assertTrue(utilAgentSetupHelper(toRun));

	}

	/**
	 * test execution of ModuleChanged method for thrown exceptions.
	 */
	@Test(timeout = USUAL_TIMEOUT)
	public void testModuleChanged() {
		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {

					// actual testcode.
					Method method = commAgg.getClass().getDeclaredMethod("moduleChanged");
					method.setAccessible(true);
					method.invoke(commAgg);

					//
					return true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return false;
			}
		};

		assertTrue(utilAgentSetupHelper(toRun));

	}

	/**
	 * clean up after the class.
	 */
	@AfterClass
	public static void exitClass() {
		Method method;
		try {
			method = commAgg.getClass().getDeclaredMethod("shutdown");
			method.setAccessible(true);
			method.invoke(commAgg);
		} catch (
				NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

		if (server != null) {
			server.disengage();
			server.shutdown(false);
		}
	}

}
