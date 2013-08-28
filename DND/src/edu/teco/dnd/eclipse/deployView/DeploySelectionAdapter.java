package edu.teco.dnd.eclipse.deployView;

import org.eclipse.swt.events.SelectionAdapter;

public class DeploySelectionAdapter extends SelectionAdapter {

	private DeployView view;
	
	public DeploySelectionAdapter(DeployView view){
		super();
		this.view = view;
	}
	
	public DeployView getView(){
		return view;
	}
	
}
