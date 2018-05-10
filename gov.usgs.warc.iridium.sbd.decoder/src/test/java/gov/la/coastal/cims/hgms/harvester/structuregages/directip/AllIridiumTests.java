package gov.la.coastal.cims.hgms.harvester.structuregages.directip;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * All iridium (non - parser) related tests
 *
 * @author darceyj
 * @since Feb 23, 2018
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ IridiumResponseTest.class, SbdProcessorTest.class,
		SbdProcessorNonMockTest.class })
public class AllIridiumTests
{
	/**
	 * Nothing
	 */
}
