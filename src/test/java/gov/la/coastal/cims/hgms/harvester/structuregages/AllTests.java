package gov.la.coastal.cims.hgms.harvester.structuregages;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import gov.la.coastal.cims.hgms.harvester.structuregages.directip.AllIridiumTests;
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.AllParserTests;
import gov.la.coastal.cims.hgms.harvester.structuregages.sixbitbinary.AllSixBitBinaryTests;

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
