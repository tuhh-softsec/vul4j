/*
 * Copyright 2003-2004 The Apache Software Foundation.
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

package org.apache.xml.security.test;

import org.apache.xml.security.test.encryption.XMLCipherTester;
import org.apache.xml.security.test.encryption.BaltimoreEncTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class EncryptionTest extends TestCase {
    public EncryptionTest(String test) {
       super(test);
    }

    public static void main(String[] args) {
		org.apache.xml.security.Init.init();
        processCmdLineArgs(args);
        TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("DOM XML Encryption Tests");
        suite.addTest(new TestSuite(XMLCipherTester.class));
        suite.addTest(new TestSuite(BaltimoreEncTest.class));
        return (suite);
    }

    private static void processCmdLineArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-d")) {
                String doc = args[i].substring(2).trim();
                System.setProperty("org.apache.xml.enc.test.doc", doc);
            } else if (args[i].startsWith("-e")) {
                String elem = args[i].substring(2).trim();
                System.setProperty("org.apache.xml.enc.test.elem", elem);
            } else if (args[i].startsWith("-i")) {
                String idx = args[i].substring(2).trim();
                System.setProperty("org.apache.xml.enc.test.idx", idx);
            }
        }
    }
}

