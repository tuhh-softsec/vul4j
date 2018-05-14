package gov.usgs.warc.iridium.sbd.decoder.parser.elements;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.usgs.warc.iridium.sbd.decoder.Tests;
import gov.usgs.warc.iridium.sbd.decoder.Tests.SkipMethod;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Header;

/**
 * Test the header information element
 *
 * @author darceyj
 * @since Jan 5, 2018
 *
 */
public class HeaderTest
{

	/**
	 * Assert that the test has all the required methods
	 *
	 * @throws java.lang.Exception
	 * @since Jan 5, 2018
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> classToTest = Header.class;
		final Class<?> testingClass = HeaderTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.BUILDER, SkipMethod.EQUALS_AND_HASHCODE,
				SkipMethod.TO_STRING, SkipMethod.CAN_EQUAL);

	}

	/**
	 * The {@link Header}
	 *
	 * @since Jan 5, 2018
	 */
	private Header m_Testable;

	/**
	 * Set up the testable header object
	 *
	 * @since Jan 8, 2018
	 */
	@Before
	public void setUp()
	{
		m_Testable = Header.builder().cdrId(1).id('0').imei('1')
				.length((short) 10).momsn((short) 0).mtmsn((short) 0)
				.sessionTime(1081157732).status('1').build();

	}

	/**
	 * Test method for {@link Header#getCdrId()}
	 *
	 * @since Jan 5, 2018
	 */
	@Test
	public void testGetCdrId()
	{
		assertThat(m_Testable.getCdrId()).isEqualTo(1);
	}

	/**
	 * Test method for {@link Header#getId()}
	 *
	 * @since Jan 5, 2018
	 */
	@Test
	public void testGetId()
	{
		assertThat(m_Testable.getId()).isEqualTo('0');

	}

	/**
	 * Test method for {@link Header#getImei()}
	 *
	 * @since Jan 5, 2018
	 */
	@Test
	public void testGetImei()
	{
		assertThat(m_Testable.getImei()).isEqualTo('1');

	}

	/**
	 * Test method for {@link Header#getLength()}
	 *
	 * @since Jan 5, 2018
	 */
	@Test
	public void testGetLength()
	{

		final short expected = 10;
		assertThat(m_Testable.getLength()).isEqualTo(expected);
	}

	/**
	 * Test method for {@link Header#getMomsn()}
	 *
	 * @since Jan 5, 2018
	 */
	@Test
	public void testGetMomsn()
	{
		final short expected = 0;
		assertThat(m_Testable.getMomsn()).isEqualTo(expected);

	}

	/**
	 * Test method for {@link Header#getMtmsn()}
	 *
	 * @since Jan 5, 2018
	 */
	@Test
	public void testGetMtmsn()
	{
		final short expected = 0;
		assertThat(m_Testable.getMtmsn()).isEqualTo(expected);
	}

	/**
	 * Test method for {@link Header#getStatus()}
	 *
	 * @since Jan 5, 2018
	 */
	@Test
	public void testGetStatus()
	{
		final char expected = '1';
		assertThat(m_Testable.getStatus()).isEqualTo(expected);
	}

	/**
	 * Test method for {@link Header#getZonedTimeFromSession()}
	 *
	 * @since Jan 5, 2018
	 */
	@Test
	public void testGetZonedTimeFromSession()
	{
		final ZonedDateTime expected = ZonedDateTime
				.of(LocalDateTime.of(2004, 4, 5, 9, 35, 32), ZoneId.of("UTC"));
		assertThat(m_Testable.getZonedTimeFromSession()).isEqualTo(expected);
	}

}
