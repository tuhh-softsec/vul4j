package gov.la.coastal.cims.hgms.harvester.structuregages.parser;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.la.coastal.cims.hgms.harvester.structuregages.Tests;
import gov.la.coastal.cims.hgms.harvester.structuregages.Tests.SkipMethod;

/**
 * Unit test for the {@link InformationElementIdentifiers}
 *
 * @author darceyj
 * @since Jan 29, 2018
 *
 */
public class InformationElementIdentifiersTest
{

	/**
	 * Assert that the testing class has all the required methods.
	 *
	 * @throws java.lang.Exception
	 * @since Jan 29, 2018
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> testingClass = InformationElementIdentifiersTest.class;
		final Class<?> classToTest = InformationElementIdentifiers.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.TO_STRING);
	}

	/**
	 * {@link InformationElementIdentifiers} to expected bytes
	 *
	 * @since Jan 29, 2018
	 */
	private BiMap<InformationElementIdentifiers, Byte> m_IdMap;

	/**
	 * Setup the map for testing
	 *
	 * @throws java.lang.Exception
	 * @since Jan 29, 2018
	 */
	@Before
	public void setUp() throws Exception
	{
		m_IdMap = HashBiMap.create();
		m_IdMap.put(InformationElementIdentifiers.CONFIRMATION, (byte) 5);
		m_IdMap.put(InformationElementIdentifiers.HEADER, (byte) 1);
		m_IdMap.put(InformationElementIdentifiers.PAYLOAD, (byte) 2);
		m_IdMap.put(InformationElementIdentifiers.LOCATION_INFORMATION,
				(byte) 3);

	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.parser.InformationElementIdentifiers#getByteId()}.
	 */
	@Test
	public void testGetByteId()
	{
		for (final InformationElementIdentifiers id : InformationElementIdentifiers
				.values())
		{
			assertThat(id.getByteId()).isEqualTo(Byte.valueOf(m_IdMap.get(id)));
		}
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.parser.InformationElementIdentifiers#getFromByte(byte)}.
	 */
	@Test
	public void testGetFromByte()
	{
		final BiMap<Byte, InformationElementIdentifiers> inverse = m_IdMap
				.inverse();
		for (final Byte b : inverse.keySet())
		{
			assertThat(inverse.get(b)).isEqualTo(
					InformationElementIdentifiers.getFromByte(b).get());
		}
	}

}
