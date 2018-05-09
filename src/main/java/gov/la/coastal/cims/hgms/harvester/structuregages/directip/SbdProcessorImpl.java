package gov.la.coastal.cims.hgms.harvester.structuregages.directip;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import gov.la.coastal.cims.hgms.common.db.IridiumDecodeOrderRepository;
import gov.la.coastal.cims.hgms.common.db.IridiumStationIdRepository;
import gov.la.coastal.cims.hgms.common.db.entity.IridiumDataType;
import gov.la.coastal.cims.hgms.common.db.entity.IridiumDecodeOrder;
import gov.la.coastal.cims.hgms.common.db.entity.IridiumStationId;
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.BinaryParser;
import gov.usgs.warc.mail.SMTP;

/**
 * Default implementation of {@link SbdProcessor}
 *
 * @author mckelvym
 * @author darceyj
 * @since Jan 5, 2018
 *
 */
@Component
public class SbdProcessorImpl implements SbdProcessor
{
	/**
	 * @author mckelvym
	 * @since Feb 27, 2018
	 */
	private static final Logger					log	= LoggerFactory
			.getLogger(SbdProcessorImpl.class);

	/**
	 * The decode order repository bean
	 *
	 * @since Feb 12, 2018
	 */
	private final IridiumDecodeOrderRepository	m_DecodeOrderRepo;

	/**
	 * The iridium station id repository bean
	 *
	 * @since Feb 12, 2018
	 */
	private final IridiumStationIdRepository	m_IridiumStationRepo;

	/**
	 * @author mckelvym
	 * @since Mar 1, 2018
	 */
	private final SMTP							m_SMTP;

	/**
	 * @param p_Context
	 *            {@link ApplicationContext}
	 * @param p_IridiumStationIdRepository
	 *            {@link IridiumStationIdRepository}
	 * @param p_IridiumDecodeOrderRepository
	 *            {@link IridiumDecodeOrderRepository}
	 * @author mckelvym
	 * @since Feb 28, 2018
	 */
	public SbdProcessorImpl(final ApplicationContext p_Context,
			final IridiumStationIdRepository p_IridiumStationIdRepository,
			final IridiumDecodeOrderRepository p_IridiumDecodeOrderRepository)
	{
		m_SMTP = requireNonNull(p_Context.getBean(SMTP.class));
		m_IridiumStationRepo = requireNonNull(p_IridiumStationIdRepository,
				"Station ID repository");
		m_DecodeOrderRepo = requireNonNull(p_IridiumDecodeOrderRepository,
				"Decode order repository");
	}

	@Override
	// @Transactional
	public Optional<IridiumResponse> process(final byte[] p_Bytes,
			final Consumer<Throwable> p_ExceptionConsumer)
	{
		final List<Byte> byteList = Lists.newArrayList();
		final Table<IridiumStationId, IridiumDataType, Double> stationDatatypeValueTable = HashBasedTable
				.create();
		for (final byte b : p_Bytes)
		{
			byteList.add(Byte.valueOf(b));
		}

		try
		{
			final BinaryParser parser = new BinaryParser(byteList);
			/**
			 * Parse the incoming bytes and return an IridumResponse
			 */
			final List<IridiumStationId> iridiumStationIds = m_IridiumStationRepo
					.findByImei(Long.toString(
							parser.getMessage().getHeader().getImei()))
					.stream().collect(Collectors.toList());
			for (final IridiumStationId iridiumStationId : iridiumStationIds)
			{
				final long stationId = iridiumStationId.getStation().getId();
				final SortedSet<IridiumDecodeOrder> decodeOrderSet = m_DecodeOrderRepo
						.findByStationId(stationId);
				parser.setDecodeOrder(decodeOrderSet);
				final Map<IridiumDataType, Double> valueMap = parser
						.getValuesFromMessage();
				log.info(String.format("Station id %s: %s", stationId,
						valueMap.toString()));
				valueMap.forEach((datatype, value) -> stationDatatypeValueTable
						.put(iridiumStationId, datatype, value));
			}

			return Optional
					.of(IridiumResponse.builder().stations(iridiumStationIds)
							.values(stationDatatypeValueTable)
							.message(parser.getMessage()).build());
		}
		catch (final Exception e)
		{
			final String message = String.format(
					"An error occurred parsing the message: %s. The error was: %s",
					Arrays.toString(p_Bytes), e.getMessage());
			log.error(message, e);
			m_SMTP.bugReport("Parse Error", message, e);
			if (p_ExceptionConsumer != null)
			{
				p_ExceptionConsumer.accept(e);
			}
			return Optional.empty();
		}
	}
}
