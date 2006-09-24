package org.apache.xml.security.test.transforms.implementations;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.apache.xml.security.test.transforms.implementations");
		//$JUnit-BEGIN$
		suite.addTest(TransformBase64DecodeTest.suite());
		suite.addTest(Xpath2TransformationTest.suite());
		//$JUnit-END$
		return suite;
	}

}
