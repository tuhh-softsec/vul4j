package gov.la.coastal.cims.hgms.harvester.structuregages.directip;

import static gov.la.coastal.cims.hgms.harvester.structuregages.ParsingTestsHelper.createStation;
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

import gov.la.coastal.cims.hgms.common.Common;
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
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.Message;
import gov.usgs.warc.mail.SMTP;
import gov.usgs.warc.test.Tests;
import gov.usgs.warc.test.Tests.SkipMethod;

/**
 * The {@link IridiumResponse} test
 *
 * @author darceyj
 * @since Feb 12, 2018
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(
		classes = { Application.class, Properties.class, Common.class })
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
		 * @author mckelvym
		 * @since Mar 1, 2018
		 */
		@MockBean
		private SMTP smtp;

		/**
		 *
		 * @param p_Properties
		 * @return {@link IridiumToHgmsData} instance
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
		 * @since Nov 8, 2017
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
	private IridiumDecodeOrderRepository	m_DecodeOrderRepo;

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
	 * The {@link IridiumResponse} to test with
	 *
	 * @since Feb 12, 2018
	 */
	private IridiumResponse					m_Testable;

	/**
	 *
	 *
	 * @throws java.lang.Exception
	 * @since Feb 12, 2018
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
