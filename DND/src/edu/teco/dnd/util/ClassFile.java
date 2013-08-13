package edu.teco.dnd.util;

import java.io.File;

/**
 * Represents a File that is a Java class.
 * 
 * @author Philipp Adolf
 */
public class ClassFile {
	/**
	 * The File represented by this object.
	 */
	private final File file;

	/**
	 * The name of the class stored in the File.
	 */
	private final String className;

	/**
	 * Initializes a new ClassFile.
	 * 
	 * @param file
	 *            the File represented by this object
	 * @param className
	 *            the name of the class in the File
	 */
	public ClassFile(final File file, final String className) {
		this.file = file;
		this.className = className;
	}

	/**
	 * Returns the File represented by this object.
	 * 
	 * @return the File represented by this object
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Returns the name of the class stored in the File.
	 * 
	 * @return the name of the class stored in the File
	 */
	public String getClassName() {
		return this.className;
	}

	@Override
	public String toString() {
		return "ClassFile[className=" + className + ",file=" + file + "]";
	}
}
