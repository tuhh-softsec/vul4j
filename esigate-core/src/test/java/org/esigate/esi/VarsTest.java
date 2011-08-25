package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;

public class VarsTest extends TestCase {

	private MockDriver provider;

	@Override
	protected void setUp() throws Exception {
		provider = new MockDriver("mock");
		provider.addResource("/test", "test");
		provider.addResource("http://www.foo.com/test", "test");
	}

	public void testHttpHost() throws IOException, HttpErrorPage {
		String page = "begin <esi:vars>$(HTTP_HOST)</esi:vars> end";

		HttpServletRequest request = EasyMock
				.createNiceMock(HttpServletRequest.class);
		EasyMock.expect(request.getHeader("Host")).andReturn(
				"http://www.foo.com");
		EasyMock.replay(request);

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin http://www.foo.com end", out.toString());
	}

	public void testCookie() throws IOException, HttpErrorPage {
		String page = "begin <esi:vars>"
				+ "<img src=\"http://www.example.com/$(HTTP_COOKIE{cookieName})/hello.gif\"/ >"
				+ "</esi:vars> end";

		HttpServletRequest request = EasyMock
				.createNiceMock(HttpServletRequest.class);
		EasyMock.expect(request.getCookies()).andReturn(
				new Cookie[] { new Cookie("cookieName", "value") });
		EasyMock.replay(request);

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals(
				"begin <img src=\"http://www.example.com/value/hello.gif\"/ > end",
				out.toString());
	}

	public void testQueryString() throws IOException, HttpErrorPage {
		String page = "begin <esi:vars>"
				+ "<img src=\"http://www.example.com/$(QUERY_STRING{param1})/hello.gif\"/ >"
				+ "</esi:vars> end";

		HttpServletRequest request = EasyMock
				.createNiceMock(HttpServletRequest.class);
		EasyMock.expect(request.getParameter("param1"))
				.andReturn("param1value");
		EasyMock.replay(request);

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals(
				"begin <img src=\"http://www.example.com/param1value/hello.gif\"/ > end",
				out.toString());
	}

	public void testHttpReferer() throws IOException, HttpErrorPage {
		String page = "begin <esi:vars>" + "$(HTTP_REFERER)"
				+ "</esi:vars> end";

		HttpServletRequest request = EasyMock
				.createNiceMock(HttpServletRequest.class);
		EasyMock.expect(request.getHeader("Referer")).andReturn(
				"http://www.example.com");
		EasyMock.replay(request);

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin http://www.example.com end", out.toString());
	}

	public void testUserAgent() throws IOException, HttpErrorPage {
		String page = "begin <esi:vars>" + "$(HTTP_USER_AGENT{os})"
				+ "</esi:vars> end";

		HttpServletRequest request = EasyMock
				.createNiceMock(HttpServletRequest.class);
		EasyMock.expect(request.getHeader("User-Agent"))
				.andReturn(
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.10) "
								+ "Gecko/20100914 Firefox/3.6.10 GTB7.1 ( .NET CLR 3.5.30729)");
		EasyMock.replay(request);

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin WIN end", out.toString());
	}

	public void testAcceprLanguage() throws IOException, HttpErrorPage {
		String page = "begin <esi:vars>" + "$(HTTP_ACCEPT_LANGUAGE{en-us})"
				+ "</esi:vars> end";

		HttpServletRequest request = EasyMock
				.createNiceMock(HttpServletRequest.class);
		EasyMock.expect(request.getHeader("Accept-Language")).andReturn(
				"en-us,en;q=0.5");
		EasyMock.replay(request);

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin true end", out.toString());
	}

}
