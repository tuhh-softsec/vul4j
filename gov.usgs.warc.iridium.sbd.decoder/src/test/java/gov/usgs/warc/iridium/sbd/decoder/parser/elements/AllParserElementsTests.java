package gov.usgs.warc.iridium.sbd.decoder.parser.elements;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Unit test suite for classes in this package
 *
 * @author mckelvym
 * @since Mar 22, 2019
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ ConfirmationTest.class, HeaderTest.class,
		LocationInformationTest.class, PayloadTest.class,
		PayloadTypeTest.class, })
public class AllParserElementsTests
{
	/**
	 * Nothing
	 */
}
