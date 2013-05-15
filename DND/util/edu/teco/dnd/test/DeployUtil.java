package edu.teco.dnd.test;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import lime.AgentID;
import lime.LimeServer;
import lime.PropertyKeys;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.deploy.DeployListener;
import edu.teco.dnd.deploy.DeploymentAgent;
import edu.teco.dnd.deploy.DistributionAlgorithm;
import edu.teco.dnd.discover.Discover;
import edu.teco.dnd.discover.DiscoverListener;
import edu.teco.dnd.module.Module;

import org.apache.logging.log4j.LogManager;

/**
 * Provides functionality to deploy an application using template methods.
 * 
 * @author philipp
 */
public abstract class DeployUtil {
	public static void bootLimeServer(String address) {
		LogManager.getLogger(DeployUtil.class).entry(address);
		LimeServer server = LimeServer.getServer();
		server.setProperty(PropertyKeys.GM_DETECTORkey, "Beaconing");
		if (address != null) {
			server.setProperty(PropertyKeys.LOCALADDRkey, address);
		}
		server.boot();
		server.engage();
	}

	protected abstract Collection<FunctionBlock> getBlocks();

	protected abstract Integer getID();

	protected abstract String getName();

	protected abstract String[] getClasspath();

	public void startDeploy() {
		final Discover discover = Discover.getSingleton();
		final AtomicBoolean started = new AtomicBoolean(false);
		discover.addListener(new DiscoverListener() {
			@Override
			public void modulesDiscovered(Map<AgentID, Module> modules) {
				if (started.compareAndSet(false, true)) {
					final DiscoverListener listener = this;
					new Thread() {
						@Override
						public void run() {
							discover.removeListener(listener);
						}
					}.start();
					deploy(modules.values());
				}
			}

			@Override
			public void applicationsDiscovered(Map<Integer, String> applications) {
			}

			@Override
			public void applicationModulesDiscovered(int appID, Map<AgentID, Long> modules) {
			}
		});
		discover.startModuleDiscovery();
	}

	private void deploy(Collection<Module> modules) {
		LogManager.getLogger(DeployUtil.class).debug("{}", modules);
		DistributionAlgorithm distributionAlgorithm = new DistributionAlgorithm();
		final Map<FunctionBlock, Module> plan = distributionAlgorithm.evaluate(distributionAlgorithm
				.getDistributionPlans(getBlocks(), modules));
		if (plan == null) {
			System.err.println("no valid plan found");
			LimeServer.getServer().shutdown(false);
			System.exit(1);
		}
		DeploymentAgent deploymentAgent = DeploymentAgent.createAgent(plan, getName(), getID(),
				getClasspath());
		deploymentAgent.addListener(new DeployListener() {
			@Override
			public void updateDeployStatus(int classesLoaded, int blocksStarted) {
				System.out.println(classesLoaded + " classes loaded, " + blocksStarted + " blocks started");
				if (classesLoaded >= plan.size() && blocksStarted >= plan.size()) {
					System.out.println("deploy successful");
					new Thread() {
						@Override
						public void run() {
							LogManager.getLogger(DeployUtil.class).entry();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							LogManager.getLogger(DeployUtil.class).info("shutting down");
							new Thread() {
								@Override
								public void run() {
									try {
										Thread.sleep(2000);
									} catch (InterruptedException e) {
										LogManager.getLogger(DeployUtil.class).catching(e);
									}
									LimeServer.getServer().shutdown(true);
								}
							}.start();
						}
					}.start();
				}
			}

			@Override
			public void deployError(String message) {
				System.err.println("deploy failed: " + message);
				LimeServer.getServer().shutdown(true);
			}
		});
	}
}
