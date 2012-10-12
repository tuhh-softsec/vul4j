package org.esigate.url;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;

public class StickySessionBaseUrlRetrieveStrategyTest extends TestCase {

	public void testGetBaseURL() {
		final String baseUrls[] = new String[] { "http://example.com/test/", "http://example1.com/test/", "http://example2.com/test/" };
		BaseUrlRetrieveStrategy strategy = new StickySessionBaseUrlRetrieveStrategy(baseUrls);

		int times = 100;
		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		HttpResponse response = EasyMock.createMock(HttpResponse.class);
		Cookie cookie = EasyMock.createMock(Cookie.class);

		EasyMock.expect(request.getCookies()).andReturn(new Cookie[] {});
		response.addCookie(EasyMock.anyObject(Cookie.class));
		EasyMock.expect(request.getCookies()).andReturn(new Cookie[] { cookie }).anyTimes();
		EasyMock.expect(cookie.getName()).andReturn(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME).anyTimes();
		final AtomicInteger counter = new AtomicInteger(0);
		EasyMock.expect(cookie.getValue()).andAnswer(new IAnswer<String>() {

			public String answer() throws Throwable {
				int ret = counter.get() % baseUrls.length;
				counter.incrementAndGet();
				return Integer.toString(ret);
			}
		}).anyTimes();

		EasyMock.replay(request, response, cookie);

		strategy.getBaseURL(request, response);

		for (int i = 0; i < times; i++) {
			assertEquals(baseUrls[i % baseUrls.length], strategy.getBaseURL(request, response));
		}

		EasyMock.verify(request, response, cookie);
	}

	public void testGetBaseURLWithWrongIndex() {
		final String baseUrls[] = new String[] { "http://example.com/test/", "http://example1.com/test/", "http://example2.com/test/" };
		BaseUrlRetrieveStrategy strategy = new StickySessionBaseUrlRetrieveStrategy(baseUrls);

		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		HttpResponse response = EasyMock.createMock(HttpResponse.class);

		Cookie cookie = new BasicClientCookie(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME, "-1");
		EasyMock.expect(request.getCookies()).andReturn(new Cookie[] { cookie });
		response.addCookie(EasyMock.isA(Cookie.class));
		EasyMock.expectLastCall();

		cookie = new BasicClientCookie(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME, "5");
		EasyMock.expect(request.getCookies()).andReturn(new Cookie[] { cookie });
		response.addCookie(EasyMock.isA(Cookie.class));
		EasyMock.expectLastCall();

		cookie = new BasicClientCookie(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME, null);
		EasyMock.expect(request.getCookies()).andReturn(new Cookie[] { cookie });
		response.addCookie(EasyMock.isA(Cookie.class));
		EasyMock.expectLastCall();

		cookie = new BasicClientCookie(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME, "a");
		EasyMock.expect(request.getCookies()).andReturn(new Cookie[] { cookie });
		response.addCookie(EasyMock.isA(Cookie.class));
		EasyMock.expectLastCall();

		EasyMock.replay(request, response);

		strategy.getBaseURL(request, response);
		strategy.getBaseURL(request, response);
		strategy.getBaseURL(request, response);
		strategy.getBaseURL(request, response);

		EasyMock.verify(request, response);
	}
}
