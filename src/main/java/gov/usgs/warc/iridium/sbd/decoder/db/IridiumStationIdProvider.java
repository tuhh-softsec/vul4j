package gov.usgs.warc.iridium.sbd.decoder.db;

import java.util.Collection;

import org.springframework.data.repository.query.Param;

import gov.usgs.warc.iridium.sbd.decoder.db.entity.IridiumStationId;

/**
 * Repository for {@link IridiumStationId}
 *
 * @author mckelvym
 * @param <T>
 *            class that implements {@link IridiumStationId}
 * @since Feb 8, 2018
 *
 */
public interface IridiumStationIdProvider<T extends IridiumStationId>
{
	/**
	 * @param p_Imei
	 *            the IMEI to search
	 * @return stream of {@link IridiumStationId} having particular IMEI
	 * @author mckelvym
	 * @since Feb 8, 2018
	 */
	Collection<T> findByImei(@Param(value = "imei") String p_Imei);
}
