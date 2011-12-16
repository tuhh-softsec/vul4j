package org.esigate.http;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.esigate.api.Cookie;
import org.esigate.cookie.BasicClientCookie;
import org.esigate.cookie.CustomCookieStore;

class CookieAdapter {

	public static CookieStore convertCookieStore(CustomCookieStore customStore) {
		return new Adapter(customStore);
	}

	public static org.apache.http.cookie.Cookie toApacheCookie(Cookie cookie) {
		BasicClientCookie2 newCookie = new BasicClientCookie2(cookie.getName(), cookie.getValue());
		newCookie.setComment(cookie.getComment());
		newCookie.setCommentURL(cookie.getCommentURL());
		newCookie.setDomain(cookie.getDomain());
		newCookie.setExpiryDate(cookie.getExpiryDate());
		newCookie.setPath(cookie.getPath());
		newCookie.setPorts(cookie.getPorts());
		newCookie.setSecure(cookie.isSecure());
		newCookie.setVersion(cookie.getVersion());

		return newCookie;
	}

	public static Cookie toCustomCookie(org.apache.http.cookie.Cookie cookie) {
		BasicClientCookie newCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
		newCookie.setComment(cookie.getComment());
		newCookie.setCommentURL(cookie.getCommentURL());
		newCookie.setDomain(cookie.getDomain());
		newCookie.setExpiryDate(cookie.getExpiryDate());
		newCookie.setPath(cookie.getPath());
		newCookie.setPorts(cookie.getPorts());
		newCookie.setSecure(cookie.isSecure());
		newCookie.setVersion(cookie.getVersion());

		return newCookie;
	}

	private static class Adapter implements CookieStore {
		private final CustomCookieStore customStore;

		private Adapter(CustomCookieStore customStore) {
			this.customStore = customStore;
		}

		public void addCookie(org.apache.http.cookie.Cookie cookie) {
			customStore.addCookie(toCustomCookie(cookie));
		}

		public List<org.apache.http.cookie.Cookie> getCookies() {
			List<Cookie> customCookieList = customStore.getCookies();
			List<org.apache.http.cookie.Cookie> cookiesList = new ArrayList<org.apache.http.cookie.Cookie>(customCookieList.size());
			for (Cookie cookie : customCookieList) {
				cookiesList.add(toApacheCookie(cookie));
			}
			return cookiesList;
		}

		public boolean clearExpired(Date date) {
			return customStore.clearExpired(date);
		}

		public void clear() {
			customStore.clear();
		}

	}
}
