package org.esigate.http.cookie;

import java.net.HttpCookie;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;

/**
 * @author Alexis Thaveau on 20/10/14.
 */
public class CookieUtil {

    public static String HTTP_ONLY_ATTR = "httponly";

    public static String encodeCookie(Cookie cookie) {
        int maxAge = -1;
        if (cookie.getExpiryDate() != null) {
            maxAge = (int) ((cookie.getExpiryDate().getTime() - System.currentTimeMillis()) / 1000);
            // According to Cookie class specification, a negative value
            // would be considered as no value. That is not what we want!
            if (maxAge < 0) {
                maxAge = 0;
            }
        }

        StringBuffer buf = new StringBuffer(cookie.getName());
        buf.append("=");
        buf.append(cookie.getValue());

        if (cookie.getComment() != null) {
            buf.append("; Comment=\"");
            buf.append(cookie.getComment());
            buf.append("\"");
        }

        if (cookie.getDomain() != null) {
            buf.append("; Domain=\"");
            buf.append(cookie.getDomain());
            buf.append("\"");
        }

        if (maxAge >= 0) {
            buf.append("; Max-Age=\"");
            buf.append(maxAge);
            buf.append("\"");
        }

        if (cookie.getPath() != null) {
            buf.append("; Path=\"");
            buf.append(cookie.getPath());
            buf.append("\"");
        }

        if (cookie.isSecure()) {
            buf.append("; Secure");
        }
        if (((BasicClientCookie) cookie).containsAttribute(HTTP_ONLY_ATTR)) {
            buf.append("; HttpOnly");
        }

        if (cookie.getVersion() > 0) {
            buf.append("; Version=\"");
            buf.append(cookie.getVersion());
            buf.append("\"");
        }

        return (buf.toString());
    }

}
