package gov.usgs.warc.iridium.sbd.decoder.parser;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Payload;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.PayloadType;
import gov.usgs.warc.iridium.sbd.decoder.sixbitbinary.Decode;
import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

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

		final List<Byte> payloadBytes = Lists.newArrayList();
		for (final byte b : p_Payload.getPayload())
		{
			payloadBytes.add(b);
		}

		final Map<SbdDataType, Double> dataMap = Maps.newLinkedHashMap();
		/**
		 * Build the map of data type and its corresponding value decoded from
		 * the payload bytes.
		 */
		for (final SbdDecodeOrder order : p_DecodeOrder)
		{
			final SbdDataType datatype = order.getDatatype();
			final int byteLength = datatype.getBytes();
			final int startIndex = (int) order.getByteOffset();

			checkElementIndex(startIndex, payloadBytes.size(), String.format(
					"The payload (%s; size: %s) does not have enough bytes to decode '%s' starting at byte %s.",
					Arrays.toString(payloadBytes.toArray()),
					payloadBytes.size(), datatype, startIndex));

			final float value = Decode.valueAtIndex(payloadBytes, startIndex,
					byteLength, 1);
			final double transformedVal = datatype.transformValue(value);

			dataMap.put(datatype, transformedVal);
		}
		return dataMap;
	}
}
