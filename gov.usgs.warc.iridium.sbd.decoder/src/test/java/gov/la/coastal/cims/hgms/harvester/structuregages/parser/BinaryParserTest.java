package gov.la.coastal.cims.hgms.harvester.structuregages.parser;

import static gov.la.coastal.cims.hgms.harvester.structuregages.ParsingTestsHelper.hexStringToByteArray;
import static gov.la.coastal.cims.hgms.harvester.structuregages.ParsingTestsHelper.setupMessageBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.UnsignedInts;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import gov.la.coastal.cims.hgms.harvester.structuregages.ParsingTestsHelper;
import gov.la.coastal.cims.hgms.harvester.structuregages.Tests;
import gov.la.coastal.cims.hgms.harvester.structuregages.Tests.SkipMethod;
import gov.la.coastal.cims.hgms.harvester.structuregages.directip.SbdProcessor;
import gov.la.coastal.cims.hgms.harvester.structuregages.directip.SbdProcessorImpl;
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.elements.LocationInformation;
import gov.usgs.warc.iridium.sbd.decoder.db.IridiumDecodeOrderProvider;
import gov.usgs.warc.iridium.sbd.decoder.db.IridiumStationIdProvider;
import gov.usgs.warc.iridium.sbd.decoder.db.entity.IridiumDataType;
import gov.usgs.warc.iridium.sbd.decoder.db.entity.IridiumDecodeOrder;
import gov.usgs.warc.iridium.sbd.decoder.db.entity.IridiumStationId;

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
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BinaryParserTest
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
		 * @param p_Context
		 *            {@link ApplicationContext}
		 * @param p_IridiumStationIdRepository
		 *            {@link IridiumStationIdRepository}
		 * @param p_IridiumDecodeOrderRepository
		 *            {@link IridiumDecodeOrderRepository}
		 * @return {@link SbdProcessorImpl} instance
		 * @author darceyj
		 * @since Nov 8, 2017
		 */
		@Bean
		@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
		public SbdProcessor processor(final ApplicationContext p_Context,
				final IridiumStationIdProvider<IridiumStationId> p_IridiumStationIdRepository,
				final IridiumDecodeOrderProvider<IridiumDecodeOrder> p_IridiumDecodeOrderRepository)
		{
			return new SbdProcessorImpl(p_Context, p_IridiumStationIdRepository,
					p_IridiumDecodeOrderRepository);
		}
	}

	/**
	 * Get the {@link IridiumDataType} from the parsed map by it's attribute
	 * name (e.x. wind speed)
	 *
	 * @param p_Stream
	 *            - the stream of {@link IridiumDataType}
	 * @param p_AttributeToSearch
	 *            - the attribute to search for
	 * @return - the {@link IridiumDataType} with the given name.
	 * @since Feb 9, 2018
	 */
	private static IridiumDataType getDatatype(
			final Stream<IridiumDataType> p_Stream,
			final String p_AttributeToSearch)
	{
		final Optional<IridiumDataType> opt = p_Stream.filter(
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
		final Class<?> classToTest = BinaryParser.class;
		final Class<?> testingClass = BinaryParserTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.TO_STRING, SkipMethod.CAN_EQUAL);
	}

	/**
	 * @author darceyj
	 * @since Jan 10, 2018
	 */
	@MockBean
	private IridiumDecodeOrderProvider<IridiumDecodeOrder>	m_DecodeOrderRepo;

	/**
	 * Rule for asserting that the proper exception is thrown
	 *
	 * @since Feb 2, 2018
	 */
	@Rule
	public ExpectedException								m_ExpectedException	= ExpectedException
			.none();

	/**
	 * The {@link IridiumStationIdProvider}
	 *
	 * @since Feb 12, 2018
	 */
	@MockBean
	private IridiumStationIdProvider<IridiumStationId>		m_IridiumStationRepo;

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
		final IridiumStationId iridiumStationId = new IridiumStationId()
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
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser#getAsUnsignedNumber(byte[])}.
	 */
	@Test
	public void testGetAsUnsignedNumber()
	{
		final String hexString = "ea5f";
		final byte[] byteArray = hexStringToByteArray(hexString);
		final int test = (int) BinaryParser.getAsUnsignedNumber(byteArray);
		final int expected = 59999;
		assertThat(UnsignedInts.parseUnsignedInt(hexString, 16))
				.isEqualTo(expected);
		assertThat(test).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser#getAsUnsignedNumber(byte[])}.
	 */
	@Test
	public void testGetAsUnsignedNumber2()
	{
		final String hexString = "43B539E1";
		final byte[] byteArray = hexStringToByteArray(hexString);
		final int test = (int) BinaryParser.getAsUnsignedNumber(byteArray);
		final int expected = 1135950305;
		assertThat(UnsignedInts.parseUnsignedInt(hexString, 16))
				.isEqualTo(expected);
		assertThat(test).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser#getIMEIFromBytes(byte[])}.
	 */
	@Test
	public void testGetIMEIFromBytes()
	{

		final Long expected = 300234010293520L;
		final String str = Long.toString(expected);
		final String finalStr = Strings.padStart(str, 15, '0');
		final byte[] byteArray = finalStr.getBytes();
		assertThat(BinaryParser.getIMEIFromBytes(byteArray))
				.isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser#getMessage()}.
	 *
	 * @throws Exception
	 *             if an error occurred during parsing.
	 */
	@Test
	public void testGetMessage() throws Exception
	{
		m_TestingByteList = setupMessageBytes("00");
		final BinaryParser parser = new BinaryParser(m_TestingByteList);
		assertThat(parser.getMessage()).isNotNull();

	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser#getMessage()}.
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
		final BinaryParser parser = new BinaryParser(m_TestingByteList);
		assertThat(parser.getMessage()).isNotNull();
		assertThat(parser.getMessage().getPayLoad()).isNotNull();

		final Optional<IridiumStationId> opt = m_IridiumStationRepo
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
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser#getValuesFromMessage()}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetValuesFromMessage() throws Exception
	{
		m_TestingByteList = setupMessageBytes("00");
		final BinaryParser parser = new BinaryParser(m_TestingByteList);
		final Optional<IridiumStationId> opt = m_IridiumStationRepo
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()))
				.stream().findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeOrder(m_DecodeOrderRepo.findByStationId(stationId));
		final Map<IridiumDataType, Double> dataMap = parser
				.getValuesFromMessage();
		final LocationInformation locationInformation = parser.getMessage()
				.getLocationInformation();
		final LocationInformation expected = LocationInformation.builder()
				.cepRadius(2000L).id((byte) 0x03).length((short) 11)
				.latitude(125.0).longitude(65.59999).build();
		assertThat(locationInformation).isEqualTo(expected);
		final Set<IridiumDataType> keySet = dataMap.keySet();
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
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser#BinaryParser(List)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testParse2() throws Exception
	{
		m_TestingByteList = setupMessageBytes("0D");
		m_ExpectedException.expect(Exception.class);
		final BinaryParser parser = new BinaryParser(m_TestingByteList);
		final Optional<IridiumStationId> opt = m_IridiumStationRepo
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()))
				.stream().findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeOrder(m_DecodeOrderRepo.findByStationId(stationId));
		parser.getValuesFromMessage();
	}

	/**
	 * Test method for {@link BinaryParser#setDecodeOrder(java.util.SortedSet)}
	 *
	 * @throws Exception
	 * @since Feb 12, 2018
	 */
	@Test
	public void testSetDecodeOrder() throws Exception
	{
		m_TestingByteList = setupMessageBytes("00");
		final BinaryParser parser = new BinaryParser(m_TestingByteList);
		final Optional<IridiumStationId> opt = m_IridiumStationRepo
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()))
				.stream().findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeOrder(m_DecodeOrderRepo.findByStationId(stationId));
		final Map<IridiumDataType, Double> valuesFromMessage = parser
				.getValuesFromMessage();
		assertThat(valuesFromMessage).isNotNull();
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser#getAsUnsignedNumber(byte[])}.
	 */
	@Test
	public void testUnsignedShort()
	{
		final String hexString = "0068";
		final byte[] byteArray = hexStringToByteArray(hexString);
		final short expected = 104;
		assertThat((short) BinaryParser.getAsUnsignedNumber(byteArray))
				.isEqualTo(expected);
	}

}
