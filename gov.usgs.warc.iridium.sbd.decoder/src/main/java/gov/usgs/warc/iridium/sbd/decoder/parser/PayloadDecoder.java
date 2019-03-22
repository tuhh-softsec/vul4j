package gov.usgs.warc.iridium.sbd.decoder.parser;

import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Payload;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.PayloadType;
import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrder;
import java.util.Map;
import java.util.SortedSet;

/**
 * Decoder used to decode a given {@link PayloadType}
 *
 * @author mckelvym
 * @since Mar 21, 2019
 *
 */
@FunctionalInterface
public interface PayloadDecoder
{

	/**
	 * Decode the provided {@link Payload} using the suggested decoding order.
	 *
	 * @param p_Payload
	 *            {@link Payload}
	 * @param p_DataTypes
	 *            sorted set of {@link SbdDataType}
	 * @param p_DecodeOrder
	 *            sorted set of {@link SbdDecodeOrder}
	 * @return the decoded data
	 * @author mckelvym
	 * @throws UnsupportedOperationException
	 *             decoder not implemented for payload type
	 * @throws RuntimeException
	 *             certain decoders may throw other runtime exceptions such a
	 *             {@link IndexOutOfBoundsException},
	 *             {@link IllegalArgumentException}, and
	 *             {@link NullPointerException}
	 * @since Mar 21, 2019
	 */
	Map<SbdDataType, Double> decode(Payload p_Payload,
			SortedSet<SbdDataType> p_DataTypes,
			SortedSet<SbdDecodeOrder> p_DecodeOrder)
			throws UnsupportedOperationException, RuntimeException;

}