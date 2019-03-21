package gov.usgs.warc.iridium.sbd.decoder.parser;

import gov.usgs.warc.iridium.sbd.decoder.parser.elements.ConfirmationTest;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.HeaderTest;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.LocationInformationTest;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.PayloadTest;
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
@SuiteClasses({ ConfirmationTest.class, HeaderTest.class,
		InformationElementIdentifiersTest.class, LocationDirectionTest.class,
		LocationInformationTest.class, MessageTest.class, PayloadTest.class,
		SbdParserTest.class, SessionStatusTest.class,
		StationDataTypesTest.class })
public class AllParserTests
{
	/**
	 * Nothing
	 */
}
