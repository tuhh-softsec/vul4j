package gov.la.coastal.cims.hgms.harvester.structuregages.parser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import gov.la.coastal.cims.hgms.harvester.structuregages.parser.elements.ConfirmationTest;
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.elements.HeaderTest;
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.elements.LocationInformationTest;
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.elements.PayloadTest;

/**
 * Unit test suite for running all parser tests.
 *
 * @author darceyj
 * @since Jan 29, 2018
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ BinaryParserTest.class, ConfirmationTest.class,
		HeaderTest.class, InformationElementIdentifiersTest.class,
		LocationDirectionTest.class, LocationInformationTest.class,
		MessageTest.class, PayloadTest.class, SessionStatusTest.class,
		StationDataTypesTest.class })
public class AllParserTests
{
	/**
	 * Nothing
	 */
}
