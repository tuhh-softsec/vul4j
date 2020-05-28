package test;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Send data to a socket endpoint. Arguments: address port status(00 or 02)
 *
 * https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
 *
 * @author mckelvym
 * @author darceyj
 * @since Jan 26, 2018
 *
 */
public class Send
{
	/**
	 * Convenience method to add bytes in array to the list
	 *
	 * @param p_List
	 *            the list to use
	 * @param p_Array
	 *            the array to scan
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
	 * @param p_Args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(final String[] p_Args)
			throws IOException, InterruptedException
	{
		try (
			final Socket echoSocket = new Socket(p_Args[0],
					Integer.parseInt(p_Args[1]));
			OutputStream outputStream = echoSocket.getOutputStream();)
		{
			final byte[] bytes = Objects
					.requireNonNull(setupMessageBytes(p_Args[2]));
			outputStream.write(bytes);
			outputStream.flush();
		}
		System.out.println("Done");
	}

	/**
	 * Setup test message from bytes given the status code.
	 *
	 * @param p_Status
	 *            the status code to use.
	 * @return a byte array with the test bytes
	 * @since Feb 2, 2018
	 */
	private static byte[] setupMessageBytes(final String p_Status)
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
		final List<Byte> byteList = Lists.newArrayList();
		final List<Byte> headerBytes = Lists.newArrayList();
		final Long expected = 300234010124740L;
		final String str = Long.toString(expected);
		final String finalStr = Strings.padStart(str, 15, '0');
		final byte[] byteArray = finalStr.getBytes();
		addBytestoListFromArray(byteList, hexStringToByteArray(revNum));
		addBytestoListFromArray(byteList, hexStringToByteArray(msgLen));
		addBytestoListFromArray(byteList, hexStringToByteArray(headerIEI));
		addBytestoListFromArray(byteList, hexStringToByteArray(headerLen));
		addBytestoListFromArray(byteList, hexStringToByteArray(cdrRef));
		addBytestoListFromArray(byteList, byteArray);
		addBytestoListFromArray(byteList, hexStringToByteArray(status));
		addBytestoListFromArray(byteList, hexStringToByteArray(MOMSN));
		addBytestoListFromArray(byteList, hexStringToByteArray(MTMSN));
		addBytestoListFromArray(byteList, hexStringToByteArray(time));

		addBytestoListFromArray(headerBytes, hexStringToByteArray(revNum));
		addBytestoListFromArray(headerBytes, hexStringToByteArray(msgLen));
		addBytestoListFromArray(headerBytes, hexStringToByteArray(headerIEI));
		addBytestoListFromArray(headerBytes, hexStringToByteArray(headerLen));
		addBytestoListFromArray(headerBytes, hexStringToByteArray(cdrRef));
		addBytestoListFromArray(headerBytes, byteArray);
		addBytestoListFromArray(headerBytes, hexStringToByteArray(status));
		addBytestoListFromArray(headerBytes, hexStringToByteArray(MOMSN));
		addBytestoListFromArray(headerBytes, hexStringToByteArray(MTMSN));
		addBytestoListFromArray(headerBytes, hexStringToByteArray(time));

		final List<Byte> payLoadByteList = Lists.newArrayList();
		if (isValid)
		{
			addBytestoListFromArray(payLoadByteList,
					hexStringToByteArray(payLoadIE));
			addBytestoListFromArray(payLoadByteList,
					hexStringToByteArray(payLoadLen));
			addBytestoListFromArray(payLoadByteList, payLoadBytes.getBytes());

			addBytestoListFromArray(byteList, hexStringToByteArray(payLoadIE));
			addBytestoListFromArray(byteList, hexStringToByteArray(payLoadLen));
			addBytestoListFromArray(byteList, payLoadBytes.getBytes());
		}

		/**
		 * Setup the location information
		 */
		byteList.add(Byte.valueOf(locationInfoIE));
		addBytestoListFromArray(byteList, locationIELength);
		byteList.addAll(Arrays.asList(formatByte, latDeg, latThou[0],
				latThou[1], lonDeg, lonThou[0], lonThou[1]));
		addBytestoListFromArray(byteList, CEPRad);

		/**
		 * Setup the location information
		 */
		final List<Byte> locationInfoBytes = Lists.newArrayList();
		byteList.add(Byte.valueOf(locationInfoIE));
		addBytestoListFromArray(byteList, locationIELength);
		byteList.addAll(Arrays.asList(formatByte, latDeg, latThou[0],
				latThou[1], lonDeg, lonThou[0], lonThou[1]));
		addBytestoListFromArray(byteList, CEPRad);

		locationInfoBytes.add(Byte.valueOf(locationInfoIE));
		addBytestoListFromArray(byteList, locationIELength);
		locationInfoBytes.addAll(Arrays.asList(formatByte, latDeg, latThou[0],
				latThou[1], lonDeg, lonThou[0], lonThou[1]));
		addBytestoListFromArray(locationInfoBytes, CEPRad);

		final byte[] writeByteArray = new byte[byteList.size()];
		int c = 0;
		for (final Byte b : byteList)
		{
			writeByteArray[c] = b;
			c++;
		}
		return writeByteArray;

	}
}
