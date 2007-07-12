package javax.xml.crypto.test.dsig;

import java.security.Provider;
import java.security.Security;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformService;

/**
 * Used by ClassLoaderTest
 */
public class AppB {

    public void dsig() throws Exception {

	Provider p = Security.getProvider("XMLDSig");
	TransformService ts = TransformService.getInstance(Transform.XPATH, "DOM", p);
    }
}
