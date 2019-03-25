package gov.usgs.warc.iridium.sbd.decoder.parser;

import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.createDataType;
import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.createDecodeOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.usgs.warc.iridium.sbd.decoder.Tests;
import gov.usgs.warc.iridium.sbd.decoder.Tests.SkipMethod;
import gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder.Status;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Payload;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.PayloadType;
import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test {@link SutronStandardCsvPayloadDecoder}
 *
 * @author mckelvym
 * @since Mar 22, 2019
 *
 */
public class SutronStandardCsvPayloadDecoderTest
{

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private static final SbdDataType	FLOOD_STAGE_DATATYPE		= createDataType(
			1L, 0, "Flood", "ft", "Water Level", "x * 0.3048");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private static final SbdDataType	HUMIDITY_DATATYPE			= createDataType(
			2L, 0, "Humidity", "%", null, "x");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private static final SbdDataType	PRESSURE_DATATYPE			= createDataType(
			3L, 0, "Baro MB", "F", "Pressure", "x");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private static final SbdDataType	PROTECTED_STAGE_DATATYPE	= createDataType(
			4L, 0, "Prtctd", "ft", "Water Level", "x * 0.3048");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private static final SbdDataType	TEMPERATURE_DATATYPE		= createDataType(
			5L, 0, "Air Temp C", "", "Temperature", "x");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private static final SbdDataType	WIND_DIRECTION_DATATYPE		= createDataType(
			6L, 0, "WdDir", "Deg", "Wind Direction", "x");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private static final SbdDataType	WIND_SPEED_DATATYPE			= createDataType(
			7L, 0, "WdSpd", "mph", "Wind Speed", "x * 0.44704");

	/**
	 * @throws java.lang.Exception
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> classToTest = SutronStandardCsvPayloadDecoder.class;
		final Class<?> testingClass = SutronStandardCsvPayloadDecoderTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.CAN_EQUAL, SkipMethod.EQUALS_AND_HASHCODE,
				SkipMethod.TO_STRING);
	}

	/**
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private SortedSet<SbdDataType>			m_DataTypes;

	/**
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private SortedSet<SbdDecodeOrder>		m_DecodeOrder;

	/**
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private List<String>					m_Line;

	/**
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private Payload							m_Payload;

	/**
	 * Object under test.
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private SutronStandardCsvPayloadDecoder	m_Testable;

	/**
	 * @throws java.lang.Exception
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	@Before
	public void setUp() throws Exception
	{
		m_DataTypes = Sets.newTreeSet();
		m_DataTypes.addAll(Arrays.asList(FLOOD_STAGE_DATATYPE,
				HUMIDITY_DATATYPE, PRESSURE_DATATYPE, PROTECTED_STAGE_DATATYPE,
				TEMPERATURE_DATATYPE, WIND_DIRECTION_DATATYPE,
				WIND_SPEED_DATATYPE));
		m_DecodeOrder = Sets.newTreeSet(Arrays.asList(
				createDecodeOrder(1L, 0, FLOOD_STAGE_DATATYPE, 1L),
				createDecodeOrder(1L, 1, HUMIDITY_DATATYPE, 2L),
				createDecodeOrder(1L, 2, PRESSURE_DATATYPE, 3L),
				createDecodeOrder(1L, 3, TEMPERATURE_DATATYPE, 4L),
				createDecodeOrder(1L, 4, WIND_DIRECTION_DATATYPE, 5L),
				createDecodeOrder(1L, 5, WIND_SPEED_DATATYPE, 6L)));
		m_Testable = new SutronStandardCsvPayloadDecoder();
		final String payLoadBytes = "003/21/2019,15:00:00,Prtctd,0.48,ft,G\r\n"
				+ "03/21/2019,15:00:00,Flood,0.53,ft,G\r\n"
				+ "03/21/2019,15:00:00,WdSpd,4.8,mph,G\r\n"
				+ "03/21/2019,15:00:00,WdDir,18,Deg,G\r\n"
				+ "03/21/2019,15:00:00,Air Temp C,13.9,,G\r\n"
				+ "03/21/2019,15:00:00,Humidity,59,%,G\r\n"
				+ "03/21/2019,15:00:00,Baro MB,1020.4,F,G\r\n"
				+ "03/21/2019,21:45:00,Prtctd,0.24,ft,G\r\n"
				+ ":YB 13.95 :YN 073802332 :SN 1611330 ";
		m_Payload = Payload.builder(PayloadType.SUTRON_STANDARD_CSV)
				.id((byte) 0x02).payload(payLoadBytes.getBytes()).build();
		m_Line = Lists.newArrayList("03/21/2019", "21:45:00", "Prtctd", "0.24",
				"ft", "G");
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#decode(Payload, SortedSet, SortedSet)}.
	 */
	@Test
	public void testDecode()
	{
		final Map<SbdDataType, Double> dataMap = m_Testable.decode(m_Payload,
				m_DataTypes, m_DecodeOrder);

		final Map<SbdDataType, Double> expectedValues = Maps.newHashMap();
		expectedValues.put(FLOOD_STAGE_DATATYPE, 0.53);
		expectedValues.put(HUMIDITY_DATATYPE, 59.);
		expectedValues.put(PRESSURE_DATATYPE, 1020.4);
		/**
		 * Skip PROTECTED_STAGE_DATATYPE
		 */
		expectedValues.put(TEMPERATURE_DATATYPE, 13.9);
		expectedValues.put(WIND_DIRECTION_DATATYPE, 18.);
		expectedValues.put(WIND_SPEED_DATATYPE, 4.8);
		expectedValues.replaceAll(
				(dataType, value) -> dataType.transformValue(value));

		for (final Entry<SbdDataType, Double> entry : expectedValues.entrySet())
		{
			final SbdDataType dataType = entry.getKey();
			final Double expected = entry.getValue();

			assertThat(dataMap.get(dataType)).isEqualTo(expected);
		}

		assertThat(expectedValues.size()).isEqualTo(dataMap.size());
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#getDateIndex()}.
	 */
	@Test
	public void testGetDateIndex()
	{
		assertThat(m_Testable.getDateIndex()).isEqualTo(0);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#getFieldCount()}.
	 */
	@Test
	public void testGetFieldCount()
	{
		assertThat(m_Testable.getFieldCount()).isEqualTo(6);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#getNameIndex()}.
	 */
	@Test
	public void testGetNameIndex()
	{
		assertThat(m_Testable.getNameIndex()).isEqualTo(2);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#getQualityGoodValue()}.
	 */
	@Test
	public void testGetQualityGoodValue()
	{
		assertThat(m_Testable.getQualityGoodValue()).isEqualTo("G");
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#getQualityIndex()}.
	 */
	@Test
	public void testGetQualityIndex()
	{
		assertThat(m_Testable.getQualityIndex()).isEqualTo(5);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#getTimeIndex()}.
	 */
	@Test
	public void testGetTimeIndex()
	{
		assertThat(m_Testable.getTimeIndex()).isEqualTo(1);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#getUnitsIndex()}.
	 */
	@Test
	public void testGetUnitsIndex()
	{
		assertThat(m_Testable.getUnitsIndex()).isEqualTo(4);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#getValueIndex()}.
	 */
	@Test
	public void testGetValueIndex()
	{
		assertThat(m_Testable.getValueIndex()).isEqualTo(3);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#processLine(java.util.List, SortedSet, java.util.function.BiConsumer)}.
	 */
	@Test
	public void testProcessLine()
	{
		for (final Status status : Status.values())
		{
			final List<String> line = Lists.newArrayList(m_Line);
			final SortedSet<SbdDataType> dataTypes = Sets
					.newTreeSet(m_DataTypes);
			final Map<SbdDataType, Double> dataMap = Maps.newHashMap();

			switch (status)
			{
				case BAD_QUALITY:
				{
					line.set(m_Testable.getQualityIndex(), "B");
					assertThat(m_Testable.processLine(line, dataTypes,
							dataMap::put)).isEqualTo(status);
					assertThat(dataMap).hasSize(0);
				}
					break;
				case NO_MATCHING_DATATYPE:
				{
					line.set(m_Testable.getNameIndex(), "Junk");
					assertThat(m_Testable.processLine(line, dataTypes,
							dataMap::put)).isEqualTo(status);
					assertThat(dataMap).hasSize(0);
				}
					break;
				case SUCCESS:
				{
					assertThat(m_Testable.processLine(line, dataTypes,
							dataMap::put)).isEqualTo(status);
					assertThat(dataMap).hasSize(1);
					assertThat(dataMap.get(PROTECTED_STAGE_DATATYPE))
							.isEqualByComparingTo(0.24 * 0.3048);
				}
					break;
				case UNPARSEABLE_VALUE:
				{
					line.set(m_Testable.getValueIndex(), "-");
					assertThat(m_Testable.processLine(line, dataTypes,
							dataMap::put)).isEqualTo(status);
					assertThat(dataMap).hasSize(0);
				}
					break;
				case WRONG_FIELD_COUNT:
				{
					line.add("Extra");
					assertThat(m_Testable.processLine(line, dataTypes,
							dataMap::put)).isEqualTo(status);
					assertThat(dataMap).hasSize(0);
				}
					break;
				default:
					fail("Not tested!");
			}
		}

	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#setDateIndex(int)}.
	 */
	@Test
	public void testSetDateIndex()
	{
		final int expected = -1;
		m_Testable.setDateIndex(expected);
		assertThat(m_Testable.getDateIndex()).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#setFieldCount(int)}.
	 */
	@Test
	public void testSetFieldCount()
	{
		final int expected = -1;
		m_Testable.setFieldCount(expected);
		assertThat(m_Testable.getFieldCount()).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#setNameIndex(int)}.
	 */
	@Test
	public void testSetNameIndex()
	{
		final int expected = -1;
		m_Testable.setNameIndex(expected);
		assertThat(m_Testable.getNameIndex()).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#setQualityGoodValue(String)}.
	 */
	@Test
	public void testSetQualityGoodValue()
	{
		final String expected = "g";
		m_Testable.setQualityGoodValue(expected);
		assertThat(m_Testable.getQualityGoodValue()).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#setQualityIndex(int)}.
	 */
	@Test
	public void testSetQualityIndex()
	{
		final int expected = -1;
		m_Testable.setQualityIndex(expected);
		assertThat(m_Testable.getQualityIndex()).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#setTimeIndex(int)}.
	 */
	@Test
	public void testSetTimeIndex()
	{
		final int expected = -1;
		m_Testable.setTimeIndex(expected);
		assertThat(m_Testable.getTimeIndex()).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#setUnitsIndex(int)}.
	 */
	@Test
	public void testSetUnitsIndex()
	{
		final int expected = -1;
		m_Testable.setUnitsIndex(expected);
		assertThat(m_Testable.getUnitsIndex()).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder#setValueIndex(int)}.
	 */
	@Test
	public void testSetValueIndex()
	{
		final int expected = -1;
		m_Testable.setValueIndex(expected);
		assertThat(m_Testable.getValueIndex()).isEqualTo(expected);
	}

}
