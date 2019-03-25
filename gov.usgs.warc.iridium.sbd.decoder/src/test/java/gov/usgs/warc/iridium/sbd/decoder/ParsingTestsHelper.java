package gov.usgs.warc.iridium.sbd.decoder;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Convenience class for methods in the parsing tests.
 *
 * @author darceyj
 * @since Feb 12, 2018
 *
 */
public class ParsingTestsHelper
{
	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	public static final SbdDataType	BATTERY_DATATYPE			= createDataType(
			9L, 1, "Battery", "V", null, "x * 0.234 + 10.6");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	public static final SbdDataType	FLOOD_STAGE_DATATYPE		= createDataType(
			1L, 3, "Flood Side", "ft", "Water Level", "x / 100");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	public static final SbdDataType	HUMIDITY_DATATYPE			= createDataType(
			2L, 3, "Relative Humidity", "%", null, "x");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	public static final SbdDataType	PRECIPITATION_DATATYPE		= createDataType(
			3L, 3, "Precipitation", "in", null, "x/100");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	public static final SbdDataType	PRESSURE_DATATYPE			= createDataType(
			4L, 3, "Barometric Pressure", "in of Hg", "Pressure", "x/10");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	public static final SbdDataType	PROTECTED_STAGE_DATATYPE	= createDataType(
			5L, 3, "Protected Side", "ft", "Water Level", "x / 100");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	public static final SbdDataType	TEMPERATURE_DATATYPE		= createDataType(
			6L, 3, "Air Temperature", "degC", "Temperature", "x * .18 + 32");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	public static final SbdDataType	WIND_DIRECTION_DATATYPE		= createDataType(
			7L, 3, "Wind Direction", "deg", "Wind Direction", "x");

	/**
	 * Testing {@link SbdDataType}
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	public static final SbdDataType	WIND_SPEED_DATATYPE			= createDataType(
			8L, 3, "Wind Speed", "mph", "Wind Speed", "x/10");

	/**
	 * Convenience method to add bytes in array to the list
	 *
	 * @param p_List
	 *            the list to use
	 * @param p_Array
	 *            the array to scan
	 * @since Jan 24, 2018
	 */
	private static void addBytestoListFromArray(final List<Byte> p_List,
			final byte[] p_Array)
	{
		for (final byte b : p_Array)
		{
			p_List.add(Byte.valueOf(b));
		}
	}

	/**
	 * Create an iridium data type to test with
	 *
	 * @param p_Id
	 * @param p_Bytes
	 * @param p_Name
	 * @param p_Units
	 * @param p_TypeName
	 * @param p_Transformation
	 * @return a new {@link SbdDataType}
	 * @since Feb 12, 2018
	 */
	public static SbdDataType createDataType(final long p_Id, final int p_Bytes,
			final String p_Name, final String p_Units, final String p_TypeName,
			final String p_Transformation)
	{
		final SbdDataType dt = new SbdDataType()
		{

			@Override
			public int getBytes()
			{
				return p_Bytes;
			}

			@Override
			public Long getId()
			{
				return p_Id;
			}

			@Override
			public String getName()
			{
				return p_Name;
			}

			@Override
			public String getTransformation()
			{
				return p_Transformation;
			}

			@Override
			public String getTypeName()
			{
				return p_TypeName;
			}

			@Override
			public String getUnits()
			{
				return p_Units;
			}

			@Override
			public String toString()
			{
				return MoreObjects.toStringHelper(this.getClass())
						.add("id", getId()).add("name", getName())
						.add("typeName", getTypeName()).add("units", getUnits())
						.add("transform", getTransformation())
						.add("bytes", getBytes()).toString();
			}
		};
		return dt;
	}

	/**
	 * Create a new decode order
	 *
	 * @param p_StationId
	 * @param p_ByteOffset
	 * @param p_IridiumDataType
	 * @param p_ID
	 * @return a default {@link SbdDecodeOrder}
	 * @since Feb 12, 2018
	 */
	public static SbdDecodeOrder createDecodeOrder(final long p_StationId,
			final long p_ByteOffset, final SbdDataType p_IridiumDataType,
			final long p_ID)
	{
		final SbdDecodeOrder decodeOrd = new SbdDecodeOrder()
		{

			@Override
			public long getByteOffset()
			{
				return p_ByteOffset;
			}

			@Override
			public SbdDataType getDatatype()
			{
				return p_IridiumDataType;
			}

			@Override
			public Long getId()
			{
				return p_ID;
			}

			@Override
			public Long getStationIdentifier()
			{
				return p_StationId;
			}
		};
		return decodeOrd;
	}

	/**
	 * @return a sorted {@link Set} of {@link SbdDataType}
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	public static SortedSet<SbdDataType> getDataTypeSet()
	{
		return Sets.newTreeSet(Arrays.asList(FLOOD_STAGE_DATATYPE,
				PROTECTED_STAGE_DATATYPE, WIND_SPEED_DATATYPE,
				WIND_DIRECTION_DATATYPE, TEMPERATURE_DATATYPE,
				HUMIDITY_DATATYPE, PRESSURE_DATATYPE, PRECIPITATION_DATATYPE,
				BATTERY_DATATYPE));
	}

	/**
	 *
	 * @return a sorted {@link Set} of {@link SbdDecodeOrder}
	 * @since Feb 12, 2018
	 */
	public static SortedSet<SbdDecodeOrder> getDecodeOrderSet()
	{
		long id = 1;
		final long stationId = 221L;
		final AtomicLong byteOffset = new AtomicLong(0);
		final LongUnaryOperator update = value -> value + 3;
		final List<SbdDecodeOrder> decodeList = Lists.newArrayList();
		decodeList.add(createDecodeOrder(stationId,
				byteOffset.getAndUpdate(update), FLOOD_STAGE_DATATYPE, id++));
		decodeList.add(
				createDecodeOrder(stationId, byteOffset.getAndUpdate(update),
						PROTECTED_STAGE_DATATYPE, id++));
		decodeList.add(createDecodeOrder(stationId,
				byteOffset.getAndUpdate(update), WIND_SPEED_DATATYPE, id++));
		decodeList.add(
				createDecodeOrder(stationId, byteOffset.getAndUpdate(update),
						WIND_DIRECTION_DATATYPE, id++));
		decodeList.add(createDecodeOrder(stationId,
				byteOffset.getAndUpdate(update), TEMPERATURE_DATATYPE, id++));
		decodeList.add(createDecodeOrder(stationId,
				byteOffset.getAndUpdate(update), HUMIDITY_DATATYPE, id++));
		decodeList.add(createDecodeOrder(stationId,
				byteOffset.getAndUpdate(update), PRESSURE_DATATYPE, id++));
		decodeList.add(createDecodeOrder(stationId,
				byteOffset.getAndUpdate(update), PRECIPITATION_DATATYPE, id++));
		decodeList.add(createDecodeOrder(stationId,
				byteOffset.getAndUpdate(update), BATTERY_DATATYPE, id++));
		return Sets.newTreeSet(decodeList);
	}

	/**
	 * @return list of List of Byte to use for testing
	 * @author mckelvym
	 * @since Mar 30, 2018
	 */
	public static List<List<Byte>> getTestingData()
	{
		final List<List<Byte>> inputByteLists = Lists.newArrayList();
		inputByteLists.add(ParsingTestsHelper.setupMessageBytes("00"));
		inputByteLists.add(ParsingTestsHelper.toByteList(1, 0, 77, 1, 0, 28,
				-109, -83, -72, -86, 51, 48, 48, 50, 51, 52, 48, 49, 48, 49, 50,
				52, 55, 52, 48, 0, -25, 84, 0, 0, 90, -86, 110, -25, 3, 0, 11,
				1, 29, -76, -5, 90, 9, -78, 0, 0, 0, 6, 2, 0, 29, 48, 66, 49,
				66, 64, 65, 67, 64, 65, 67, 64, 64, 70, 64, 68, 94, 64, 65, 87,
				64, 64, 65, 66, 95, 110, 64, 64, 64, 74));
		inputByteLists.add(ParsingTestsHelper.toByteList(1, 0, 77, 1, 0, 28,
				-112, 76, 119, 117, 51, 48, 48, 50, 51, 52, 48, 49, 48, 49, 50,
				52, 55, 52, 48, 0, -28, -49, 0, 0, 90, -95, -109, 95, 3, 0, 11,
				1, 29, -57, -35, 90, 9, -77, 0, 0, 0, 7, 2, 0, 29, 48, 66, 49,
				66, 64, 64, 82, 64, 64, 84, 64, 64, 118, 64, 67, 102, 64, 66,
				82, 64, 64, 64, 66, 95, 106, 64, 64, 121, 77));
		return inputByteLists;
	}

	/**
	 * @return list of List of Byte to use for testing
	 * @author mckelvym
	 * @since Mar 30, 2018
	 */
	public static List<List<Byte>> getTestingDataBadPayload()
	{
		final List<List<Byte>> inputByteLists = Lists.newArrayList();
		/**
		 * java.lang.NumberFormatException: For input string:
		 * "-10001-10001-10001"
		 */
		inputByteLists.add(ParsingTestsHelper.toByteList(1, 0, 61, 1, 0, 28,
				-101, 26, 31, 27, 51, 48, 48, 50, 51, 52, 48, 49, 48, 49, 50,
				53, 55, 52, 48, 0, -100, 64, 0, 0, 90, -69, 75, -24, 3, 0, 11,
				1, 29, -52, 16, 90, 29, 33, 0, 0, 0, 3, 2, 0, 13, 50, 66, 50,
				65, 64, 66, 117, 47, 47, 47, 64, 64, 104));
		inputByteLists.add(ParsingTestsHelper.toByteList(1, 0, 77, 1, 0, 28,
				-99, -89, -41, 76, 51, 48, 48, 50, 51, 52, 48, 49, 48, 49, 50,
				52, 55, 52, 48, 0, -19, -39, 0, 0, 90, -63, 90, -89, 3, 0, 11,
				1, 29, -71, 46, 90, 19, 103, 0, 0, 0, 3, 2, 0, 29, 48, 66, 49,
				66, 47, 47, 47, 47, 47, 47, 64, 64, 70, 64, 65, 109, 47, 47, 47,
				47, 47, 47, 66, 94, 119, 64, 65, 80, 77));
		return inputByteLists;
	}

	/**
	 * @return list of List of Byte to use for testing
	 * @author mckelvym
	 * @since Apr 2, 2018
	 */
	public static List<List<Byte>> getTestingDataShortPayload()
	{
		final List<List<Byte>> inputByteLists = Lists.newArrayList();
		/**
		 * java.lang.IndexOutOfBoundsException: Invalid start index. (x) must be
		 * less than size (y)
		 */
		inputByteLists.add(toByteList(1, 0, 61, 1, 0, 28, -101, 24, -84, -16,
				51, 48, 48, 50, 51, 52, 48, 49, 48, 49, 50, 53, 55, 52, 48, 0,
				-100, 63, 0, 0, 90, -69, 72, -50, 3, 0, 11, 1, 29, -45, 102, 90,
				29, 33, 0, 0, 0, 4, 2, 0, 13, 50, 66, 50, 67, 64, 66, 116, 64,
				66, 118));
		return inputByteLists;
	}

	/**
	 * Taken from
	 * https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
	 *
	 * @param p_InputStr
	 *            the input string
	 * @return the byte array converted from hex to binary
	 * @since Jan 10, 2018
	 */
	public static byte[] hexStringToByteArray(final String p_InputStr)
	{
		final int len = p_InputStr.length();
		final byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2)
		{
			data[i / 2] = (byte) ((Character.digit(p_InputStr.charAt(i),
					16) << 4) + Character.digit(p_InputStr.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Setup message from bytes given the status code.
	 *
	 * @param p_Status
	 *            the status code to use.
	 * @return a list of bytes representing a successful directip message
	 * @since Feb 2, 2018
	 */
	public static List<Byte> setupMessageBytes(final String p_Status)
	{
		final boolean isValid = p_Status.equals("00") || p_Status.equals("02");
		final String revNum = "01";
		final String msgLen = isValid ? "0050" : "002D";
		final String headerIEI = "01";
		final String headerLen = "001C";
		final String cdrRef = "0012D687";
		final String status = p_Status;
		final String MOMSN = "D431";
		final String MTMSN = "3039";
		final String time = "43B539E1";
		final String payLoadIE = "02";
		final String payLoadLen = "001D";
		final String payLoadBytes = "0B1B??T??\\@AB@@@@@i@@@B`e@@\\N";
		final byte locationInfoIE = 0x03;
		final byte[] locationIELength = hexStringToByteArray("000B");
		final byte[] latThou = hexStringToByteArray("0000");
		final byte[] lonThou = hexStringToByteArray("EA5F");
		final byte lonDeg = 0x41;
		final byte formatByte = 0x00;
		final byte latDeg = 0x7D;
		final byte[] CEPRad = hexStringToByteArray("000007D0");

		final Long expected = 300234010124740L;
		final String str = Long.toString(expected);
		final String finalStr = Strings.padStart(str, 15, '0');
		final byte[] imeiByteArray = finalStr.getBytes();
		final List<Byte> testingByteList = Lists.newArrayList();

		addBytestoListFromArray(testingByteList, hexStringToByteArray(revNum));
		addBytestoListFromArray(testingByteList, hexStringToByteArray(msgLen));
		addBytestoListFromArray(testingByteList,
				hexStringToByteArray(headerIEI));
		addBytestoListFromArray(testingByteList,
				hexStringToByteArray(headerLen));
		addBytestoListFromArray(testingByteList, hexStringToByteArray(cdrRef));
		addBytestoListFromArray(testingByteList, imeiByteArray);
		addBytestoListFromArray(testingByteList, hexStringToByteArray(status));
		addBytestoListFromArray(testingByteList, hexStringToByteArray(MOMSN));
		addBytestoListFromArray(testingByteList, hexStringToByteArray(MTMSN));
		addBytestoListFromArray(testingByteList, hexStringToByteArray(time));

		if (isValid)
		{

			addBytestoListFromArray(testingByteList,
					hexStringToByteArray(payLoadIE));
			addBytestoListFromArray(testingByteList,
					hexStringToByteArray(payLoadLen));
			addBytestoListFromArray(testingByteList, payLoadBytes.getBytes());
		}

		/**
		 * Setup the location information
		 */
		testingByteList.add(Byte.valueOf(locationInfoIE));
		addBytestoListFromArray(testingByteList, locationIELength);
		testingByteList.addAll(Arrays.asList(formatByte, latDeg, latThou[0],
				latThou[1], lonDeg, lonThou[0], lonThou[1]));
		addBytestoListFromArray(testingByteList, CEPRad);

		/**
		 * Setup the location information
		 */
		testingByteList.add(Byte.valueOf(locationInfoIE));
		addBytestoListFromArray(testingByteList, locationIELength);
		testingByteList.addAll(Arrays.asList(formatByte, latDeg, latThou[0],
				latThou[1], lonDeg, lonThou[0], lonThou[1]));
		addBytestoListFromArray(testingByteList, CEPRad);
		return testingByteList;

	}

	/**
	 * Convert a {@link Byte} {@link List} to a {@code byte} array
	 *
	 * @param p_Bytes
	 *            {@link Byte} {@link List}
	 * @return {@code byte} array
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	public static byte[] toByteArray(final List<Byte> p_Bytes)
	{
		final byte[] array = new byte[p_Bytes.size()];
		IntStream.range(0, array.length)
				.forEach(i -> array[i] = p_Bytes.get(i));
		return array;
	}

	/**
	 * Transform integers to a byte list
	 *
	 * @param p_Values
	 *            the integer values to transform
	 * @return a list of byte
	 * @author mckelvym
	 * @since Mar 30, 2018
	 */
	private static List<Byte> toByteList(final Integer... p_Values)
	{
		return Arrays.asList(p_Values).stream().map(v -> v.byteValue())
				.collect(Collectors.toList());
	}

}
