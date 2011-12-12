package org.esigate.http;

import java.text.ParseException;
import java.util.Date;

import org.apache.http.impl.cookie.DateParseException;


public final class DateUtils {
	
	/**
     * Parses a date value.  The formats used for parsing the date value are retrieved from
     * the default http params.
     *
     * @param dateValue the date value to parse
     *
     * @return the parsed date
     *
     * @throws ParseException if the value could not be parsed using any of the
     * supported date formats
     */
    public static Date parseDate(String dateValue) throws ParseException {
        return parseDate(dateValue, null, null);
    }

    /**
     * Parses the date value using the given date formats.
     *
     * @param dateValue the date value to parse
     * @param dateFormats the date formats to use
     *
     * @return the parsed date
     *
     * @throws ParseException if none of the dataFormats could parse the dateValue
     */
    public static Date parseDate(final String dateValue, String[] dateFormats)
        throws ParseException {
        return parseDate(dateValue, dateFormats, null);
    }

    /**
     * Parses the date value using the given date formats.
     *
     * @param dateValue the date value to parse
     * @param dateFormats the date formats to use
     * @param startDate During parsing, two digit years will be placed in the range
     * <code>startDate</code> to <code>startDate + 100 years</code>. This value may
     * be <code>null</code>. When <code>null</code> is given as a parameter, year
     * <code>2000</code> will be used.
     *
     * @return the parsed date
     *
     * @throws ParseException if none of the dataFormats could parse the dateValue
     */
    public static Date parseDate(
        String dateValue,
        String[] dateFormats,
        Date startDate
    ) throws ParseException {
    	
    	try {
			return org.apache.http.impl.cookie.DateUtils.parseDate(dateValue, dateFormats, startDate);
		} catch (DateParseException e) {
			throw new ParseException(e.getMessage(), 0);
		}
    }

    /**
     * Formats the given date according to the RFC 1123 pattern.
     *
     * @param date The date to format.
     * @return An RFC 1123 formatted date string.
     *
     * @see org.apache.http.impl.cookie.DateUtils.PATTERN_RFC1123
     */
    public static String formatDate(Date date) {
        return org.apache.http.impl.cookie.DateUtils.formatDate(date);
    }

    /**
     * Formats the given date according to the specified pattern.  The pattern
     * must conform to that used by the {@link SimpleDateFormat simple date
     * format} class.
     *
     * @param date The date to format.
     * @param pattern The pattern to use for formatting the date.
     * @return A formatted date string.
     *
     * @throws IllegalArgumentException If the given date pattern is invalid.
     *
     * @see SimpleDateFormat
     */
    public static String formatDate(Date date, String pattern) {
        return  org.apache.http.impl.cookie.DateUtils.formatDate(date, pattern);
    }

}
