package org.esigate.http;

import java.util.Date;

public final class DateUtils {

    private DateUtils() {
    }

    /**
     * Formats the given date according to the RFC 1123 pattern.
     * 
     * @param date
     *            The date to format.
     * @return An RFC 1123 formatted date string.
     * 
     */
    public static String formatDate(Date date) {
        return org.apache.http.client.utils.DateUtils.formatDate(date);
    }

    /**
     * Formats the given date according to the RFC 1123 pattern.
     * 
     * @param date
     *            The date to format.
     * @return An RFC 1123 formatted date string.
     * 
     */
    public static String formatDate(long date) {
        return org.apache.http.client.utils.DateUtils.formatDate(new Date(date));
    }

}
