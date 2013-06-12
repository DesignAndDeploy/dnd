package edu.teco.dnd.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Pattern;

import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import sun.security.jca.JCAUtil;

/**
 * Provides methods that can be used to get the dependencies for a given class.
 * 
 * @author Philipp Adolf
 */
public class Dependencies {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Dependencies.class);
	
	/**
	 * Marker for {@link Logger#entry()} and {@link Logger#exit()}.
	 */
	private static final Marker FLOW_MARKER = MarkerManager.getMarker("FLOW");
	
	/**
	 * Repository used to load classes.
	 */
	private final Repository repository;
	
	/**
	 * ClassPath used to search for class files.
	 */
	private final ClassPath classPath;
	
	/**
	 * Used to exclude packages or classes.
	 */
	private final Collection<Pattern> filter;
	
	/**
	 * Initializes a new Dependencies object.
	 * 
	 * @param classPath the ClassPath to use
	 * @param filter these are used to filter the classes. If a class name (possibly including suffixes like
	 * 			<code>$1</code> for anonymous inner classes) is matched by any of the Patterns, the class is not
	 * 			inspected
	 */
	public Dependencies(final ClassPath classPath, final Collection<Pattern> filter) {
		LOGGER.entry(classPath, filter);
		this.classPath = classPath;
		
		this.repository = SyntheticRepository.getInstance(classPath);
		
		this.filter = Collections.unmodifiableCollection(new ArrayList<Pattern>(filter));
		LOGGER.exit();
	}
	
	/**
	 * Initializes a new Dependencies object without filters.
	 * 
	 * @param classPath the ClassPath to use
	 */
	public Dependencies(final ClassPath classPath) {
		this(classPath, Collections.<Pattern>emptyList());
	}
	
	/**
	 * Initializes a new Dependencies object.
	 * 
	 * @param classPath the classpath to use
	 * @param filter the filters to use
	 * @see #Dependencies(ClassPath, Collection)
	 */
	public Dependencies(final String classPath, final Collection<Pattern> filter) {
		this(new ClassPath(classPath), filter);
	}
	
	/**
	 * Initializes a new Dependencies object without filters
	 * 
	 * @param classPath the classpath to use
	 */
	public Dependencies(final String classPath) {
		this(new ClassPath(classPath));
	}
	
	/**
	 * Initializes a new Dependencies object with the system class path.
	 * 
	 * @param filter the filters to use
	 * @see #Dependencies(ClassPath, Collection)
	 */
	public Dependencies(final Collection<Pattern> filter) {
		this(ClassPath.SYSTEM_CLASS_PATH, filter);
	}
	
	/**
	 * Initializes a new Dependencies object with the system class path and without filters.
	 */
	public Dependencies() {
		this(ClassPath.SYSTEM_CLASS_PATH);
	}
	
	/**
	 * Returns a collection of all files that the given class depends on. There may be dependencies that are not
	 * included in the returned collection, for example if the given class file is not found in the classpath. The
	 * given class will always be included if it can be loaded, even if filters would keep it from getting inspected.
	 * 
	 * @param className the class to inspect
	 * @return a Collection of all Files that are dependencies of the given class
	 */
	public Collection<File> getDependencies(final String className) {
		LOGGER.entry(className);
		final Collection<File> dependencies = new HashSet<File>();
		
		JavaClass cls = null;
		
		try {
			cls = repository.loadClass(className);
		} catch (final ClassNotFoundException e) {
			LOGGER.catching(Level.WARN, e);
			LOGGER.exit(dependencies);
			return dependencies;
		}
		
		for (final JavaClass c : getDependencies(cls)) {
			try {
				dependencies.add(new File(classPath.getClassFile(c.getClassName()).getPath()));
			} catch (final IOException e) {
				LOGGER.catching(Level.DEBUG, e);
			}
		}
		
		LOGGER.exit(dependencies);
		return dependencies;
	}
	
	/**
	 * Returns a collection of a all JavaClasses that are dependencies of the given class. This includes the given
	 * class.
	 * 
	 * @param cls the JavaClass to inspect
	 * @return all dependencies of the class that are not filtered
	 */
	public Collection<JavaClass> getDependencies(final JavaClass cls) {
		if (LOGGER.isTraceEnabled(FLOW_MARKER)) {
			LOGGER.entry(cls.getClassName());
		}
		
		final ConstantPool cp = cls.getConstantPool();
		final Collection<JavaClass> visited = new HashSet<JavaClass>();
		final Queue<JavaClass> toVisit = new LinkedList<JavaClass>();
		final Collection<JavaClass> dependencies = new HashSet<JavaClass>();
		toVisit.add(cls);
		
		final Visitor visitor = new EmptyVisitor() {
			@Override
			public void visitConstantClass(final ConstantClass cc) {
				LOGGER.entry(cc);
				JavaClass cls = null;
				try {
					cls = repository.loadClass(cc.getBytes(cp));
				} catch (ClassNotFoundException e) {
					return;
				}
				if (cls != null && !visited.contains(cls) && !isFiltered(cls)) {
					toVisit.add(cls);
				}
				LOGGER.exit();
			}
		};
		
		while (!toVisit.isEmpty()) {
			final JavaClass c = toVisit.remove();
			
			if (visited.contains(c)) {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("class {} was already visited", c.getClassName());
				}
				continue;
			}
			
			LOGGER.trace("adding class {}", c.getClassName());
			visited.add(c);
			dependencies.add(c);
			
			new DescendingVisitor(cls, visitor).visit();
		}
		
		if (LOGGER.isTraceEnabled(FLOW_MARKER)) {
			final Collection<String> names = new ArrayList<String>();
			for (final JavaClass c : dependencies) {
				names.add(c.getClassName());
			}
			LOGGER.exit(names);
		}
		return dependencies;
	}
	
	/**
	 * Checks to see if a given class is filtered.
	 * 
	 * @param cls the class to check
	 * @return true if the class is filtered
	 */
	private boolean isFiltered(final JavaClass cls) {
		if (LOGGER.isTraceEnabled(FLOW_MARKER)) {
			LOGGER.entry(cls.getClassName());
		}
		final String name = cls.getClassName();
		for (final Pattern pattern : filter) {
			if (pattern.matcher(name).matches()) {
				LOGGER.exit(true);
				return true;
			}
		}
		LOGGER.exit(false);
		return false;
	}
}
