package gov.usgs.warc.iridium.sbd.domain;

import java.util.Collection;

/**
 * Repository for {@link SbdStationId}
 *
 * @author mckelvym
 * @param <T>
 *            class that implements {@link SbdStationId}
 * @since Feb 8, 2018
 *
 */
public interface SbdStationIdProvider<T extends SbdStationId>
{
	/**
	 * @param p_Imei
	 *            the IMEI to search
	 * @return stream of {@link SbdStationId} having particular IMEI
	 * @author mckelvym
	 * @since Feb 8, 2018
	 */
	Collection<T> findByImei(String p_Imei);
}
