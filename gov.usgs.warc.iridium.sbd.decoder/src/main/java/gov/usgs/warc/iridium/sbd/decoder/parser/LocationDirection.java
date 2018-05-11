package gov.usgs.warc.iridium.sbd.decoder.parser;

/**
 * Directional heading for the location information element
 *
 * @author darceyj
 * @since Jan 12, 2018
 *
 */
public enum LocationDirection
{
	/**
	 * @author darceyj
	 * @since Jan 12, 2018
	 */
	EAST,
	/**
	 * @author darceyj
	 * @since Jan 12, 2018
	 */
	NORTH,
	/**
	 * @author darceyj
	 * @since Jan 12, 2018
	 */
	SOUTH,
	/**
	 * @author darceyj
	 * @since Jan 12, 2018
	 */
	WEST,;

	/**
	 * @param p_FlagBit
	 *            the bit to use
	 * @return the East West Location from the bit
	 * @since Jan 12, 2018
	 */
	public static LocationDirection fromEWIBit(final boolean p_FlagBit)
	{
		return p_FlagBit ? WEST : EAST;
	}

	/**
	 *
	 * @param p_FlagBit
	 *            the flag bit to use
	 * @return the {@link LocationDirection} from the bit
	 * @since Jan 12, 2018
	 */
	public static LocationDirection fromNSIBit(final boolean p_FlagBit)
	{
		return p_FlagBit ? SOUTH : NORTH;
	}
}
