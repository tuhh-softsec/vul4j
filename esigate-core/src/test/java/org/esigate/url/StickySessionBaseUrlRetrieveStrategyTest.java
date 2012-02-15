package org.esigate.url;


import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.api.Cookie;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;

public class StickySessionBaseUrlRetrieveStrategyTest extends TestCase{
	
	public void testGetBaseURL() {
		final String baseUrls[] = new String[] { "http://example.com/test/",
				"http://example1.com/test/", "http://example2.com/test/" };
		BaseUrlRetrieveStrategy strategy = new StickySessionBaseUrlRetrieveStrategy(
				baseUrls);

		int times = 100;
		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		HttpResponse responce = EasyMock.createMock(HttpResponse.class);
		Cookie cookie = EasyMock.createMock(Cookie.class);
		
		EasyMock.expect(request.getCookies()).andReturn(new Cookie[]{});
		responce.addCookie(EasyMock.anyObject(Cookie.class));
		EasyMock.expect(request.getCookies()).andReturn(new Cookie[]{cookie}).anyTimes();
		EasyMock.expect(cookie.getName()).andReturn(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME).anyTimes();
		final AtomicInteger counter = new AtomicInteger(0);
		EasyMock.expect(cookie.getValue()).andAnswer(new IAnswer<String>() {

			public String answer() throws Throwable {
				int ret = counter.get() % baseUrls.length;
				counter.incrementAndGet();
				return Integer.toString(ret);
			}
		}).anyTimes();

		EasyMock.replay(request, responce, cookie);

		strategy.getBaseURL(request, responce);
		
		for(int i =0; i < times; i++){
			assertEquals(baseUrls[i % baseUrls.length], strategy.getBaseURL(request, responce));
		}

		EasyMock.verify(request, responce, cookie);
	}
	
	public void testGetBaseURLWithWrongIndex() {
		final String baseUrls[] = new String[] { "http://example.com/test/",
				"http://example1.com/test/", "http://example2.com/test/" };
		BaseUrlRetrieveStrategy strategy = new StickySessionBaseUrlRetrieveStrategy(
				baseUrls);

		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		HttpResponse responce = EasyMock.createMock(HttpResponse.class);
		Cookie cookie = EasyMock.createMock(Cookie.class);
		
		EasyMock.expect(request.getCookies()).andReturn(new Cookie[]{cookie});
		EasyMock.expect(cookie.getName()).andReturn(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME);
		EasyMock.expect(cookie.getValue()).andReturn("-1");
		cookie.setValue(EasyMock.anyObject(String.class));
		
		EasyMock.expect(request.getCookies()).andReturn(new Cookie[]{cookie});
		EasyMock.expect(cookie.getName()).andReturn(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME);
		EasyMock.expect(cookie.getValue()).andReturn("5");
		cookie.setValue(EasyMock.anyObject(String.class));
		
		EasyMock.expect(request.getCookies()).andReturn(new Cookie[]{cookie});
		EasyMock.expect(cookie.getName()).andReturn(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME);
		EasyMock.expect(cookie.getValue()).andReturn(null);
		cookie.setValue(EasyMock.anyObject(String.class));
		
		EasyMock.expect(request.getCookies()).andReturn(new Cookie[]{cookie});
		EasyMock.expect(cookie.getName()).andReturn(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME);
		EasyMock.expect(cookie.getValue()).andReturn("a");
		cookie.setValue(EasyMock.anyObject(String.class));
		

		EasyMock.replay(request, responce, cookie);

		strategy.getBaseURL(request, responce);
		strategy.getBaseURL(request, responce);
		strategy.getBaseURL(request, responce);
		strategy.getBaseURL(request, responce);
		

		EasyMock.verify(request, responce, cookie);
	}
}
