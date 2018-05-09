package gov.usgs.warc.iridium.sbd.decoder.db.entity;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ComparisonChain;

/**
 * Stores all of the decodes and the byte offset they should have
 *
 * @author mckelvym
 * @since Feb 2, 2018
 *
 */
public interface IridiumDecodeOrder extends Comparable<IridiumDecodeOrder>
{
	@Override
	public default int compareTo(final IridiumDecodeOrder p_Other)
	{
		checkArgument(getStationId().equals(p_Other.getStationId()),
				"Cannot compare two stations.");
		return ComparisonChain.start()
				.compare(getByteOffset(), p_Other.getByteOffset()).result();
	}

	/**
	 * The byteOffset into the SBD payload
	 *
	 * @author mckelvym
	 * @since Feb 16, 2018
	 */
	long getByteOffset();

	/**
	 * The data type to decode
	 *
	 * @author mckelvym
	 * @since Feb 2, 2018
	 */
	IridiumDataType getDatatype();

	/**
	 * ID
	 *
	 * @author mckelvym
	 * @since Feb 2, 2018
	 */
	Long getId();

	/**
	 * The station
	 *
	 * @author mckelvym
	 * @since Feb 2, 2018
	 */
	Long getStationId();

}
