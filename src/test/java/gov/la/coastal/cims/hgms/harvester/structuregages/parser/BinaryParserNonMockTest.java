package gov.la.coastal.cims.hgms.harvester.structuregages.parser;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import gov.la.coastal.cims.hgms.common.Common;
import gov.la.coastal.cims.hgms.common.db.IridiumDecodeOrderRepository;
import gov.la.coastal.cims.hgms.common.db.IridiumStationIdRepository;
import gov.la.coastal.cims.hgms.common.db.entity.IridiumStationId;
import gov.la.coastal.cims.hgms.harvester.structuregages.ParsingTestsHelper;
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.elements.Header;

/**
 * Test the binary parser with testing data
 *
 * @author mckelvym
 * @since Mar 30, 2018
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { Common.class })
@DataJpaTest
@ActiveProfiles("test")
public class BinaryParserNonMockTest
{
	/**
	 * @author mckelvym
	 * @since Mar 30, 2018
	 */
	@Autowired
	private IridiumDecodeOrderRepository	m_IridiumDecodeOrderRepository;

	/**
	 * The {@link IridiumStationIdRepository}
	 *
	 * @since Mar 30, 2018
	 */
	@Autowired
	private IridiumStationIdRepository		m_IridiumStationIdRepository;

	/**
	 * The list of bytes to parse
	 *
	 * @since Mar 30, 2018
	 */
	private List<List<Byte>>				m_TestingData;

	/**
	 * @throws java.lang.Exception
	 * @since Mar 30, 2018
	 */
	@Before
	public void setUp() throws Exception
	{
		m_TestingData = ParsingTestsHelper.getTestingData();
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser#getValuesFromMessage()}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testBadPayload() throws Exception
	{
		for (final List<Byte> bytes : ParsingTestsHelper
				.getTestingDataBadPayload())
		{
			assertThatThrownBy(() -> new BinaryParser(bytes))
					.hasSameClassAs(new IllegalArgumentException());
		}
	}

	/**
	 * Test method for
	 * {@link gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser#getValuesFromMessage()}.
	 */
	@Test
	public void testGetValuesFromMessage()
	{
		for (final List<Byte> bytes : m_TestingData)
		{
			try
			{
				final BinaryParser parser = new BinaryParser(bytes);
				final Message message = parser.getMessage();
				final Header header = message.getHeader();
				final long imei = header.getImei();
				final String imeiString = String.valueOf(imei);
				final Optional<IridiumStationId> opt = m_IridiumStationIdRepository
						.findByImei(imeiString).stream().findFirst();
				final Long stationId = opt.get().getStation().getId();
				parser.setDecodeOrder(m_IridiumDecodeOrderRepository
						.findByStationId(stationId));
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

}
