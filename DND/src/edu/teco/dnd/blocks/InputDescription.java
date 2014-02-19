package edu.teco.dnd.blocks;

/**
 * A location of an {@link Input}, that is the ID of the {@link FunctionBlock} as well as the name of the Input.
 */
public class InputDescription {
	private final FunctionBlockID block;
	private final String input;

	public InputDescription(final FunctionBlockID block, final String input) {
		this.block = block;
		this.input = input;
	}

	public FunctionBlockID getBlock() {
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
		InputDescription other = (InputDescription) obj;
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