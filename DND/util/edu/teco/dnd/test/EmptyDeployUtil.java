package edu.teco.dnd.test;

import java.util.ArrayList;
import java.util.Collection;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.test.EmptyFunctionBlock;

public class EmptyDeployUtil extends DeployUtil {
	private final int id;

	public EmptyDeployUtil(int id) {
		this.id = id;
	}

	@Override
	protected Collection<FunctionBlock> getBlocks() {
		Collection<FunctionBlock> blocks = new ArrayList<>();
		blocks.add(new EmptyFunctionBlock("empty"));
		return blocks;
	}

	@Override
	protected Integer getID() {
		return id;
	}

	@Override
	protected String getName() {
		return "empty";
	}

	@Override
	protected String[] getClasspath() {
		return null;
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: " + EmptyDeployUtil.class.getName() + " id address");
			System.exit(1);
		}
		int id = Integer.valueOf(args[0]);
		DeployUtil.bootLimeServer(args[1]);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		new EmptyDeployUtil(id).startDeploy();
	}
}
