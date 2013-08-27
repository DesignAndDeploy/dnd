package edu.teco.dnd.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.deploy.Distribution;
import edu.teco.dnd.deploy.Distribution.BlockTarget;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.server.DistributionCreator;
import edu.teco.dnd.server.ModuleManager;
import edu.teco.dnd.server.NoBlocksException;
import edu.teco.dnd.server.NoModulesException;
import edu.teco.dnd.server.ServerManager;

/**
 * This class provides the loop to be run in the command line program, after the necessary variables have been
 * initialized.
 * 
 * @author jung
 * 
 */
public class CommandLoop {

	/**
	 * To be entered by user to create and display a distribution.
	 */
	public static final String CREATE = "create";

	/**
	 * To be entered by user do create, display and deploy a distribution.
	 */
	public static final String DEPLOY = "deploy";

	/**
	 * To be entered by user to quit the program.
	 */
	public static final String QUIT = "quit";

	Distribution dist;
	ModuleManager moduleManager;
	Collection<FunctionBlockModel> blocks;

	public CommandLoop(Collection<FunctionBlockModel> functionBlocks) {
		moduleManager = ServerManager.getDefault().getModuleManager();
		blocks = functionBlocks;
	}

	public void loop(String initialInput) {
		boolean done = false;
		String getInput = initialInput;
		while (!done) {
			if (QUIT.equals(getInput)) {
				done = true;
				exit();
				break;
			} else if (CREATE.equals(getInput)) {
				dist = createDistribution();
				if (distributionSucceeded(dist) && askForUserConfirm("Deploy now?")){
					deployDistribution(dist);
				}
			} else if (DEPLOY.equals(getInput)) {
				dist = createDistribution();
				if (distributionSucceeded(dist)) {
					deployDistribution(dist);
				}
			}
			getInput =
					askForUserInput("Enter 'create' to create a distribution, 'deploy' to create and directly deploy one, or 'quit' to quit.");
		}
	}

	/**
	 * Creates a distribution, if possible.
	 * 
	 * @return
	 */
	private Distribution createDistribution() {
		Map<UUID, Module> map = moduleManager.getMap();

		Distribution dist = null;
		try {
			dist = DistributionCreator.createDistribution(blocks, null);
		} catch (NoBlocksException e) {
			System.out.println("Function Block Loading not implemented yet.");
		} catch (NoModulesException e) {
			System.out.println("No running modules available. Wait for more modules to register.");
			System.out.println("Currently running:");
			for (UUID id : map.keySet()) {
				if (map.get(id) != null) {
					System.out.print(map.get(id).getName());
				}
				System.out.println(" : " + id.toString());
			}
		}
		return dist;
	}

	private boolean distributionSucceeded(Distribution dist) {
		if (dist == null){
			System.out.println("No distribution exists.");
			return false;
		}
		System.out.println("Distribution succeeded:");
		Map<FunctionBlockModel, BlockTarget> map = dist.getMapping();
		for (FunctionBlockModel block : map.keySet()) {
			Module mod = map.get(block).getModule();
			System.out.println(block.getBlockName() + " mapped to " + mod.getName() + " : " + mod.getUUID().toString());
		}
		return true;
	}

	private void deployDistribution(Distribution dist) {
		// TODO: Deploy distribution.
	}

	private String askForUserInput(String message) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String userInput = null;

		System.out.println(message);
		try {
			userInput = br.readLine();
		} catch (IOException e) {
		}
		if (QUIT.equals(userInput) || CREATE.equals(userInput) || DEPLOY.equals(userInput)) {
			return userInput;
		} else {
			System.out.println("No valid command.");
			return askForUserInput(message);
		}
	}

	/**
	 * Asks the user to confirm (Y) or abort (N) something.
	 * 
	 * @return true if user wants to to something.
	 */
	private boolean askForUserConfirm(String message) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String userInput = null;

		System.out.println(message);
		try {
			userInput = br.readLine();
		} catch (IOException e) {
		}
		if ("Y".equals(userInput) || "y".equals(userInput) || "Yes".equals(userInput) || "yes".equals(userInput)) {
			return true;
		} else if ("N".equals(userInput) || "n".equals(userInput) || "No".equals(userInput) || "no".equals(userInput)) {
			return false;
		} else {
			System.out.println("No valid command.");
			return askForUserConfirm(message);
		}
	}

	/**
	 * Called whenever Program is closed regularly (by user or because there's nothing more to do).
	 */
	private static void exit() {
		if (ServerManager.getDefault().isRunning()) {
			ServerManager.getDefault().shutdownServer();
		}
	}

}
