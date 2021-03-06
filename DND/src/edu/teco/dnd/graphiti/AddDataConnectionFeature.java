package edu.teco.dnd.graphiti;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

import edu.teco.dnd.graphiti.model.InputModel;
import edu.teco.dnd.graphiti.model.OutputModel;

/**
 * Adds the graphical representation of a connection between an {@link OutputModel} and an {@link InputModel}.
 */
public class AddDataConnectionFeature extends AbstractAddFeature {
	private static final Logger LOGGER = LogManager.getLogger(AddDataConnectionFeature.class);

	public AddDataConnectionFeature(final IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(final IAddContext context) {
		if (!(context instanceof IAddConnectionContext)) {
			return false;
		}
		IAddConnectionContext iac = (IAddConnectionContext) context;
		Object sourceBo = getBusinessObjectForPictogramElement(iac.getSourceAnchor());
		Object targetBo = getBusinessObjectForPictogramElement(iac.getTargetAnchor());
		return sourceBo instanceof OutputModel && targetBo instanceof InputModel;
	}

	@Override
	public PictogramElement add(final IAddContext context) {
		LOGGER.entry(context);

		IAddConnectionContext addConContext = (IAddConnectionContext) context;
		IPeCreateService peCreateService = Graphiti.getPeCreateService();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Connecting {} and {}", getBusinessObjectForPictogramElement(addConContext.getSourceAnchor()),
					getBusinessObjectForPictogramElement(addConContext.getTargetAnchor()));
		}

		// CONNECTION WITH POLYLINE
		LOGGER.debug("creating polyline");
		Connection connection = peCreateService.createFreeFormConnection(getDiagram());
		connection.setStart(addConContext.getSourceAnchor());
		connection.setEnd(addConContext.getTargetAnchor());
		LOGGER.debug("Connection is {}", connection);
		IGaService gaService = Graphiti.getGaService();
		Polyline polyline = gaService.createPolyline(connection);
		polyline.setLineWidth(2);
		polyline.setForeground(manageColor(IColorConstant.RED));
		LOGGER.debug("Polyline is {}", polyline);

		LOGGER.exit(connection);
		return connection;
	}
}
