package org.esigate.http.cookie;

import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.impl.cookie.AbstractCookieSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/**
 * A BrowserCompatSpecFactory that register HttpOnly handler
 * 
 * @author Alexis Thaveau on 21/10/14.
 */
@Immutable
@SuppressWarnings("deprecation")
public class CustomBrowserCompatSpecFactory extends BrowserCompatSpecFactory {

    /**
     *
     */
    public final static String CUSTOM_BROWSER_COMPATIBILITY = "custom_browser_compatibility";

    @Override
    public CookieSpec newInstance(final HttpParams params) {
        AbstractCookieSpec cookieSpec = (AbstractCookieSpec) super.newInstance(params);
        cookieSpec.registerAttribHandler(CookieUtil.HTTP_ONLY_ATTR, new HttpOnlyHandler());
        return cookieSpec;
    }

    @Override
    public CookieSpec create(final HttpContext context) {
        AbstractCookieSpec cookieSpec = (AbstractCookieSpec) super.create(context);
        cookieSpec.registerAttribHandler(CookieUtil.HTTP_ONLY_ATTR, new HttpOnlyHandler());
        return cookieSpec;
    }

}
