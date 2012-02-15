package org.esigate.url;

import junit.framework.TestCase;

import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;

public class IpHashBaseUrlRetrieveStrategyTest extends TestCase{
	
	public void testGetBaseURLRandom() {
		String baseUrls[] = new String[] { "http://example.com/test/",
				"http://example1.com/test/", "http://example2.com/test/" };
		BaseUrlRetrieveStrategy strategy = new IpHashBaseUrlRetrieveStrategy(
				baseUrls);

		int times = 100;
		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		HttpResponse responce = EasyMock.createMock(HttpResponse.class);
		EasyMock.expect(request.getRemoteAddr())
		.andAnswer(new IAnswer<String>() {

			public String answer() throws Throwable {
				return getRandomIp();
			}
		}).times(times);
		EasyMock.replay(request, responce);


		for(int i =0; i < times; i++){
			strategy.getBaseURL(request, responce);
		}

		EasyMock.verify(request, responce);
	}
	
	public void testGetBaseURLInvalidIp() {
		String baseUrls[] = new String[] { "http://example.com/test/",
				"http://example1.com/test/", "http://example2.com/test/" };
		BaseUrlRetrieveStrategy strategy = new IpHashBaseUrlRetrieveStrategy(
				baseUrls);


		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		HttpResponse responce = EasyMock.createMock(HttpResponse.class);
		EasyMock.expect(request.getRemoteAddr()).andReturn("");
		EasyMock.expect(request.getRemoteAddr()).andReturn(null);
		EasyMock.expect(request.getRemoteAddr()).andReturn("not_ip");
		EasyMock.expect(request.getRemoteAddr()).andReturn("a.b.c.d");
		EasyMock.replay(request, responce);

		
		strategy.getBaseURL(request, responce);
		strategy.getBaseURL(request, responce);
		strategy.getBaseURL(request, responce);
		strategy.getBaseURL(request, responce);
			

		EasyMock.verify(request, responce);
	}
	
	public void testGetBaseURLSameIpSameBaseUrl() {
		String baseUrls[] = new String[] { "http://example.com/test/",
				"http://example1.com/test/", "http://example2.com/test/" };
		BaseUrlRetrieveStrategy strategy = new IpHashBaseUrlRetrieveStrategy(
				baseUrls);

		int times = 100;
		
		for(int i =0; i < times; i++){
			HttpRequest request = EasyMock.createMock(HttpRequest.class);
			HttpResponse responce = EasyMock.createMock(HttpResponse.class);
			
			String ip = getRandomIp();
			
			EasyMock.expect(request.getRemoteAddr()).andReturn(ip);
			EasyMock.expect(request.getRemoteAddr()).andReturn(ip);
			EasyMock.expect(request.getRemoteAddr()).andReturn(ip);
			
			EasyMock.replay(request, responce);
			
			String baseURL1 = strategy.getBaseURL(request, responce);
			String baseURL2 = strategy.getBaseURL(request, responce);
			String baseURL3 = strategy.getBaseURL(request, responce);
			
			assertEquals(baseURL1, baseURL2);
			assertEquals(baseURL1, baseURL3);
			
			EasyMock.verify(request, responce);
		}

		
	}

	private String getRandomIp() {
		String[] arr = new String[4];
		for(int i = 0; i < 4; i++){
			arr[i] = Integer.toString((int)(Math.random() * 256));
		}
		return StringUtils.join(arr, ".");
	}
}
