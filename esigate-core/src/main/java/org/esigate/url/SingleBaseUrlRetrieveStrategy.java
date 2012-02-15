package org.esigate.url;

import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;

public class SingleBaseUrlRetrieveStrategy implements BaseUrlRetrieveStrategy {

	private final String baseUrl;

	public SingleBaseUrlRetrieveStrategy(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBaseURL(HttpRequest originalRequest,
			HttpResponse originalResponse) {
		return baseUrl;
	}

}
