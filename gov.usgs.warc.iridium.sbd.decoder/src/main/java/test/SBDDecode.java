package test;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import gov.la.coastal.cims.hgms.harvester.structuregages.sixbitbinary.Decode;

/**
 * http://www.sutron.com/documents/xlite-9210b-user-manual-3.pdf
 *
 * @author mckelvym
 * @since Jan 4, 2018
 *
 */
public class SBDDecode
{
	/**
	 *
	 * @param p_Args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(final String[] p_Args)
			throws IOException, InterruptedException
	{
		final String path = p_Args[0];
		final List<File> files = Lists.newArrayList(new File(path)
				.listFiles(file -> file.getName().endsWith(".sbd")));
		Collections.sort(files);
		for (final File file : files)
		{
			/**
			 * Read bytes from file...
			 */
			final List<Byte> bytes = Lists.newArrayList();
			final ByteSource byteSource = Files.asByteSource(file);
			for (final byte value : byteSource.read())
			{
				bytes.add(value);
			}

			System.out.println(String.format("%n%s, bytes: %s", file.getName(),
					bytes.size()));

			/**
			 * First byte is a char payload IEI
			 */
			final char payloadIEI = (char) bytes.remove(0).byteValue();
			System.out.println(String.format("Payload IEI: %s", payloadIEI));

			/**
			 * Next 2 bytes are unsigned short.
			 */
			final ByteBuffer payloadLengthBuffer = ByteBuffer.allocate(2)
					.order(ByteOrder.BIG_ENDIAN);
			payloadLengthBuffer.put(bytes.remove(0));
			payloadLengthBuffer.put(bytes.remove(0));
			final int payloadLength = payloadLengthBuffer.getShort(0);
			System.out.println(
					String.format("Payload length: %s", payloadLength));

			/**
			 * Remaining bytes correspond to sensor values.
			 */

			System.out.println(String.format("wind speed: %s",
					Decode.valueAtIndex(bytes, 7, 3, 10)));

			System.out.println(String.format("wind direction: %s",
					Decode.valueAtIndex(bytes, 10, 3, 1)));

			System.out.println(String.format("flood side: %s",
					Decode.valueAtIndex(bytes, 1, 3, 100)));

			System.out.println(String.format("protected side: %s",
					Decode.valueAtIndex(bytes, 4, 3, 100)));

			System.out.println(String.format("temperature: %s",
					Decode.valueAtIndex(bytes, 13, 3, 1) * .18 + 32));

			System.out.println(String.format("humidity: %s",
					Decode.valueAtIndex(bytes, 16, 3, 1)));

			System.out.println(String.format("pressure: %s",
					Decode.valueAtIndex(bytes, 19, 3, 10)));

			System.out.println(String.format("precipitation: %s",
					Decode.valueAtIndex(bytes, 22, 3, 100)));

			System.out.println(String.format("battery: %s",
					Decode.valueAtIndex(bytes, 25, 1, 1) * 0.234 + 10.6));

			/**
			 * Print a string that contains the 3-byte "raw" 6-bit decoded
			 * values
			 */
			final int byteCount = 3;
			final List<String> strings = Lists.newArrayList();
			for (int i = bytes.size() - 1; i >= 0; i -= byteCount)
			{
				strings.add(String.valueOf(Decode.valueFromBytes(
						bytes.subList(Math.max(i - byteCount, 0), i))));
			}
			Collections.reverse(strings);
			System.out
					.println(strings.stream().collect(Collectors.joining(" ")));
		}
	}
}
