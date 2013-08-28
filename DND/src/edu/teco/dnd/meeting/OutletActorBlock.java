package edu.teco.dnd.meeting;

import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.graphiti.BlockType;
import edu.teco.dnd.uPart.OutletControl;

/**
 * This FunctionBlock serves to control the Beamer.
 * 
 */
@BlockType("Meeting")
public class OutletActorBlock extends FunctionBlock {
	private static final long serialVersionUID = -8839932451597237481L;

	/**
	 * Type of this block.
	 */
	public static final String BLOCK_TYPE = "actorOutlet";

	/**
	 * URL of the Outlet of the beamer. Default is already set; doesn't contain ID.
	 */
	public static final String OPTION_URL = "http://cumulus.teco.edu:5000/plugwise/";

	/**
	 * ID of the outlet to control.
	 */
	public static final String OPTION_OUTLET_ID = null;

	/**
	 * tells BeamerActorBlock to turn on (true) or switch off (false) the beamer.
	 */
	private Input<Boolean> beamer;

	/**
	 * OutletControl to control the outlet of the beamer.
	 */
	private OutletControl outletControl;

	/**
	 * Initializes BeamerActorBlock.
	 */
	@Override
	public void init(final Map<String, String> options) {
		final String url = options.get("URL");
		final String outletID = options.get("OUTLET_ID");
		if (url == null || outletID == null) {
			return;
		}
		String fullURL = url + outletID;
		this.outletControl = new OutletControl(fullURL);
	}

	/**
	 * Shutdown Block.
	 */
	@Override
	public void shutdown() {
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
