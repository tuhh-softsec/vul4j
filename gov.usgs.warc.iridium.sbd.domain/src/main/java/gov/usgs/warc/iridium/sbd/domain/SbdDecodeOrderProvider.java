package gov.usgs.warc.iridium.sbd.domain;

import java.util.SortedSet;

/**
 * Repository for {@link SbdDecodeOrder}
 *
 * @author mckelvym
 * @param <T>
 *            class that implements {@link SbdDecodeOrder}
 * @since Feb 2, 2018
 *
 */
public interface SbdDecodeOrderProvider<T extends SbdDecodeOrder>
{
	/**
	 * @param p_StationID
	 *            the station ID
	 * @return the sorted set of {@link SbdDecodeOrder} (by
	 *         {@link SbdDecodeOrder#compareTo(SbdDecodeOrder)} for a
	 *         particular station ID
	 * @author mckelvym
	 * @since Feb 5, 2018
	 */
	SortedSet<T> findByStationId(Long p_StationID);
}
