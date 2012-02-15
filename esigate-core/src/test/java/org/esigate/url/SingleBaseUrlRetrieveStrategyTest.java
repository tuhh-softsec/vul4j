package org.esigate.url;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;

public class SingleBaseUrlRetrieveStrategyTest extends TestCase{
	
	public void testGetBaseURL(){
		String baseUrl = "http://example.com/test/";
		BaseUrlRetrieveStrategy strategy = new SingleBaseUrlRetrieveStrategy(baseUrl);
		
		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		HttpResponse responce = EasyMock.createMock(HttpResponse.class);
		EasyMock.replay(request, responce);
		
		String baseURL2 = strategy.getBaseURL(request, responce);
		
		assertEquals(baseUrl, baseURL2);
		
		EasyMock.verify(request, responce);
	}
}
