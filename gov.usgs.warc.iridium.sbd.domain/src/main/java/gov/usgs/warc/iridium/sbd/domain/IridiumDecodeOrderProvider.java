package gov.usgs.warc.iridium.sbd.domain;

import java.util.SortedSet;

/**
 * Repository for {@link IridiumDecodeOrder}
 *
 * @author mckelvym
 * @param <T>
 *            class that implements {@link IridiumDecodeOrder}
 * @since Feb 2, 2018
 *
 */
public interface IridiumDecodeOrderProvider<T extends IridiumDecodeOrder>
{
	/**
	 * @param p_StationID
	 *            the station ID
	 * @return the sorted set of {@link IridiumDecodeOrder} (by
	 *         {@link IridiumDecodeOrder#compareTo(IridiumDecodeOrder)} for a
	 *         particular station ID
	 * @author mckelvym
	 * @since Feb 5, 2018
	 */
	SortedSet<T> findByStationId(Long p_StationID);
}
