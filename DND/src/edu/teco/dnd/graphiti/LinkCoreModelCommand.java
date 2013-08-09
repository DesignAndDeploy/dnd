package edu.teco.dnd.graphiti;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.services.GraphitiUi;


public class LinkCoreModelCommand extends RecordingCommand {

	private Diagram diagram;
	private EObject coreModel;
	private String providerId;

	public LinkCoreModelCommand(TransactionalEditingDomain domain,
			Diagram diagram, EObject coreModel, String providerId) {
		super(domain);
		this.diagram = diagram;
		this.coreModel = coreModel;
		this.providerId = providerId;
	}

	@Override
	protected void doExecute() {
		IDiagramTypeProvider dtp = GraphitiUi.getExtensionManager()
				.createDiagramTypeProvider(diagram, providerId);
		IFeatureProvider featureProvider = dtp.getFeatureProvider();
		featureProvider.link(diagram, coreModel);
	}
}