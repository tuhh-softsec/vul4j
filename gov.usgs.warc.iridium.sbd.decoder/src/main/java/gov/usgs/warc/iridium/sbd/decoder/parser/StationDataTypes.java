package gov.usgs.warc.iridium.sbd.decoder.parser;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Data types that can be recorded by the iridium station
 *
 * @author darceyj
 * @since Jan 11, 2018
 *
 */
public enum StationDataTypes
{
	/**
	 * Air temperature
	 *
	 * @author darceyj
	 * @since Feb 16, 2018
	 */
	AIR_TEMPERATURE
	{
		@Override
		public String toString()
		{
			return "Temperature";
		}
	},
	/**
	 * Barometric pressure
	 *
	 * @author darceyj
	 * @since Feb 16, 2018
	 */
	BAROMETRIC_PRESSURE
	{
		@Override
		public String toString()
		{
			return "Pressure";
		}
	},
	/**
	 * Water level / stage
	 *
	 * @author darceyj
	 * @since Feb 16, 2018
	 *
	 */
	WATER_LEVEL
	{
		@Override
		public String toString()
		{
			return "Water Level";
		}
	},
	/**
	 * Wind direction
	 *
	 * @author darceyj
	 * @since Feb 16, 2018
	 */
	WIND_DIRECTION
	{
		@Override
		public String toString()
		{
			return "Wind Direction";
		}
	},
	/**
	 * Wind speed
	 *
	 * @author darceyj
	 * @since Feb 16, 2018
	 *
	 */
	WIND_SPEED
	{
		@Override
		public String toString()
		{
			return "Wind Speed";
		}
	};

	/**
	 * @return the map of name to enum
	 * @since Feb 16, 2018
	 */
	public static Map<String, StationDataTypes> getNameToEnum()
	{
		return Arrays.stream(StationDataTypes.values()).collect(Collectors
				.toMap(StationDataTypes::toString, Function.identity()));
	}
}
