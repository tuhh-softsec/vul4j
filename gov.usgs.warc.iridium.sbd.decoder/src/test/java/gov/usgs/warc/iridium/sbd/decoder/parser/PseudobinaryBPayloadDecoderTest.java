package gov.usgs.warc.iridium.sbd.decoder.parser;

import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.BATTERY_DATATYPE;
import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.FLOOD_STAGE_DATATYPE;
import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.HUMIDITY_DATATYPE;
import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.PRECIPITATION_DATATYPE;
import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.PRESSURE_DATATYPE;
import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.PROTECTED_STAGE_DATATYPE;
import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.TEMPERATURE_DATATYPE;
import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.WIND_DIRECTION_DATATYPE;
import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.WIND_SPEED_DATATYPE;
import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.getDecodeOrderSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper;
import gov.usgs.warc.iridium.sbd.decoder.Tests;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Payload;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.PayloadType;
import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test {@link PseudobinaryBPayloadDecoder}
 *
 * @author mckelvym
 * @since Mar 22, 2019
 *
 */
public class PseudobinaryBPayloadDecoderTest
{
	/**
	 * @throws java.lang.Exception
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> classToTest = PseudobinaryBPayloadDecoder.class;
		final Class<?> testingClass = PseudobinaryBPayloadDecoderTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass);
	}

	/**
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private SortedSet<SbdDataType>		m_DataTypes;

	/**
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private SortedSet<SbdDecodeOrder>	m_DecoderOrder;

	/**
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private Payload						m_Payload;

	/**
	 * Object under test.
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private PayloadDecoder				m_Testable;

	/**
	 * @throws java.lang.Exception
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	@Before
	public void setUp() throws Exception
	{
		m_DataTypes = Sets.newTreeSet(ParsingTestsHelper.getDataTypeSet());
		m_DecoderOrder = getDecodeOrderSet();
		m_Testable = new PseudobinaryBPayloadDecoder();
		m_Payload = Payload.builder(PayloadType.PSEUDOBINARY_B_DATA_FORMAT)
				.id((byte) 0x02)
				.payload("??T??\\@AB@@@@@i@@@B`e@@\\N".getBytes()).build();
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.PseudobinaryBPayloadDecoder#decode(Payload, SortedSet, SortedSet)}
	 */
	@Test
	public void testDecode()
	{
		final Map<SbdDataType, Double> dataMap = m_Testable.decode(m_Payload,
				m_DataTypes, m_DecoderOrder);

		final Map<SbdDataType, Double> expectedValues = Maps.newHashMap();
		expectedValues.put(BATTERY_DATATYPE, 13.876);
		expectedValues.put(FLOOD_STAGE_DATATYPE, -0.44);
		expectedValues.put(HUMIDITY_DATATYPE, 0.);
		expectedValues.put(PRECIPITATION_DATATYPE, 0.28);
		expectedValues.put(PRESSURE_DATATYPE, 1027.7);
		expectedValues.put(PROTECTED_STAGE_DATATYPE, -0.36);
		expectedValues.put(TEMPERATURE_DATATYPE, 39.38);
		expectedValues.put(WIND_DIRECTION_DATATYPE, 0.);
		expectedValues.put(WIND_SPEED_DATATYPE, 6.6);

		for (final Entry<SbdDataType, Double> entry : expectedValues.entrySet())
		{
			final SbdDataType dataType = entry.getKey();
			final Double expected = entry.getValue();

			assertThat(dataMap.get(dataType)).isEqualTo(expected);
		}
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.PseudobinaryBPayloadDecoder#decode(Payload, SortedSet, SortedSet)}.
	 */
	@Test
	public void testDecodeBadType()
	{
		assertThatThrownBy(
				() -> m_Testable.decode(
						Payload.builder(PayloadType.SUTRON_STANDARD_CSV)
								.id(m_Payload.getId())
								.payload(m_Payload.getPayload()).build(),
						m_DataTypes, m_DecoderOrder))
								.isInstanceOf(IllegalArgumentException.class);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.PseudobinaryBPayloadDecoder#decode(Payload, SortedSet, SortedSet)}.
	 */
	@Test
	public void testDecodeShortPayload()
	{
		final Payload payload = Payload
				.builder(PayloadType.PSEUDOBINARY_B_DATA_FORMAT).id((byte) 0x02)
				.payload("?xd?zG@IC///J".getBytes()).build();
		final Map<SbdDataType, Double> dataMap = m_Testable.decode(payload,
				m_DataTypes, m_DecoderOrder);

		final Map<SbdDataType, Double> expectedValues = Maps.newHashMap();
		expectedValues.put(BATTERY_DATATYPE, 12.94);
		expectedValues.put(FLOOD_STAGE_DATATYPE,
				FLOOD_STAGE_DATATYPE.transformValue(-476));
		expectedValues.put(PROTECTED_STAGE_DATATYPE,
				PROTECTED_STAGE_DATATYPE.transformValue(-377));
		expectedValues.put(WIND_SPEED_DATATYPE,
				WIND_SPEED_DATATYPE.transformValue(579));
		expectedValues.put(WIND_DIRECTION_DATATYPE,
				WIND_DIRECTION_DATATYPE.transformValue(Double.NaN));

		for (final Entry<SbdDataType, Double> entry : expectedValues.entrySet())
		{
			final SbdDataType dataType = entry.getKey();
			final Double expected = entry.getValue();

			assertThat(dataMap.get(dataType)).isEqualTo(expected);
		}
		assertThat(expectedValues.size()).isEqualTo(dataMap.size());
	}

}
