package edu.teco.dnd.meeting;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.graphiti.BlockType;
import edu.teco.dnd.uPart.OutletControl;

/**
 * This FunctionBlock serves to control the Beamer.
 * 
 */
@BlockType("Meeting")
public class OutletActorBlock extends FunctionBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8839932451597237481L;
	/**
	 * tells BeamerActorBlock to turn on (true) or switch off (false) the beamer.
	 */
	private Input<Boolean> beamer;

	/**
	 * URL of the Outlet of the beamer. Default is already set; doesn't contain ID.
	 */
	private Option url;
	// TODO: set default "http://cumulus.teco.edu:5000/plugwise/"

	/**
	 * ID of the outlet to control.
	 */
	private Option outletID;

	/**
	 * OutletControl to control the outlet of the beamer.
	 */
	private OutletControl outletControl;

	/**
	 * Initializes BeamerActorBlock.
	 */
	@Override
	public void init() {
		if (url == null || outletID == null) {
			return;
		}
		String fullURL = url.getValue() + outletID.getValue();
		this.outletControl = new OutletControl(fullURL);
	}

	/**
	 * Turns the beamer on or off.
	 */
	@Override
	public void update() {
		if (beamer == null || outletControl == null) {
			return;
		}
		Boolean beamerState = null;
		while (beamer.hasMoreValues()) {
			Boolean state = beamer.popValue();
			if (state != null) {
				beamerState = state;
			}
		}
		if (beamerState != null) {
			if (beamerState) {
				outletControl.activateOutlet();
			} else {
				outletControl.deactivateOutlet();
			}
		}
	}
}
