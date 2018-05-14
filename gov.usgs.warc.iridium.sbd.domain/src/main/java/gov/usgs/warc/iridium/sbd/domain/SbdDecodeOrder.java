package gov.usgs.warc.iridium.sbd.domain;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ComparisonChain;

/**
 * Stores all of the decodes and the byte offset they should have
 *
 * @author mckelvym
 * @since Feb 2, 2018
 *
 */
public interface SbdDecodeOrder extends Comparable<SbdDecodeOrder>
{
	@Override
	public default int compareTo(final SbdDecodeOrder p_Other)
	{
		checkArgument(getStationIdentifier().equals(p_Other.getStationIdentifier()),
				"Cannot compare two stations.");
		return ComparisonChain.start()
				.compare(getByteOffset(), p_Other.getByteOffset()).result();
	}

	/**
	 * The byteOffset into the SBD payload
	 *
	 * @return byteOffset into the SBD payload
	 * @author mckelvym
	 * @since Feb 16, 2018
	 */
	long getByteOffset();

	/**
	 * The data type to decode
	 *
	 * @return {@link SbdDataType} to decode
	 * @author mckelvym
	 * @since Feb 2, 2018
	 */
	SbdDataType getDatatype();

	/**
	 * ID
	 *
	 * @return decode order identifier
	 * @author mckelvym
	 * @since Feb 2, 2018
	 */
	Long getId();

	/**
	 * The station
	 *
	 * @return station identifier
	 * @author mckelvym
	 * @since Feb 2, 2018
	 */
	Long getStationIdentifier();

}
