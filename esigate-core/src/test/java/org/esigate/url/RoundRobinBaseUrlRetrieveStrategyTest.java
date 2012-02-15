package org.esigate.url;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;

public class RoundRobinBaseUrlRetrieveStrategyTest extends TestCase {

	public void testGetBaseURL() {
		String baseUrls[] = new String[] { "http://example.com/test/",
				"http://example1.com/test/", "http://example2.com/test/" };
		BaseUrlRetrieveStrategy strategy = new RoundRobinBaseUrlRetrieveStrategy(
				baseUrls);

		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		HttpResponse responce = EasyMock.createMock(HttpResponse.class);
		EasyMock.replay(request, responce);
		int times = 5;
		int requestsCount = baseUrls.length * times;
		ConcurrentMap<String, AtomicInteger> counterMap = new ConcurrentHashMap<String, AtomicInteger>();

		for(int i =0; i < requestsCount; i++){
			String baseUrl = strategy.getBaseURL(request, responce);
			counterMap.putIfAbsent(baseUrl, new AtomicInteger(0));
			counterMap.get(baseUrl).incrementAndGet();
		}

		for(String baseUrl : baseUrls){
			assertEquals(times, counterMap.get(baseUrl).get());
		}

		EasyMock.verify(request, responce);
	}
}
