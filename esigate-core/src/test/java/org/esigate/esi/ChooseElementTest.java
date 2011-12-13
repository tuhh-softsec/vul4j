package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.esigate.cookie.BasicClientCookie;
import org.esigate.test.MockHttpRequest;

public class ChooseElementTest extends TestCase {

	private MockHttpRequest request;
	private ResourceContext ctx;
	private EsiRenderer tested;


	@Override
	protected void setUp() throws Exception {
		request = new MockHttpRequest();

		ctx = new ResourceContext(null, null, null, request, null);
		tested = new EsiRenderer();
	}

	public void testChoose() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>inside choose</esi:choose> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin inside choose end", out.toString());
	}

	public void testSingleWhen() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">inside when</esi:when>"
				+ "</esi:choose> end";
		request.addCookie(new BasicClientCookie("group", "Advanced"));
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin inside when end", out.toString());
	}

	public void testMultipleWhen() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">unexpected cookie '$(HTTP_COOKIE{group})'</esi:when>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">expected cookie '$(HTTP_COOKIE{group})'</esi:when>"
				+ "</esi:choose> end";
		request.addCookie(new BasicClientCookie("group", "Beginner"));
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin expected cookie 'Beginner' end", out.toString());
	}

	public void testMultipleWhenEvaluated() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">expected cookie</esi:when>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">unexpected cookie</esi:when>"
				+ "</esi:choose> end";
		request.addCookie(new BasicClientCookie("group", "Beginner"));
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin expected cookie end", out.toString());
	}

	public void testMultipleWhenOtherwise1() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">expected cookie</esi:when>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">unexpected cookie</esi:when>"
				+ "<esi:otherwise>inside otherwise</esi:otherwise>"
				+ "</esi:choose> end";
		request.addCookie(new BasicClientCookie("group", "Intermediate"));
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin inside otherwise end", out.toString());
	}

	public void testMultipleWhenOtherwise2() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">expected cookie '$(HTTP_COOKIE{group})'</esi:when>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">unexpected cookie '$(HTTP_COOKIE{group})'</esi:when>"
				+ "<esi:otherwise>inside otherwise</esi:otherwise>"
				+ "</esi:choose> end";
		request.addCookie(new BasicClientCookie("group", "Advanced"));
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin expected cookie 'Advanced' end", out.toString());
	}

	public void testOtherwise() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">inside when</esi:when>"
				+ "<esi:otherwise>inside otherwise with '$(HTTP_COOKIE{group})' cookie</esi:otherwise>"
				+ "</esi:choose> end";
		request.addCookie(new BasicClientCookie("group", "Advanced"));
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin inside otherwise with 'Advanced' cookie end", out.toString());
	}

}
