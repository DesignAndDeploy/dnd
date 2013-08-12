package edu.teco.dnd.meeting;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.graphiti.BlockType;

/**
 * This operating FunctionBlock is used to control the Beamer.
 * 
 */
@BlockType("Meeting")
public class BeamerOperatorBlock extends FunctionBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = -889186768492376346L;

	/**
	 * The measured energy at the outlet.
	 */
	private Input<Integer> outlet;

	/**
	 * tells whether there is a meeting (true) or not (false).
	 */
	private Input<Boolean> meeting;

	/**
	 * used to tell BeamerActorBlock to turn on (true) or switch off (false) the beamer.
	 */
	private Output<Boolean> beamer;

	/**
	 * Initializes BeamerOperatorBlock.
	 */
	@Override
	public void init() {
	}

	/**
	 * Checks whether there is a meeting and if the outlet is used to control a BeamerActorBlock. If both is
	 * true: beamer-power activate!
	 */
	@Override
	protected void update() {
		if (outlet == null || meeting == null) {
			return;
		}
		Integer outletValue = outlet.popValue();
		Boolean meetingState = meeting.popValue();
		if (outletValue == null || meetingState == null) {
			return;
		}
		beamer.setValue(outletValue > 0 && meetingState);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((meeting == null) ? 0 : meeting.hashCode());
		result = prime * result + ((outlet == null) ? 0 : outlet.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BeamerOperatorBlock other = (BeamerOperatorBlock) obj;
		if (meeting == null) {
			if (other.meeting != null) {
				return false;
			}
		} else if (!meeting.equals(other.meeting)) {
			return false;
		}
		if (outlet == null) {
			if (other.outlet != null) {
				return false;
			}
		} else if (!outlet.equals(other.outlet)) {
			return false;
		}
		if (beamer == null) {
			if (other.beamer != null) {
				return false;
			}
		} else if (!beamer.equals(other.beamer)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BeamerOperatorBlock [outlet=" + outlet + ", meeting=" + meeting + "]";
	}

}
