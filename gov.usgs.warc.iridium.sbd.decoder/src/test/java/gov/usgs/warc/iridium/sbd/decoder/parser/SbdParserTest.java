package gov.usgs.warc.iridium.sbd.decoder.parser;

import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.hexStringToByteArray;
import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.setupMessageBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedInts;
import gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper;
import gov.usgs.warc.iridium.sbd.decoder.Tests;
import gov.usgs.warc.iridium.sbd.decoder.Tests.SkipMethod;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.LocationInformation;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Payload;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.PayloadType;
import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import gov.usgs.warc.iridium.sbd.domain.SbdDataTypeProvider;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrder;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrderProvider;
import gov.usgs.warc.iridium.sbd.domain.SbdStationId;
import gov.usgs.warc.iridium.sbd.domain.SbdStationIdProvider;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test {@link SbdParser}
 *
 * @author darceyj
 * @since Jan 10, 2018
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
public class SbdParserTest
{

	/**
	 * @author darceyj
	 * @since Feb 12, 2018
	 *
	 */
	@Configuration
	static class ContextConfiguration
	{
		/**
		 * Nothing for now.
		 */
	}

	/**
	 * Get the {@link SbdDataType} from the parsed map by it's attribute name
	 * (e.x. wind speed)
	 *
	 * @param p_Stream
	 *            the stream of {@link SbdDataType}
	 * @param p_AttributeToSearch
	 *            the attribute to search for
	 * @return the {@link SbdDataType} with the given name.
	 * @since Feb 9, 2018
	 */
	public static SbdDataType getDatatype(final Stream<SbdDataType> p_Stream,
			final String p_AttributeToSearch)
	{
		final Optional<SbdDataType> opt = p_Stream.filter(
				dt -> dt.getName().equalsIgnoreCase(p_AttributeToSearch))
				.findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());

		return opt.get();
	}

	/**
	 * Make sure that the public methods are tested
	 *
	 * @throws java.lang.Exception
	 * @since Jan 10, 2018
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> classToTest = SbdParser.class;
		final Class<?> testingClass = SbdParserTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.TO_STRING, SkipMethod.CAN_EQUAL);
	}

	/**
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	@MockBean
	private SbdDataTypeProvider<SbdDataType>		m_DataTypeRepository;

	/**
	 * @author darceyj
	 * @since Jan 10, 2018
	 */
	@MockBean
	private SbdDecodeOrderProvider<SbdDecodeOrder>	m_DecodeOrderRepository;

	/**
	 * Rule for asserting that the proper exception is thrown
	 *
	 * @since Feb 2, 2018
	 */
	@Rule
	public ExpectedException						m_ExpectedException	= ExpectedException
			.none();

	/**
	 * The {@link SbdStationIdProvider}
	 *
	 * @since Feb 12, 2018
	 */
	@MockBean
	private SbdStationIdProvider<SbdStationId>		m_IridiumStationRepository;

	/**
	 * The station ID to test with
	 *
	 * @since May 9, 2018
	 */
	private Long									m_StationIdTest;

	/**
	 * The list of bytes to parse
	 *
	 * @since Jan 24, 2018
	 */
	private List<Byte>								m_TestingByteList;

	/**
	 * Setup a successful directip MO message to test
	 *
	 * @throws java.lang.Exception
	 * @since Jan 10, 2018
	 */
	@Before
	public void setUp() throws Exception
	{
		m_StationIdTest = 1L;
		final String imei = "300234010124740";
		final SbdStationId iridiumStationId = new SbdStationId()
		{

			@Override
			public Long getId()
			{
				return 221L;
			}

			@Override
			public String getImei()
			{
				return imei;
			}

			@Override
			public Long getStationId()
			{
				return m_StationIdTest;
			}
		};

		when(m_IridiumStationRepository.findByImei(imei))
				.thenReturn(Lists.newArrayList(iridiumStationId));
		when(m_DataTypeRepository.findByStationId(m_StationIdTest))
				.thenReturn(ParsingTestsHelper.getDataTypeSet());
		when(m_DecodeOrderRepository.findByStationId(m_StationIdTest))
				.thenReturn(ParsingTestsHelper.getDecodeOrderSet());

	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#getAsUnsignedNumber(byte[])}.
	 */
	@Test
	public void testGetAsUnsignedNumber()
	{
		final String hexString = "ea5f";
		final byte[] byteArray = hexStringToByteArray(hexString);
		final int test = (int) SbdParser.getAsUnsignedNumber(byteArray);
		final int expected = 59999;
		assertThat(UnsignedInts.parseUnsignedInt(hexString, 16))
				.isEqualTo(expected);
		assertThat(test).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#getAsUnsignedNumber(byte[])}.
	 */
	@Test
	public void testGetAsUnsignedNumber2()
	{
		final String hexString = "43B539E1";
		final byte[] byteArray = hexStringToByteArray(hexString);
		final int test = (int) SbdParser.getAsUnsignedNumber(byteArray);
		final int expected = 1135950305;
		assertThat(UnsignedInts.parseUnsignedInt(hexString, 16))
				.isEqualTo(expected);
		assertThat(test).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#getIMEIFromBytes(byte[])}.
	 */
	@Test
	public void testGetIMEIFromBytes()
	{

		final Long expected = 300234010293520L;
		final String str = Long.toString(expected);
		final String finalStr = Strings.padStart(str, 15, '0');
		final byte[] byteArray = finalStr.getBytes();
		assertThat(SbdParser.getIMEIFromBytes(byteArray)).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#getMessage()}.
	 *
	 * @throws Exception
	 *             if an error occurred during parsing.
	 */
	@Test
	public void testGetMessage() throws Exception
	{
		m_TestingByteList = setupMessageBytes("00");
		final SbdParser parser = new SbdParser(m_TestingByteList);
		assertThat(parser.getMessage()).isNotNull();

	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#getMessage()}.
	 *
	 * Test with a 'bad quality' status
	 *
	 * @throws Exception
	 *             if an error occurred during parsing.
	 */
	@Test
	public void testGetMessage2() throws Exception
	{
		m_TestingByteList = setupMessageBytes("02");
		final SbdParser parser = new SbdParser(m_TestingByteList);
		assertThat(parser.getMessage()).isNotNull();
		assertThat(parser.getMessage().getPayLoad()).isNotNull();

		final Optional<SbdStationId> opt = m_IridiumStationRepository
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()))
				.stream().findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeConfiguration(
				m_DataTypeRepository.findByStationId(stationId),
				m_DecodeOrderRepository.findByStationId(stationId));
		assertThat(parser.getValuesFromMessage()).isNotEmpty();

	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#getValuesFromMessage()}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetValuesFromMessage() throws Exception
	{
		m_TestingByteList = setupMessageBytes("00");
		final SbdParser parser = new SbdParser(m_TestingByteList);
		final Optional<SbdStationId> opt = m_IridiumStationRepository
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()))
				.stream().findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeConfiguration(
				m_DataTypeRepository.findByStationId(stationId),
				m_DecodeOrderRepository.findByStationId(stationId));
		final Map<SbdDataType, Double> dataMap = parser.getValuesFromMessage();
		final LocationInformation locationInformation = parser.getMessage()
				.getLocationInformation();
		final LocationInformation expectedLocationInformation = LocationInformation
				.builder().cepRadius(2000L).id((byte) 0x03).length((short) 11)
				.latitude(125.0).longitude(65.59999).build();
		assertThat(locationInformation).isEqualTo(expectedLocationInformation);

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
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#getValuesFromMessage()}.
	 */
	@Test
	public void testGetValuesFromMessage2()
	{
		for (final List<Byte> bytes : ParsingTestsHelper.getTestingData())
		{
			try
			{
				final SbdParser parser = new SbdParser(bytes);
				parser.setDecodeConfiguration(
						ParsingTestsHelper.getDataTypeSet(),
						ParsingTestsHelper.getDecodeOrderSet());
				parser.getValuesFromMessage();
			}
			catch (final Exception e)
			{
				e.printStackTrace();
				final String message = String.format("Failed on %s: %s",
						bytes.stream().map(String::valueOf)
								.collect(Collectors.joining(", ")),
						Arrays.toString(e.getStackTrace()));
				fail(message);
			}
		}
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#SbdParser(List)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testParse2() throws Exception
	{
		m_TestingByteList = setupMessageBytes("0D");
		m_ExpectedException.expect(Exception.class);
		final SbdParser parser = new SbdParser(m_TestingByteList);
		final Optional<SbdStationId> opt = m_IridiumStationRepository
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()))
				.stream().findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeConfiguration(
				m_DataTypeRepository.findByStationId(stationId),
				m_DecodeOrderRepository.findByStationId(stationId));
		parser.getValuesFromMessage();
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#processPayload(java.nio.ByteBuffer)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testProcessPayload() throws Exception
	{
		testProcessPayloadPsuedobinaryB();
		testProcessPayloadPsuedobinaryC();
		testProcessPayloadPsuedobinaryD();
		testProcessPayloadSutronStandardCsv();
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#processPayload(java.nio.ByteBuffer)}.
	 *
	 * Original implementation for pseudobinary b format
	 *
	 * @throws Exception
	 */
	@Test
	public void testProcessPayloadPsuedobinaryB() throws Exception
	{
		/**
		 * Log using 'nc' to a text file and then use 'hexdump -c' to get
		 * characters for this string.
		 */
		final String payLoadBytes = "0B1B@AC@AC@@F@D^@AW@@AB_n@@@J";
		final ByteBuffer byteBuffer = ByteBuffer.wrap(payLoadBytes.getBytes());

		final Payload payload = SbdParser.processPayload(byteBuffer);
		assertThat(payload.getPayloadType())
				.isEqualTo(PayloadType.PSEUDOBINARY_B_DATA_FORMAT);
		assertThat(payload.getPayload().length)
				.isEqualTo(payLoadBytes.getBytes().length - 4);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#processPayload(java.nio.ByteBuffer)}.
	 *
	 *
	 * @throws Exception
	 */
	@Test
	public void testProcessPayloadPsuedobinaryC() throws Exception
	{
		final String payLoadBytes = "0C1B@AC@AC@@F@D^@AW@@AB_n@@@J";
		final ByteBuffer byteBuffer = ByteBuffer.wrap(payLoadBytes.getBytes());

		final Payload payload = SbdParser.processPayload(byteBuffer);
		assertThat(payload.getPayloadType())
				.isEqualTo(PayloadType.PSEUDOBINARY_C_DATA_FORMAT);
		assertThat(payload.getPayload().length)
				.isEqualTo(payLoadBytes.getBytes().length);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#processPayload(java.nio.ByteBuffer)}.
	 *
	 *
	 * @throws Exception
	 */
	@Test
	public void testProcessPayloadPsuedobinaryD() throws Exception
	{
		final String payLoadBytes = "0D1B@AC@AC@@F@D^@AW@@AB_n@@@J";
		final ByteBuffer byteBuffer = ByteBuffer.wrap(payLoadBytes.getBytes());

		final Payload payload = SbdParser.processPayload(byteBuffer);
		assertThat(payload.getPayloadType())
				.isEqualTo(PayloadType.PSEUDOBINARY_D_DATA_FORMAT);
		assertThat(payload.getPayload().length)
				.isEqualTo(payLoadBytes.getBytes().length);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#processPayload(java.nio.ByteBuffer)}.
	 *
	 *
	 * @throws Exception
	 */
	@Test
	public void testProcessPayloadSutronStandardCsv() throws Exception
	{
		final String payLoadBytes = "003/21/2019,15:00:00,Prtctd,0.48,ft,G";
		final ByteBuffer byteBuffer = ByteBuffer.wrap(payLoadBytes.getBytes());

		final Payload payload = SbdParser.processPayload(byteBuffer);
		assertThat(payload.getPayloadType())
				.isEqualTo(PayloadType.SUTRON_STANDARD_CSV);
		assertThat(payload.getPayload().length)
				.isEqualTo(payLoadBytes.getBytes().length - 1);
	}

	/**
	 * Test method for
	 * {@link SbdParser#setDecodeConfiguration(java.util.SortedSet, java.util.SortedSet)}
	 *
	 * @throws Exception
	 * @since Feb 12, 2018
	 */
	@Test
	public void testSetDecodeConfiguration() throws Exception
	{
		m_TestingByteList = setupMessageBytes("00");
		final SbdParser parser = new SbdParser(m_TestingByteList);
		final Optional<SbdStationId> opt = m_IridiumStationRepository
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()))
				.stream().findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeConfiguration(
				m_DataTypeRepository.findByStationId(stationId),
				m_DecodeOrderRepository.findByStationId(stationId));
		final Map<SbdDataType, Double> valuesFromMessage = parser
				.getValuesFromMessage();
		assertThat(valuesFromMessage).isNotNull();
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#getAsUnsignedNumber(byte[])}.
	 */
	@Test
	public void testUnsignedShort()
	{
		final String hexString = "0068";
		final byte[] byteArray = hexStringToByteArray(hexString);
		final short expected = 104;
		assertThat((short) SbdParser.getAsUnsignedNumber(byteArray))
				.isEqualTo(expected);
	}

}
