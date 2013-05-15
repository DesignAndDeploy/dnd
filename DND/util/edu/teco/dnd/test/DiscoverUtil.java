package edu.teco.dnd.test;

import java.util.Map;

import lime.AgentCreationException;
import lime.AgentID;
import lime.LimeServer;
import lime.PropertyKeys;

import edu.teco.dnd.discover.Discover;
import edu.teco.dnd.discover.DiscoverListener;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simple main class that issues different discoveries.
 * 
 * @author philipp
 * 
 */
public class DiscoverUtil implements DiscoverListener {
	private static final Logger LOGGER = LogManager.getLogger(DiscoverUtil.class);

	private enum Task {
		MODULES, APPLICATIONS, SCAN
	}

	private static final String ARGUMENT_MODULES = "--discoverModules";
	private static final String ARGUMENT_APPLICATIONS = "--discoverApplications";
	private static final String ARGUMENT_SCAN = "--scanApplication";
	private static final String ARGUMENT_ADDRESS = "--address";

	public DiscoverUtil(String address) {
		LimeServer server = LimeServer.getServer();
		server.setProperty(PropertyKeys.GM_DETECTORkey, "Beaconing");
		if (address != null) {
			server.setProperty(PropertyKeys.LOCALADDRkey, address);
		}
		server.boot();
		server.engage();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Discover discover = Discover.getSingleton();
		discover.addListener(this);
	}

	private final void discoverModules() {
		Discover.getSingleton().startModuleDiscovery();
	}

	private final void discoverApplications() {
		Discover.getSingleton().startApplicationDiscovery();
	}

	private final void scanApplication(int id) {
		Discover.getSingleton().startApplicationScan(id);
	}

	private static void printUsage() {
		System.err.println("Usage:");
		System.err.println("java " + DiscoverUtil.class.getName() + " " + ARGUMENT_MODULES + " ["
				+ ARGUMENT_ADDRESS + " address]");
		System.err.println("java " + DiscoverUtil.class.getName() + " " + ARGUMENT_APPLICATIONS + " ["
				+ ARGUMENT_ADDRESS + " address]");
		System.err.println("java " + DiscoverUtil.class.getName() + " " + ARGUMENT_SCAN + " ["
				+ ARGUMENT_ADDRESS + " address]");
		System.err.println();
		System.err
				.println("If "
						+ ARGUMENT_MODULES
						+ " is passed, the ModuleSpace is scanned for all modules and information about them is printed.");
		System.err
				.println("If "
						+ ARGUMENT_APPLICATIONS
						+ " is passed, the ModuleSpace is scanned for all running applications and their ids and names are printed");
		System.err
				.println("if "
						+ ARGUMENT_SCAN
						+ " is passed along with an id the ApplicationSpace for the given id is scanned and all modules and blocks are printed");
	}

	/**
	 * Starts different discoveries. Right now, only module discovery is supported.
	 * 
	 * @param args
	 * @throws AgentCreationException
	 */
	public static void main(String[] args) throws AgentCreationException {
		Task task = null;
		int id = 0;
		String address = null;
		int i = 0;
		while (i < args.length) {
			switch (args[i]) {
			case ARGUMENT_ADDRESS:
				if (i + 1 >= args.length) {
					System.err.println("missing argument for " + ARGUMENT_ADDRESS);
					System.exit(1);
				}
				address = args[i + 1];
				i++;
				break;

			case ARGUMENT_APPLICATIONS:
				task = Task.APPLICATIONS;
				break;

			case ARGUMENT_MODULES:
				task = Task.MODULES;
				break;

			case ARGUMENT_SCAN:
				if (i + 1 >= args.length) {
					System.err.println("missing argument for " + ARGUMENT_SCAN);
					System.exit(1);
				}
				task = Task.SCAN;
				try {
					id = Integer.valueOf(args[i + 1]);
				} catch (NumberFormatException e) {
					System.err.println("id is not a number");
					System.exit(1);
				}
				i++;
				break;

			default:
				System.err.println("unknown argument: '" + args[i] + "'");
				System.exit(1);
			}
			i++;
		}
		if (task == null) {
			printUsage();
			System.exit(1);
		}
		DiscoverUtil util = new DiscoverUtil(address);
		switch (task) {
		case APPLICATIONS:
			util.discoverApplications();
			break;

		case MODULES:
			util.discoverModules();
			break;

		case SCAN:
			util.scanApplication(id);
			break;
		}
	}

	@Override
	public void modulesDiscovered(Map<AgentID, Module> modules) {
		LOGGER.entry(modules);
		for (Module module : modules.values()) {
			ModuleConfig mc = module.getModuleConfig();
			System.out.println("module: " + mc.getName());
			System.out.println("	id: " + module.getID());
			System.out.println("	running: " + module.getIsRunning());
			System.out.println("	canRun: " + module.getCanRun());
			System.out.println("	cpu: " + mc.getCpuMHz());
			System.out.println("	memory: " + mc.getMemory());
			System.out.println("	location: " + mc.getLocation());
			System.out.println("	maxBlocks: " + mc.getMaxNumberOfBlocks());
		}
		shutdown();
		LOGGER.exit();
	}

	@Override
	public void applicationModulesDiscovered(int appID, Map<AgentID, Long> modules) {
		LOGGER.entry(appID, modules);
		for (Long id : modules.values()) {
			System.out.println(id);
		}
		shutdown();
		LOGGER.exit();
	}

	@Override
	public void applicationsDiscovered(Map<Integer, String> applications) {
		LOGGER.entry(applications);
		for (Map.Entry<Integer, String> entry : applications.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
		shutdown();
		LOGGER.exit();
	}

	private void shutdown() {
		new Thread() {
			@Override
			public void run() {
				LOGGER.entry();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					LOGGER.catching(e);
				}
				LOGGER.info("shutting down");
				LimeServer.getServer().shutdown(true);
				LOGGER.exit();
			}
		}.start();
	}
}
