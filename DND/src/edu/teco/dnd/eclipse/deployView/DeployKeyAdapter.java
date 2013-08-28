package edu.teco.dnd.eclipse.deployView;

import org.eclipse.swt.events.KeyAdapter;

public class DeployKeyAdapter extends KeyAdapter{
	private DeployView view;
	
	public DeployKeyAdapter(DeployView view){
		super();
		this.view = view;
	}
	
	public DeployView getView(){
		return view;
	}
	
}
