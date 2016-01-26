package org.esigate.test.cases;

/**
 * @author Alexis Thaveau on 26/01/16.
 */
public class DNSExtensionTest extends BaseAggregatorTest {

    public void testDNS() throws Exception {
        doSimpleTest("dns/block.html", "block.html");
    }
}
