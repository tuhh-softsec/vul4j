package org.apache.xml.security.test.transforms;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.transforms.Transform;

import junit.framework.TestCase;

/**
 * RegisterTest tests registering a custom Transform implementation loaded
 * by a URLCLassLoader.
 */
public class RegisterTest extends TestCase {

    private final static String basedir = System.getProperty("basedir", "./");

    public RegisterTest(String name) {
        super(name);
    }

    public static void test() throws Exception {

        Transform.init();
        File file = new File(basedir);
        URL[] urls = new URL[1];
        urls[0] = file.toURI().toURL();
        URLClassLoader ucl = new URLClassLoader(urls);
        Class c = ucl.loadClass
            ("org.apache.xml.security.test.transforms.SampleTransform");
        Constructor cons = c.getConstructor();
        Object o = cons.newInstance();
        // Apache code swallows the ClassNotFoundExc, so we need to
        // check if the Transform has already been registered by registering
        // it again and catching an AlgorithmAlreadyRegisteredExc
        try {
            Transform.register
                (SampleTransform.uri, 
                 "org.apache.xml.security.test.transforms.SampleTransform");
            throw new Exception("ClassLoaderTest failed");
        } catch (AlgorithmAlreadyRegisteredException e) {
            // test passed
        }
    }
}

