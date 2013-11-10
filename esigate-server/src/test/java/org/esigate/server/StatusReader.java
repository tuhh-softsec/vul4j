package org.esigate.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Utility class for parsing status responses.
 * 
 * @author Nicolas Richeton
 * 
 */
public final class StatusReader {
    private StatusReader() {
    }

    /**
     * Get long value from status.
     * 
     * @param statusBody
     *            html content
     * @param name
     *            attribute name
     * @return long value or null if not found
     */
    public static Long getLong(String statusBody, String name) {
        BufferedReader br = new BufferedReader(new StringReader(statusBody));
        String str;
        try {
            while ((str = br.readLine()) != null) {
                if (str.startsWith(name + ":")) {
                    str = str.substring(str.indexOf(":") + 1).trim();
                    return Long.valueOf(str);
                }
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get Double value from status.
     * 
     * @param statusBody
     *            html content
     * @param name
     *            attribute name
     * @return Double value or null if not found
     */
    public static Double getDouble(String statusBody, String name) {
        BufferedReader br = new BufferedReader(new StringReader(statusBody));
        String str;
        try {
            while ((str = br.readLine()) != null) {
                if (str.startsWith(name + ":")) {
                    str = str.substring(str.indexOf(":") + 1).trim();
                    return Double.valueOf(str);
                }
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
