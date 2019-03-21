package gov.usgs.warc.iridium.sbd.decoder.parser;

import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.hexStringToByteArray;
import static gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper.setupMessageBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.UnsignedInts;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

import gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper;
import gov.usgs.warc.iridium.sbd.decoder.Tests;
import gov.usgs.warc.iridium.sbd.decoder.Tests.SkipMethod;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrderProvider;
import gov.usgs.warc.iridium.sbd.domain.SbdStationIdProvider;
import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrder;
import gov.usgs.warc.iridium.sbd.domain.SbdStationId;
import gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.LocationInformation;

/**
 * Test the binary parser
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
	 * Get the {@link SbdDataType} from the parsed map by it's attribute
	 * name (e.x. wind speed)
	 *
	 * @param p_Stream
	 *            the stream of {@link SbdDataType}
	 * @param p_AttributeToSearch
	 *            the attribute to search for
	 * @return the {@link SbdDataType} with the given name.
	 * @since Feb 9, 2018
	 */
	private static SbdDataType getDatatype(
			final Stream<SbdDataType> p_Stream,
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
	 * @author darceyj
	 * @since Jan 10, 2018
	 */
	@MockBean
	private SbdDecodeOrderProvider<SbdDecodeOrder>	m_DecodeOrderRepo;

	/**
	 * Rule for asserting that the proper exception is thrown
	 *
	 * @since Feb 2, 2018
	 */
	@Rule
	public ExpectedException								m_ExpectedException	= ExpectedException
			.none();

	/**
	 * The {@link SbdStationIdProvider}
	 *
	 * @since Feb 12, 2018
	 */
	@MockBean
	private SbdStationIdProvider<SbdStationId>		m_IridiumStationRepo;

	/**
	 * The station ID to test with
	 *
	 * @since May 9, 2018
	 */
	private Long											m_StationIdTest;

	/**
	 * The list of bytes to parse
	 *
	 * @since Jan 24, 2018
	 */
	private List<Byte>										m_TestingByteList;

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

		when(m_IridiumStationRepo.findByImei(imei))
				.thenReturn(Lists.newArrayList(iridiumStationId));
		when(m_DecodeOrderRepo.findByStationId(m_StationIdTest)).thenReturn(
				Sets.newTreeSet(ParsingTestsHelper.getDecodeList()));

	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#getValuesFromMessage()}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testBadPayload() throws Exception
	{
		for (final List<Byte> bytes : ParsingTestsHelper
				.getTestingDataBadPayload())
		{
			assertThatThrownBy(() -> new SbdParser(bytes))
					.hasSameClassAs(new IllegalArgumentException());
		}
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
		assertThat(SbdParser.getIMEIFromBytes(byteArray))
				.isEqualTo(expected);
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

		final Optional<SbdStationId> opt = m_IridiumStationRepo
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()))
				.stream().findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeOrder(m_DecodeOrderRepo.findByStationId(stationId));
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
		final Optional<SbdStationId> opt = m_IridiumStationRepo
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()))
				.stream().findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeOrder(m_DecodeOrderRepo.findByStationId(stationId));
		final Map<SbdDataType, Double> dataMap = parser
				.getValuesFromMessage();
		final LocationInformation locationInformation = parser.getMessage()
				.getLocationInformation();
		final LocationInformation expected = LocationInformation.builder()
				.cepRadius(2000L).id((byte) 0x03).length((short) 11)
				.latitude(125.0).longitude(65.59999).build();
		assertThat(locationInformation).isEqualTo(expected);
		final Set<SbdDataType> keySet = dataMap.keySet();
		assertThat(dataMap.get(getDatatype(keySet.stream(), "wind speed")))
				.isEqualTo(6.6);
		assertThat(dataMap.get(getDatatype(keySet.stream(), "wind direction")))
				.isEqualTo(0);
		assertThat(dataMap.get(getDatatype(keySet.stream(), "flood side")))
				.isEqualTo(-0.44);
		assertThat(dataMap.get(getDatatype(keySet.stream(), "protected side")))
				.isEqualTo(-0.36);
		assertThat(
				dataMap.get(getDatatype(keySet.stream(), "relative humidity")))
						.isEqualTo(0);
		assertThat(dataMap
				.get(getDatatype(keySet.stream(), "barometric pressure")))
						.isEqualTo(1027.7);
		assertThat(dataMap.get(getDatatype(keySet.stream(), "precipitation")))
				.isEqualTo(0.28);
		assertThat(dataMap.get(getDatatype(keySet.stream(), "battery")))
				.isEqualTo(13.876);
		assertThat(dataMap.get(getDatatype(keySet.stream(), "air temperature")))
				.isEqualTo(39.38);
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
				parser.setDecodeOrder(
						Sets.newTreeSet(ParsingTestsHelper.getDecodeList()));
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
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser#BinaryParser(List)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testParse2() throws Exception
	{
		m_TestingByteList = setupMessageBytes("0D");
		m_ExpectedException.expect(Exception.class);
		final SbdParser parser = new SbdParser(m_TestingByteList);
		final Optional<SbdStationId> opt = m_IridiumStationRepo
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()))
				.stream().findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeOrder(m_DecodeOrderRepo.findByStationId(stationId));
		parser.getValuesFromMessage();
	}

	/**
	 * Test method for {@link SbdParser#setDecodeOrder(java.util.SortedSet)}
	 *
	 * @throws Exception
	 * @since Feb 12, 2018
	 */
	@Test
	public void testSetDecodeOrder() throws Exception
	{
		m_TestingByteList = setupMessageBytes("00");
		final SbdParser parser = new SbdParser(m_TestingByteList);
		final Optional<SbdStationId> opt = m_IridiumStationRepo
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()))
				.stream().findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeOrder(m_DecodeOrderRepo.findByStationId(stationId));
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
