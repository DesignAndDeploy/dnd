package edu.teco.dnd.graphiti;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.InputModel;
import edu.teco.dnd.graphiti.model.OutputModel;

/**
 * A CustomFeature that stores information about the selected object in the log.
 */
public class DebugFeature extends AbstractCustomFeature {
	private static final Logger LOGGER = LogManager.getLogger(DebugFeature.class);

	public DebugFeature(final IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(final ICustomContext context) {
		for (PictogramElement pe : context.getPictogramElements()) {
			if (pe instanceof Connection) {
				Connection connection = (Connection) pe;
				return getBusinessObjectForPictogramElement(connection.getStart()) instanceof OutputModel
						&& getBusinessObjectForPictogramElement(connection.getEnd()) instanceof InputModel;
			} else {
				Object bo = getBusinessObjectForPictogramElement(pe);
				if (bo instanceof FunctionBlockModel || bo instanceof OutputModel || bo instanceof InputModel) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void execute(final ICustomContext context) {
		if (!LOGGER.isDebugEnabled()) {
			LOGGER.warn("debuging is not enabled"); //$NON-NLS-1$
			return;
		}
		for (PictogramElement pe : context.getPictogramElements()) {
			if (pe instanceof Connection) {
				logConnection((Connection) pe);
			} else {
				Object bo = getBusinessObjectForPictogramElement(pe);
				if (bo instanceof FunctionBlockModel) {
					logFunctionBlock((FunctionBlockModel) bo);
				} else if (bo instanceof OutputModel) {
					logOutput((OutputModel) bo);
				} else if (bo instanceof InputModel) {
					logInput((InputModel) bo);
				}
			}
		}
	}

	/**
	 * Logs information about a connection.
	 * 
	 * @param connection
	 *            the connection to inspect
	 */
	private void logConnection(final Connection connection) {
		OutputModel output = (OutputModel) getBusinessObjectForPictogramElement(connection.getStart());
		InputModel input = (InputModel) getBusinessObjectForPictogramElement(connection.getEnd());
		LOGGER.debug("Connecting {}({}):{} to {}({}):{}", output.getFunctionBlock().getID(), output.getFunctionBlock() //$NON-NLS-1$
				.getType(), output.getName(), input.getFunctionBlock().getID(), input.getFunctionBlock().getType(),
				input.getName());
	}

	/**
	 * Logs information about a FunctionBlock.
	 * 
	 * @param block
	 *            the FunctionBlock to inspect
	 */
	private void logFunctionBlock(final FunctionBlockModel block) {
		LOGGER.debug("FunctionBlock {} of type {}", block.getID(), block.getType()); //$NON-NLS-1$
	}

	/**
	 * Logs information about an Output.
	 * 
	 * @param output
	 *            the Output to inspect
	 */
	private void logOutput(final OutputModel output) {
		LOGGER.debug("Output {} of {} of type {}", output.getName(), output.getFunctionBlock().getType(), //$NON-NLS-1$
				output.getType());
	}

	/**
	 * Logs information about an Input.
	 * 
	 * @param input
	 *            the Input to inspect
	 */
	private void logInput(final InputModel input) {
		LOGGER.debug("Input {} of {} of type {}", input.getName(), input.getFunctionBlock().getType(), input.getType()); //$NON-NLS-1$
	}

	@Override
	public String getName() {
		return Messages.Graphiti_DEBUG;
	}

	@Override
	public String getDescription() {
		return Messages.Graphiti_DEBUG_DESCRIPTION;
	}
}
