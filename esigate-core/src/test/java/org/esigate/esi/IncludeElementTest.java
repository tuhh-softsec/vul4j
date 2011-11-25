package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.test.MockHttpServletRequest;
import org.esigate.test.MockHttpServletResponse;

public class IncludeElementTest extends TestCase {

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

	public void testIncludeProvider() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"$PROVIDER({mock})/test\" /> after";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before test after", out.toString());
	}

	public void testIncludeAbsolute() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"http://www.foo.com/test\" /> after";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before test after", out.toString());
	}

	public void testIncludeFragment() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"$PROVIDER({mock})/testFragment\" fragment =\"myFragment\" /> after";
		provider.addResource("/testFragment", "before fragment <esi:fragment name=\"myFragment\">---fragment content---</esi:fragment> after fragment");
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before ---fragment content--- after", out.toString());
	}

	public void testIncludeQueryString() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"$PROVIDER({mock})/test?$(QUERY_STRING)\" /> after";
		provider.addResource("/test?queryparameter1=test&queryparameter2=test2", "query OK");
		request = new MockHttpServletRequest("http://localhost/test?queryparameter1=test&queryparameter2=test2");
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before query OK after", out.toString());
	}

	public void testIncludeQueryStringParameter() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"$PROVIDER({mock})/$(QUERY_STRING{queryparameter2})\" /> after";
		provider.addResource("/test2", "queryparameter2 OK");
		request = new MockHttpServletRequest("http://localhost/test?queryparameter1=test&queryparameter2=test2");
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before queryparameter2 OK after", out.toString());
	}

	public void testIncludeInlineCache() throws IOException, HttpErrorPage {
		String page = "before <esi:include src='$PROVIDER({mock})/inline-cache' /> after";
		InlineCache.storeFragment("$PROVIDER({mock})/inline-cache", null, false, null, "---inline cache item---");
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before ---inline cache item--- after", out.toString());

		InlineCache.storeFragment("$PROVIDER({mock})/inline-cache", new Date(System.currentTimeMillis() + 10L * 1000L), false, null,
				"---updated inline cache item---");
		out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before ---updated inline cache item--- after", out.toString());

		InlineCache.storeFragment("$PROVIDER({mock})/inline-cache", new Date(System.currentTimeMillis() - 10L * 1000L), false, null,
				"---expired inline cache item---");
		out = new StringWriter();
		provider.addResource("/inline-cache", "---fetched inline cache item---");
		tested.render(null, page, out);
		assertEquals("before ---fetched inline cache item--- after", out.toString());
	}

	public void testIncludeInlineElement() throws IOException, HttpErrorPage {
		String page = "before <esi:include src='$PROVIDER({mock})/inline-cache' /> middle "
				+ "<esi:inline name='$PROVIDER({mock})/inline-cache' fetchable='false'>---inline cache item---</esi:inline>"
				+ "<esi:include src='$PROVIDER({mock})/inline-cache' /> after";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		provider.addResource("/inline-cache", "---fetched inline cache item---");
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before ---fetched inline cache item--- middle ---inline cache item--- after", out.toString());
	}

	public void testIncludeReplaceElementFragment() throws IOException, HttpErrorPage {
		String page = "before <esi:include src='$PROVIDER({mock})/include-replace' >"
				+ "<esi:replace fragment='replaceable-fragment'>fragment replaced</esi:replace>"
				+ "</esi:include> after";
		String includedPage = "-incl-page-start"
				+ " <esi:fragment name='replaceable-fragment'>replaced content</esi:fragment>"
				+ " <esi:fragment name='untouched-fragment' />"
				+ " incl-page-end-";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		provider.addResource("/include-replace", includedPage);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals(
				"before -incl-page-start fragment replaced <esi:fragment name='untouched-fragment' /> incl-page-end- after",
				out.toString());
	}

	public void testIncludeReplaceElementRegexp() throws IOException, HttpErrorPage {
		String page = "before <esi:include src='$PROVIDER({mock})/include-replace' >"
				+ "<esi:replace regexp='replaceable-regexp'>regexp replaced</esi:replace>"
				+ "</esi:include> after";
		String includedPage = "-incl-page-start"
				+ " <esi:fragment name='untouched-fragment'>zzz</esi:fragment>"
				+ " replaceable-regexp"
				+ " incl-page-end-";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		provider.addResource("/include-replace", includedPage);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals(
				"before -incl-page-start <esi:fragment name='untouched-fragment'>zzz</esi:fragment> regexp replaced incl-page-end- after",
				out.toString());
	}

	public void testIncludeXpath() throws IOException, HttpErrorPage {
		String page = "before "
				+ "<esi:include src='$PROVIDER({mock})/inline-xpath' xpath='//html:body/text()' />"
				+ " after";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		provider.addResource("/inline-xpath",
				"<html><title>The header</title><body>-the body-<br><ul><li>list item</li></ul></body></html>");
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before -the body- after", out.toString());
	}
	
	public void testIncludeXSLT() throws IOException, HttpErrorPage {
		String page = "before "
				+ "<esi:include src='$PROVIDER({mock})/inline-xslt' stylesheet=\"http://www.foo.com/test.xsl\" />"
				+ " after";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		provider.addResource("/inline-xslt",
				"<html><body>The body<br></body></html>");
		provider.addResource("http://www.foo.com/test.xsl", "<?xml version=\"1.0\"?>"
				+ "<xsl:stylesheet version=\"1.0\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:html=\"http://www.w3.org/1999/xhtml\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
				+ "<xsl:output method=\"xml\" omit-xml-declaration=\"yes\"/> indent=\"no\""
				+ "<xsl:template match=\"//html:body\">"
				+ "<xsl:copy-of select=\".\"/>"
				+ "</xsl:template>"
				+ "</xsl:stylesheet>");
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before <body xmlns=\"http://www.w3.org/1999/xhtml\">The body<br/></body> after", out.toString());
	}

	public void testIncludeAlt1() throws IOException, HttpErrorPage {
		String page = "before "
				+ "<esi:include src='$PROVIDER({mock})/alt-url' alt=\"http://www.foo.com/test\" />"
				+ " after";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		provider.addResource("/alt-url", "---fetched alt url---");
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before ---fetched alt url--- after", out.toString());
	}

	public void testIncludeAlt2() throws IOException, HttpErrorPage {
		String page = "before "
				+ "<esi:include src='$PROVIDER({mock})/not-found' alt=\"http://www.foo.com/test\" />"
				+ " after";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before test after", out.toString());
	}

	public void testOnError() throws IOException {
		String page = "before <esi:include src=\"http://www.foo.com/test-onerror\" /> after";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		try {
			tested.render(null, page, out);
			fail("should throw HttpErrorPage");
		} catch (HttpErrorPage e) {
			assertEquals(404, e.getStatusCode());
		}
	}
	
	public void testOnErrorContinue() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"http://www.foo.com/test-onerror\" onerror=\"continue\"/> after";
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before  after", out.toString());
	}
	
	public void testIncludeReplaceAbsolute() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"http://www.foo.com/test-rewriteUrl\" rewriteabsoluteurl=\"true\"  /> after";	
		String deafultBaseUrl = "http://www.foo.com/context/";
		String visibleBaseURL = "http://www.foo.com/contextExt/";

		Properties defaultProps = new Properties();
		defaultProps.setProperty("remoteUrlBase", deafultBaseUrl);
		defaultProps.setProperty("visibleUrlBase", visibleBaseURL);
		defaultProps.setProperty("fixResources", "true");
		
		MockDriver provider = new MockDriver("mock", defaultProps);
		
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		provider.addResource("http://www.foo.com/test-rewriteUrl", 
				"<IMG src=\"http://www.foo.com/context/~miko/counter.gif?name=idocsguide\">" +
				"<a href=\"http://www.foo.com/test\">"+
				"<a href=\"http://www.foo.com/context/test\">");
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before <IMG src=\"/contextExt/~miko/counter.gif?name=idocsguide\">" +
				"<a href=\"http://www.foo.com/test\"><a href=\"/contextExt/test\"> after", out.toString());
	}
	
	public void testIncludeReplaceAbsoluteBaseUrl() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"http://www.foo.com/test-rewriteUrl\" rewriteabsoluteurl=\"true\"  /> after";	
		String deafultBaseUrl = "http://www.foo.com/context";

		Properties defaultProps = new Properties();
		defaultProps.setProperty("remoteUrlBase", deafultBaseUrl);
		
		MockDriver provider = new MockDriver("mock", defaultProps);
		
		EsiRenderer tested = new EsiRenderer(request, response, provider);
		provider.addResource("http://www.foo.com/test-rewriteUrl", 
				"<IMG src=\"http://www.foo.com/context/~miko/counter.gif?name=idocsguide\">" +
				"<a href=\"http://www.foo.com/test\">"+
				"<a href=\"http://www.foo.com/context/test\">");
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("before <IMG src=\"/context/~miko/counter.gif?name=idocsguide\">" +
				"<a href=\"http://www.foo.com/test\"><a href=\"/context/test\"> after", out.toString());
	}
	
}
