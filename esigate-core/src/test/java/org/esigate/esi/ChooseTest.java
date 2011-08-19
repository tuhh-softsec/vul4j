package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.Cookie;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.esi.EsiRenderer;
import org.springframework.mock.web.MockHttpServletRequest;

public class ChooseTest extends TestCase {

	private MockDriver provider;

	@Override
	protected void setUp() throws Exception {
		provider = new MockDriver("mock");
		provider.addResource("/test", "test");
		provider.addResource("http://www.foo.com/test", "test");
	}

	public void testChoose() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>inside choose</esi:choose>end";

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Host", "http://www.foo.com");

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin end", out.toString());
	}

	public void testWhen() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">inside when</esi:when>"
				+ "</esi:choose> end";

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Host", "http://www.foo.com");
		request.setCookies(new Cookie[] { new Cookie("group", "Advanced") });

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin inside when end", out.toString());
	}

	public void testOtherwise() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">inside when</esi:when>"
				+ "<esi:otherwise>inside otherwise</esi:otherwise>"
				+ "</esi:choose> end";

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Host", "http://www.foo.com");
		request.setCookies(new Cookie[] { new Cookie("group", "Advanced") });

		EsiRenderer tested = new EsiRenderer(request, null, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin inside otherwise end", out.toString());
	}

}
