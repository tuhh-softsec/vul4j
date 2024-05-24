package gov.usgs.warc.iridium.sbd.decoder.parser.elements;

import static java.util.Objects.requireNonNull;

import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrder;
import java.util.Map;
import java.util.SortedSet;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * The payload information element
 *
 * @author darceyj
 * @since Jan 8, 2018
 *
 */
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Payload
{
	/**
	 * @param p_PayloadType
	 *            {@link PayloadType}
	 * @return a new builder to build instances of this class.
	 * @since Jan 8, 2018
	 */
	public static PayloadBuilder builder(final PayloadType p_PayloadType)
	{
		return new PayloadBuilder().payloadType(
				requireNonNull(p_PayloadType, "Payload type is required."));
	}

	/**
	 * The payload ie id
	 *
	 * @since Jan 8, 2018
	 */
	private final byte			id;

	/**
	 * The pay load bytes in the byte array
	 *
	 * @since Jan 8, 2018
	 */
	private final byte[]		payload;

	/**
	 * See {@link PayloadType}
	 *
	 * @author mckelvym
	 * @since Mar 21, 2019
	 */
	private final PayloadType	payloadType;

	/**
	 * Decode this payload into a mapping of {@link SbdDataType} to value.
	 * Depending on the {@link PayloadType} of this payload, the operation may
	 * or may not be supported.
	 *
	 * @param p_DataTypes
	 *            sorted set of {@link SbdDataType}
	 * @param p_DecodeOrder
	 *            hint for decoding order, a sorted set of
	 *            {@link SbdDecodeOrder}
	 * @return the mapping of {@link SbdDataType} to values
	 * @throws UnsupportedOperationException
	 * @author mckelvym
	 * @since Mar 21, 2019
	 */
	public Map<SbdDataType, Double> decode(
			final SortedSet<SbdDataType> p_DataTypes,
			final SortedSet<SbdDecodeOrder> p_DecodeOrder)
			throws UnsupportedOperationException
	{
		return getPayloadType().getPayloadDecoder().decode(this, p_DataTypes,
				p_DecodeOrder);
	}
}
