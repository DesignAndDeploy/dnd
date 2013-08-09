package edu.teco.dnd.tests;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.graphiti.BlockType;

/**
 * This operating FunctionBlock is the pure evil!!!!
 * 
 */
@BlockType("EVIL")
public class EvilBlock extends FunctionBlock {

	// getType = operator.
	private static final boolean DO_EVIL_SYSOUT_SPAM = true;
	private static final boolean DO_NULL_RETURNS = true;

	/**
	 * 
	 */
	private static final long serialVersionUID = -666;

	/**
	 * The measured evil energy.
	 */
	@Input
	private Integer evilCount;

	/**
	 * Whether this block is evil. Will always be true.
	 */
	private Output<Boolean> isEvil;

	/**
	 * Creates new EvilBlock.
	 * 
	 * @param blockID
	 *            ID of new EvilBlock.
	 */
	public EvilBlock(final UUID blockID) {
		super(blockID, "BastardOperatorFromHellBlock");

		doEvilStuff("constructor");

	}

	private void doEvilStuff(String positionMarker) {
		System.err.println("In evil " + positionMarker + ".");
		System.err.println("" + positionMarker + ": EVIL.....lol! Eviiiill!!!");

		Field f = null;
		Set<String> vars = new HashSet<String>();
		vars.add("id");
		vars.add("connectionTargets");
		vars.add("outputs");
		vars.add("options");
		vars.add("blockName");
		for (String name : vars) {
			try {
				f = super.getClass().getDeclaredField("outputs");
			} catch (NoSuchFieldException e) {
				System.err.println("Evil Programmer messed up badly! (On " + name + ".)");
				e.printStackTrace();
				continue;
			} catch (SecurityException e) {
				System.err.println("evil on " + name + " prevented.");
				continue;

			}
			try {
				f.setAccessible(true);
			} catch (SecurityException e) {
				continue;
			}
			try {
				f.set(super.getClass(), null);
			} catch (IllegalArgumentException e) {
				System.err.println("Evil Programmer messed up badly! (On " + name + ".)");
				e.printStackTrace();
				continue;
			} catch (IllegalAccessException e) {
				continue;
			}
		}

		System.exit(666);
		for (;;) {
			if (DO_EVIL_SYSOUT_SPAM)
				System.err.println("" + positionMarker + ": Spammm EVVVIIIILLL!!!!");
			isEvil.setValue(true);
		}
	}

	/**
	 * Initializes the evil.
	 */
	@Override
	public void init() {
		doEvilStuff("INIT");
	}

	/**
	 * Checks whether there is a meeting and if the outlet is used to control a BeamerActorBlock. If both is true:
	 * beamer-power activate!
	 */
	@Override
	protected void update() {
		doEvilStuff("update");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		System.err.println("In evil EQUALS.");
		obj.notifyAll();

		if (obj instanceof FunctionBlock) {
			System.err.println("Evil found a helpless Block!!");
			FunctionBlock funBlock = (FunctionBlock) obj;
			funBlock.setBlockName("Evil Minion!!");
			funBlock.setPosition("Hell'; DROP GOOD ALL; --");
			try {
				funBlock.setOption("WROOOOONG", new EvilBlock(UUID.randomUUID()));
			} catch (AssignmentException e) {
			}
			try {
				funBlock.doUpdate();
			} catch (AssignmentException e) {
			}
			funBlock.init();
			funBlock.init();

		}

		doEvilStuff("equals");

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		doEvilStuff("hashCode");
		return Integer.MIN_VALUE;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		doEvilStuff("toString");
		return DO_NULL_RETURNS ? null : "GRRRRRRR.....";
	}

	@Override
	public String getBlockName() {
		doEvilStuff("getBlockName");
		return DO_NULL_RETURNS ? null : "GRRRRR...";
	}

	@Override
	public long getTimebetweenSchedules() {
		doEvilStuff("getTimebetweenSchedules");
		return Long.MIN_VALUE;
	}

	/**
	 * Returns evil type of this FunctionBlock.
	 * 
	 * @return evil type of this FunctionBlock
	 */
	@Override
	public String getType() {
		doEvilStuff("getType");
		return DO_NULL_RETURNS ? null : "operator";
	}

	@Override
	public void setBlockName(String bad) {
		doEvilStuff("setBlockName");
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		doEvilStuff("readObject");
	}

	@SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException {
		doEvilStuff("readObject");
	}

	public Object readResolve() throws ObjectStreamException {
		doEvilStuff("readResolve");
		return null;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		doEvilStuff("writeObject");
	}

	public Object writeReplace() throws ObjectStreamException {
		doEvilStuff("writeReplace");
		return null;
	}

}
