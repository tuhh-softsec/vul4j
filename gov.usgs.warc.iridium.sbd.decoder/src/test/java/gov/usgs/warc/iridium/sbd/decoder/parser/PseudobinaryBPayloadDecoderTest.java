package gov.usgs.warc.iridium.sbd.decoder.parser;

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
import java.util.Arrays;
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
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.PseudobinaryBPayloadDecoder#decode(Payload, SortedSet, SortedSet)}.
	 */
	@Test
	public void testDecodeBadPayload()
	{
		assertThatThrownBy(() -> m_Testable.decode(
				Payload.builder(m_Payload.getPayloadType())
						.id(m_Payload.getId())
						.payload(Arrays.copyOf(m_Payload.getPayload(),
								m_Payload.getPayload().length - 1))
						.build(),
				m_DataTypes, m_DecoderOrder))
						.isInstanceOf(IndexOutOfBoundsException.class);
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

}
