package gov.la.coastal.cims.hgms.harvester.structuregages.parser.elements;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.usgs.warc.test.Tests;
import gov.usgs.warc.test.Tests.SkipMethod;

/**
 * Test the {@link Payload} element
 *
 * @author darceyj
 * @since Jan 8, 2018
 *
 */
public class PayloadTest
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
		final Class<?> classToTest = Payload.class;
		final Class<?> testingClass = PayloadTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.BUILDER, SkipMethod.TO_STRING,
				SkipMethod.EQUALS_AND_HASHCODE, SkipMethod.CAN_EQUAL);

	}

	/**
	 * The {@link Payload} object to use
	 *
	 * @since Jan 8, 2018
	 */
	private Payload m_Testable;

	/**
	 * Setup the payload object
	 *
	 * @since Jan 8, 2018
	 */
	@Before
	public void setUp()
	{
		m_Testable = Payload.builder().id((byte) 0x02).length((short) 1)
				.payload(new Byte[] { (byte) 0x00 }).build();
	}

	/**
	 * Test method for {@link Payload#getId()}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetId()
	{
		assertThat(m_Testable.getId()).isEqualTo((byte) 0x02);
	}

	/**
	 * Test method for {@link Payload#getLength()}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetLength()
	{
		final short expected = 1;
		assertThat(m_Testable.getLength()).isEqualTo(expected);
	}

	/**
	 * Test method for {@link Payload#getPayload()}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetPayload()
	{
		final Byte[] expected = new Byte[] { (byte) 0x00 };
		assertThat(m_Testable.getPayload()).isEqualTo(expected);
	}

}
