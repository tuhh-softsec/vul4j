package gov.usgs.warc.iridium.sbd.decoder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import gov.usgs.warc.iridium.sbd.decoder.directip.AllIridiumTests;
import gov.usgs.warc.iridium.sbd.decoder.parser.AllParserTests;
import gov.usgs.warc.iridium.sbd.decoder.sixbitbinary.AllSixBitBinaryTests;

/**
 * Convenience test suite.
 *
 * @author mckelvym
 * @since Jan 5, 2018
 */
@RunWith(Suite.class)
@SuiteClasses({ AllSixBitBinaryTests.class, AllParserTests.class,
		AllIridiumTests.class })
public class AllTests
{
	/**
	 * Nothing
	 */
}
