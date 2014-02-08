package edu.teco.dnd.eclipse.deployEditor;

import org.eclipse.swt.events.KeyAdapter;

public class DeployKeyAdapter extends KeyAdapter{
	private DeployEditor view;
	
	public DeployKeyAdapter(DeployEditor view){
		super();
		this.view = view;
	}
	
	public DeployEditor getView(){
		return view;
	}
	
}
