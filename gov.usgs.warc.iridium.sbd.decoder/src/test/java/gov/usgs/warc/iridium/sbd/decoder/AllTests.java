package gov.usgs.warc.iridium.sbd.decoder;

import gov.usgs.warc.iridium.sbd.decoder.directip.AllIridiumTests;
import gov.usgs.warc.iridium.sbd.decoder.parser.AllParserTests;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.AllParserElementsTests;
import gov.usgs.warc.iridium.sbd.decoder.sixbitbinary.AllSixBitBinaryTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Convenience test suite for sub test suite classes
 *
 * @author mckelvym
 * @since Jan 5, 2018
 */
@RunWith(Suite.class)
@SuiteClasses({ AllIridiumTests.class, AllParserTests.class,
		AllParserElementsTests.class, AllSixBitBinaryTests.class, })
public class AllTests
{
	/**
	 * Nothing
	 */
}
