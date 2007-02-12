package javax.xml.crypto.test.dsig;

import java.lang.reflect.Method;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.*;

/**
 * This test uses more than one classloader to load a class (Driver) that
 * invokes the XMLSignature API. It tests that there are not provider class 
 * loading issues with more than one classloader (see 6380953).
 */
public class ClassLoaderTest extends TestCase {

    public ClassLoaderTest(String name) {
	super(name);
    }

    public void test_multiple_loaders() throws Exception {

	String baseDir = System.getProperty("basedir");
	String fs = System.getProperty("file.separator");
        File file0 = new File(baseDir + fs + "build" + fs + "classes" + fs);
        File file1 = new File(baseDir + fs + "build" + fs + "test" + fs);
        URL[] urls = new URL[2];
        urls[0] = file0.toURL();
        urls[1] = file1.toURL();
        URLClassLoader uc1 = new URLClassLoader(urls, null);
        URLClassLoader uc2 = new URLClassLoader(urls, null);

        Class c1 = uc1.loadClass("javax.xml.crypto.test.dsig.Driver");
        Class c2 = uc2.loadClass("javax.xml.crypto.test.dsig.Driver");
        Object o1 = c1.newInstance();
        Object o2 = c2.newInstance();
        Method m1 = c1.getMethod("dsig", (Class[]) null);
        Method m2 = c2.getMethod("dsig", (Class[]) null);
        m1.invoke(o1, (Object[]) null);
        m2.invoke(o2, (Object[]) null);
    }
}
