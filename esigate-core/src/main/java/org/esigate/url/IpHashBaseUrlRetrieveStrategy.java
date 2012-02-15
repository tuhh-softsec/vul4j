package org.esigate.url;

import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;

public class IpHashBaseUrlRetrieveStrategy implements BaseUrlRetrieveStrategy{
	private final String[] urls;

	public IpHashBaseUrlRetrieveStrategy(String[] urls) {
		this.urls = urls;
	}

	public String getBaseURL(HttpRequest originalRequest,
			HttpResponse originalResponse) {
		int index = getHashCode(originalRequest.getRemoteAddr()) % urls.length;
		return urls[Math.abs(index)];
	}
	
	private int getHashCode(String ip) {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		return result;
	}

}
