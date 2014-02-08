package edu.teco.dnd.eclipse.deployEditor;

import org.eclipse.swt.events.SelectionAdapter;

public class DeploySelectionAdapter extends SelectionAdapter {

	private DeployEditor view;
	
	public DeploySelectionAdapter(DeployEditor view){
		super();
		this.view = view;
	}
	
	public DeployEditor getView(){
		return view;
	}
	
}
