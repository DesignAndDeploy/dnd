package edu.teco.dnd.graphiti;

import org.eclipse.graphiti.mm.PropertyContainer;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.services.Graphiti;

/**
 * This class manages the properties added to several property containers in the
 * graphiti gui.
 * 
 * @author jung
 * 
 */
public class TypePropertyUtil {

	/**
	 * Key for text fields.
	 */
	public static final String TEXT_KEY = "text_id";

	/**
	 * Value for the blockName text field.
	 */
	public static final String TEXT_VALUE_BLOCKNAME = "blockName";

	/**
	 * Value for the position text field.
	 */
	public static final String TEXT_VALUE_POSITION = "position";

	/**
	 * Registers the PropertyContainer as a blockName text field.
	 * 
	 * @param pc
	 *            PropertyContainer to register, should be a text field.
	 * @return true if successfully registered.
	 */
	public static final boolean setBlockNameText(PropertyContainer pc) {
		if (pc instanceof Text) {
			Graphiti.getPeService().setPropertyValue(pc, TEXT_KEY,
					TEXT_VALUE_BLOCKNAME);
			return true;
		}
		return false;
	}

	/**
	 * Registers the PropertyContainer as a position text field.
	 * 
	 * @param pc
	 *            PropertyContainer to register, e.g. a Text field.
	 * @return true if successfully registered.
	 */
	public static final boolean setPositionText(PropertyContainer pc) {
		if (pc instanceof Text) {
			Graphiti.getPeService().setPropertyValue(pc, TEXT_KEY,
					TEXT_VALUE_POSITION);
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the given PropertyContainer was registered as a blockName
	 * text field.
	 * 
	 * @param pc
	 *            PropertyContainer to test
	 * @return true if pc is a blockName text field
	 */
	public static boolean isBlockNameText(PropertyContainer pc) {
		return TEXT_VALUE_BLOCKNAME.equals(Graphiti.getPeService()
				.getPropertyValue(pc, TEXT_KEY));
	}

	/**
	 * Returns true if the given PropertyContainer was registered as a position
	 * text field.
	 * 
	 * @param pc
	 *            PropertyContainer to test
	 * @return true if pc is a position text field
	 */
	public static boolean isPositionText(PropertyContainer pc) {
		return TEXT_VALUE_POSITION.equals(Graphiti.getPeService()
				.getPropertyValue(pc, TEXT_KEY));
	}

}
