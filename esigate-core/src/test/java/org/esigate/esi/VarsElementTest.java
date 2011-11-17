package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.Cookie;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.test.MockHttpServletRequest;
import org.esigate.test.MockHttpServletResponse;

public class VarsElementTest extends TestCase {

	private MockDriver provider;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@Override
	protected void setUp() throws Exception {
		provider = new MockDriver("mock");
		provider.addResource("/test", "test");
		provider.addResource("http://www.foo.com/test", "test");
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	public void testHttpHost() throws IOException, HttpErrorPage {
		String page = "begin <esi:vars>$(HTTP_HOST)</esi:vars> end";
		request = new MockHttpServletRequest("http://www.foo.com");
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin www.foo.com end", out.toString());
	}

	public void testCookie() throws IOException, HttpErrorPage {
		String page = "begin <esi:vars>"
				+ "<img src=\"http://www.example.com/$(HTTP_COOKIE{cookieName})/hello.gif\"/ >"
				+ "</esi:vars> end";
		request.addCookie(new Cookie("cookieName", "value"));
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin <img src=\"http://www.example.com/value/hello.gif\"/ > end", out.toString());
	}

	public void testQueryString() throws IOException, HttpErrorPage {
		String page = "begin <esi:vars>"
				+ "<img src=\"http://www.example.com/$(QUERY_STRING{param1})/hello.gif\"/ >"
				+ "</esi:vars> end";
		request = new MockHttpServletRequest("http://localhost/?param1=param1value");
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin <img src=\"http://www.example.com/param1value/hello.gif\"/ > end", out.toString());
	}

	public void testHttpReferer() throws IOException, HttpErrorPage {
		String page = "begin <esi:vars>" + "$(HTTP_REFERER)" + "</esi:vars> end";
		request.setHeader("Referer", "http://www.example.com");
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin http://www.example.com end", out.toString());
	}

	public void testUserAgent() throws IOException, HttpErrorPage {
		String page = "begin <esi:vars>" + "$(HTTP_USER_AGENT{os})" + "</esi:vars> end";
		request.setHeader("User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.10) Gecko/20100914 Firefox/3.6.10 GTB7.1 ( .NET CLR 3.5.30729)");
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin WIN end", out.toString());
	}

	public void testAcceptLanguage() throws IOException, HttpErrorPage {
		String page = "begin <esi:vars>" + "$(HTTP_ACCEPT_LANGUAGE{en-us})" + "</esi:vars> end";
		request.setHeader("Accept-Language", "en-us,en;q=0.5");
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin true end", out.toString());
	}

}
