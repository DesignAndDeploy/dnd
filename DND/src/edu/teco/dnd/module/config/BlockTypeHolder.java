package edu.teco.dnd.module.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.SerializedName;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;

/**
 * BlockTypeHolders are used to specify what kind of {@link FunctionBlock}s a {@link Module} can run. The
 * BlockTypeHolders of a Module are a tree. The leaves have a {@link #getType() type} and an {@link #getAmountAllowed()
 * amount} set. Those can be used to store the given amount of FunctionBlocks of the given type.
 * 
 * Non-leaf nodes have a variable number of children. They can also have an amount set. This is a total amount that may
 * not be exceeded by all children. For example, if root has an amount of 2 and two children. One which can hold two
 * FunctionBlocks of type <code>foo</code> and one that can hold one of type <code>bar</code>, then the Module could run
 * one FunctionBlocks of type <code>foo</code> or one or two of type <code>bar</code> (but not three as that would
 * exceed the root) or one of type <code>foo</code> and one of type <code>bar</code> (not more as the root will be
 * full).
 */
public class BlockTypeHolder implements Iterable<BlockTypeHolder> {
	private static final Logger LOGGER = LogManager.getLogger(BlockTypeHolder.class);

	/**
	 * The types of FunctionBlocks controlled by this BlockTypeHolder. This is used as a
	 */
	private final String type;

	/**
	 * Number of FunctionBlocks that can be stored in this BlockTypeHolder. Negative values mean unrestricted.
	 */
	@SerializedName("amount")
	private int amountAllowed = -1;

	/**
	 * Number of slots that are left. Negative means unrestricted.
	 */
	private int amountLeft = -1;

	/**
	 * An identifier that will be unique on a Module.
	 */
	private int id = -1;

	/**
	 * Children of this BlockTypeHolder. May be empty.
	 */
	private final Set<BlockTypeHolder> children = new HashSet<BlockTypeHolder>();

	/**
	 * Parent of this BlockTypeHolder. Will be <code>null</code> for the root.
	 */
	private transient BlockTypeHolder parent = null;

	/**
	 * Constructor without arguments for Gson.
	 */
	@SuppressWarnings("unused")
	private BlockTypeHolder() {
		this((String) null, -1);
	}

	/**
	 * Initializes a new leaf node.
	 * 
	 * @param type
	 *            the type this leaf node is used for
	 * @param amount
	 *            the amount of FunctionBlocks this BlockTypeHolder can hold. Negative values mean unrestricted.
	 */
	public BlockTypeHolder(String type, int amount) {
		// leave node
		this.type = type;
		this.amountAllowed = amount;
		this.amountLeft = amount;
	}

	/**
	 * Initializes a new non-leaf node.
	 * 
	 * @param children
	 *            initial children of the BlockTypeHolder
	 * @param amount
	 *            the amount of FunctionBlocks this BlockTypeHolder can hold. Negative values mean unrestricted. This is
	 *            a total maximum for all children.
	 */
	public BlockTypeHolder(Set<BlockTypeHolder> children, int amount) {
		// non leave node
		this.type = null;
		if (children != null) {
			this.children.addAll(children);
		}
		this.amountAllowed = amount;
		this.amountLeft = amount;
	}

	/**
	 * Initializes a new non-leaf node without any children.
	 * 
	 * @param amount
	 *            the amount of FunctionBlocks this BlockTypeHolder can hold. Negative values mean unrestricted. This is
	 *            a total maximum for all children.
	 */
	public BlockTypeHolder(final int amount) {
		this(Collections.<BlockTypeHolder> emptySet(), amount);
	}

	/**
	 * Returns the children of this BlockTypeHolder. Will be empty for leaf nodes.
	 * 
	 * @return the children of this BlockTypeHolder. Non-<code>null</code>, but possibly empty.
	 */
	public Set<BlockTypeHolder> getChildren() {
		return children;
	}

	/**
	 * Returns the parent of this BlockTypeHolder.
	 * 
	 * @return the parent of this BlockTypeHolder. May be <code>null</code>.
	 */
	public BlockTypeHolder getParent() {
		return parent;
	}

	/**
	 * Sets the parent of this BlockTypeHolder. This does not change the children of <code>parent</code>.
	 * 
	 * @param parent
	 *            the new parent of this BlockTypeHolder. May be <code>null</code>.
	 */
	public void setParent(BlockTypeHolder parent) {
		this.parent = parent;
	}

	/**
	 * Returns the number of {@link FunctionBlock}s that can be hold by this BlockTypeHolder. This is the total amount.
	 * 
	 * @return the number of FunctionBlocks that can be hold by this BlockTypeHolder. A negative number means that there
	 *         is no limit.
	 * @see #getAmountLeft()
	 */
	public int getAmountAllowed() {
		return amountAllowed;
	}

	/**
	 * Returns the number of {@link FunctionBlock}s that can still be added to this BlockTypeHolder before it is full.
	 * 
	 * @return the number of FunctionBlocks that can be added to this BlockTypeHolder before it is full. A negative
	 *         number means that there is no limit.
	 * @see #getAmountAllowed()
	 */
	public int getAmountLeft() {
		return amountLeft;
	}

	/**
	 * Sets the number of {@link FunctionBlock}s that can be added to this BlockTypeHolder before it is full.
	 * 
	 * @param amountLeft
	 *            the number of FunctionBlocks that can be added to this {@link BlockTypeHolder} before it is full. A
	 *            negative number means that there is no limit.
	 * @see #getAmountLeft()
	 */
	public void setAmountLeft(int amountLeft) {
		LOGGER.trace("setting amountLeft to {}", amountLeft);
		this.amountLeft = amountLeft;
	}

	/**
	 * Returns the type of {@link FunctionBlock}s this BlockTypeHolder is used for. This is used as a RegEx.
	 * 
	 * @return a RegEx describing the types of {@link FunctionBlock}s this BlockTypeHolder is used for.
	 *         <code>null</code> for non-leaf nodes
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Returns the ID of this BlockTypeHolder. This should be unique per {@link Module}, however this class has no
	 * directly control over the IDs.
	 * 
	 * @return the ID of this BlockTypeHolder
	 */
	public int getID() {
		return id;
	}

	/**
	 * Sets the ID of this BlockTypeHolder. Must be positive. This should be unique per {@link Module}, however no
	 * checks for uniqueness are made in this method.
	 * 
	 * @param id
	 *            the new ID of this BlockTypeHolder
	 */
	public void setID(int id) {
		if (id < 0) {
			throw new IllegalArgumentException();
		}
		this.id = id;
	}

	/**
	 * Tries to add a {@link FunctionBlock} of the given type to this BlockTypeHolder. This only works for leaf nodes.
	 * 
	 * This will fail if the given type does not match the {@link #getType()} RegEx. Also this BlockTypeHolder as well
	 * as all its {@link #getParent() parents} have to have at least one {@link #getAmountLeft() free slot}.
	 * 
	 * This method can be called concurrently with itself and/or {@link #increase()}.
	 * 
	 * @param blockType
	 *            the type of FunctionBlock to add
	 * @return true if the block was successfully added, false otherwise
	 */
	public synchronized boolean tryAdd(final String blockType) {
		String normalizedBlockType = blockType;
		if (normalizedBlockType == null) {
			normalizedBlockType = "";
		}
		if (!normalizedBlockType.matches(type)) {
			return false;
		}
		return tryDecrease();
	}

	/**
	 * Tries to decrease this BlockTypeHolder’s {@link #getAmountLeft()} and that of all its parents. Fails if any
	 * BlockTypeHolder involved has no free slots. In that case no amount is changed.
	 * 
	 * If a {@link #getAmountLeft()} is negative it is not changed and interpreted as having free slots.
	 * 
	 * @return <code>true</code> if this BlockTypeHolder and all its parents had at least one free slot and those have
	 *         be decreased by one.
	 */
	private synchronized boolean tryDecrease() {
		LOGGER.entry();
		if (amountLeft != 0 && (parent == null || parent.tryDecrease())) {
			if (amountLeft > 0) {
				amountLeft--;
			}
			LOGGER.exit(true);
			return true;
		} else {
			LOGGER.exit(false);
			return false;
		}
	}

	/**
	 * Adds a free slot to this {@link BlockTypeHolder} and all its parents. Will not increase past
	 * {@link #getAmountAllowed()}.
	 * 
	 * @see #getAmountLeft()
	 */
	public synchronized void increase() {
		if (amountLeft < 0) {
			if (parent != null) {
				parent.increase();
			}
		} else {
			if (amountLeft < amountAllowed) {
				if (parent != null) {
					parent.increase();
				}
				amountLeft++;
			} else {
				LOGGER.warn("more block=>{} freed than marked as in use.", type);
			}
		}
	}

	/**
	 * Returns <code>true</code> if this is a leaf node.
	 * 
	 * @return <code>true</code> if this is a leaf node
	 */
	public boolean isLeaf() {
		return !children.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amountAllowed;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BlockTypeHolder other = (BlockTypeHolder) obj;
		if (amountAllowed != other.amountAllowed) {
			return false;
		}
		if (children == null) {
			if (other.children != null) {
				return false;
			}
		} else if (!children.equals(other.children)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	@Override
	public Iterator<BlockTypeHolder> iterator() {
		return new BlockTypeHolderIterator(this);
	}
}
