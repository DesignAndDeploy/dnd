package edu.teco.dnd.deploy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.config.BlockTypeHolder;

public class Distribution {
	private static final Logger LOGGER = LogManager.getLogger(Distribution.class);
	
	private final HashMap<FunctionBlock, BlockTarget> blocks;
	
	private final HashMap<BlockTarget, Collection<FunctionBlock>> targets;
	
	private final HashMap<BlockTypeHolder, BlockTarget> blockTargets;
	
	public Map<FunctionBlock, BlockTarget> getMapping() {
		return Collections.unmodifiableMap(blocks);
	}
	
	public Distribution() {
		blocks = new HashMap<FunctionBlock, Distribution.BlockTarget>();
		targets = new HashMap<Distribution.BlockTarget, Collection<FunctionBlock>>();
		blockTargets = new HashMap<BlockTypeHolder, Distribution.BlockTarget>();
	}
	
	public Distribution(final Distribution old) {
		this(
			new HashMap<FunctionBlock, BlockTarget>(old.blocks),
			new HashMap<BlockTarget, Collection<FunctionBlock>>(old.targets),
			new HashMap<BlockTypeHolder, BlockTarget>(old.blockTargets)
		);
	}
	
	private Distribution(final HashMap<FunctionBlock, BlockTarget> blocks, final HashMap<BlockTarget, Collection<FunctionBlock>> targets, final HashMap<BlockTypeHolder, BlockTarget> blockTargets) {
		this.blocks = blocks;
		this.targets = targets;
		this.blockTargets = blockTargets;
	}
	
	public boolean add(final FunctionBlock block, final Module module, final BlockTypeHolder typeHolder) {
		LOGGER.entry(block, module, typeHolder);
		final BlockTarget blockTarget = getBlockTarget(module, typeHolder);
		
		final boolean result = blockTarget.add(this, block);
		LOGGER.exit(result);
		return result;
	}
	
	public boolean canAdd(final FunctionBlock block, final Module module, final BlockTypeHolder typeHolder) {
		return getBlockTarget(module, typeHolder).canAdd(this, block);
	}
	
	public void remove(final FunctionBlock block) {
		LOGGER.entry(block);
		final BlockTarget blockTarget = this.blocks.remove(block);
		LOGGER.trace("block target {}", blockTarget);
		if (blockTarget != null) {
			final Collection<FunctionBlock> t = targets.get(blockTarget);
			LOGGER.trace("t {}", t);
			if (t != null) {
				t.remove(block);
			}
		}
		LOGGER.exit();
	}
	
	private BlockTarget getBlockTarget(final Module module, final BlockTypeHolder typeHolder) {
		BlockTarget target = blockTargets.get(typeHolder);
		if (target == null) {
			target = new BlockTarget(module, typeHolder);
			blockTargets.put(typeHolder, target);
		}
		
		return target;
	}
		
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Distribution[");
		boolean first = true;
		for (Entry<FunctionBlock, BlockTarget> entry : blocks.entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append(entry.getKey());
			sb.append(" => ");
			sb.append(entry.getValue());
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static class BlockTarget {
		private final Module module;
		
		private final BlockTypeHolder holder;
		
		public BlockTarget(final Module module, final BlockTypeHolder holder) {
			this.module = module;
			this.holder = holder;
		}
		
		public Module getModule() {
			return module;
		}
		
		public BlockTypeHolder getBlockTypeHolder() {
			return holder;
		}
		
		public int getDistributionUsage(final Distribution distribution) {
			if (holder.isLeave()) {
				Collection<FunctionBlock> targetCollection = distribution.targets.get(this);
				if (targetCollection == null) {
					return 0;
				} else {
					return targetCollection.size();
				}
			} else {
				int sum = 0;
				for (final BlockTypeHolder child : holder.getChildren()) {
					sum += distribution.getBlockTarget(module, child).getDistributionUsage(distribution);
				}
				return sum;
			}
		}
		
		public int getFree(final Distribution distribution) {
			return holder.getAmountLeft() - getDistributionUsage(distribution);
		}
		
		public boolean canAdd(final Distribution distribution, final FunctionBlock block) {
			return holder.isLeave() && holder.getType().equals(block.getType()) && getFree(distribution) > 0;
		}
		
		public boolean add(final Distribution distribution, final FunctionBlock block) {
			LOGGER.entry(block);
			if (!canAdd(distribution, block)) {
				LOGGER.exit(false);
				return false;
			}
			
			distribution.blocks.put(block, this);
			Collection<FunctionBlock> targetCollection = distribution.targets.get(this);
			if (targetCollection == null) {
				targetCollection = new ArrayList<FunctionBlock>();
				distribution.targets.put(this, targetCollection);
			}
			targetCollection.add(block);
			
			LOGGER.exit(true);
			return true;
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("BlockTypeHolder[module=");
			sb.append(module.getUUID());
			if (holder.isLeave()) {
				sb.append(",type='");
				sb.append(holder.getType());
				sb.append("',amount=");
				sb.append(holder.getAmountAllowed());
			}
			sb.append("]");
			return sb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((holder == null) ? 0 : holder.hashCode());
			result = prime * result
					+ ((module == null) ? 0 : module.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BlockTarget other = (BlockTarget) obj;
			if (holder == null) {
				if (other.holder != null)
					return false;
			} else if (!holder.equals(other.holder))
				return false;
			if (module == null) {
				if (other.module != null)
					return false;
			} else if (!module.equals(other.module))
				return false;
			return true;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((blockTargets == null) ? 0 : blockTargets.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Distribution other = (Distribution) obj;
		if (blockTargets == null) {
			if (other.blockTargets != null)
				return false;
		} else if (!blockTargets.equals(other.blockTargets))
			return false;
		return true;
	}
	
	public static class ResourceConstraint implements Constraint {
		@Override
		public boolean isAllowed(final Distribution distribution,
				final FunctionBlock block, final Module module, final BlockTypeHolder holder) {
			return distribution.canAdd(block, module, holder);
		}
	}
}
