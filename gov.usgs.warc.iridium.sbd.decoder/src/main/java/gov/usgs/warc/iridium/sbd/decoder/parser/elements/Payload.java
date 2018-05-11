package gov.usgs.warc.iridium.sbd.decoder.parser.elements;

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
	 *
	 * @return a new builder to build instances of this class.
	 * @since Jan 8, 2018
	 */
	public static PayloadBuilder builder()
	{
		return new PayloadBuilder();
	}

	/**
	 * The payload ie id
	 *
	 * @since Jan 8, 2018
	 */
	private final byte		id;

	/**
	 * The payload length (should be between 1 - 1960) bytes
	 *
	 * @since Jan 8, 2018
	 */
	private final short		length;

	/**
	 * The pay load bytes in the byte array
	 *
	 * @since Jan 8, 2018
	 */
	private final Byte[]	payload;
}
