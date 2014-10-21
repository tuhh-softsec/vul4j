package org.esigate.http.cookie;

import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.impl.cookie.AbstractCookieAttributeHandler;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.Args;

/**
 * Handler for HttpOnly attribute
 * 
 * @author Alexis Thaveau on 20/10/14.
 */
@Immutable
public class HttpOnlyHandler extends AbstractCookieAttributeHandler {

    public HttpOnlyHandler() {
        super();
    }

    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {

        Args.notNull(cookie, "Cookie");
        ((BasicClientCookie) cookie).setAttribute(CookieUtil.HTTP_ONLY_ATTR, "");

    }

}
