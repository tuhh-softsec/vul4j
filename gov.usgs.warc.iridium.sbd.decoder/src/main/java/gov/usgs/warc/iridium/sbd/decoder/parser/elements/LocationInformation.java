package gov.usgs.warc.iridium.sbd.decoder.parser.elements;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Location information element
 *
 * @author darceyj
 * @since Jan 5, 2018
 *
 */
@Getter
@Builder
@EqualsAndHashCode
@ToString
public class LocationInformation
{
	/**
	 *
	 * @return a {@link LocationInformationBuilder} to use
	 * @since Jan 5, 2018
	 */
	public static LocationInformationBuilder builder()
	{
		return new LocationInformationBuilder();
	}

	/**
	 * The CEP Radius
	 *
	 * @since Jan 26, 2018
	 */
	private final long		cepRadius;
	/**
	 * The information element id
	 *
	 * @since Jan 26, 2018
	 */
	private final byte		id;

	/**
	 * The latitude
	 *
	 * @since Jan 26, 2018
	 */
	private final double	latitude;
	/**
	 * The information element byte length
	 *
	 * @since Jan 26, 2018
	 */
	private final short		length;
	/**
	 * The longitude
	 *
	 * @since Jan 26, 2018
	 */
	private final double	longitude;

}
