package net.webassembletool.test.cases;

import net.webassembletool.test.junit.HttpTestCase;

/**
 * TODO Type javadoc
 * 
 * @author FRBON
 */
public class AggregatorTest extends HttpTestCase {

    public void testAggregatorWebapp() throws Exception {
        assertBodyGetEqualsLocalFile("aggregator");
    }

}
