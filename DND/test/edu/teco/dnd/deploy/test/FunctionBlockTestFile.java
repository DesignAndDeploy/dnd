package edu.teco.dnd.deploy.test;

import edu.teco.dnd.blocks.FunctionBlock;

public class FunctionBlockTestFile extends FunctionBlock {

	public FunctionBlockTestFile(String id, String type) {
		super(id);
		this.type = type;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;

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
