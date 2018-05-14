package gov.usgs.warc.iridium.sbd.decoder.parser;

import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Confirmation;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Header;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.LocationInformation;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Payload;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * An entire directip message. It contains two distinct elements: the
 * {@link Header} and {@link Payload}. Optionally, it could have a
 * {@link LocationInformation} element and a {@link Confirmation}
 *
 * @author darceyj
 * @since Jan 8, 2018
 *
 */
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Message
{
	/**
	 * Create a new builder to make new messages
	 *
	 * @return a builder
	 * @since Jan 8, 2018
	 */
	public static MessageBuilder builder()
	{
		return new MessageBuilder();
	}

	/**
	 * The {@link Confirmation} element.
	 *
	 * @since Jan 26, 2018
	 */
	private final Confirmation			confirmationElement;

	/**
	 * The {@link Header} of the message
	 *
	 * @since Jan 8, 2018
	 */
	private final Header				header;

	/**
	 * Overall message length
	 *
	 * @since Jan 8, 2018
	 */
	private final int					length;

	/**
	 * The {@link LocationInformation} information element. Could be null.
	 *
	 * @since Jan 11, 2018
	 */
	private final LocationInformation	locationInformation;

	/**
	 * The {@link Payload} information element
	 *
	 * @since Jan 8, 2018
	 */
	private final Payload				payLoad;

	/**
	 * Protocol version
	 */
	private final byte					protocolVersion;
}
