package gov.usgs.warc.iridium.sbd.decoder.parser;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Payload;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.PayloadType;
import gov.usgs.warc.iridium.sbd.decoder.sixbitbinary.Decode;
import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decoder for {@link PayloadType#PSEUDOBINARY_B_DATA_FORMAT}
 *
 * @author mckelvym
 * @since Mar 21, 2019
 *
 */
public class PseudobinaryBPayloadDecoder implements PayloadDecoder
{

	/**
	 * @author mckelvym
	 * @since Mar 26, 2019
	 */
	private static final Logger log = LoggerFactory
			.getLogger(PseudobinaryBPayloadDecoder.class);

	/**
	 * @author mckelvym
	 * @since Mar 21, 2019
	 */
	public PseudobinaryBPayloadDecoder()
	{
		/**
		 * Nothing here.
		 */
	}

	@Override
	public Map<SbdDataType, Double> decode(final Payload p_Payload,
			final SortedSet<SbdDataType> p_DataTypes,
			final SortedSet<SbdDecodeOrder> p_DecodeOrder)
			throws UnsupportedOperationException, RuntimeException
	{
		checkArgument(p_Payload
				.getPayloadType() == PayloadType.PSEUDOBINARY_B_DATA_FORMAT,
				"Invalid payload type for this decoder.");

		final byte[] payload = p_Payload.getPayload();
		log.info(String.format("Payload:%n%s",
				new String(payload, Charsets.UTF_8)));

		final Queue<Byte> payloadQueue = Queues
				.newArrayBlockingQueue(payload.length);
		IntStream.range(0, payload.length)
				.forEach(i -> payloadQueue.offer(payload[i]));

		final Map<SbdDataType, Double> dataMap = Maps.newLinkedHashMap();

		/**
		 * Build the map of data type and its corresponding value decoded from
		 * the payload bytes.
		 */
		for (final SbdDecodeOrder order : p_DecodeOrder)
		{
			final SbdDataType datatype = order.getDatatype();
			final int byteLength = datatype.getBytes();

			if (payloadQueue.size() >= byteLength)
			{
				final List<Byte> decodeList = IntStream.range(0, byteLength)
						.mapToObj(i -> payloadQueue.poll())
						.collect(Collectors.toList());
				float value = Float.NaN;
				try
				{
					value = Decode.valueAtIndex(decodeList, 0,
							decodeList.size(), 1);
				}
				catch (final IllegalArgumentException e)
				{
					log.warn(String.format(
							"Unable to decode '%s' (%s): %s%nUsing NaN instead.",
							datatype, Arrays.toString(decodeList.toArray()),
							e.getMessage()));
				}
				final double transformedVal = Float.isNaN(value) ? Float.NaN
						: datatype.transformValue(value);

				log.info(String.format("Decoded '%s': %s -> %s", datatype,
						value, transformedVal));
				dataMap.put(datatype, transformedVal);
			}
			else
			{
				log.warn(String.format(
						"The remaining payload (size: %s) does not have enough bytes to decode '%s': need %s byte(s).",
						payloadQueue.size(), datatype, byteLength));
			}
		}
		return dataMap;
	}
}
