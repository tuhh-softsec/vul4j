package org.apache.xml.security.test.utils;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.apache.xml.security.test.utils");
		//$JUnit-BEGIN$
		suite.addTest(IdResolverTest.suite());
		suite.addTest(Base64Test.suite());
		suite.addTestSuite(OldApiTest.class);
		//$JUnit-END$
		suite.addTest(org.apache.xml.security.test.utils.resolver.AllTests.suite());
		return suite;
	}

}
