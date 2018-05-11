package gov.usgs.warc.iridium.sbd.domain;

/**
 * Stores the IMEI for an Iridium transmitter, which is linked to a station
 *
 * @author mckelvym
 * @since Feb 8, 2018
 *
 */
public interface SbdStationId
{
	/**
	 * Unique ID
	 *
	 * @return the identifier for this data type
	 * @author mckelvym
	 * @since Feb 8, 2017
	 */
	Long getId();

	/**
	 * The IMEI
	 *
	 * @return imei
	 * @author mckelvym
	 * @since Feb 8, 2018
	 */
	String getImei();

	/**
	 * The station
	 **
	 * @return station identifier
	 * @author mckelvym
	 * @since Feb 8, 2018
	 */
	Long getStationId();
}
