package edu.teco.dnd.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.UUID;

import lights.adapters.Tuple;
import lights.interfaces.IField;
import lights.interfaces.ITuple;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.util.MuServerProvider;

/**
 * This message is used to tell a module that it should run a FunctionBlock.
 */
public class ApplicationStartBlockMessage implements Message {

	/**
	 * Used to hold a FunctionBlock. Lime reactions are only triggered if the class given in the template
	 * matches exactly. So, packing a FunctionBlock in a tuple doesn't work as it is an abstract class and
	 * each instance will be of a subtype. Instead, an object of this class is put into the tuple and the
	 * template can use this class.
	 */
	public static final class FunctionBlockHolder implements Serializable {
		/**
		 * This class is used to serialize the functionBlock and uses the classLoader of the muServer.
		 */
		public static final class MuObjectInputStream extends ObjectInputStream {
			/**
			 * Initializes a new MuObjectInputStream.
			 * 
			 * @param in
			 *            the InputStream
			 * @throws IOException
			 *             if error occurs with Stream stuff.
			 */
			public MuObjectInputStream(final InputStream in) throws IOException {
				super(in);
			}

			@Override
			protected Class<?> resolveClass(final ObjectStreamClass osc) throws ClassNotFoundException {
				System.out.println("resolving " + osc.getName());
				Class<?> cls = null;
				try {
					cls = Class.forName(osc.getName());
				} catch (ClassNotFoundException e) {

				}
				if (cls == null) {
					System.out.println("trying mucode");
					cls = MuServerProvider.getMuServer().getSharedClassSpace().getClass(osc.getName());
					if (cls == null) {
						System.out.println(osc.getName() + " not found in mucode");
						throw new ClassNotFoundException("Class " + osc.getName() + " not found");
					}
				}
				return cls;
			}
		}

		/**
		 * Used for serialization.
		 */
		private static final long serialVersionUID = 8656579882381949870L;

		/**
		 * Serialized functionBlock.
		 */
		private byte[] serialized;

		/**
		 * Initializes a new FunctionBlockHolder.
		 * 
		 * @param functionBlock
		 *            the FunctionBlock to hold
		 * @throws IOException
		 *             If the stream doesn't work.
		 */
		public FunctionBlockHolder(final FunctionBlock functionBlock) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			new ObjectOutputStream(baos).writeObject(functionBlock);
			serialized = baos.toByteArray();
		}

		/**
		 * Returns the FunctionBlock.
		 * 
		 * @return the FunctionBlock
		 * @throws IOException
		 *             If the stream doesn't work.
		 * @throws ClassNotFoundException
		 *             if class is not found
		 */
		public FunctionBlock getFunctionBlock() throws ClassNotFoundException, IOException {
			try (MuObjectInputStream stream = new MuObjectInputStream(new ByteArrayInputStream(serialized))) {
				return (FunctionBlock) stream.readObject();
			} catch (IOException ex) {
				throw ex;
			}
		}
	}

	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "StartBlock";

	/**
	 * Index of the FunctionBlockHolder in the tuple.
	 */
	public static final int HOLDERID_INDEX = 1;

	/**
	 * Index of the uid in the tuple.
	 */
	public static final int UID_INDEX = 2;

	/**
	 * Returns a template tuple for this kind of message.
	 * 
	 * @return a template tuple for this kind of message
	 */
	@Override
	public ITuple getTemplate() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addFormal(FunctionBlockHolder.class);
		tuple.addFormal(Long.class);
		return tuple;

	}

	/**
	 * The FunctionBlock to execute.
	 */
	private FunctionBlock functionBlock;

	/**
	 * UID used for managing the message, as lights is buggy and won't properly retrive tuples containing
	 * ValueHolders.
	 */
	private Long uid;

	/**
	 * Initializes a new ApplicationStartBlockMessage.
	 * 
	 * @param functionBlock
	 *            the FunctionBlock to start
	 */
	public ApplicationStartBlockMessage(final FunctionBlock functionBlock) {
		this.functionBlock = functionBlock;
		uid = UUID.randomUUID().getLeastSignificantBits();
	}

	/**
	 * Initializes a new AppilcationStartBlockMessage from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from, should be valid
	 * 
	 * @see #getTemplate()
	 */
	public ApplicationStartBlockMessage(final ITuple tuple) {
		setTuple(tuple);
	}

	/**
	 * Initializes a new ApplicationStartBlockMessage without parameters.
	 */
	public ApplicationStartBlockMessage() {
		this((FunctionBlock) null);
	}

	/**
	 * Sets the FunctionBlock that should be executed.
	 * 
	 * @param functionBlock
	 *            the FunctionBlock to execute
	 */
	public void setFunctionBlock(final FunctionBlock functionBlock) {
		this.functionBlock = functionBlock;
	}

	/**
	 * Returns the FunctionBlock to execute.
	 * 
	 * @return the FunctionBlock to execute
	 */
	public FunctionBlock getFunctionBlock() {
		return functionBlock;
	}

	/**
	 * Returns a tuple representing this message.
	 * 
	 * @return a tuple representing this message
	 */
	@Override
	public ITuple getTuple() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		try {
			tuple.addActual(new FunctionBlockHolder(functionBlock));
		} catch (IOException e) {
			return null;
		}
		tuple.addActual(uid);
		return tuple;
	}

	/**
	 * Used to get a template to match this tuple in tuplespace. Necessary to work around a ligths bug.
	 * 
	 * @return the matcher for this tuple.
	 */
	public ITuple getUidMatcherTuple() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addFormal(FunctionBlockHolder.class);
		tuple.addActual(uid);
		return tuple;
	}

	/**
	 * Sets the data of this message from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from, should be valid
	 * @see #getTemplate()
	 */
	@Override
	public void setTuple(final ITuple tuple) {
		if (checkTuple(tuple)) {
			throw new IllegalArgumentException("invalid tuple");
		}
		IField[] fields = tuple.getFields();
		try {
			functionBlock = ((FunctionBlockHolder) fields[HOLDERID_INDEX].getValue()).getFunctionBlock();
		} catch (
				ClassNotFoundException | IOException e) {
		}
		uid = (Long) fields[UID_INDEX].getValue();
	}

	/**
	 * Checks whether a tuple matches the required template and has valid fields.
	 * 
	 * @param tuple
	 *            Tuple to check
	 * @return false if tuple can be used, true if there are any problems
	 */
	private boolean checkTuple(final ITuple tuple) {
		if (!getTemplate().matches(tuple)) {
			return true;
		} else {
			IField[] fields = tuple.getFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].isFormal()) {
					return true;
				}
			}
		}
		return false;
	}

}
