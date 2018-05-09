package gov.la.coastal.cims.hgms.common.db;

import org.springframework.data.repository.PagingAndSortingRepository;

import gov.la.coastal.cims.hgms.common.db.entity.IridiumDataType;

/**
 * Repository for {@link IridiumDataType}
 *
 * @author mckelvym
 * @since Feb 2, 2018
 *
 */
public interface IridiumDataTypeRepository
		extends PagingAndSortingRepository<IridiumDataType, Long>
{
	/**
	 * Nothing here
	 */
}
