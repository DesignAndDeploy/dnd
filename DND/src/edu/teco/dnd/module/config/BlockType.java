package edu.teco.dnd.module.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author cryptkiddy
 * 
 */
public class BlockType {
	public final String type;
	/** allowed blocks of this type, <0 means infinity. */
	@SerializedName("amount")
	private int amountAllowed = -1;
	/** null if none */
	private Set<BlockType> children = null;
	
	private transient BlockType parent = null;
	private transient int amountLeft = -1;

	public BlockType() {
		this((String) null, -1);
	}

	public BlockType(String type, int amount) {
		//leave node
		this.type = type;
		this.children = null;
		this.amountAllowed = amount;
		this.amountLeft = amount;
	}
	
	public BlockType(Set<BlockType> childblocks, int amount) {
		//non leave node
		this.type = null;
		this.children = (childblocks == null)? new HashSet<BlockType>() : childblocks;
		this.amountAllowed = amount;
		this.amountLeft = amount;
	}
	
	public BlockType(int amount) {
		//non leave node
		this.type = null;
		this.children =  new HashSet<BlockType>();
		this.amountAllowed = amount;
		this.amountLeft = amount;
	}

	public void addChild(BlockType child) {
		addChild(Arrays.asList(child));
	}

	public void addChild(Collection<BlockType> children) {
		if (type != null) throw new IllegalStateException("Node has type and is not a leave node.");
		if (children == null) children = new HashSet<BlockType>();
		this.children.addAll(children);
	}

	/**
	 * @return childblocks, null if none.
	 */
	public Set<BlockType> getChildren() {
		if (children != null && children.isEmpty()) {
			return null;
		} else {
			return children;
		}
	}

	public BlockType getParent() {
		return parent;
	}

	public void setParent(BlockType parent) {
		this.parent = parent;
	}

	public int getAmountAllowed() {
		return amountAllowed;
	}

	public void setAmountAllowed(int amount) {
		amountAllowed = amount;
		amountLeft = amount;
	}

	public int getAmountLeft() {
		return amountLeft;
	}

	public void setAmountLeft(int amountLeft) {
		this.amountLeft = amountLeft;
	}

}