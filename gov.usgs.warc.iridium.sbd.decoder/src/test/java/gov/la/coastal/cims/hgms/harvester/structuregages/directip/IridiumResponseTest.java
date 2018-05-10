package gov.la.coastal.cims.hgms.harvester.structuregages.directip;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser;
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.Message;
import gov.usgs.warc.iridium.sbd.decoder.db.IridiumDecodeOrderProvider;
import gov.usgs.warc.iridium.sbd.decoder.db.IridiumStationIdProvider;
import gov.usgs.warc.iridium.sbd.decoder.db.entity.IridiumDataType;
import gov.usgs.warc.iridium.sbd.decoder.db.entity.IridiumDecodeOrder;
import gov.usgs.warc.iridium.sbd.decoder.db.entity.IridiumStationId;

/**
 * The {@link IridiumResponse} test
 *
 * @author darceyj
 * @since Feb 12, 2018
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class IridiumResponseTest
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
		 *            {@link IridiumStationIdProvider}
		 * @param p_IridiumDecodeOrderRepository
		 *            {@link IridiumDecodeOrderProvider}
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
	 * Assert that the testing class has all the required methods.
	 *
	 * @throws java.lang.Exception
	 * @since Feb 12, 2018
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> classToTest = IridiumResponse.class;
		final Class<?> testingClass = IridiumResponseTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.BUILDER, SkipMethod.CAN_EQUAL,
				SkipMethod.EQUALS_AND_HASHCODE, SkipMethod.TO_STRING);

	}

	/**
	 * The decode order repository bean
	 *
	 * @since Feb 12, 2018
	 */
	@MockBean
	private IridiumDecodeOrderProvider<IridiumDecodeOrder>	m_DecodeOrderRepo;

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
	 * The {@link IridiumResponse} to test with
	 *
	 * @since Feb 12, 2018
	 */
	private IridiumResponse									m_Testable;

	/**
	 *
	 *
	 * @throws java.lang.Exception
	 * @since Feb 12, 2018
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
		final BinaryParser parser = new BinaryParser(
				ParsingTestsHelper.setupMessageBytes("00"));
		final Collection<IridiumStationId> stationsList = m_IridiumStationRepo
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()));
		final Optional<IridiumStationId> opt = stationsList.stream()
				.findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeOrder(m_DecodeOrderRepo.findByStationId(stationId));
		final Map<IridiumDataType, Double> dataMap = parser
				.getValuesFromMessage();
		final Table<IridiumStationId, IridiumDataType, Double> valueTable = HashBasedTable
				.create();
		dataMap.forEach((datatype, value) -> valueTable.put(iridiumStationId,
				datatype, value));
		m_Testable = IridiumResponse.builder().message(parser.getMessage())
				.stations(stationsList).values(valueTable).build();
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.directip.IridiumResponse#getMessage()}.
	 */
	@Test
	public void testGetMessage()
	{
		assertThat(m_Testable.getMessage()).isNotNull();
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.directip.IridiumResponse#getStations()}.
	 */
	@Test
	public void testGetStations()
	{
		assertThat(m_Testable.getStations()).isNotNull();
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.directip.IridiumResponse#getValues()}.
	 */
	@Test
	public void testGetValues()
	{
		assertThat(m_Testable.getValues()).isNotNull();

	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.directip.IridiumResponse#setMessage(Message)}.
	 */
	@Test
	public void testSetMessage()
	{
		assertThat(m_Testable.getMessage()).isNotNull();
		m_Testable.setMessage(null);
		assertThat(m_Testable.getMessage()).isNull();
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.directip.IridiumResponse#setStations(Collection)}.
	 */
	@Test
	public void testSetStations()
	{
		assertThat(m_Testable.getStations()).isNotNull();
		m_Testable.setStations(null);
		assertThat(m_Testable.getStations()).isNull();
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.directip.IridiumResponse#setValues(Table)}.
	 */
	@Test
	public void testSetValues()
	{
		assertThat(m_Testable.getValues()).isNotNull();
		m_Testable.setValues(null);
		assertThat(m_Testable.getValues()).isNull();
	}

}
