package javax.xml.crypto.test.dsig;

import java.lang.reflect.Method;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.Provider;
import java.security.Security;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import org.jcp.xml.dsig.internal.dom.XMLDSigRI;

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

    public void test_provider_multiple_loaders() throws Exception {
        String baseDir = System.getProperty("basedir");
        String fs = System.getProperty("file.separator");
        File file0 = new File(baseDir + fs + "build" + fs + "classes" + fs);
        File file1 = new File(baseDir + fs + "build" + fs + "test" + fs);
        URL[] urls = new URL[2];
        urls[0] = file0.toURL();
        urls[1] = file1.toURL();

        URLClassLoader uc1 = new URLClassLoader(urls, null);
        //load security provider using current class loader
        final Provider provider = new XMLDSigRI();
        AccessController.doPrivileged(new java.security.PrivilegedAction() {
            public Object run() {
                Security.addProvider(provider);
                return null;
            }
        });
        // get the provider from java.security.Security using URLClassLoader. 
	// Need to use introspection to invoke methods to avoid using the 
	// current class loader
        String factoryName = "javax.xml.crypto.dsig.XMLSignatureFactory";
        Class factoryClass = uc1.loadClass(factoryName);
        Method factoryMethod = factoryClass.getDeclaredMethod
	    ("getInstance", new Class[]{String.class});
        Class methodParameterClass = uc1.loadClass
	    ("javax.xml.crypto.dsig.spec.C14NMethodParameterSpec");
        Method canonicalizationMethod = factoryClass.getDeclaredMethod
	    ("newCanonicalizationMethod", 
                new Class[]{String.class,methodParameterClass});
        Object factory = factoryMethod.invoke(null, new Object[]{"DOM"});
        long start = System.currentTimeMillis();
        for (int i=0; i<100; i++) {
            canonicalizationMethod.invoke
		(factory, new Object[]{CanonicalizationMethod.EXCLUSIVE,null});
        }
        long end = System.currentTimeMillis();
        long elapsed = end-start;
        System.out.println("Elapsed:"+elapsed);
        System.out.println("dsig succeeded");
    }

    public void test_provider_multiple_loaders_two() throws Exception {

        String baseDir = System.getProperty("basedir");
        String fs = System.getProperty("file.separator");
        File file0 = new File(baseDir + fs + "build" + fs + "classes" + fs);
        File file1 = new File(baseDir + fs + "build" + fs + "test" + fs);
        URL[] urls = new URL[2];
        urls[0] = file0.toURL();
        urls[1] = file1.toURL();
        URLClassLoader uc1 = new URLClassLoader(urls, null);
        URLClassLoader uc2 = new URLClassLoader(urls, null);

        Class c1 = uc1.loadClass("javax.xml.crypto.test.dsig.AppA");
        Class c2 = uc2.loadClass("javax.xml.crypto.test.dsig.AppB");
        Object o1 = c1.newInstance();
        Object o2 = c2.newInstance();
        Method m1 = c1.getMethod("dsig", (Class[]) null);
        Method m2 = c2.getMethod("dsig", (Class[]) null);
        m1.invoke(o1, (Object[]) null);
        m2.invoke(o2, (Object[]) null);
    }
}
