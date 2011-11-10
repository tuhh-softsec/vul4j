package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.Cookie;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.test.MockHttpServletRequest;
import org.esigate.test.MockHttpServletResponse;

public class ChooseElementTest extends TestCase {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@Override
	protected void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	public void testChoose() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>inside choose</esi:choose>end";
		EsiRenderer tested = new EsiRenderer(request, response, null);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin end", out.toString());
	}

	public void testSingleWhen() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">inside when</esi:when>"
				+ "</esi:choose> end";
		request.addCookie(new Cookie("group", "Advanced"));
		EsiRenderer tested = new EsiRenderer(request, response, null);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin inside when end", out.toString());
	}

	public void testMultipleWhen() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">unexpected cookie '$(HTTP_COOKIE{group})'</esi:when>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">expected cookie '$(HTTP_COOKIE{group})'</esi:when>"
				+ "</esi:choose> end";
		request.addCookie(new Cookie("group", "Beginner"));
		EsiRenderer tested = new EsiRenderer(request, response, null);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin expected cookie 'Beginner' end", out.toString());
	}

	public void testMultipleWhenOtherwise() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">expected cookie</esi:when>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">unexpected cookie</esi:when>"
				+ "<esi:otherwise>inside otherwise</esi:otherwise>"
				+ "</esi:choose> end";
		request.addCookie(new Cookie("group", "Intermediate"));
		EsiRenderer tested = new EsiRenderer(request, response, null);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin inside otherwise end", out.toString());
	}

	public void testOtherwise() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">inside when</esi:when>"
				+ "<esi:otherwise>inside otherwise with '$(HTTP_COOKIE{group})' cookie</esi:otherwise>"
				+ "</esi:choose> end";
		request.addCookie(new Cookie("group", "Advanced"));
		EsiRenderer tested = new EsiRenderer(request, response, null);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin inside otherwise with 'Advanced' cookie end", out.toString());
	}

}
