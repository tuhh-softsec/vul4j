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

