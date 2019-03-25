package gov.usgs.warc.iridium.sbd.domain;

import java.util.SortedSet;

/**
 * Repository for {@link SbdDataType}
 *
 * @author mckelvym
 * @param <T>
 *            class that implements {@link SbdDataType}
 * @since Mar 22, 2019
 *
 */
public interface SbdDataTypeProvider<T extends SbdDataType>
{
	/**
	 * @param p_StationID
	 *            the station ID
	 * @return the sorted set of {@link SbdDataType} (by
	 *         {@link SbdDataType#compareTo(SbdDataType)} for a particular
	 *         station ID
	 * @author mckelvym
	 * @since Mar 22, 2018
	 */
	SortedSet<T> findByStationId(Long p_StationID);
}
