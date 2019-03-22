package gov.usgs.warc.iridium.sbd.decoder.directip;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser;
import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import gov.usgs.warc.iridium.sbd.domain.SbdDataTypeProvider;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrder;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrderProvider;
import gov.usgs.warc.iridium.sbd.domain.SbdStationId;
import gov.usgs.warc.iridium.sbd.domain.SbdStationIdProvider;
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
	private static final Logger										log	= LoggerFactory
			.getLogger(SbdProcessorImpl.class);

	/**
	 * The data type repository
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private final SbdDataTypeProvider<? extends SbdDataType>		m_DataTypeRepository;

	/**
	 * The decode order repository bean
	 *
	 * @since Feb 12, 2018
	 */
	private final SbdDecodeOrderProvider<? extends SbdDecodeOrder>	m_DecodeOrderRepository;

	/**
	 * The iridium station id repository bean
	 *
	 * @since Feb 12, 2018
	 */
	private final SbdStationIdProvider<? extends SbdStationId>		m_IridiumStationRepository;

	/**
	 * @param p_Context
	 *            {@link ApplicationContext}
	 * @param p_IridiumStationIdRepository
	 *            {@link SbdStationIdProvider}
	 * @param p_IridiumDataTypeRepository
	 *            {@link SbdDataTypeProvider}
	 * @param p_IridiumDecodeOrderRepository
	 *            {@link SbdDecodeOrderProvider}
	 * @author mckelvym
	 * @since Feb 28, 2018
	 */
	public SbdProcessorImpl(final ApplicationContext p_Context,
			final SbdStationIdProvider<? extends SbdStationId> p_IridiumStationIdRepository,
			final SbdDataTypeProvider<? extends SbdDataType> p_IridiumDataTypeRepository,
			final SbdDecodeOrderProvider<? extends SbdDecodeOrder> p_IridiumDecodeOrderRepository)
	{
		m_IridiumStationRepository = requireNonNull(
				p_IridiumStationIdRepository,
				"Station ID repository required.");
		m_DataTypeRepository = requireNonNull(p_IridiumDataTypeRepository,
				"Data type repository required.");
		m_DecodeOrderRepository = requireNonNull(p_IridiumDecodeOrderRepository,
				"Decode order repository required.");
	}

	@Override
	public Optional<IridiumResponse> process(final byte[] p_Bytes,
			final Consumer<Throwable> p_ExceptionConsumer)
	{
		final List<Byte> byteList = Lists.newArrayList();
		final Table<SbdStationId, SbdDataType, Double> stationDatatypeValueTable = HashBasedTable
				.create();
		for (final byte b : p_Bytes)
		{
			byteList.add(Byte.valueOf(b));
		}

		try
		{
			final SbdParser parser = new SbdParser(byteList);
			/**
			 * Parse the incoming bytes and return an IridumResponse
			 */
			final List<SbdStationId> iridiumStationIds = m_IridiumStationRepository
					.findByImei(Long.toString(
							parser.getMessage().getHeader().getImei()))
					.stream().collect(Collectors.toList());
			for (final SbdStationId iridiumStationId : iridiumStationIds)
			{
				final long stationId = iridiumStationId.getStationId();
				final SortedSet<? extends SbdDataType> dataTypeSet = m_DataTypeRepository
						.findByStationId(stationId);
				final SortedSet<? extends SbdDecodeOrder> decodeOrderSet = m_DecodeOrderRepository
						.findByStationId(stationId);
				parser.setDecodeConfiguration(dataTypeSet, decodeOrderSet);
				final Map<SbdDataType, Double> valueMap = parser
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
			if (p_ExceptionConsumer != null)
			{
				p_ExceptionConsumer.accept(e);
			}
			return Optional.empty();
		}
	}
}
