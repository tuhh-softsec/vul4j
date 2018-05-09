package gov.la.coastal.cims.hgms.harvester.structuregages.directip;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.primitives.Bytes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import gov.la.coastal.cims.hgms.common.Common;
import gov.la.coastal.cims.hgms.common.db.IridiumDecodeOrderRepository;
import gov.la.coastal.cims.hgms.common.db.IridiumStationIdRepository;
import gov.la.coastal.cims.hgms.common.db.entity.IridiumDataType;
import gov.la.coastal.cims.hgms.common.db.entity.IridiumStationId;
import gov.la.coastal.cims.hgms.harvester.structuregages.Application;
import gov.la.coastal.cims.hgms.harvester.structuregages.ParsingTestsHelper;
import gov.la.coastal.cims.hgms.harvester.structuregages.Properties;
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser;
import gov.usgs.warc.mail.SMTP;
import gov.usgs.warc.test.Tests;

/**
 * Test the {@link SbdProcessor}
 *
 * @author mckelvym
 * @author darceyj
 * @since Mar 30, 2018
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(
		classes = { Application.class, Properties.class, Common.class })
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class SbdProcessorNonMockTest
{

	/**
	 * @author darceyj
	 * @since Mar 30, 2018
	 *
	 */
	@Configuration
	static class ContextConfiguration
	{
		/**
		 * @author mckelvym
		 * @since Mar 30, 2018
		 */
		@MockBean
		private SMTP smtp;

		/**
		 * @param p_Properties
		 * @return {@link IridiumToHgmsData} bean
		 * @author darceyj
		 * @since Mar 30, 2018
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
		 * @since Mar 30, 2018
		 */
		@Bean
		@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
		@Primary
		public SbdProcessor sbdProcessor(final ApplicationContext p_Context,
				final IridiumStationIdRepository p_IridiumStationIdRepository,
				final IridiumDecodeOrderRepository p_IridiumDecodeOrderRepository)
		{
			return new SbdProcessorImpl(p_Context, p_IridiumStationIdRepository,
					p_IridiumDecodeOrderRepository);
		}

	}

	/**
	 * Assert that the required test methods are present
	 *
	 * @throws java.lang.Exception
	 * @since Mar 30, 2018
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> classToTest = SbdProcessor.class;
		final Class<?> testingClass = SbdProcessorNonMockTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass);
	}

	/**
	 * @author mckelvym
	 * @since Mar 30, 2018
	 */
	@Autowired
	private IridiumDecodeOrderRepository		m_IridiumDecodeOrderRepository;

	/**
	 * The {@link IridiumStationIdRepository}
	 *
	 * @since Mar 30, 2018
	 */
	@Autowired
	private IridiumStationIdRepository			m_IridiumStationIdRepository;

	/**
	 * The {@link SbdProcessor} to test with.
	 *
	 * @since Mar 30, 2018
	 */
	@Autowired
	private SbdProcessor						m_Testable;

	/**
	 * @author mckelvym
	 * @since Mar 30, 2018
	 */
	private Map<List<Byte>, IridiumResponse>	m_TestingData;

	/**
	 * @throws Exception
	 * @author mckelvym
	 * @author darceyj
	 * @since Mar 30, 2018
	 */
	@Before
	public void setUp() throws Exception
	{
		m_TestingData = Maps.newLinkedHashMap();

		final List<List<Byte>> inputByteLists = ParsingTestsHelper
				.getTestingData();
		for (final List<Byte> inputBytes : inputByteLists)
		{
			final BinaryParser parser = new BinaryParser(inputBytes);
			final Collection<IridiumStationId> stationsList = m_IridiumStationIdRepository
					.findByImei(String.valueOf(
							parser.getMessage().getHeader().getImei()));
			final Optional<IridiumStationId> opt = stationsList.stream()
					.findFirst();
			assertThat(opt).isNotEqualTo(Optional.empty());
			final IridiumStationId iridiumStationId = opt.get();
			final Long stationId = iridiumStationId.getStation().getId();

			parser.setDecodeOrder(
					m_IridiumDecodeOrderRepository.findByStationId(stationId));
			final Map<IridiumDataType, Double> dataMap = parser
					.getValuesFromMessage();
			final Table<IridiumStationId, IridiumDataType, Double> valueTable = HashBasedTable
					.create();
			dataMap.forEach((datatype, value) -> valueTable
					.put(iridiumStationId, datatype, value));
			final IridiumResponse output = IridiumResponse.builder()
					.message(parser.getMessage()).stations(stationsList)
					.values(valueTable).build();
			m_TestingData.put(inputBytes, output);
		}
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.directip.SbdProcessorImpl#process(byte[], java.util.function.Consumer)}.
	 */
	@Test
	public void testProcess()
	{
		for (final Entry<List<Byte>, IridiumResponse> entry : m_TestingData
				.entrySet())
		{
			final List<Byte> input = entry.getKey();
			final IridiumResponse expected = entry.getValue();
			try
			{
				final IridiumResponse response = m_Testable
						.process(Bytes.toArray(input), null).get();
				assertThat(response.getMessage())
						.isEqualToComparingFieldByField(expected.getMessage());
				assertThat(response.getStations())
						.isEqualTo(expected.getStations());
				assertThat(response.getValues().values().size())
						.isEqualTo(expected.getValues().values().size() * 2);
			}
			catch (final Exception e)
			{
				fail(String.format(
						"Unable to parse the values from the binary directip message.  %s",
						Arrays.toString(e.getStackTrace())));
			}
		}
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.directip.SbdProcessorImpl#process(byte[], java.util.function.Consumer)}.
	 */
	@Test
	public void testProcessBadOrShortPayload()
	{
		final List<List<Byte>> testingData = Lists.newArrayList();
		testingData.addAll(ParsingTestsHelper.getTestingDataBadPayload());
		testingData.addAll(ParsingTestsHelper.getTestingDataShortPayload());

		for (final List<Byte> bytes : testingData)
		{
			final AtomicReference<Throwable> t = new AtomicReference<>();
			assertThat(m_Testable.process(Bytes.toArray(bytes), t::set))
					.isEqualTo(Optional.empty());
			assertThat(t.get()).isNotNull();
		}
	}

}
