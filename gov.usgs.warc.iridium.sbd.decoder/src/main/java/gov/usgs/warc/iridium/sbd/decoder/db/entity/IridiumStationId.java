package gov.usgs.warc.iridium.sbd.decoder.db.entity;

/**
 * Stores the IMEI for an Iridium transmitter, which is linked to a station
 *
 * @author mckelvym
 * @since Feb 8, 2018
 *
 */
public interface IridiumStationId
{
	/**
	 * Unique ID
	 *
	 * @author mckelvym
	 * @since Feb 8, 2017
	 */
	Long getId();

	/**
	 * The IMEI
	 *
	 * @author mckelvym
	 * @since Feb 8, 2018
	 */
	String getImei();

	/**
	 * The station
	 *
	 * @author mckelvym
	 * @since Feb 8, 2018
	 */
	Long getStationId();
}
