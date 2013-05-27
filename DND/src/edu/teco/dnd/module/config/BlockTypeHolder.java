package edu.teco.dnd.module.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author cryptkiddy
 * 
 */
public class BlockTypeHolder {

	private static final Logger LOGGER = LogManager.getLogger(BlockTypeHolder.class);

	public final String type;
	/** allowed blocks of this type, <0 means infinity. */
	@SerializedName("amount")
	private int amountAllowed = -1;
	/** null if none */
	private Set<BlockTypeHolder> children = null;

	private transient BlockTypeHolder parent = null;
	private transient int amountLeft = -1;

	public BlockTypeHolder() {
		this((String) null, -1);
	}

	public BlockTypeHolder(String type, int amount) {
		// leave node
		this.type = type;
		this.children = null;
		this.amountAllowed = amount;
		this.amountLeft = amount;
	}

	public BlockTypeHolder(Set<BlockTypeHolder> childblocks, int amount) {
		// non leave node
		this.type = null;
		this.children = (childblocks == null) ? new HashSet<BlockTypeHolder>() : childblocks;
		this.amountAllowed = amount;
		this.amountLeft = amount;
	}

	public BlockTypeHolder(int amount) {
		// non leave node
		this.type = null;
		this.children = new HashSet<BlockTypeHolder>();
		this.amountAllowed = amount;
		this.amountLeft = amount;
	}

	public void addChild(BlockTypeHolder child) {
		addChild(Arrays.asList(child));
	}

	public void addChild(Collection<BlockTypeHolder> children) {
		if (type != null)
			throw new IllegalStateException("Node has type and is not a leave node.");
		if (children == null)
			children = new HashSet<BlockTypeHolder>();
		this.children.addAll(children);
	}

	/**
	 * @return childblocks, null if none.
	 */
	public Set<BlockTypeHolder> getChildren() {
		if (children != null && children.isEmpty()) {
			return null;
		} else {
			return children;
		}
	}

	public BlockTypeHolder getParent() {
		return parent;
	}

	public void setParent(BlockTypeHolder parent) {
		this.parent = parent;
	}

	public int getAmountAllowed() {
		return amountAllowed;
	}

	public int getAmountLeft() {
		return amountLeft;
	}

	public void setAmountLeft(int amountLeft) {
		this.amountLeft = amountLeft;
	}

	/**
	 * Recursively tries to decrease the values of amountLeft up to the
	 * parent-node. Returns true if every node had at least one allowed left (or
	 * was negative), if at any point 0 allowed are encountered reverses the
	 * whole process and returns false. <br>
	 * Process is atomic. If two different blocks mutually excluding each other
	 * are requested exactly one will succeed however which one is not
	 * deterministic.<br>
	 * (tl;dr: try decrease amount of allowed blocks up to parent node. If
	 * amount=0 --> reverse everything and return false; else true)
	 * 
	 * @return true iff the action is possible false otherwise. see above!
	 */
	public synchronized boolean tryDecrease() {
		if (amountLeft != 0 && (parent == null || parent.tryDecrease())) {
			if (amountLeft > 0) {
				amountLeft--;
			}
			return true;
		} else {
			return false;
		}
	}

	public synchronized void increase() {
		if (amountLeft >= 0) {
			if (amountLeft < amountAllowed) {
				parent.increase();
				amountLeft++;
			} else {
				LOGGER.warn("more block=>{} freed than marked as in use.", type);
			}
		}
	}

}