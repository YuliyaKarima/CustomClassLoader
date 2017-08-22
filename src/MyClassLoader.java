import java.io.*;

import java.util.*;
import java.util.jar.*;
import java.lang.reflect.*;

/**
 * ClassLoader realization
 * 
 * @author Yuliya
 *
 */
public class MyClassLoader extends ClassLoader {
	// An attribute to cache loaded classes
	private HashMap<String, Class<?>> cache = new HashMap<String, Class<?>>();

	/**
	 * Constructor that takes path name to a file on the disk as an argument It
	 * analyzes file contents and put founded .class files into cache
	 * 
	 * @param path_name
	 *            path to a file on a disk which may contain file with extension
	 *            *.class
	 */
	public MyClassLoader(String path_name) {
		File file = new File(path_name);
		File[] files = file.listFiles();
		// if file name has extension *.jar then it must be loaded by method
		// loadJarFile
		if (path_name.endsWith(".jar")) {
			loadJarFile(path_name);
			// if file name has extension *.class then it must be loaded by
			// method loadClassFile
		} else if (path_name.endsWith(".class")) {
			loadClassFile(path_name, files[0].getAbsolutePath());
			// and if path name is a directory then look for .class files in it
			// and load them by method loadClassFile
		} else if (file.isDirectory()) {
			String[] s = file.list();
			try {
				for (int i = 0; i < s.length; i++) {
					if (s[i].endsWith(".class"))
						loadClassFile(s[i], files[i].getPath());
				}
			} catch (Exception e) {

			}
		} else
			// in other situations path name is incorrect or the directory
			// doesn't contain .class files
			System.out.println("Error: incorrect path name or there is no class file in this directory");
	}

	/**
	 * Overrides method loadClass by checking if the class name starts with
	 * definite letters or a word. At first, it try to find class in cache,
	 * then, if not found, loading a class by system loader. After finding the
	 * class it checks for class size by calling method check_size(class). Class
	 * will not be returned if it's size bigger then 2000 bytes
	 */
	public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
		// we don't load class when it's name starts with 'My'
		if (!(name.startsWith("My"))) {
			String className = name + ".class";
			// firstly look for a class in cache
			Class<?> loadedClass = cache.get(className);
			// If not found, loads class by system loader
			if (loadedClass == null) {
				loadedClass = super.findSystemClass(name);
			}
			// checking class size
			if (check_size(loadedClass)) {
				// if class's size is lower then definite size then return class
				return loadedClass;
			} else
				System.out.println("Class can not be loaded: oversized");
			return null;
		} else {
			System.out.println("Class can not be loaded: class name contains unpermitted symbols");
			return null;
		}

	}

	/**
	 * Method loads .class files from .jar file
	 * 
	 * @param jarName
	 *            path to .jar file
	 */
	public void loadJarFile(String jarName) {
		try (JarFile jarFile = new JarFile(jarName)) {
			// looking for .class file among jar archieve contents
			Enumeration<JarEntry> jarFiles = jarFile.entries();
			while (jarFiles.hasMoreElements()) {
				JarEntry entry = jarFiles.nextElement();
				String jarEntryName = entry.getName();
				if (jarEntryName.endsWith(".class")) {
					String className = jarEntryName.substring(0, jarEntryName.length() - 6);
					InputStream in = jarFile.getInputStream(entry);
					byte[] classData = new byte[(int) entry.getSize()];
					in.read(classData);
					Class<?> newClass = defineClass(className, classData, 0, classData.length);
					cache.put(jarEntryName, newClass);
				}
			}
		} catch (IOException e) {
			System.out.println("Error while loading file " + e);
		}
	}

	/**
	 * Loads class from file with extension *.class
	 * 
	 * @param className
	 *            name of the class
	 * @param class_path
	 *            path to the class file
	 */
	public void loadClassFile(String className, String class_path) {
		File f = new File(class_path);
		System.out.println(f.isFile() + f.getName());
		String the_className = className.substring(0, className.length() - 6);
		System.out.println(the_className);
		try (FileInputStream bis = new FileInputStream(f)) {
			byte[] classData = new byte[(int) f.length()];
			bis.read(classData);
			Class<?> newClass = defineClass(the_className, classData, 0, classData.length);
			cache.put(className, newClass);
		} catch (IOException e) {
			System.out.println("Error while loading class " + e);
		}
	}

	/**
	 * Calculates class size by summarize fields' size
	 * 
	 * @param a_class
	 *            class
	 * @return boolean true, if class size less then 2000 bytes and false, when
	 *         more then 2000 bytes
	 * 
	 */
	private boolean check_size(Class<?> a_class) {
		int class_size = 0;
		boolean to_load = false;
		Field[] class_field = a_class.getDeclaredFields();
		for (Field f : class_field) {
			Class<?> field_type = f.getType();
			class_size += size(field_type.getName());
			System.out.println(field_type.getName() + " - " + size(field_type.getName()));
		}
		if (class_size > 2000)
			to_load = false;
		else
			to_load = true;
		System.out.println(class_size);
		return to_load;
	}

	/**
	 * Returns size in bytes dependently on concrete primitive type or reference
	 * 
	 * @param field_type_class
	 * @return size in bytes
	 */
	private int size(String field_type_class) {
		int field_size = 0;
		// we use "switch - case" construction to determine size of each of
		// eight primitive type and reference type
		switch (field_type_class) {
		case ("int"):
			field_size = 32;
			break;
		case ("long"):
			field_size = 64;
			break;
		case ("short"):
			field_size = 16;
			break;
		case ("double"):
			field_size = 64;
			break;
		case ("float"):
			field_size = 32;
			break;
		case ("boolean"):
			field_size = 8;
			break;
		case ("char"):
			field_size = 16;
			break;
		case ("byte"):
			field_size = 8;
			break;
		default:
			field_size = 8;
			break;
		}
		return field_size;
	}
}
