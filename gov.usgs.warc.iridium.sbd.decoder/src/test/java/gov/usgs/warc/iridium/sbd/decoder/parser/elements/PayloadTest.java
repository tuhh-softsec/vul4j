package gov.usgs.warc.iridium.sbd.decoder.parser.elements;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Maps;
import gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper;
import gov.usgs.warc.iridium.sbd.decoder.Tests;
import gov.usgs.warc.iridium.sbd.decoder.Tests.SkipMethod;
import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private byte[]		m_PayloadBytes;

	/**
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private PayloadType	m_PayloadType;

	/**
	 * The {@link Payload} object to use
	 *
	 * @since Jan 8, 2018
	 */
	private Payload		m_Testable;

	/**
	 * Setup the payload object
	 *
	 * @since Jan 8, 2018
	 */
	@Before
	public void setUp()
	{
		m_PayloadType = PayloadType.PSEUDOBINARY_B_DATA_FORMAT;
		m_PayloadBytes = "??T??\\@AB@@@@@i@@@B`e@@\\N".getBytes();
		m_Testable = Payload.builder(m_PayloadType).id((byte) 0x02)
				.payload(m_PayloadBytes).build();
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.elements.Payload#decode(java.util.SortedSet, java.util.SortedSet)}.
	 */
	@Test
	public void testDecode()
	{
		final Map<SbdDataType, Double> dataMap = m_Testable.decode(
				ParsingTestsHelper.getDataTypeSet(),
				ParsingTestsHelper.getDecodeOrderSet());

		final Map<SbdDataType, Double> expectedValues = Maps.newHashMap();
		expectedValues.put(ParsingTestsHelper.BATTERY_DATATYPE, 13.876);
		expectedValues.put(ParsingTestsHelper.FLOOD_STAGE_DATATYPE, -0.44);
		expectedValues.put(ParsingTestsHelper.HUMIDITY_DATATYPE, 0.);
		expectedValues.put(ParsingTestsHelper.PRECIPITATION_DATATYPE, 0.28);
		expectedValues.put(ParsingTestsHelper.PRESSURE_DATATYPE, 1027.7);
		expectedValues.put(ParsingTestsHelper.PROTECTED_STAGE_DATATYPE, -0.36);
		expectedValues.put(ParsingTestsHelper.TEMPERATURE_DATATYPE, 39.38);
		expectedValues.put(ParsingTestsHelper.WIND_DIRECTION_DATATYPE, 0.);
		expectedValues.put(ParsingTestsHelper.WIND_SPEED_DATATYPE, 6.6);

		for (final Entry<SbdDataType, Double> entry : expectedValues.entrySet())
		{
			final SbdDataType dataType = entry.getKey();
			final Double expected = entry.getValue();

			assertThat(dataMap.get(dataType)).isEqualTo(expected);
		}
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
	 * Test method for {@link Payload#getPayload()}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetPayload()
	{
		assertThat(m_Testable.getPayload()).isEqualTo(m_PayloadBytes);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.elements.Payload#getPayloadType()}.
	 */
	@Test
	public void testGetPayloadType()
	{
		assertThat(m_Testable.getPayloadType()).isEqualTo(m_PayloadType);
	}

}
