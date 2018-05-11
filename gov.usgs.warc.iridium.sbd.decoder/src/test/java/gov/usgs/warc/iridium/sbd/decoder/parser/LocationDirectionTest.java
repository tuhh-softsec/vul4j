package gov.usgs.warc.iridium.sbd.decoder.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import gov.usgs.warc.iridium.sbd.decoder.Tests;
import gov.usgs.warc.iridium.sbd.decoder.Tests.SkipMethod;
import gov.usgs.warc.iridium.sbd.decoder.parser.LocationDirection;

/**
 * The location direction test
 *
 * @author darceyj
 * @since Jan 26, 2018
 *
 */
public class LocationDirectionTest
{

	/**
	 * Assert that the testing class has all of the required methods
	 *
	 * @throws java.lang.Exception
	 * @since Jan 26, 2018
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> classToTest = LocationDirection.class;
		final Class<?> testingClass = LocationDirectionTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.TO_STRING, SkipMethod.CAN_EQUAL, SkipMethod.BUILDER);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.LocationDirection#fromEWIBit(boolean)}.
	 */
	@Test
	public void testFromEWIBit()
	{
		assertThat(LocationDirection.fromEWIBit(false))
				.isEqualTo(LocationDirection.EAST);
		assertThat(LocationDirection.fromEWIBit(true))
				.isEqualTo(LocationDirection.WEST);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.LocationDirection#fromNSIBit(boolean)}.
	 */
	@Test
	public void testFromNSIBit()
	{
		assertThat(LocationDirection.fromNSIBit(false))
				.isEqualTo(LocationDirection.NORTH);
		assertThat(LocationDirection.fromNSIBit(true))
				.isEqualTo(LocationDirection.SOUTH);
	}

}
