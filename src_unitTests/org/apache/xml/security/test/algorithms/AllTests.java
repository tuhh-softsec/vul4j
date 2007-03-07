package org.apache.xml.security.test.algorithms;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.apache.xml.security.test.algorithms");
		//$JUnit-BEGIN$
		suite.addTest(SignatureAlgorithmTest.suite());
		//$JUnit-END$
		return suite;
	}

}
