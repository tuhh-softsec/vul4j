package gov.la.coastal.cims.hgms.harvester.structuregages;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import gov.la.coastal.cims.hgms.common.db.entity.IridiumDataType;
import gov.la.coastal.cims.hgms.common.db.entity.IridiumDecodeOrder;
import gov.la.coastal.cims.hgms.common.db.entity.Provider;
import gov.la.coastal.cims.hgms.common.db.entity.Station;
import gov.la.coastal.cims.hgms.common.db.entity.Type;

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
	 * Convenience method to add bytes in array to the list
	 *
	 * @param p_List
	 *            - the list to use
	 * @param p_Array
	 *            - the array to scan
	 * @since Jan 24, 2018
	 */
	public static void addBytestoListFromArray(final List<Byte> p_List,
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
	 * @return a new {@link IridiumDataType}
	 * @since Feb 12, 2018
	 */
	public static IridiumDataType createDataType()
	{
		final IridiumDataType dt = new IridiumDataType();
		dt.setId(221L);
		dt.setBytes(3);
		dt.setType(null);
		return dt;
	}

	/**
	 * Create a new decode order
	 *
	 * @param p_TestStation
	 *            - the test station to use.
	 *
	 * @return a default {@link IridiumDecodeOrder}
	 * @since Feb 12, 2018
	 */
	public static IridiumDecodeOrder createDecodeOrder(
			final Station p_TestStation)
	{
		final IridiumDecodeOrder decodeOrd = new IridiumDecodeOrder();
		decodeOrd.setId(221L);
		decodeOrd.setStation(p_TestStation);
		return decodeOrd;
	}

	/**
	 * @return a new instance for testing
	 * @author mckelvym
	 * @since Feb 2, 2018
	 */
	public static Station createStation()
	{
		final Station item = new Station();
		item.setId(1L);
		item.setLocation(
				new GeometryFactory().createPoint(new Coordinate(0, 1)));
		item.setMetadata("metadata");
		item.setName("name");
		final Provider provider = newProvider();
		provider.setId(1L);
		item.setProvider(provider);
		item.setStationId("station-id");
		item.setTypeFlags(1);
		item.setDataHealthLimitHealthy(30);
		item.setDataHealthLimitStale(60);
		return item;
	}

	/**
	 * Create a new {@link Type} with the given name and id.
	 *
	 * @param p_Id
	 *            - the id to use
	 * @param p_Name
	 *            - the name to use
	 * @return the new {@link Type}
	 * @since Feb 23, 2018
	 */
	public static Type createType(final Long p_Id, final String p_Name)
	{
		final Type item = new Type();
		item.setId(p_Id);
		item.setName(p_Name);
		return item;
	}

	/**
	 *
	 * @return a list of {@link IridiumDecodeOrder}
	 * @since Feb 12, 2018
	 */
	public static List<IridiumDecodeOrder> getDecodeList()
	{
		final Station stationTest = createStation();

		final IridiumDataType fsDataType = createDataType();
		fsDataType.setName("Flood Side");
		fsDataType.setUnits("ft");
		fsDataType.setTransformation("x / 100");
		final Type fsType = createType(1L, "Water Level");
		fsDataType.setType(fsType);
		final IridiumDataType psDataType = createDataType();
		psDataType.setName("Protected Side");
		psDataType.setUnits("ft");
		psDataType.setTransformation("x / 100");
		final Type wsType = createType(2L, "Wind Speed");
		final IridiumDataType windSpeedDataType = createDataType();
		windSpeedDataType.setName("Wind Speed");
		windSpeedDataType.setUnits("mph");
		windSpeedDataType.setTransformation("x/10");
		windSpeedDataType.setType(wsType);
		final IridiumDataType windDirectionDataType = createDataType();
		final Type wdType = createType(3L, "Wind Direction");
		windDirectionDataType.setName("Wind Direction");
		windDirectionDataType.setUnits("mph");
		windDirectionDataType.setTransformation("x");
		windDirectionDataType.setType(wdType);
		final IridiumDataType temperatureDataType = createDataType();
		final Type tempType = createType(4L, "Temperature");
		temperatureDataType.setName("Air Temperature");
		temperatureDataType.setTransformation("x * .18 + 32");
		temperatureDataType.setUnits("degC");
		temperatureDataType.setType(tempType);

		final IridiumDataType humidityDataType = createDataType();
		humidityDataType.setName("Relative Humidity");
		humidityDataType.setTransformation("x");
		humidityDataType.setUnits("%");
		final IridiumDataType bpDataType = createDataType();
		final Type pressureType = createType(5L, "Pressure");
		bpDataType.setName("Barometric Pressure");
		bpDataType.setTransformation("x/10");
		bpDataType.setUnits("in of Hg");
		bpDataType.setType(pressureType);
		final IridiumDataType precip = createDataType();
		precip.setName("Precipitation");
		precip.setTransformation("x/100");
		precip.setUnits("in");
		final IridiumDataType batteryDT = createDataType();
		batteryDT.setName("Battery");
		batteryDT.setTransformation("x * 0.234 + 10.6");
		batteryDT.setUnits("V");
		batteryDT.setBytes(1);
		final List<IridiumDecodeOrder> decodeList = Lists.newArrayList();
		IridiumDecodeOrder order = createDecodeOrder(stationTest);
		order.setDatatype(fsDataType);
		order.setByteOffset(0L);

		decodeList.add(order);
		order = createDecodeOrder(stationTest);
		order.setDatatype(psDataType);
		order.setByteOffset(3L);
		decodeList.add(order);
		order = createDecodeOrder(stationTest);
		order.setDatatype(windSpeedDataType);
		order.setByteOffset(6L);
		decodeList.add(order);
		order = createDecodeOrder(stationTest);
		order.setDatatype(windDirectionDataType);
		order.setByteOffset(9L);
		decodeList.add(order);
		order = createDecodeOrder(stationTest);
		order.setDatatype(temperatureDataType);
		order.setByteOffset(12L);
		decodeList.add(order);
		order = createDecodeOrder(stationTest);
		order.setDatatype(humidityDataType);
		order.setByteOffset(15L);
		decodeList.add(order);
		order = createDecodeOrder(stationTest);
		order.setDatatype(bpDataType);
		order.setByteOffset(18L);
		decodeList.add(order);
		order = createDecodeOrder(stationTest);
		order.setDatatype(precip);
		order.setByteOffset(21L);
		decodeList.add(order);
		order = createDecodeOrder(stationTest);
		order.setDatatype(batteryDT);
		order.setByteOffset(24L);
		decodeList.add(order);
		return decodeList;

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
		 * java.lang.IndexOutOfBoundsException: Invalid start index. (12) must
		 * be less than size (9)
		 */
		inputByteLists.add(toByteList(1, 0, 61, 1, 0, 28, -101, 24, -84, -16,
				51, 48, 48, 50, 51, 52, 48, 49, 48, 49, 50, 53, 55, 52, 48, 0,
				-100, 63, 0, 0, 90, -69, 72, -50, 3, 0, 11, 1, 29, -45, 102, 90,
				29, 33, 0, 0, 0, 4, 2, 0, 13, 50, 66, 50, 67, 64, 66, 116, 64,
				66, 118, 64, 64, 115));
		return inputByteLists;
	}

	/**
	 * Taken from
	 * https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
	 *
	 * @param p_InputStr
	 *            - the input string
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
	 * @return a new {@link Provider} instance for testing
	 * @author mckelvym
	 * @since Dec 1, 2017
	 */
	protected static Provider newProvider()
	{
		final Provider p = new Provider();
		p.setFetchInterval(1);
		p.setName("test");
		p.setUrl("url");
		p.setUrlTemplate("url-template");
		return p;
	}

	/**
	 * Setup message from bytes given the status code.
	 *
	 * @param p_Status
	 *            - the status code to use.
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
	 * Setup message from bytes given the status code.
	 *
	 * @param p_Status
	 *            - the status code to use.
	 * @return a byte array of the message bytes
	 * @since Feb 2, 2018
	 */
	public static byte[] setupMessageBytesAsArray(final String p_Status)
	{
		final List<Byte> testingByteList = setupMessageBytes(p_Status);

		final byte[] returnedArray = new byte[testingByteList.size()];
		int c = 0;
		for (final Byte b : testingByteList)
		{
			returnedArray[c] = b.byteValue();
			c++;
		}
		return returnedArray;

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
	public static List<Byte> toByteList(final Integer... p_Values)
	{
		return Arrays.asList(p_Values).stream().map(v -> v.byteValue())
				.collect(Collectors.toList());
	}

}
