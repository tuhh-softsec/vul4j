package gov.la.coastal.cims.hgms.harvester.structuregages.sixbitbinary;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

import com.google.common.base.Strings;
import com.google.common.collect.Range;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Decoding utilities for 6-bit binary encoded format.
 *
 * @author mckelvym
 * @since Jan 5, 2018
 *
 */
public final class Decode
{

	/**
	 * Uses {@link #valueFromBytes(List)} to decode a value at the specified
	 * start index and amount of bytes from that index. Returns the decoded
	 * value divided by the provided divisor.
	 *
	 * @param p_Bytes
	 *            the bytes to sublist from
	 * @param p_StartIndex
	 *            the start index into the provided bytes
	 * @param p_NumBytes
	 *            the size from start index into the provided bytes
	 * @param p_Divisor
	 *            the divisor to apply to the returned value
	 * @return the value at the provided start index and num bytes from the
	 *         provided byte list with divisor applied.
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	public static Float valueAtIndex(final List<Byte> p_Bytes,
			final int p_StartIndex, final int p_NumBytes, final float p_Divisor)
	{
		checkNotNull(p_Bytes);
		checkElementIndex(p_StartIndex, p_Bytes.size(), "Invalid start index.");
		checkPositionIndex(p_StartIndex + p_NumBytes, p_Bytes.size(),
				"Invalid number of bytes considering start index");
		checkArgument(p_NumBytes > 0, "Num bytes must be greater than 0.");
		checkArgument(Float.compare(p_Divisor, 0) != 0, "Divisor cannot be 0.");

		return valueFromBytes(
				p_Bytes.subList(p_StartIndex, p_StartIndex + p_NumBytes))
						.floatValue()
				/ p_Divisor;
	}

	/**
	 * Decode 6-bit binary encoded format
	 * (http://www.sutron.com/documents/xlite-9210b-user-manual-3.pdf)
	 *
	 * @param p_Bytes
	 *            the bytes to decode (1 - 3)
	 * @return the decoded number
	 * @author darceyj
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	public static Integer valueFromBytes(final List<Byte> p_Bytes)
	{
		checkNotNull(p_Bytes);
		checkArgument(Range.closed(1, 3).contains(p_Bytes.size()),
				"Number of bytes should be 1-3.");
		p_Bytes.forEach(
				b -> checkArgument(b >= 63, "Minimum byte value must be 63"));

		final Integer[] asciiNum = new Integer[p_Bytes.size()];
		/**
		 * For each ascii number subtract 64 from it
		 */
		for (int i = 0; i < p_Bytes.size(); i++)
		{
			if (p_Bytes.get(i) == 63)
			{
				asciiNum[i] = (int) p_Bytes.get(i);
			}
			else
			{
				asciiNum[i] = p_Bytes.get(i) - 64;
			}
		}

		/**
		 * Using the new ascii decimal, turn into 6 bit binary, add padding 0s
		 * if needed. Concatenating the binary to one 18 bit binary string
		 */
		final String byteString = Arrays.stream(asciiNum)
				.map(n -> Integer.toString(n, 2))
				.map(n -> Strings.padStart(n, 6, '0'))
				.collect(Collectors.joining());
		/**
		 * Parse the binary string as an int
		 */

		final int num = BitString.parseTwosComplement(byteString);
		return num;
	}

}
