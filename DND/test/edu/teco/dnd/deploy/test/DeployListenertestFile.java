package edu.teco.dnd.deploy.test;

import edu.teco.dnd.deploy.DeployListener;

public class DeployListenertestFile implements DeployListener {

	String error = "";
	int classesLoaded = 0;
	int blocksStarted = 0;

	@Override
	public void updateDeployStatus(int classesLoaded, int blocksStarted) {
		this.classesLoaded = classesLoaded;
		this.blocksStarted = blocksStarted;

	}

	@Override
	public void deployError(String message) {
		error = message;

	}

}
