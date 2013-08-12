package edu.teco.dnd.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Scans through directories and JAR files and returns all found classes using BCEL.
 * 
 * @author philipp
 */
public class ClassScanner {
	/**
	 * A filter that accepts all classes.
	 * 
	 * @author philipp
	 */
	public static final class NullFilter implements ClassFilter {
		/**
		 * Always accepts.
		 * 
		 * @param cls
		 *            the class to accept
		 * @return true
		 */
		@Override
		public boolean acceptClass(final JavaClass cls) {
			return true;
		}
	}

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ClassScanner.class);

	/**
	 * The repository used to load the classes.
	 */
	private final Repository repository;

	/**
	 * The filter used by this scanner.
	 */
	private final ClassFilter filter;

	/**
	 * Initializes a new scanner.
	 * 
	 * @param repository the Repository to use for loading classes. Must not be null
	 * @param filter
	 *            the filter to use. Must not be null.
	 */
	public ClassScanner(final Repository repository, final ClassFilter filter) {
		if (repository == null) {
			throw new IllegalArgumentException("repository must not be null");
		}
		if (filter == null) {
			throw new IllegalArgumentException("filter must not be null");
		}
		this.repository = repository;
		this.filter = filter;
	}
	
	public ClassScanner(final Collection<File> paths, final ClassFilter filter) {
		if (paths == null) {
			throw new IllegalArgumentException("paths must not be null");
		}
		if (filter == null) {
			throw new IllegalArgumentException("filter must not be null");
		}
		
		final ClassPath classPath = new ClassPath(StringUtil.joinIterable(new HashSet<File>(paths), ":"));
		this.repository = SyntheticRepository.getInstance(classPath);
		this.filter = filter;
	}
	
	public ClassScanner(final Repository repository) {
		this(repository, new NullFilter());
	}
	
	public ClassScanner(final Collection<File> paths) {
		this(paths, new NullFilter());
	}

	/**
	 * Scans the given directories.
	 * 
	 * @param files
	 *            the directories to scan
	 * @return a set containing all classes in the given directories that were accepted by the filter
	 */
	public Set<JavaClass> getClasses(final String... files) {
		Set<JavaClass> classes = new HashSet<JavaClass>();
		for (String f : files) {
			classes.addAll(getClasses(new File(f)));
		}
		return classes;
	}

	/**
	 * Scans the given directories.
	 * 
	 * @param files
	 *            the directories to scan
	 * @return a set containing all classes in the given directories that were accepted by the filter
	 */
	public Set<JavaClass> getClasses(final File... files) {
		Set<JavaClass> classes = new HashSet<JavaClass>();
		for (File f : files) {
			classes.addAll(getClasses(f));
		}
		return classes;
	}
	
	public Set<JavaClass> getClasses(final Iterable<File> files) {
		Set<JavaClass> classes = new HashSet<JavaClass>();
		for (File f : files) {
			classes.addAll(getClasses(f));
		}
		return classes;
	}

	/**
	 * Scans the given directory or JAR file (the latter is not yet implemented). Uses the given ClassLoader
	 * or the ClassLoader this class was loaded with if null is passed.
	 * 
	 * @param f
	 *            the directory or JAR file to scan
	 * @return a list of all classes accepted by the filter
	 */
	public Set<JavaClass> getClasses(final File f) {
		if (f == null) {
			throw new IllegalArgumentException("f must not be null");
		} else if (f.isDirectory()) {
			return getClassesDirectory(f, "");
		} else if (f.isFile()) {
			return getClassesJar(f);
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Returns all classes accepted by the filter in the given jar file.
	 * 
	 * @param jar
	 *            the jar file
	 * @return the classes accepted by the filter in the given jar file
	 */
	private Set<JavaClass> getClassesJar(final File jar) {
		assert jar != null;
		if (!jar.exists()) {
			return Collections.emptySet();
		}
		ZipFile zip = null;
		try {
			zip = new ZipFile(jar);
		} catch (IOException e) {
			return Collections.emptySet();
		}
		Set<JavaClass> classes = new HashSet<JavaClass>();
		Enumeration<? extends ZipEntry> entries = zip.entries();
		for (ZipEntry entry = null; entries.hasMoreElements();) {
			entry = entries.nextElement();
			if (entry.isDirectory()) {
				continue;
			}
			if (!entry.getName().endsWith(".class")) {
				continue;
			}
			String className = entry.getName();
			className = className.substring(0, className.length() - 6).replace('/', '.');
			JavaClass cls = null;
			try {
				cls = repository.loadClass(className);
			} catch (ClassNotFoundException e) {
				LOGGER.catching(Level.DEBUG, e);
				continue;
			}
			if (filter.acceptClass(cls)) {
				LOGGER.debug("adding class {}", cls);
				classes.add(cls);
			}
		}
		try {
			zip.close();
		} catch (IOException e) {
		}
		return classes;
	}

	/**
	 * Returns all classes accepted by the filter in the given directory.
	 * 
	 * @param directory
	 *            the directory to scan
	 * @param base
	 *            base name
	 * @return the classes accepted by the filter in the given directory
	 */
	private Set<JavaClass> getClassesDirectory(final File directory, final String base) {
		assert directory != null;
		assert directory.isDirectory();
		assert base != null;
		LOGGER.entry(directory, base);
		Set<JavaClass> classes = new HashSet<JavaClass>();
		for (File f : directory.listFiles()) {
			if (f.isDirectory()) {
				classes.addAll(getClassesDirectory(f, (base.isEmpty() ? "" : base + ".") + f.getName()));
			} else if (f.getName().endsWith(".class")) {
				String className = f.getName();
				className = className.substring(0, className.length() - 6);
				JavaClass cls = null;
				try {
					cls = repository.loadClass((base.isEmpty() ? "" : base + ".") + className);
				} catch (ClassNotFoundException e) {
					LOGGER.catching(Level.DEBUG, e);
					continue;
				}
				if (filter.acceptClass(cls)) {
					LOGGER.debug("adding class {}", cls);
					classes.add(cls);
				}
			}
		}
		LOGGER.exit(classes);
		return classes;
	}
}
