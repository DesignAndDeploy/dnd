package edu.teco.dnd.graphiti;

import org.eclipse.graphiti.mm.PropertyContainer;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.services.Graphiti;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;

/**
 * This class manages the properties added to several property containers in the graphiti gui.
 * 
 * @author jung
 */
public class TypePropertyUtil {
	public static final String SHAPE_KEY = "shape_id";
	public static final String SHAPE_VALUE_BLOCK = "block";

	public static final String TEXT_KEY = "text_id";
	public static final String TEXT_VALUE_BLOCKNAME = "blockName";
	public static final String TEXT_VALUE_POSITION = "position";

	/**
	 * Sets the properties on <code>pc</code> so that they reflect that the <code>PropertyContainer</code> is associated
	 * with a {@link FunctionBlockModel}.
	 * 
	 * @param pc
	 *            the container for which the properties should be set
	 */
	public static void setBlockShape(PropertyContainer pc) {
		Graphiti.getPeService().setPropertyValue(pc, SHAPE_KEY, SHAPE_VALUE_BLOCK);
	}

	/**
	 * Returns <code>true</code> if <code>pc</code> is associated with a {@link FunctionBlockModel}.
	 * 
	 * @param pc
	 *            the <code>PropertyContainer</code> to check
	 * @return <code>true</code> if <code>pc</code> is associated with a FunctionBlockModel
	 * @see #setBlockShape(PropertyContainer)
	 */
	public static boolean isBlockShape(PropertyContainer pc) {
		return SHAPE_VALUE_BLOCK.equals(Graphiti.getPeService().getPropertyValue(pc, SHAPE_KEY));
	}

	/**
	 * Sets the properties on <code>text</code> so that they reflect that the <code>Text</code> represents the
	 * {@link FunctionBlockModel#getBlockName() name} of a {@link FunctionBlockModel}.
	 * 
	 * @param text
	 *            the Text for which the properties should be set
	 */
	public static void setBlockNameText(Text text) {
		Graphiti.getPeService().setPropertyValue(text, TEXT_KEY, TEXT_VALUE_BLOCKNAME);
	}

	/**
	 * Returns <code>true</code> if <code>pc</code> is associated with the name of a FunctionBlockModel.
	 * 
	 * @param pc
	 *            the <code>PropertyContainer</code> to check
	 * @return <code>true</code> if <code>pc</code> is associated with the name of a FunctionBlockModel
	 */
	public static boolean isBlockNameText(PropertyContainer pc) {
		return TEXT_VALUE_BLOCKNAME.equals(Graphiti.getPeService().getPropertyValue(pc, TEXT_KEY));
	}

	/**
	 * Sets the properties on <code>text</code> so that they reflect that the <code>Text</code> represents the
	 * {@link FunctionBlockModel#getPosition() position} of a {@link FunctionBlockModel}.
	 * 
	 * @param text
	 *            the Text for which the properties should be set
	 */
	public static void setPositionText(Text text) {
		Graphiti.getPeService().setPropertyValue(text, TEXT_KEY, TEXT_VALUE_POSITION);
	}

	/**
	 * Returns <code>true</code> if <code>pc</code> is associated with the position of a FunctionBlockModel.
	 * 
	 * @param pc
	 *            the <code>PropertyContainer</code> to check
	 * @return <code>true</code> if <code>pc</code> is associated with the position of a FunctionBlockModel
	 */
	public static boolean isPositionText(PropertyContainer pc) {
		return TEXT_VALUE_POSITION.equals(Graphiti.getPeService().getPropertyValue(pc, TEXT_KEY));
	}
}
