package org.esigate.cookie;

import java.util.Date;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.esigate.ResourceContext;
import org.esigate.extension.Extension;

public interface CookieManager extends Extension {

	public void addCookie(Cookie cookie,ResourceContext resourceContext);

	public List<Cookie> getCookies(ResourceContext resourceContext);

	public boolean clearExpired(Date date, ResourceContext resourceContext);

	public void clear(ResourceContext resourceContext);

}
