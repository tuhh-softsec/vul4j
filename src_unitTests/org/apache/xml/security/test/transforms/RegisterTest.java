/*
 * Copyright  1999-2009 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.xml.security.test.transforms;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.transforms.Transform;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * RegisterTest tests registering a custom Transform implementation loaded
 * by a URLCLassLoader.
 */
public class RegisterTest extends TestCase {

    private final static String basedir = System.getProperty("basedir", "./");

    public RegisterTest(String name) {
        super(name);
    }

    public static Test suite() {
       return new TestSuite(RegisterTest.class);
    }

    public static void test() throws Exception {

        Transform.init();
        File file = new File(basedir);
        URL[] urls = new URL[1];
        urls[0] = file.toURI().toURL();
        URLClassLoader ucl = new URLClassLoader(urls);
        Class c = ucl.loadClass
            ("org.apache.xml.security.test.transforms.SampleTransform");
        Constructor cons = c.getConstructor(new Class[0]);
        cons.newInstance(new Object[0]);
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

