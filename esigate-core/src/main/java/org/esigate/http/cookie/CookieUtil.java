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

package org.esigate.http.cookie;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.util.CharArrayBuffer;

/**
 * Utility class for {@link Cookie}.
 * 
 * @author Alexis Thaveau on 20/10/14.
 */
public final class CookieUtil {

    private static final int ONE_SECOND = 1000;

    private CookieUtil() {
        // Do not instantiate
    }

    /**
     * Attribute name used to tag a {@link BasicClientCookie} as httpOnly.
     */
    public static final String HTTP_ONLY_ATTR = "httponly";

    /**
     * Utility method to transform a Cookie into a Set-Cookie Header.
     * 
     * @param cookie
     *            the {@link Cookie} to format
     * 
     * @return the value of the Set-Cookie header
     */
    public static String encodeCookie(Cookie cookie) {
        int maxAge = -1;
        if (cookie.getExpiryDate() != null) {
            maxAge = (int) ((cookie.getExpiryDate().getTime() - System.currentTimeMillis()) / ONE_SECOND);
            // According to Cookie class specification, a negative value
            // would be considered as no value. That is not what we want!
            if (maxAge < 0) {
                maxAge = 0;
            }
        }

        CharArrayBuffer buf =
                BasicHeaderValueFormatter.INSTANCE.formatHeaderElement(null, new BasicHeaderElement(cookie.getName(),
                        cookie.getValue()), false);

        appendAttribute(buf, "Comment", cookie.getComment());
        appendAttribute(buf, "Domain", cookie.getDomain());
        appendAttribute(buf, "Max-Age", maxAge);
        appendAttribute(buf, "Path", cookie.getPath());
        if (cookie.isSecure()) {
            appendAttribute(buf, "Secure");
        }
        if (((BasicClientCookie) cookie).containsAttribute(HTTP_ONLY_ATTR)) {
            appendAttribute(buf, "HttpOnly");
        }
        appendAttribute(buf, "Version", cookie.getVersion());

        return (buf.toString());
    }

    private static void appendAttribute(CharArrayBuffer buf, String name, String value) {
        if (value != null) {
            buf.append("; ");
            buf.append(name);
            buf.append("=");
            buf.append(value);
        }
    }

    private static void appendAttribute(CharArrayBuffer buf, String name, int value) {
        if (value > 0) {
            appendAttribute(buf, name, Integer.toString(value));
        }
    }

    private static void appendAttribute(CharArrayBuffer buf, String name) {
        buf.append("; ");
        buf.append(name);
    }
}
