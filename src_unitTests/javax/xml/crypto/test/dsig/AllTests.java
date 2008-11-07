package javax.xml.crypto.test.dsig;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for javax.xml.crypto.test.dsig");
		//$JUnit-BEGIN$
		suite.addTestSuite(IaikCoreFeaturesTest.class);
		suite.addTestSuite(SignatureMethodTest.class);
		suite.addTestSuite(SignaturePropertyTest.class);
		suite.addTestSuite(BaltimoreXPathFilter2ThreeTest.class);
		suite.addTestSuite(ClassLoaderTest.class);
		suite.addTestSuite(XMLValidateContextTest.class);
		suite.addTestSuite(BaltimoreIaik2Test.class);
		suite.addTestSuite(C14N11Test.class);
		suite.addTestSuite(Baltimore23Test.class);
		suite.addTestSuite(XMLSignContextTest.class);
		suite.addTestSuite(DetachedTest.class);
		suite.addTestSuite(SignaturePropertiesTest.class);
		suite.addTestSuite(ValidateSignatureTest.class);
		suite.addTestSuite(SecureXSLTTest.class);
		suite.addTestSuite(CreateBaltimore23Test.class);
		suite.addTestSuite(ManifestTest.class);
		suite.addTestSuite(DigestMethodTest.class);
		suite.addTestSuite(IaikTransformsTest.class);
		suite.addTestSuite(BaltimoreExcC14n1Test.class);
		suite.addTestSuite(XMLObjectTest.class);
		suite.addTestSuite(PhaosXMLDSig3Test.class);
		suite.addTestSuite(CreateInteropXFilter2Test.class);
		suite.addTestSuite(XMLSignatureFactoryTest.class);
		suite.addTestSuite(SignedInfoTest.class);
		suite.addTestSuite(IaikSignatureAlgosTest.class);
		suite.addTestSuite(CreateInteropExcC14NTest.class);
		suite.addTestSuite(CreatePhaosXMLDSig3Test.class);
		suite.addTestSuite(XMLSignatureTest.class);
		suite.addTestSuite(ReferenceTest.class);
		suite.addTestSuite(CanonicalizationMethodTest.class);
		suite.addTestSuite(Baltimore18Test.class);
		suite.addTestSuite(ComRSASecurityTest.class);
		suite.addTestSuite(InteropC14nTest.class);
		suite.addTestSuite(TransformTest.class);
		//$JUnit-END$
		return suite;
	}

}
