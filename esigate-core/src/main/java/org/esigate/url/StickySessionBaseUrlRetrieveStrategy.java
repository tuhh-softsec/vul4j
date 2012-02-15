package org.esigate.url;

import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.api.Cookie;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;
import org.esigate.cookie.BasicClientCookie;

public class StickySessionBaseUrlRetrieveStrategy implements
		BaseUrlRetrieveStrategy {
	public static final String ESI_SESSION_COOKIE_NAME = "_esigate_session_cookie";
	private final String[] urls;

	public StickySessionBaseUrlRetrieveStrategy(String[] urls) {
		super();
		this.urls = urls;
	}

	public String getBaseURL(HttpRequest originalRequest,
			HttpResponse originalResponse) {
		
		Cookie sessionCookie = getEsiSessionCookie(originalRequest.getCookies());
		int index = 0;
		boolean toGenerate = true;

		if (null != sessionCookie) {
			toGenerate = false;
			String indexStr = sessionCookie.getValue();
			
			if (null != indexStr) {
				try{
				Integer indexInt = Integer.parseInt(indexStr);
				index = indexInt.intValue();
				}catch (Exception e) {
					index = -1;
				}
				if (index < 0 || index >= urls.length) {
					toGenerate = true;
				}
			} else {
				toGenerate = true;
			}
		}
		if (toGenerate) {
			index = generateIndex();
			if(null != sessionCookie){
				sessionCookie.setValue(Integer.toString(index));
			}else{
				Cookie cookie = new BasicClientCookie(ESI_SESSION_COOKIE_NAME, Integer.toString(index));
				originalResponse.addCookie(cookie);
			}
			
		}

		return urls[index];
	}

	private int generateIndex() {
		return (int) (Math.random() * urls.length);
	}

	private Cookie getEsiSessionCookie(Cookie[] cookies) {
		Cookie ret = null;
		if (null != cookies && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (ESI_SESSION_COOKIE_NAME.equals(cookie.getName())) {
					ret = cookie;
					break;
				}
			}
		}
		return ret;
	}

}
