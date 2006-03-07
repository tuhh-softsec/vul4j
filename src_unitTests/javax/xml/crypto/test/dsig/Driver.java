package javax.xml.crypto.test.dsig;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;

/**
 * Used by ClassLoaderTest
 */
public class Driver {

    public void dsig() throws Exception {

        XMLSignatureFactory fac = XMLSignatureFactory.getInstance
            ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
        long start = System.currentTimeMillis();
        for (int i=0; i<100; i++) {
        CanonicalizationMethod cm = fac.newCanonicalizationMethod
            (CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null);
        }
        long end = System.currentTimeMillis();
        long elapsed = end-start;
        System.out.println("Elapsed:"+elapsed);
        System.out.println("dsig succeeded");
    }
}
