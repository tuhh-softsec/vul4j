package gov.la.coastal.cims.hgms.common.db;

import java.util.Collection;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import gov.la.coastal.cims.hgms.common.db.entity.IridiumStationId;

/**
 * Repository for {@link IridiumStationId}
 *
 * @author mckelvym
 * @since Feb 8, 2018
 *
 */
public interface IridiumStationIdRepository
		extends PagingAndSortingRepository<IridiumStationId, Long>
{
	/**
	 * @param p_Imei
	 *            the IMEI to search
	 * @return stream of {@link IridiumStationId} having particular IMEI
	 * @author mckelvym
	 * @since Feb 8, 2018
	 */
	Collection<IridiumStationId> findByImei(
			@Param(value = "imei") String p_Imei);
}
