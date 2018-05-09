package gov.la.coastal.cims.hgms.common.db;

import java.util.SortedSet;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import gov.la.coastal.cims.hgms.common.db.entity.IridiumDecodeOrder;

/**
 * Repository for {@link IridiumDecodeOrder}
 *
 * @author mckelvym
 * @since Feb 2, 2018
 *
 */
public interface IridiumDecodeOrderRepository
		extends PagingAndSortingRepository<IridiumDecodeOrder, Long>
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
	SortedSet<IridiumDecodeOrder> findByStationId(
			@Param(value = "id") Long p_StationID);
}
