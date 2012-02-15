package org.esigate.api;


public interface BaseUrlRetrieveStrategy {
	public String getBaseURL(HttpRequest originalRequest, HttpResponse originalResponse);
}
