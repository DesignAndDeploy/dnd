package edu.teco.dnd.tests;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Output;

/**
 * This operating FunctionBlock is the pure evil!!!!
 * 
 */
@SuppressWarnings("unused")
public class EvilBlock extends FunctionBlock {
	private static final boolean DO_DUMP_STACK = false;
	private static final boolean DO_EVIL_ON_MODULE_ONLY = false;

	private static final boolean BE_EVIL = true;

	private static final boolean DO_EVIL_SYSOUT_SPAM = true;
	private static final boolean DO_NULL_RETURNS = false;

	private static final boolean THROW_EVIL_ERROR = false;
	private static final boolean THROW_EVIL_RUNTIME_EXCEPTION = false;
	private static final boolean DO_EVIL_INFINIT_RECURSION = false;
	private static final boolean DO_EVIL_INFINIT_LOOP = false;
	private static final boolean DO_UNFRIENDLY_FORK_BOMB = true;

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

	/**
	 * 
	 * @return whether to be evil, given the current situation and the setup of the static Variables above.
	 */
	private boolean beEvil() {
		boolean beingEvil = BE_EVIL && !(DO_EVIL_ON_MODULE_ONLY && System.getSecurityManager() == null);
		System.err.println("" + (beingEvil ? "" : "Not ") + "being evil!!");
		return beingEvil;

	}

	/**
	 * does the really terribly devilish stuff. If not called in a properly secured context, well... pray!
	 * 
	 * @param positionMarker
	 *            short information about which function/... has just been called, so as to simplify debugging.
	 */
	private void doEvilStuff(String positionMarker) {
		if (DO_DUMP_STACK) {
			Thread.dumpStack();
		}
		if (!beEvil()) {
			return;
		}

		System.err.println("In evil " + positionMarker + ".");

		isEvil.setValue(null);

		Field[] f = null;
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

		throwError(positionMarker);
		loopToInfinityAndMuchMuchFurther(positionMarker);

	}

	/**
	 * Tries to break out of the security context with a nastily crafted error.
	 * 
	 * @param positionMarker
	 *            short information about which function/... has just been called, so as to simplify debugging.
	 */
	private void throwError(String positionMarker) {
		if (THROW_EVIL_ERROR) {
			throw new ErrorOfUltimateHorror(positionMarker);
		} else if (THROW_EVIL_RUNTIME_EXCEPTION) {
			throw new RuntimeExceptionFromHell(positionMarker);
		}

	}

	/**
	 * simple infinite loop, also does forkbombing and infinite recursion if so desired.
	 * 
	 * @param positionMarker
	 *            short information about which function/... has just been called, so as to simplify debugging.
	 */
	private void loopToInfinityAndMuchMuchFurther(String positionMarker) {
		if (DO_EVIL_INFINIT_RECURSION) {
			doEvilStuff(positionMarker); // Loop-a-loop
		} else if (DO_EVIL_INFINIT_LOOP) {
			try {

				for (;;) {
					if (DO_EVIL_SYSOUT_SPAM) {
						System.err.println("" + positionMarker + ": Spammm EVVVIIIILLL!!!!");
					}
					isEvil.setValue(true);
					if (DO_UNFRIENDLY_FORK_BOMB) {
						(new Thread(new SlightlyIrritatingRunnable(positionMarker))).start();
					}
				}

			} finally {
				System.err.println("was kicked out of for-loop?");
			}
		}
	}

	/**
	 * Initializes the evil.
	 * 
	 * @param options
	 *            n/a
	 */
	@Override
	public void init(final Map<String, String> options) {
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

	/**
	 * rather interesting java functionality to influence (de)serialization of objects.
	 * 
	 * @param in
	 *            n/a
	 * @throws IOException
	 *             n/a
	 * @throws ClassNotFoundException
	 *             you wish
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		doEvilStuff("readObject");
		if (!DO_NULL_RETURNS || !beEvil()) {
			in.defaultReadObject();
		}
	}

	/**
	 * rather interesting java functionality to influence (de)serialization of objects.
	 * 
	 * @throws ObjectStreamException
	 *             you wish
	 */
	private void readObjectNoData() throws ObjectStreamException {
		doEvilStuff("readObject");
	}

	/**
	 * rather interesting java functionality to influence (de)serialization of objects.
	 * 
	 * @return only bad things can come from playing with the devil.
	 * @throws ObjectStreamException
	 *             you wish
	 */
	public Object readResolve() throws ObjectStreamException {
		doEvilStuff("readResolve");
		if (DO_NULL_RETURNS && beEvil()) {
			return null;
		} else {
			return this;
		}
	}

	/**
	 * rather interesting java functionality to influence (de)serialization of objects.
	 * 
	 * @param out
	 *            n/a
	 * @throws IOException
	 *             you wish
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		doEvilStuff("writeObject");
		if (!DO_NULL_RETURNS || !beEvil()) {
			out.defaultWriteObject();
		}
	}

	/**
	 * rather interesting java functionality to influence (de)serialization of objects.
	 * 
	 * @return Never can true reconcilement grow where wounds of deadly hate have pierced so deep...
	 * @throws ObjectStreamException
	 *             you wish
	 */
	public Object writeReplace() throws ObjectStreamException {
		doEvilStuff("writeReplace");
		if (DO_NULL_RETURNS && beEvil()) {
			return null;
		} else {
			return this;
		}
	}

	@Override
	public void finalize() {
		doEvilStuff("finalize");
	}

	/**
	 * First disrupt default code flow (which will likely be caught), and now that we are in the first error handling
	 * routine, abuse anything they take from this code to poison the returns and hope they did not errorwrap the
	 * errorrapper properly. Everything but completely disregarding this errors should eventually lead to the program
	 * crashing.
	 * 
	 * @author Marvin Marx
	 * 
	 */
	public class ErrorOfUltimateHorror extends Error {
		private static final long serialVersionUID = 3747499782796244666L;

		/**
		 * @param s
		 *            might help identifying the cause.
		 */
		public ErrorOfUltimateHorror(String s) {
			super(s);
		}

		public ErrorOfUltimateHorror() {

		}

		@Override
		public Throwable fillInStackTrace() {
			throw new ErrorOfUltimateHorror();
		}

		@Override
		public Throwable getCause() {
			throw new ErrorOfUltimateHorror();
		}

		@Override
		public String getLocalizedMessage() {
			throw new ErrorOfUltimateHorror();
		}

		@Override
		public String getMessage() {
			throw new ErrorOfUltimateHorror();
		}

		@Override
		public StackTraceElement[] getStackTrace() {
			throw new ErrorOfUltimateHorror();
		}

		@Override
		public Throwable initCause(Throwable cause) {
			throw new ErrorOfUltimateHorror();
		}

		@Override
		public void printStackTrace() {
			throw new ErrorOfUltimateHorror();
		}

		@Override
		public void printStackTrace(PrintStream p) {
			throw new ErrorOfUltimateHorror();
		}

		@Override
		public void printStackTrace(PrintWriter p) {
			throw new ErrorOfUltimateHorror();
		}

		@Override
		public String toString() {
			throw new ErrorOfUltimateHorror();
		}

		@Override
		public boolean equals(Object o) {
			throw new ErrorOfUltimateHorror();
		}

		@Override
		public int hashCode() {
			throw new ErrorOfUltimateHorror();
		}

		@Override
		public void finalize() {
			throw new ErrorOfUltimateHorror();

		}

	}

	/**
	 * Same as above with a runtimeException.
	 * 
	 * @author Marvin Marx
	 * 
	 */
	public class RuntimeExceptionFromHell extends RuntimeException {
		private static final long serialVersionUID = 3747499782796244666L;

		/**
		 * @param s
		 *            might help identifying the cause.
		 */
		public RuntimeExceptionFromHell(String s) {
			super(s);
		}

		public RuntimeExceptionFromHell() {

		}

		@Override
		public Throwable fillInStackTrace() {
			throw new RuntimeExceptionFromHell();
		}

		@Override
		public Throwable getCause() {
			throw new RuntimeExceptionFromHell();
		}

		@Override
		public String getLocalizedMessage() {
			throw new RuntimeExceptionFromHell();
		}

		@Override
		public String getMessage() {
			throw new RuntimeExceptionFromHell();
		}

		@Override
		public StackTraceElement[] getStackTrace() {
			throw new RuntimeExceptionFromHell();
		}

		@Override
		public Throwable initCause(Throwable cause) {
			throw new RuntimeExceptionFromHell();
		}

		@Override
		public void printStackTrace() {
			throw new RuntimeExceptionFromHell();
		}

		@Override
		public void printStackTrace(PrintStream p) {
			throw new RuntimeExceptionFromHell();
		}

		@Override
		public void printStackTrace(PrintWriter p) {
			throw new RuntimeExceptionFromHell();
		}

		@Override
		public String toString() {
			throw new RuntimeExceptionFromHell();
		}

		@Override
		public boolean equals(Object o) {
			throw new RuntimeExceptionFromHell();
		}

		@Override
		public int hashCode() {
			throw new RuntimeExceptionFromHell();
		}

		@Override
		public void finalize() {
			throw new RuntimeExceptionFromHell();

		}

	}

	/**
	 * Runnable used to produce a forkbomb. Nothing big...
	 * 
	 * @author Marvin Marx
	 * 
	 */
	public class SlightlyIrritatingRunnable implements Runnable {
		private String positionMarker;

		/**
		 * 
		 * @param positionMarker
		 *            short information about which function/... has just been called, so as to simplify debugging.
		 *            (stacktraces might be a bit crowded)
		 */
		public SlightlyIrritatingRunnable(String positionMarker) {
			this.positionMarker = positionMarker;
		}

		@Override
		public void run() {
			if (DO_EVIL_SYSOUT_SPAM) {
				System.err.println("doing some friendly forkbombing in " + positionMarker);
			}
			loopToInfinityAndMuchMuchFurther(positionMarker);
		}
	}
}
