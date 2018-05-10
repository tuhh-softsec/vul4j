package gov.la.coastal.cims.hgms.harvester.structuregages.sixbitbinary;

import com.google.common.base.Strings;
import com.google.common.collect.Range;

/**
 * Utilities for strings of characters that corresponds to the 6-bit binary
 * representation.
 *
 * @author mckelvym
 * @since Jan 5, 2018
 *
 */
public class BitString
{
	/**
	 * Parses a string with 0s and 1s, that uses the 6-bit binary representation
	 * The string is pre-padded with 0s if length not divisible by 6. If the
	 * leading character is 0, then it is parsed as an integer. If leading
	 * character is 1, then 2s complement is performed before parsing as an
	 * integer.
	 *
	 * @param p_String
	 *            the string of 0s and 1s.
	 * @return the parsed value
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	public static Integer parseTwosComplement(final String p_String)
	{
		String string = p_String;
		final int length = string.length();
		if (length < 12)
		{
			string = Strings.padStart(string, 12, '0');
		}
		else if (Range.closed(12, 17).contains(length))
		{
			string = Strings.padStart(string, 18, '0');
		}

		if (!string.startsWith("1"))
		{
			return Integer.parseInt(string, 2);
		}

		final StringBuilder str = new StringBuilder(string);
		for (int i = 0; i < str.length(); i++)
		{
			if (str.charAt(i) == '0')
			{
				str.setCharAt(i, '1');
			}
			else
			{
				str.setCharAt(i, '0');
			}
		}

		for (int i = str.length() - 1; i >= 0; i--)
		{
			if (str.charAt(i) == '0')
			{
				str.setCharAt(i, '1');
				break;
			}
			str.setCharAt(i, '0');
		}

		return -Integer.parseInt(str.toString(), 2);
	}

	/**
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	private BitString()
	{
		/**
		 * nothing
		 */
	}

}
