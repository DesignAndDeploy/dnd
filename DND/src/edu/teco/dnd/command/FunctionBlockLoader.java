package edu.teco.dnd.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.ModelFactory;

/**
 * This class is responsible for loading the functionBlockModels from a .blocks file.
 * 
 * @author jung
 * 
 */
public class FunctionBlockLoader {

	private String path;
	private String applicationName;
	private Collection<FunctionBlockModel> blocks;

	/**
	 * Creates a new FunctionBlockLoader that loads the FunctionBlockModels from the given path.
	 * 
	 * @param path
	 *            Path to load the FunctionBlockModels from.
	 */
	public FunctionBlockLoader(String path) {
		this.path = path;
		applicationName = path.replaceAll("\\.blocks", "");
		java.net.URI uuri = new File(path).toURI();
		System.out.println(uuri.toASCIIString());
		// blocks = loadBlocks();
	}

	// TODO: How are the connections stored?
	public void loadBlocks() {
		Collection<FunctionBlockModel> functionBlocks = new ArrayList<FunctionBlockModel>();

		ModelFactory.eINSTANCE.eClass();

		URI uri = URI.createURI(new File(path).toURI().toASCIIString());

		Resource resource = new XMIResourceImpl(uri);

		try {
			resource.load(null);
		} catch (IOException e) {
			System.out.println("Loading blocks failed.");
			e.printStackTrace();
		}
		for (EObject object : resource.getContents()) {
			if (object instanceof FunctionBlockModel) {
				FunctionBlockModel blockmodel = (FunctionBlockModel) object;
				functionBlocks.add(blockmodel);
				System.out.println(blockmodel.getBlockName());
			}
		}
		blocks = functionBlocks;
	}

	/**
	 * Returns the blocks loaded by the FunctionBlockLoader.
	 * 
	 * @return Collection of loaded function block models.
	 */
	public Collection<FunctionBlockModel> getBlocks() {
		return blocks;
	}

	/**
	 * Returns the name of the application loaded by the FunctionBlockLoader.
	 * 
	 * @return Name of the application.
	 */
	public String getApplicationName() {
		return applicationName;
	}

}