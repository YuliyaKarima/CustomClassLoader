import java.io.File;
import java.lang.reflect.Method;

/**
 * Testing customized class loader
 * 
 * @author Yuliya
 *
 */
public class ClassLoaderDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// creating object of class loader and passing to the constructor path
		// to a file on the disk
		MyClassLoader loader = new MyClassLoader("D:/BeatBoxImpl.jar");
		try {
			Class<?> newclass = loader.loadClass("BeatBoxImpl");
			System.out.println("Class " + newclass.getName() + " has been loaded by class loader "
					+ newclass.getClassLoader().getClass().getName());
			// creating object of an interface that is implemented by loaded
			// class
			BeatBoxInterface o = (BeatBoxInterface) newclass.newInstance();
			Method[] methods = newclass.getDeclaredMethods();
			System.out.println("Class's methods: ");
			for (Method m : methods) {
				System.out.println(m.getName());
			}
			// trying to invoke class method using reflection
			Method method = newclass.getMethod("buildGUI");
			method.invoke(o);
		} catch (Exception e) {
			System.out.println("Class object has not been created " + e);
		}

	}
}