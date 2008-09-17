package net.webassembletool.test.cases;

import net.webassembletool.test.junit.HttpTestCase;

/**
 * TODO Type javadoc
 * 
 * @author FRBON
 */
public class ProviderTest extends HttpTestCase {

    public void testProviderWebapp() throws Exception {
        assertBodyGetEqualsLocalFile("provider");
    }

}
