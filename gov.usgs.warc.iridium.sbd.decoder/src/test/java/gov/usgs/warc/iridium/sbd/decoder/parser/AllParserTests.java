package gov.usgs.warc.iridium.sbd.decoder.parser;

import gov.usgs.warc.iridium.sbd.decoder.parser.elements.LocationInformationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Unit test suite for running all parser tests.
 *
 * @author darceyj
 * @since Jan 29, 2018
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ InformationElementIdentifiersTest.class,
		LocationDirectionTest.class, LocationInformationTest.class,
		MessageTest.class, PseudobinaryBPayloadDecoderTest.class,
		SbdParserTest.class, SessionStatusTest.class })
public class AllParserTests
{
	/**
	 * Nothing
	 */
}
