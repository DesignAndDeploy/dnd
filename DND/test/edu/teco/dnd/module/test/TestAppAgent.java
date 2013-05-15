package edu.teco.dnd.module.test;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import lights.interfaces.ITuple;
import lime.AgentLocation;
import lime.LimeServer;
import lime.LimeTupleSpace;
import mucode.ClassSpace;
import mucode.MuServer;
import mucode.util.ClassInspector;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.messages.ApplicationBlockMessage;
import edu.teco.dnd.messages.ApplicationClassLoadedMessage;
import edu.teco.dnd.messages.ApplicationKillMessage;
import edu.teco.dnd.messages.ApplicationLoadClassErrorMessage;
import edu.teco.dnd.messages.ApplicationLoadClassMessage;
import edu.teco.dnd.messages.ApplicationModuleMessage;
import edu.teco.dnd.messages.ApplicationStartBlockMessage;
import edu.teco.dnd.messages.ApplicationValueMessage;
import edu.teco.dnd.messages.GlobalApplicationMessage;
import edu.teco.dnd.module.ApplicationAgent;
import edu.teco.dnd.module.CommunicationAgent;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleConfig;
import edu.teco.dnd.module.RemoteConnectionTarget;
import edu.teco.dnd.util.MuServerProvider;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the AppAgent class.
 */
@SuppressWarnings("static-method")
public class TestAppAgent implements Serializable {

	private static LimeServer server = null;
	private static CommunicationAgent commAgg = null;
	private static ApplicationAgent appAgent = null;
	private static final long ONE_SECOND = 1000L;
	private static final long USUAL_TIMEOUT = 3L * ONE_SECOND;
	private static final Random randomGen = new Random();
	/**
	 * Agent used to execute things that need a StationaryAgent.
	 */
	private static UtilAgent utilAgent;

	/**
	 * init function.
	 * 
	 * @throws Exception
	 *             We don't want error handling here.
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

		Serializable[] param = { TestModule.TEST_APPLICATION_ID, utilAgent.getMgr().getID() };
		appAgent = (ApplicationAgent) server.loadAgent("edu.teco.dnd.module.ApplicationAgent", param);

		try {
			Thread.sleep(ONE_SECOND); // this is not nice, but as this runs asynchrounous, we might run into
										// strange
			// raceconditions otherwise.
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
	 * An artifical FunctionBlock for testing.
	 */
	class TestFuncBlock extends FunctionBlock {
		/** type the block claims to be of. */
		private final String type;
		/** value the output was set to last time it was changed. */
		private boolean outValue = false;
		/** wheather tests for remote value setting are enabled. */
		private boolean testSetRemote = false;

		/**
		 * @param id
		 *            the id to set
		 */
		public TestFuncBlock(final String id) {
			super(id);
			this.type = "block1";
		}

		/**
		 * @param id
		 *            the id of the funcBlock
		 * @param type
		 *            the type of the funcBlock
		 * @param testRemoteValue
		 *            if we want to test setting of remote value.
		 */
		public TestFuncBlock(final String id, final String type, final boolean testRemoteValue) {
			super(id);
			this.type = type;
			testBoolOut.addConnection(new RemoteConnectionTarget("testConnectionTarget", id, "testBoolIn",
					Boolean.class));
			testSetRemote = testRemoteValue;
		}

		@Override
		public String getType() {
			return type;
		}

		@Override
		public void init() {
			if (testSetRemote) {
				testBoolOut.setValue(true);
				outValue = true;
			}
		}

		@Override
		protected void update() {
			if (testSetRemote) {
				if (testBoolIn != outValue) {
					throw new Error("Setting Remote Value did not work properly.");
				}
			}
		}

		private Output<Boolean> testBoolOut = new Output<>("testBoolOut");

		@Input
		private Boolean testBoolIn = false;

	}

	/**
	 * Test for Constructor with nullpointer.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor1() {
		new ApplicationAgent(null, utilAgent.getMgr().getID());
	}

	/**
	 * Test for Constructor with nullpointer.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor2() {
		new ApplicationAgent(1, null);
	}

	/**
	 * Test load non existing class.
	 */
	@Test(timeout = 35000)
	// the wait time before class loading fails is 30s so to test failure off classloading we wait longer.
	public void testLoadClassWrongClass() {

		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {

					// actual testcode

					MuServer muserver = MuServerProvider.getMuServer();

					Set<String> classNames = new HashSet<>();
					classNames.add("INVALID D:");
					ApplicationLoadClassMessage msg = new ApplicationLoadClassMessage(classNames, LimeServer
							.getServer().getLocalAddress().getHostAddress()
							+ ":" + muserver.getPort(), "INVALID D:");
					getAppSpace().out(new AgentLocation(appAgent.getMgr().getID()), msg.getTuple());
					getAppSpace().rd(AgentLocation.UNSPECIFIED, AgentLocation.UNSPECIFIED,
							new ApplicationLoadClassErrorMessage().getTemplate()); // returns iff everything
																					// went well.
					return true;
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
	 * test loading of a correct class.
	 */
	@Test(timeout = USUAL_TIMEOUT)
	public void testLoadClass() {

		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {

					// actual testcode

					MuServer muserver = MuServerProvider.getMuServer();
					ClassSpace priv = muserver.getPrivateClassSpace();
					ClassSpace shared = muserver.getSharedClassSpace();

					Class<?> cls = TestAppAgent.class;
					String[] cp = { "./testbin/" };

					Class<?>[] classes = new Class[] { cls };
					classes = ClassInspector.getFullClassClosure(cls.getClassLoader(), cp, cls, muserver);

					Set<String> classNames = new HashSet<>();
					classNames.add(cls.getName());
					for (Class<?> c : classes) {
						classNames.add(c.getName());
						if (shared.containsClass(c.getName())) {
							continue;
						}
						priv.copyClassTo(c.getClassLoader(), cp, c.getName(), shared);
					}

					ApplicationLoadClassMessage msg = new ApplicationLoadClassMessage(classNames, LimeServer
							.getServer().getLocalAddress().getHostAddress()
							+ ":" + muserver.getPort(), cls.getName());

					getAppSpace().out(new AgentLocation(appAgent.getMgr().getID()), msg.getTuple());

					getAppSpace().rd(AgentLocation.UNSPECIFIED, AgentLocation.UNSPECIFIED,
							new ApplicationClassLoadedMessage().getTemplate()); // returns iff everything
																				// went well.

					return true;
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
	 * test starting of a block.
	 */
	@Test(timeout = USUAL_TIMEOUT)
	public void testStartBlock() {

		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {

					// actual testcode.

					ApplicationStartBlockMessage msg = new ApplicationStartBlockMessage(new TestFuncBlock(
							"1234"));
					getAppSpace().out(new AgentLocation(appAgent.getMgr().getID()), msg.getTuple());
					getAppSpace().rd(AgentLocation.UNSPECIFIED, AgentLocation.UNSPECIFIED,
							new ApplicationBlockMessage().getTemplate()); // returns iff everything
																			// went well.
					return true;
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
	 * test setting of a remote value.
	 */
	@Test(timeout = USUAL_TIMEOUT)
	public void testSetRemoteValue() {
		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {

					// actual testcode.

					ApplicationStartBlockMessage msg = new ApplicationStartBlockMessage(new TestFuncBlock(
							"4321", "block2", true));
					getAppSpace().out(new AgentLocation(appAgent.getMgr().getID()), msg.getTuple());
					getAppSpace().rd(AgentLocation.UNSPECIFIED, AgentLocation.UNSPECIFIED,
							new ApplicationBlockMessage().getTemplate()); // returns iff everything
																			// went well.
					return true;
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
	 * test calling setLocalValue.
	 * 
	 * @throws Exception
	 *             not handled.
	 */
	@Test(timeout = USUAL_TIMEOUT)
	public void testSetLocalValue() throws Exception {
		ApplicationValueMessage msg = new ApplicationValueMessage("wrongblock", "noinput", new Integer(4));

		Method method = appAgent.getClass().getDeclaredMethod("setLocalValue", ApplicationValueMessage.class);
		method.setAccessible(true);
		method.invoke(appAgent, msg);
	}

	/**
	 * test the kill command. (timeout is higher because killing can take a little longer)
	 */
	@Test(timeout = USUAL_TIMEOUT * 3)
	public void testKill() {

		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {

					Serializable[] param = { TestModule.TEST_APPLICATION_ID + 1, utilAgent.getMgr().getID() };
					ApplicationAgent killAppAgent = (ApplicationAgent) LimeServer.getServer().loadAgent(
							"edu.teco.dnd.module.ApplicationAgent", param);

					LimeTupleSpace killAppSpace = new LimeTupleSpace("ApplicationSpace"
							+ (TestModule.TEST_APPLICATION_ID + 1));

					killAppSpace.setShared(true);
					LimeServer.getServer().engage();

					ITuple globalAppMsg = new GlobalApplicationMessage("killName",
							TestModule.TEST_APPLICATION_ID + 1).getTuple();
					getModuleSpace().out(new AgentLocation(commAgg.getMgr().getID()), globalAppMsg);

					try {
						Thread.sleep(ONE_SECOND);
					} catch (InterruptedException e) {
					}

					ApplicationStartBlockMessage appStartMsg = new ApplicationStartBlockMessage(
							new TestFuncBlock("42 42", "block2", false));
					killAppSpace
							.out(new AgentLocation(killAppAgent.getMgr().getID()), appStartMsg.getTuple());
					killAppSpace.rd(AgentLocation.UNSPECIFIED, AgentLocation.UNSPECIFIED,
							new ApplicationBlockMessage().getTemplate());

					ApplicationKillMessage appKillMsg = new ApplicationKillMessage();
					killAppSpace.out(new AgentLocation(killAppAgent.getMgr().getID()), appKillMsg.getTuple());

					while (null != getModuleSpace().rdp(new AgentLocation(killAppAgent.getMgr().getID()),
							AgentLocation.UNSPECIFIED, globalAppMsg)) {
						Thread.sleep(ONE_SECOND); // times out if touple can not be removed.

					}
					return true;
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
	 * Test if the initialization of the appAgent as done properly.
	 */
	@Test(timeout = USUAL_TIMEOUT)
	public void testDoRunCheckup() {
		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {

					// actual testcode.
					getAppSpace().rd(new AgentLocation(appAgent.getMgr().getID()), AgentLocation.UNSPECIFIED,
							new ApplicationModuleMessage().getTemplate());

					// fails only on timeout
					return true;
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
	 * clean up lime.
	 */
	@AfterClass
	public static void exitClass() {
		if (server != null) {
			server.disengage();
			server.shutdown(false);
		}
	}

}
