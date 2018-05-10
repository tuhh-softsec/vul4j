package gov.la.coastal.cims.hgms.harvester.structuregages.parser.elements;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.la.coastal.cims.hgms.harvester.structuregages.parser.InformationElementIdentifiers;
import gov.la.coastal.cims.hgms.harvester.structuregages.Tests;
import gov.la.coastal.cims.hgms.harvester.structuregages.Tests.SkipMethod;

/**
 * Test the Confirmation element
 *
 * @author darceyj
 * @since Jan 8, 2018
 *
 */
public class ConfirmationTest
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
		final Class<?> classToTest = Confirmation.class;
		final Class<?> testingClass = ConfirmationTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.BUILDER);

	}

	/**
	 * The {@link Confirmation} element to test with.
	 *
	 * @since Jan 8, 2018
	 */
	private Confirmation m_Testable;

	/**
	 * Set up the {@link Confirmation} testing object
	 *
	 * @since Jan 8, 2018
	 */
	@Before
	public void setUp()
	{
		m_Testable = Confirmation.builder().id((byte) 0x05).length((short) 1)
				.status((byte) 0x01).build();
	}

	/**
	 * Test method for {@link Confirmation#getId()}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetId()
	{
		assertThat(m_Testable.getId())
				.isEqualTo(InformationElementIdentifiers.CONFIRMATION
						.getByteId().byteValue());
	}

	/**
	 * Test method for {@link Confirmation#getLength()}
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
	 * Test method for {@link Confirmation#getStatus()}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetStatus()
	{
		final byte expected = (byte) 0x01;
		assertThat(m_Testable.getStatus()).isEqualTo(expected);
	}

}
