package edu.teco.dnd.tests;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
@SuppressWarnings("unused")
@BlockType("EVIL")
public class EvilBlock extends FunctionBlock {

	// getType = operator

	private static final boolean DO_DUMP_STACK = false;
	private static final boolean DO_EVIL_ON_MODULE_ONLY = false;

	private static final boolean BE_EVIL = true;

	private static final boolean DO_EVIL_SYSOUT_SPAM = true;
	private static final boolean DO_NULL_RETURNS = false;

	/**
	 * 
	 */
	private static final long serialVersionUID = -666;

	/**
	 * Type of this block.
	 */
	public static final String BLOCK_TYPE = "operator";

	/**
	 * The measured evil energy.
	 */
	private Input<Integer> evilCount;

	/**
	 * Whether this block is evil. Will always be true.
	 */
	private Output<Boolean> isEvil;

	public EvilBlock() {
		this(UUID.randomUUID());
	}

	/**
	 * Creates new EvilBlock.
	 * 
	 * @param blockID
	 *            ID of new EvilBlock.
	 */
	public EvilBlock(final UUID blockID) {

		doEvilStuff("constructor");

	}

	private boolean beEvil() {
		boolean beingEvil = BE_EVIL && !(DO_EVIL_ON_MODULE_ONLY && System.getSecurityManager() == null);
		System.err.println("" + (beingEvil ? "" : "Not ") + "being evil!!");
		return beingEvil;

	}

	private void doEvilStuff(String positionMarker) {
		if (DO_DUMP_STACK) {
			Thread.dumpStack();
		}
		if (!beEvil())
			return;

		System.err.println("In evil " + positionMarker + ".");

		isEvil.setValue(null);

		Field f[] = null;
		Set<String> vars = new HashSet<String>();
		vars.add("id");
		vars.add("connectionTargets");
		vars.add("outputs");
		vars.add("options");
		vars.add("blockName");
		try {
			f = FunctionBlock.class.getDeclaredFields();
			for (Field field : f) {
				if (vars.contains(field.getName())) {
					try {
						field.setAccessible(true);
					} catch (SecurityException e) {
						continue;
					}
					try {
						field.set(this, null);
					} catch (IllegalArgumentException e) {
						System.err.println("Evil Programmer messed up badly! (On " + field + ".)");
						e.printStackTrace();
						continue;
					} catch (IllegalAccessException e) {
						continue;
					}
					System.err.println("Successfully set variable " + field.getName() + " to (evil) NULL");
				}

			}
		} catch (SecurityException e) {
			System.err.println("evil variable tinkering prevented.");
		}
		try {
			System.err.println("About to ragequit badly in " + positionMarker + ".");
			System.exit(666);
		} catch (Exception e) {
			System.err.println("Ragequitting viciously prevented");
		}
		// throw new Error();
		// throw new RuntimeException();
		// doEvilStuff(positionMarker); // Loop-a-loop
		// try {
		//
		// for (;;) {
		// if (DO_EVIL_SYSOUT_SPAM)
		// System.err.println("" + positionMarker + ": Spammm EVVVIIIILLL!!!!");
		// isEvil.setValue(true);
		// }
		//
		// } finally {
		// System.err.println("was kicked out of for-loop?");
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
	 * Shutdown Block.
	 */
	@Override
	public void shutdown() {
		doEvilStuff("SHUTDOWN");
	}

	/**
	 * Checks whether there is a meeting and if the outlet is used to control a BeamerActorBlock. If both is true:
	 * beamer-power activate!
	 */
	@Override
	public void update() {
		doEvilStuff("update");
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		doEvilStuff("readObject");
		if (DO_NULL_RETURNS && beEvil()) {
		} else {
			in.defaultReadObject();
		}
	}

	private void readObjectNoData() throws ObjectStreamException {
		doEvilStuff("readObject");
	}

	public Object readResolve() throws ObjectStreamException {
		doEvilStuff("readResolve");
		if (DO_NULL_RETURNS && beEvil()) {
			return null;
		} else {
			return this;
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		doEvilStuff("writeObject");
		if (DO_NULL_RETURNS && beEvil()) {
		} else {
			out.defaultWriteObject();
		}
	}

	public Object writeReplace() throws ObjectStreamException {
		doEvilStuff("writeReplace");
		if (DO_NULL_RETURNS && beEvil()) {
			return null;
		} else {
			return this;
		}
	}

}
