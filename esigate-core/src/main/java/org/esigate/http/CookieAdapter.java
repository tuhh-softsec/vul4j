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
	
	public static CookieStore convertCookieStore(CustomCookieStore customStore)
	{		
		return new Adapter(customStore);	
	}
	
	public static org.apache.http.cookie.Cookie toApacheCookie(Cookie cookie)
	{
		org.apache.http.cookie.Cookie newCookie = new BasicClientCookie2(cookie.getName(), cookie.getValue());
		if(cookie != null){
			((BasicClientCookie2)newCookie).setComment(cookie.getComment());
			((BasicClientCookie2)newCookie).setCommentURL(cookie.getCommentURL());
			((BasicClientCookie2)newCookie).setDomain(cookie.getDomain());
			((BasicClientCookie2)newCookie).setExpiryDate(cookie.getExpiryDate());
			((BasicClientCookie2)newCookie).setPath(cookie.getPath());
			((BasicClientCookie2)newCookie).setPorts(cookie.getPorts());
			((BasicClientCookie2)newCookie).setSecure(cookie.isSecure());
			((BasicClientCookie2)newCookie).setVersion(cookie.getVersion());
		}
		return newCookie;
		
	}
	
	public static Cookie toCustomCookie(org.apache.http.cookie.Cookie cookie)
	{
		Cookie newCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
		if(cookie != null){
			((BasicClientCookie)newCookie).setComment(cookie.getComment());
			((BasicClientCookie)newCookie).setCommentURL(cookie.getCommentURL());
			((BasicClientCookie)newCookie).setDomain(cookie.getDomain());
			((BasicClientCookie)newCookie).setExpiryDate(cookie.getExpiryDate());
			((BasicClientCookie)newCookie).setPath(cookie.getPath());
			((BasicClientCookie)newCookie).setPorts(cookie.getPorts());
			((BasicClientCookie)newCookie).setSecure(cookie.isSecure());
			((BasicClientCookie)newCookie).setVersion(cookie.getVersion());
		}
		return newCookie;

		
	}
	
	private static class Adapter implements CookieStore {
		
		private CustomCookieStore customStore;
		
		private Adapter(CustomCookieStore customStore){
			this.customStore = customStore;
		}
		
		public void addCookie(org.apache.http.cookie.Cookie cookie) {
			customStore.addCookie(toCustomCookie(cookie));	
		}
	
		public List<org.apache.http.cookie.Cookie> getCookies() {
			
			List<Cookie> customCookieList = customStore.getCookies();
			List<org.apache.http.cookie.Cookie> cookiesList = 
					new ArrayList<org.apache.http.cookie.Cookie>(customCookieList.size());
			
			for(Cookie cookie : customCookieList)
			{
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
