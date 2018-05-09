package gov.la.coastal.cims.hgms.harvester.structuregages.parser;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Maps;

import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.usgs.warc.test.Tests;

/**
 * {@link StationDataTypes} test
 *
 * @author darceyj
 * @since Feb 23, 2018
 *
 */
public class StationDataTypesTest
{

	/**
	 * Assert that the testing class has the required test methods.
	 *
	 * @throws java.lang.Exception
	 * @since Feb 23, 2018
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> testingClass = StationDataTypesTest.class;
		final Class<?> classToTest = StationDataTypes.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass);
	}

	/**
	 * The test map to use
	 *
	 * @since Feb 23, 2018
	 */
	private Map<StationDataTypes, String> m_TestMap;

	/**
	 * Setup the test map
	 *
	 * @throws java.lang.Exception
	 * @since Feb 23, 2018
	 */
	@Before
	public void setUp() throws Exception
	{
		m_TestMap = Maps.newHashMap();
		m_TestMap.put(StationDataTypes.AIR_TEMPERATURE, "Temperature");
		m_TestMap.put(StationDataTypes.BAROMETRIC_PRESSURE, "Pressure");
		m_TestMap.put(StationDataTypes.WIND_DIRECTION, "Wind Direction");
		m_TestMap.put(StationDataTypes.WIND_SPEED, "Wind Speed");
		m_TestMap.put(StationDataTypes.WATER_LEVEL, "Water Level");

	}

	/**
	 * Test method for {@link StationDataTypes#getNameToEnum()}
	 *
	 * @since Feb 23, 2018
	 */
	@Test
	public void testGetNameToEnum()
	{
		m_TestMap.forEach((type, val) -> assertThat(type)
				.isEqualTo(StationDataTypes.getNameToEnum().get(val)));
	}
}
