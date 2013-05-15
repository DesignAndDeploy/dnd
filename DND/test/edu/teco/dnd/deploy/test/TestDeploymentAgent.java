package edu.teco.dnd.deploy.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import lights.interfaces.ITuple;
import lime.AgentCreationException;
import lime.AgentLocation;
import lime.LimeServer;
import lime.LimeTupleSpace;
import lime.TupleSpaceEngineException;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.test.EmptyFunctionBlock;
import edu.teco.dnd.deploy.DeploymentAgent;
import edu.teco.dnd.messages.ApplicationBlockMessage;
import edu.teco.dnd.messages.ApplicationBlockStartErrorMessage;
import edu.teco.dnd.messages.ApplicationClassLoadedMessage;
import edu.teco.dnd.messages.ApplicationLoadClassErrorMessage;
import edu.teco.dnd.messages.ApplicationLoadClassMessage;
import edu.teco.dnd.messages.ApplicationModuleMessage;
import edu.teco.dnd.messages.ApplicationStartBlockMessage;
import edu.teco.dnd.messages.GlobalApplicationMessage;
import edu.teco.dnd.messages.GlobalJoinMessage;
import edu.teco.dnd.messages.GlobalModuleMessage;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleConfig;
import edu.teco.dnd.module.test.ExecutionHelper;
import edu.teco.dnd.module.test.HashMapConfigFile;
import edu.teco.dnd.module.test.TestModule;
import edu.teco.dnd.module.test.UtilAgent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDeploymentAgent {

	private static DeploymentAgent depAgent;
	private static DeploymentAgent depAgentSecond;
	private static ModuleTestFile module;
	private static FunctionBlock block;
	private static LimeServer server;
	private static Map<FunctionBlock, Module> plan;
	private static HashMapConfigFile config;
	private static ModuleConfig moduleConfig;
	private static UtilAgent utilAgent;
	private static int APP_TEST_ID = 1;
	private static int APP_TEST_ID_2 = 2;
	private static String APP_TEST_NAME = "TEST_NAME_1";
	private static Long MODULE_TEST_ID = 1L;
	private static DeployListenertestFile listener;
	private static DeployListenertestFile listenerSecond;

	/**
	 * Initializes the server.
	 */
	@BeforeClass
	public static void init() {
		server = LimeServer.getServer();
		if (server.getServerID() == null) {
			server.boot();
		}

		config = new HashMapConfigFile();
		moduleConfig = new ModuleConfig();
		config.setProperty(ModuleConfig.NAME_ID, TestModule.TEST_NAME);
		config.setProperty(ModuleConfig.LOCATION_ID, TestModule.TEST_LOCATION);
		config.setProperty(ModuleConfig.BLOCK_NUMBER_ID, Integer.toString(TestModule.TEST_BLOCK_NUMBER));
		config.setProperty(ModuleConfig.MEMORY_ID, Integer.toString(TestModule.TEST_MEMORY));
		config.setProperty(ModuleConfig.MHZ_ID, Integer.toString(TestModule.TEST_MHZ));
		config.setProperty(ModuleConfig.BLOCKS_ID, TestModule.TEST_BLOCKS_STRING);
		moduleConfig.setAndReadConfig(config);
		plan = new HashMap<FunctionBlock, Module>();
		block = new EmptyFunctionBlock("testblock");

		module = new ModuleTestFile(MODULE_TEST_ID);
		plan.put(block, module);
		try {
			utilAgent = (UtilAgent) server.loadAgent(UtilAgent.class.getCanonicalName(), null);
		} catch (AgentCreationException e) {
			e.printStackTrace();
		}
		depAgent = DeploymentAgent.createAgent(plan, APP_TEST_NAME, APP_TEST_ID, null);
		listener = new DeployListenertestFile();
		depAgent.addListener(listener);

		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {
					getModuleSpace().out(new AgentLocation(depAgent.getMgr().getID()),
							new GlobalModuleMessage(module, utilAgent.getMgr().getID()).getTuple());
				} catch (TupleSpaceEngineException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		};
		utilAgentSetupHelper(toRun);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Tests whether the deploymentAgent sends a GlobalJoinMessage when started.
	 */
	@Test(timeout = 3000)
	public void testGlobalJoinMessageSending() {
		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {
					ITuple tuple = getModuleSpace().rd(new GlobalJoinMessage().getTemplate());
					if (tuple != null) {
						GlobalJoinMessage msg = new GlobalJoinMessage(tuple);
						if ((msg.getApplicationID() == APP_TEST_ID)
								|| (msg.getApplicationID() == APP_TEST_ID_2)) {
							return true;
						}
					}
				} catch (TupleSpaceEngineException e) {
					return false;
				}
				return false;
			}
		};

		assertTrue(utilAgentSetupHelper(toRun));
	}

	/**
	 * Tests whether the deploymentAgent sends a GlobalApplicationMessage when started.
	 */
	@Test(timeout = 3000)
	public void testGlobalApplicationMessageSending() {
		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {
					ITuple tuple = getModuleSpace().rd(new GlobalApplicationMessage().getTemplate());
					if (tuple != null) {
						GlobalApplicationMessage msg = new GlobalApplicationMessage(tuple);
						if (((msg.getApplicationID() == APP_TEST_ID) || (msg.getApplicationID() == APP_TEST_ID_2))
								&& (msg.getName().equals(APP_TEST_NAME))) {
							return true;
						}
					}
				} catch (TupleSpaceEngineException e) {
					return false;
				}
				return false;
			}
		};

		assertTrue(utilAgentSetupHelper(toRun));
	}

	/**
	 * Tests the JoinAppListener by sending an ApplicationModuleMessage and expecting an
	 * ApplicationLoadClassMessage as reply.
	 */
	@Test(timeout = 3000)
	public void testJoinAppListener() {

		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {
					getAppSpace().setShared(true);
					LimeServer.getServer().engage();
					getAppSpace().out(
							new AgentLocation(depAgent.getMgr().getID()),
							new ApplicationModuleMessage(MODULE_TEST_ID, utilAgent.getMgr().getID(),
									moduleConfig).getTuple());
					ITuple tuple = getAppSpace().in(new ApplicationLoadClassMessage().getTemplate());
					if (tuple != null) {
						return true;
					}
				} catch (TupleSpaceEngineException e) {
					return false;
				}
				return false;

			}
		};
		assertTrue(utilAgentSetupHelper(toRun));
	}

	/**
	 * Tests the LoadClassErrorListener by sending an ApplicationLoadClassErrorMessage and expecting an
	 * appropriate update of the used listener.
	 */
	@Test(timeout = 3000)
	public void testLoadClassErrorListener() {

		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {

				ApplicationLoadClassErrorMessage msg;
				try {
					getAppSpace().setShared(true);
					LimeServer.getServer().engage();
					msg = new ApplicationLoadClassErrorMessage(block.getClass().getName(), "testError",
							new Throwable(), MODULE_TEST_ID);
					getAppSpace().out(new AgentLocation(depAgent.getMgr().getID()), msg.getTuple());
				} catch (TupleSpaceEngineException e) {
					return false;
				}
				if (listener.error.equals("could not load " + block.getID() + " because the class "
						+ msg.getClassName() + " failed to load on " + msg.getModuleID())) {
					return true;
				}
				return false;

			}
		};
		assertTrue(utilAgentSetupHelper(toRun));
	}

	/**
	 * Tests the StartBlockErrorListener by sending an ApplicationBlockStartErrorMessage and expecting an
	 * appropriate update of the used listener.
	 */
	@Test(timeout = 3000)
	public void testStartBlockErrorListener() {

		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				ApplicationBlockStartErrorMessage msg;
				try {
					getAppSpace().setShared(true);
					LimeServer.getServer().engage();
					msg = new ApplicationBlockStartErrorMessage(block.getID(), "testError", new Throwable(
							"test"));
					getAppSpace().out(msg.getTuple());
				} catch (TupleSpaceEngineException e) {
					return false;
				}
				if (listener.error.equals("failed to start block " + msg.getBlockID() + ". Reason: "
						+ msg.getMessage() + " (caused by " + msg.getCause() + ")")) {
					return true;
				}
				return false;

			}
		};
		assertTrue(utilAgentSetupHelper(toRun));
	}

	/**
	 * Tests the JoinAppListener by sending an ApplicationClassLoadedMessage and expecting an
	 * ApplicationLoadClassMessage as reply (The sent ApplicationModuleMessage is needed so that the
	 * deploymentAgent knows the ID of the agent the message is sent to).
	 */
	@Test(timeout = 3000)
	public void testLoadClassListener() {
		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {
					getAppSpace().setShared(true);
					LimeServer.getServer().engage();
					getAppSpace().out(
							new AgentLocation(depAgent.getMgr().getID()),
							new ApplicationModuleMessage(MODULE_TEST_ID, utilAgent.getMgr().getID(),
									moduleConfig).getTuple());
					getAppSpace().out(
							new AgentLocation(depAgent.getMgr().getID()),
							new ApplicationClassLoadedMessage(MODULE_TEST_ID, block.getClass().getName())
									.getTuple());
					ITuple tuple = getAppSpace().in(new ApplicationStartBlockMessage().getTemplate());
					if (tuple != null) {
						return true;
					}
				} catch (TupleSpaceEngineException e) {
					return false;
				}
				return false;
			}
		};
		assertTrue(utilAgentSetupHelper(toRun));
	}

	/**
	 * Tests the StartBlockListener by sending an ApplicationBlockMessage and expecting an appropriate update
	 * of the used listener.
	 */
	@Test(timeout = 3000)
	public void testStartBlockListener() {
		depAgentSecond = DeploymentAgent.createAgent(plan, APP_TEST_NAME, APP_TEST_ID_2, null);
		listenerSecond = new DeployListenertestFile();
		depAgentSecond.addListener(listenerSecond);

		ExecutionHelper toRun = new ExecutionHelper() {

			@Override
			public boolean run() {
				try {
					LimeTupleSpace space = new LimeTupleSpace("ApplicationSpace" + APP_TEST_ID_2);
					space.setShared(true);
					LimeServer.getServer().engage();
					space.out(new AgentLocation(depAgentSecond.getMgr().getID()),
							new ApplicationBlockMessage(utilAgent.getMgr().getID(), block.getID()).getTuple());
				} catch (TupleSpaceEngineException e) {
					return false;
				}
				if ((listenerSecond.blocksStarted == 1) && (listenerSecond.classesLoaded == 1)) {
					depAgentSecond.removeListener(listenerSecond);
					return true;
				}
				return false;
			}
		};
		assertTrue(utilAgentSetupHelper(toRun));

	}

	/**
	 * Shuts the server down
	 */
	@AfterClass
	public static void shutdownServer() {
		if (server != null) {
			server.disengage();
			server.shutdown(false);
		}

	}

	public static boolean utilAgentSetupHelper(ExecutionHelper toRun) {
		toRun.returnId = new Random().nextInt();

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

}
