package edu.teco.dnd.eclipse;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Utility class for getting projects and class paths.
 */
public final class EclipseUtil {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(EclipseUtil.class);

	/**
	 * Utility class.
	 */
	private EclipseUtil() {
	}

	/**
	 * Gets the project of an IPath.
	 * 
	 * @param path
	 *            the path to look up
	 * @return the project or null
	 */
	public static IProject getWorkspaceProject(final IPath path) {
		LOGGER.entry(path);
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
		IProject project = null;
		if (resource != null) {
			project = resource.getProject();
		}
		LOGGER.exit(project);
		return project;
	}

	/**
	 * Gets the project the Diagram is part of.
	 * 
	 * @param uri
	 *            the URI to look up
	 * @return the project the Diagram is part of or null
	 */
	public static IProject getWorkspaceProject(final URI uri) {
		LOGGER.entry(uri);
		IProject project = null;
		URI u = uri;
		if (u.isPlatform()) {
			u = URI.createURI(u.toPlatformString(true));
		}
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = workspaceRoot.findMember(u.toString());
		LOGGER.trace(resource);
		if (resource == null && u.segmentCount() > 0) {
			resource = workspaceRoot.findMember(u.segment(0));
			LOGGER.trace(resource);
		}
		if (resource != null) {
			project = resource.getProject();
		}
		LOGGER.exit(project);
		return project;
	}

	/**
	 * Gets the absolute paths of all output folders for binary objects of a Java Project.
	 * 
	 * @param project
	 *            the project to inspect
	 * @return a set containing all output folders
	 */
	public static Set<IPath> getAbsoluteBinPaths(final IProject project) {
		LOGGER.entry(project);
		if (project == null) {
			return Collections.emptySet();
		}
		Set<IPath> paths = new HashSet<>();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IJavaProject javaProject = JavaCore.create(project);
		try {
			paths.add(workspaceRoot.getFile(javaProject.getOutputLocation()).getRawLocation());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		try {
			for (IClasspathEntry cp : javaProject.getRawClasspath()) {
				IPath path = null;
				LOGGER.trace("checking {}", cp);
				switch (cp.getEntryKind()) {
				case IClasspathEntry.CPE_SOURCE:
					path = cp.getOutputLocation();
					LOGGER.debug("source {}", cp);
					break;

				case IClasspathEntry.CPE_LIBRARY:
					path = cp.getPath();
					LOGGER.debug("library {}", cp);
					break;

				default:
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("{}, {}, {}", cp, cp.getPath(), cp.getOutputLocation());
					}
					break;
				}
				if (path != null) {
					if (workspaceRoot.getRawLocation().isPrefixOf(path)) {
						LOGGER.debug("path {} is not raw");
						path = workspaceRoot.getFile(path).getRawLocation();
					}
					LOGGER.debug("adding path {}", path);
					paths.add(path);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return paths;
	}
}
