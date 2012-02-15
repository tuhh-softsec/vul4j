package org.esigate.url;

import java.util.concurrent.atomic.AtomicInteger;

import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;

public class RoundRobinBaseUrlRetrieveStrategy implements BaseUrlRetrieveStrategy {
	private final AtomicInteger counter = new AtomicInteger(0);
	private final String[] urls;
	
	public RoundRobinBaseUrlRetrieveStrategy(String[] urls) {
		this.urls = urls;
	}

	public String getBaseURL(HttpRequest originalRequest,
			HttpResponse originalResponse) {
		int incremented = counter.incrementAndGet();
		int index = incremented % urls.length;
		return urls[Math.abs(index)];
	}
}
