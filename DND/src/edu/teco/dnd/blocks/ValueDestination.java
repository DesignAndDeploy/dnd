package edu.teco.dnd.blocks;

import java.util.UUID;

public class ValueDestination {
	private final UUID block;
	private final String input;

	public ValueDestination(final UUID block, final String input) {
		this.block = block;
		this.input = input;
	}

	public UUID getBlock() {
		return this.block;
	}

	public String getInput() {
		return this.input;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((block == null) ? 0 : block.hashCode());
		result = prime * result + ((input == null) ? 0 : input.hashCode());
		return result;
	}

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
		ValueDestination other = (ValueDestination) obj;
		if (block == null) {
			if (other.block != null) {
				return false;
			}
		} else if (!block.equals(other.block)) {
			return false;
		}
		if (input == null) {
			if (other.input != null) {
				return false;
			}
		} else if (!input.equals(other.input)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ValueDestination [block=" + block + ", input=" + input + "]";
	}
}