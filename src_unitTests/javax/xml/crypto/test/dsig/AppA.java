package javax.xml.crypto.test.dsig;

import java.security.Security;

/**
 * Used by ClassLoaderTest
 */
public class AppA {

    public void dsig() throws Exception {

	Security.addProvider(new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
    }
}
