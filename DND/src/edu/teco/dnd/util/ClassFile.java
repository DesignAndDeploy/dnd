package edu.teco.dnd.util;

import java.io.File;

/**
 * Represents a File that is a Java class.
 */
public class ClassFile {
	private final File file;
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

	public File getFile() {
		return this.file;
	}

	public String getClassName() {
		return this.className;
	}

	@Override
	public String toString() {
		return "ClassFile[className=" + className + ",file=" + file + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassFile other = (ClassFile) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}
}
