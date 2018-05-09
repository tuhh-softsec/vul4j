package gov.la.coastal.cims.hgms.harvester.structuregages.directip;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.la.coastal.cims.hgms.common.HarvestToHgmsData;
import gov.la.coastal.cims.hgms.common.db.DataId;
import gov.la.coastal.cims.hgms.common.db.entity.IridiumDataType;
import gov.la.coastal.cims.hgms.common.db.entity.IridiumStationId;
import gov.la.coastal.cims.hgms.common.db.entity.PressureData;
import gov.la.coastal.cims.hgms.common.db.entity.TemperatureData;
import gov.la.coastal.cims.hgms.common.db.entity.WaterLevelData;
import gov.la.coastal.cims.hgms.common.db.entity.WindData;
import gov.la.coastal.cims.hgms.harvester.structuregages.Properties;
import gov.la.coastal.cims.hgms.harvester.structuregages.parser.StationDataTypes;

/**
 * Take in an {@link IridiumResponse}, convert to correct HGMS units and push to
 * the HGMS database
 *
 * @author darceyj
 * @since Feb 15, 2018
 *
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IridiumToHgmsData extends HarvestToHgmsData<IridiumResponse>
{
	/**
	 * Class logger.
	 *
	 * @author mckelvym
	 * @since Mar 3, 2018
	 */
	private static final Logger	log			= LoggerFactory
			.getLogger(IridiumToHgmsData.class);

	/**
	 * The wind datatype key into the multimap
	 *
	 * @since Feb 22, 2018
	 */
	private static final String	WIND_KEY	= "Wind";

	/**
	 * Default Constructor
	 *
	 * @param p_Properties
	 *            the {@link Properties}
	 * @since Feb 15, 2018
	 */
	public IridiumToHgmsData(final Properties p_Properties)
	{
		super(p_Properties.getHgmsProviderId());
	}

	/**
	 *
	 * Clear the pending data lists.
	 *
	 * @since Mar 1, 2018
	 */
	public void clear()
	{
		getWaterLevel().clear();
		getWind().clear();
		getTemperature().clear();
		getPressure().clear();
		getTide().clear();
		getDischarge().clear();

	}

	@Override
	public void ingest(final IridiumResponse p_Response)
	{
		final IridiumResponse response = requireNonNull(p_Response);
		final Collection<IridiumStationId> stations = response.getStations();
		checkState(!stations.isEmpty(),
				"Error finding the stations related to the iridium station");
		final Table<IridiumStationId, IridiumDataType, Double> values = response
				.getValues();

		stations.forEach(station ->
		{
			final Map<IridiumDataType, Double> map = values.rowMap()
					.get(station);
			final List<IridiumDataType> iridiumDTList = map.keySet().stream()
					.filter(datatype -> datatype.getType() != null)
					.collect(Collectors.toList());
			final Multimap<String, IridiumDataType> multimap = ArrayListMultimap
					.create();

			final Map<String, StationDataTypes> nameToEnum = StationDataTypes
					.getNameToEnum();
			iridiumDTList.forEach(datatype ->
			{

				final StationDataTypes stationDataType = nameToEnum
						.get(datatype.getType().getName());
				switch (stationDataType)
				{
					case AIR_TEMPERATURE:
						multimap.put(
								StationDataTypes.AIR_TEMPERATURE.toString(),
								datatype);
						break;
					case BAROMETRIC_PRESSURE:
						multimap.put(
								StationDataTypes.BAROMETRIC_PRESSURE.toString(),
								datatype);
						break;
					case WATER_LEVEL:
						multimap.put(StationDataTypes.WATER_LEVEL.toString(),
								datatype);
						break;
					case WIND_DIRECTION:
						multimap.put(WIND_KEY, datatype);
						break;
					case WIND_SPEED:
						multimap.put(WIND_KEY, datatype);
						break;
					default:
						break;

				}

			});
			for (final String key : multimap.keySet())
			{
				final List<IridiumDataType> datatypes = (List<IridiumDataType>) multimap
						.get(key);

				final AtomicReference<ZonedDateTime> now = new AtomicReference<>(
						ZonedDateTime.now());
				final Map<IridiumDataType, Double> mapOfIridiumDTToValues = datatypes
						.stream().collect(Collectors.toMap(k -> k, map::get));
				try
				{
					/**
					 * Find the proper list to put the value in
					 */
					final DataId dId = DataId.create(
							station.getStation().getId(), now.getAndUpdate(
									t -> t.plus(1, ChronoUnit.MILLIS)));

					final ZonedDateTime valueTime = response.getMessage()
							.getHeader().getZonedTimeFromSession();

					for (final IridiumDataType dataType : mapOfIridiumDTToValues
							.keySet())
					{
						final Double value = mapOfIridiumDTToValues
								.get(dataType);
						final StationDataTypes hgmsDataType = nameToEnum
								.get(dataType.getType().getName());
						switch (hgmsDataType)
						{
							case AIR_TEMPERATURE:
								getTemperature().add(TemperatureData.create(dId,
										valueTime, value));
								break;
							case BAROMETRIC_PRESSURE:
								getPressure().add(PressureData.create(dId,
										valueTime, value));
								break;
							case WATER_LEVEL:
								getWaterLevel().add(WaterLevelData.create(dId,
										valueTime, value));
								break;
							case WIND_DIRECTION:
								/**
								 * Do nothing
								 */
								break;
							case WIND_SPEED:
								/**
								 * Do nothing
								 */
								break;
							default:
								throw new Exception(
										"No valid value found. Unable to map iridium datatype to hgms station datatype.  ");

						}
					}
					if (key.equals(WIND_KEY))
					{
						final IridiumDataType windDirectionType = mapOfIridiumDTToValues
								.keySet().stream()
								.filter(dt -> dt.getType().getName()
										.equals(StationDataTypes.WIND_DIRECTION
												.toString()))
								.findFirst().orElseThrow(() -> new Exception(
										"Unable to find wind direction datatype."));
						final IridiumDataType windSpeedType = mapOfIridiumDTToValues
								.keySet().stream()
								.filter(dt -> dt.getType().getName().equals(
										StationDataTypes.WIND_SPEED.toString()))
								.findFirst().orElseThrow(() -> new Exception(
										"Unable to find wind speed datatype."));
						final Double windDirectionVal = mapOfIridiumDTToValues
								.get(windDirectionType);
						final Double windSpeedVal = mapOfIridiumDTToValues
								.get(windSpeedType);
						if (windDirectionVal != null && windSpeedVal != null)
						{
							getWind().add(WindData.create(dId,
									response.getMessage().getHeader()
											.getZonedTimeFromSession(),
									windSpeedVal, windDirectionVal));
						}
					}
				}
				catch (final Exception e)
				{
					log.warn(e.getMessage());
					return;
				}

			}
		});
	}
}
