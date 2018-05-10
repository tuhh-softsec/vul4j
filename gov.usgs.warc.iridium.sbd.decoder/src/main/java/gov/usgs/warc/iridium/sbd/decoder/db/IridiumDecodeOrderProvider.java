package gov.usgs.warc.iridium.sbd.decoder.db;

import java.util.SortedSet;

import org.springframework.data.repository.query.Param;

import gov.usgs.warc.iridium.sbd.decoder.db.entity.IridiumDecodeOrder;

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
	SortedSet<T> findByStationId(@Param(value = "id") Long p_StationID);
}
