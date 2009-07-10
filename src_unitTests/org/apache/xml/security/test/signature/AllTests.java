package org.apache.xml.security.test.signature;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
	TestSuite suite = new TestSuite(
			"Test for org.apache.xml.security.test.signature");
	//$JUnit-BEGIN$
	suite.addTest(CreateSignatureTest.suite());
	suite.addTestSuite(X509DataTest.class);
	suite.addTestSuite(SignatureTest.class);
	suite.addTestSuite(XmlSecTest.class);
	suite.addTestSuite(InvalidKeyTest.class);
	suite.addTest(XMLSignatureInputTest.suite());
	suite.addTest(UnknownAlgoSignatureTest.suite());
	suite.addTest(KeyValueTest.suite());
	suite.addTest(ProcessingInstructionTest.suite());
	suite.addTest(NoKeyInfoTest.suite());
	//$JUnit-END$
	return suite;
    }
}
