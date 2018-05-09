package gov.la.coastal.cims.hgms.common.db.entity;

/**
 * Stores the IMEI for an Iridium transmitter, which is linked to a
 * {@link Station}
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
