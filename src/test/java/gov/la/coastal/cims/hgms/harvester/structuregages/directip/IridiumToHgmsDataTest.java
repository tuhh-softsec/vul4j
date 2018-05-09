package gov.la.coastal.cims.hgms.harvester.structuregages.directip;

import static gov.la.coastal.cims.hgms.harvester.structuregages.ParsingTestsHelper.createStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

import gov.la.coastal.cims.hgms.common.Common;
import gov.la.coastal.cims.hgms.common.HarvestToHgmsData;
import gov.la.coastal.cims.hgms.common.db.IridiumDataTypeRepository;
import gov.la.coastal.cims.hgms.common.db.IridiumDecodeOrderRepository;
import gov.la.coastal.cims.hgms.common.db.IridiumStationIdRepository;
import gov.la.coastal.cims.hgms.common.db.entity.IridiumDataType;
import gov.la.coastal.cims.hgms.common.db.entity.IridiumStationId;
import gov.la.coastal.cims.hgms.common.db.entity.Station;
import gov.la.coastal.cims.hgms.harvester.structuregages.Application;
import gov.la.coastal.cims.hgms.harvester.structuregages.ParsingTestsHelper;
import gov.la.coastal.cims.hgms.harvester.structuregages.Properties;
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser;
import gov.usgs.warc.mail.SMTP;
import gov.usgs.warc.test.Tests;
import gov.usgs.warc.test.Tests.SkipMethod;

/**
 * Test the {@link IridiumToHgmsData} class
 *
 * @author darceyj
 * @since Feb 22, 2018
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(
		classes = { Application.class, Common.class, Properties.class })
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class IridiumToHgmsDataTest
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
		 * @author mckelvym
		 * @since Mar 1, 2018
		 */
		@MockBean
		private SMTP smtp;

		/**
		 *
		 * @param p_Properties
		 * @return the {@link IridiumToHgmsData} instance
		 * @since Feb 23, 2018
		 */
		@Bean
		@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
		public IridiumToHgmsData iridiumToHgmsData(
				final Properties p_Properties)
		{
			return new IridiumToHgmsData(p_Properties);
		}

		/**
		 * @param p_Context
		 *            {@link ApplicationContext}
		 * @param p_IridiumStationIdRepository
		 *            {@link IridiumStationIdRepository}
		 * @param p_IridiumDecodeOrderRepository
		 *            {@link IridiumDecodeOrderRepository}
		 * @return {@link SbdProcessorImpl} instance
		 * @author darceyj
		 * @since Feb 23, 2017
		 */
		@Bean
		@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
		public SbdProcessor processor(final ApplicationContext p_Context,
				final IridiumStationIdRepository p_IridiumStationIdRepository,
				final IridiumDecodeOrderRepository p_IridiumDecodeOrderRepository)
		{
			return new SbdProcessorImpl(p_Context, p_IridiumStationIdRepository,
					p_IridiumDecodeOrderRepository);
		}
	}

	/**
	 * Assert that the testing class has all the required methods.
	 *
	 * @throws Exception
	 * @since Feb 22, 2018
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> classToTest = IridiumToHgmsData.class;
		final Class<?> testingClass = IridiumToHgmsDataTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.EQUALS_AND_HASHCODE, SkipMethod.TO_STRING,
				SkipMethod.CAN_EQUAL);

	}

	/**
	 * The decode order repository bean
	 *
	 * @since Feb 12, 2018
	 */

	@MockBean
	private IridiumDecodeOrderRepository	m_DecodeOrderRepo;

	/**
	 * The expected {@link IridiumResponse} from the {@link SbdProcessor}
	 *
	 * @since Feb 14, 2018
	 */
	private IridiumResponse					m_ExpectedResponse;

	/**
	 * The {@link IridiumStationIdRepository}
	 *
	 * @since Feb 12, 2018
	 */
	@MockBean
	private IridiumStationIdRepository		m_IridiumStationRepo;

	/**
	 * IridiumDataType repository bean
	 *
	 * @since Feb 12, 2018
	 */
	@MockBean
	private IridiumDataTypeRepository		m_StationDataTypeRepo;

	/**
	 * The {@link Station} to test with
	 *
	 * @since Feb 12, 2018
	 */
	private Station							m_StationTest;
	/**
	 * The {@link IridiumToHgmsData} to test with
	 *
	 * @since Feb 22, 2018
	 */
	@Autowired
	private IridiumToHgmsData				m_Testable;

	/**
	 * Setup the beans and the {@link IridiumResponse} for the test.
	 *
	 * @throws Exception
	 * @since Feb 23, 2018
	 */
	@Before
	public void setUp() throws Exception
	{
		m_StationTest = createStation();
		final String imei = "300234010124740";
		final IridiumStationId iridiumStationId = new IridiumStationId();
		iridiumStationId.setId(221L);
		iridiumStationId.setImei(imei);
		iridiumStationId.setStation(m_StationTest);

		when(m_IridiumStationRepo.findByImei(imei))
				.thenReturn(Lists.newArrayList(iridiumStationId));
		when(m_DecodeOrderRepo.findByStationId(m_StationTest.getId()))
				.thenReturn(
						Sets.newTreeSet(ParsingTestsHelper.getDecodeList()));
		final BinaryParser parser = new BinaryParser(
				ParsingTestsHelper.setupMessageBytes("00"));
		final Collection<IridiumStationId> stationsList = m_IridiumStationRepo
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()));
		final Optional<IridiumStationId> opt = stationsList.stream()
				.findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStation().getId();

		parser.setDecodeOrder(m_DecodeOrderRepo.findByStationId(stationId));
		final Map<IridiumDataType, Double> dataMap = parser
				.getValuesFromMessage();
		final Table<IridiumStationId, IridiumDataType, Double> valueTable = HashBasedTable
				.create();
		dataMap.forEach((datatype, value) -> valueTable.put(iridiumStationId,
				datatype, value));
		m_ExpectedResponse = IridiumResponse.builder()
				.message(parser.getMessage()).stations(stationsList)
				.values(valueTable).build();
	}

	/**
	 * Test method for {@link IridiumToHgmsData#clear()}
	 *
	 * @since Mar 1, 2018
	 */
	@Test
	public void testClear()
	{
		assertTrue(m_Testable.getDischarge().isEmpty());
		assertTrue(m_Testable.getPressure().isEmpty());
		assertTrue(m_Testable.getTemperature().isEmpty());
		assertTrue(m_Testable.getTide().isEmpty());
		assertTrue(m_Testable.getWaterLevel().isEmpty());
		assertTrue(m_Testable.getWave().isEmpty());
		assertTrue(m_Testable.getWind().isEmpty());

		m_Testable.ingest(Arrays.asList(m_ExpectedResponse));

		assertTrue(m_Testable.getDischarge().isEmpty());
		assertEquals(1, m_Testable.getPressure().size());
		assertEquals(1, m_Testable.getTemperature().size());
		assertTrue(m_Testable.getTide().isEmpty());
		assertEquals(1, m_Testable.getWaterLevel().size());
		assertTrue(m_Testable.getWave().isEmpty());
		assertEquals(1, m_Testable.getWind().size());

		m_Testable.clear();

		assertTrue(m_Testable.getDischarge().isEmpty());
		assertTrue(m_Testable.getPressure().isEmpty());
		assertTrue(m_Testable.getTemperature().isEmpty());
		assertTrue(m_Testable.getTide().isEmpty());
		assertTrue(m_Testable.getWaterLevel().isEmpty());
		assertTrue(m_Testable.getWave().isEmpty());
		assertTrue(m_Testable.getWind().isEmpty());

	}

	/**
	 * Test method for the {@link IridiumToHgmsData#ingest(IridiumResponse)}
	 *
	 * @since Feb 22, 2018
	 */
	@Test
	public void testIngestIridiumResponse()
	{
		assertTrue(m_Testable.getDischarge().isEmpty());
		assertTrue(m_Testable.getPressure().isEmpty());
		assertTrue(m_Testable.getTemperature().isEmpty());
		assertTrue(m_Testable.getTide().isEmpty());
		assertTrue(m_Testable.getWaterLevel().isEmpty());
		assertTrue(m_Testable.getWave().isEmpty());
		assertTrue(m_Testable.getWind().isEmpty());

		m_Testable.ingest(m_ExpectedResponse);

		assertTrue(m_Testable.getDischarge().isEmpty());
		assertEquals(1, m_Testable.getPressure().size());
		assertEquals(1, m_Testable.getTemperature().size());
		assertTrue(m_Testable.getTide().isEmpty());
		assertEquals(1, m_Testable.getWaterLevel().size());
		assertTrue(m_Testable.getWave().isEmpty());
		assertEquals(1, m_Testable.getWind().size());
	}

	/**
	 * Test method for {@link HarvestToHgmsData#ingest(java.util.Collection)}
	 *
	 * @since Feb 22, 2018
	 */
	@Test
	public void testIngestObject()
	{
		assertTrue(m_Testable.getDischarge().isEmpty());
		assertTrue(m_Testable.getPressure().isEmpty());
		assertTrue(m_Testable.getTemperature().isEmpty());
		assertTrue(m_Testable.getTide().isEmpty());
		assertTrue(m_Testable.getWaterLevel().isEmpty());
		assertTrue(m_Testable.getWave().isEmpty());
		assertTrue(m_Testable.getWind().isEmpty());

		m_Testable.ingest(Arrays.asList(m_ExpectedResponse));

		assertTrue(m_Testable.getDischarge().isEmpty());
		assertEquals(1, m_Testable.getPressure().size());
		assertEquals(1, m_Testable.getTemperature().size());
		assertTrue(m_Testable.getTide().isEmpty());
		assertEquals(1, m_Testable.getWaterLevel().size());
		assertTrue(m_Testable.getWave().isEmpty());
		assertEquals(1, m_Testable.getWind().size());

	}

	/**
	 * Test method for {@link HarvestToHgmsData#save()}
	 *
	 * @since Feb 23, 2018
	 */
	@Test
	public void testSave()
	{
		final Common common = m_Testable.getCommon();
		final long dischargeSize = common.getDischarge().count();
		final long pressureSize = common.getPressure().count();
		final long temperatureSize = common.getTemperature().count();
		final long tideSize = common.getTide().count();
		final long waterLevelSize = common.getWaterLevel().count();
		final long waveSize = common.getWave().count();
		final long windSize = common.getWind().count();

		m_Testable.ingest(Arrays.asList(m_ExpectedResponse));
		m_Testable.save();

		assertEquals(dischargeSize, common.getDischarge().count());
		assertEquals(pressureSize + 1, common.getPressure().count());
		assertEquals(temperatureSize + 1, common.getTemperature().count());
		assertEquals(tideSize, common.getTide().count());
		assertEquals(waterLevelSize + 1, common.getWaterLevel().count());
		assertEquals(waveSize, common.getWave().count());
		assertEquals(windSize + 1, common.getWind().count());

	}

}
