package edu.teco.dnd.meeting;

import java.util.UUID;

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
	@Input(newOnly = true)
	private Boolean beamer;

	/**
	 * URL of the Outlet of the beamer. Default is already set; doesn't contain ID.
	 */
	@Option
	private String url = "http://cumulus.teco.edu:5000/plugwise/";

	/**
	 * ID of the outlet to control.
	 */
	@Option
	private String outletID;

	/**
	 * OutletControl to control the outlet of the beamer.
	 */
	private OutletControl outletControl;

	/**
	 * Creates new BeamerActorBlock.
	 * 
	 * @param blockID
	 *            ID of new BeamerActorBlock
	 */
	public OutletActorBlock(final UUID blockID) {
		super(blockID);
	}

	/**
	 * Returns type of this FunctionBlock.
	 * 
	 * @return type of this FunctionBlock
	 */
	@Override
	public String getType() {
		return "actorBeamer";
	}

	/**
	 * Initializes BeamerActorBlock.
	 */
	@Override
	public void init() {
		if (url == null || outletID == null) {
			return;
		}
		url = url.concat(outletID);
		this.outletControl = new OutletControl(url);
	}

	/**
	 * Turns the beamer on or off.
	 */
	@Override
	protected void update() {
		if (beamer == null || outletControl == null) {
			return;
		}
		if (beamer) {
			outletControl.activateOutlet();
		} else {
			outletControl.deactivateOutlet();
		}

	}

}
