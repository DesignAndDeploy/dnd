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

	// getType = operator

	private static boolean DO_EVIL_ON_MODULE_ONLY = true;

	private static boolean BE_EVIL = true;
	private static boolean DO_EVIL_SYSOUT_SPAM = true;
	private static boolean DO_NULL_RETURNS = true;

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

		if (DO_EVIL_ON_MODULE_ONLY && System.getSecurityManager() == null) {
			BE_EVIL = false;
		} else {
			BE_EVIL = true;
		}

		doEvilStuff("constructor");

	}

	private void doEvilStuff(String positionMarker) {
		if (!BE_EVIL)
			return;

		System.err.println("In evil " + positionMarker + ".");

		Field f = null;
		Set<String> vars = new HashSet<String>();
		vars.add("id");
		vars.add("connectionTargets");
		vars.add("outputs");
		vars.add("options");
		vars.add("blockName");
		for (String name : vars) {
			try {
				f = FunctionBlock.class.getDeclaredField("outputs");
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

		try {
			System.err.println("About to ragequit badly in "+ positionMarker + ".");
			System.exit(666);
		} catch (Exception e) {
			System.err.println("Ragequitting viciously prevented");
		}
		// throw new Error();

		// for (;;) {
		// if (DO_EVIL_SYSOUT_SPAM)
		// System.err.println("" + positionMarker + ": Spammm EVVVIIIILLL!!!!");
		// isEvil.setValue(true);
		// }
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
		if (!BE_EVIL)
			return super.equals(obj);

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
		if (!BE_EVIL)
			return super.hashCode();
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
		if (!BE_EVIL)
			return super.toString();
		doEvilStuff("toString");
		return DO_NULL_RETURNS ? null : "GRRRRRRR.....";
	}

	@Override
	public String getBlockName() {
		if (!BE_EVIL)
			return super.getBlockName();
		doEvilStuff("getBlockName");
		return DO_NULL_RETURNS ? null : "grrrr";
	}

	@Override
	public long getTimebetweenSchedules() {
		if (!BE_EVIL)
			return super.getTimebetweenSchedules();
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
		if (!BE_EVIL)
			return "operator";
		doEvilStuff("getType");
		return DO_NULL_RETURNS ? null : "operator";
	}

	@Override
	public void setBlockName(String bad) {
		if (!BE_EVIL)
			super.setBlockName(bad);
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
