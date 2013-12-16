/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
