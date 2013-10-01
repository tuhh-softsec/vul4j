/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.Parameters;
import org.esigate.test.TestUtils;

public class IncludeElementTest extends TestCase {

	private MockDriver provider;
	private HttpEntityEnclosingRequest request;
	private EsiRenderer tested;

	@Override
	protected void setUp() throws Exception {
		provider = new MockDriver("mock");
		provider.addResource("/test", "test");
		provider.addResource("http://www.foo.com/test", "test");
		request = TestUtils.createRequest();
		provider.initHttpRequestParams(request, null);
		tested = new EsiRenderer();
	}

	public void testIncludeProvider() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"$(PROVIDER{mock})/test\" /> after";
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before test after", out.toString());
	}
	
	public void testIncludeProvider10() throws IOException, HttpErrorPage {
		int nb = 10;
		
		String page = "before ";
		for( int i = 0; i < nb; i ++ ){
			page = page+"<esi:include src=\"$(PROVIDER{mock})/test\" />";
		}
		page =page+ " after";
		
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		
		String result = "before ";
		for( int i = 0; i < nb; i ++ ){
			result = result+"test";
		}
		result =result+ " after";
		assertEquals(result, out.toString());
	}
	

	public void testIncludeProviderLegacy() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"$PROVIDER({mock})/test\" /> after";
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before test after", out.toString());
	}

	public void testIncludeAbsolute() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"http://www.foo.com/test\" /> after";
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before test after", out.toString());
	}

	public void testIncludeFragment() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"$(PROVIDER{mock})/testFragment\" fragment =\"myFragment\" /> after";
		provider.addResource("/testFragment", "before fragment <esi:fragment name=\"myFragment\">---fragment content---</esi:fragment> after fragment");
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before ---fragment content--- after", out.toString());
	}

	public void testIncludeQueryString() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"$(PROVIDER{mock})/test?$(QUERY_STRING)\" /> after";
		provider.addResource("/test?queryparameter1=test&queryparameter2=test2", "query OK");
		request = TestUtils.createRequest("http://localhost/test?queryparameter1=test&queryparameter2=test2");
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before query OK after", out.toString());
	}

	public void testIncludeQueryStringParameter() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"$(PROVIDER{mock})/$(QUERY_STRING{queryparameter2})\" /> after";
		provider.addResource("/test2", "queryparameter2 OK");
		request = TestUtils.createRequest("http://localhost/test?queryparameter1=test&queryparameter2=test2");
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before queryparameter2 OK after", out.toString());
	}

	public void testIncludeInlineCache() throws IOException, HttpErrorPage {
		String page = "before <esi:include src='$(PROVIDER{mock})/inline-cache' /> after";
		InlineCache.storeFragment("$(PROVIDER{mock})/inline-cache", null, false, null, "---inline cache item---");
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before ---inline cache item--- after", out.toString());

		InlineCache.storeFragment("$(PROVIDER{mock})/inline-cache", new Date(System.currentTimeMillis() + 10L * 1000L), false, null, "---updated inline cache item---");
		out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before ---updated inline cache item--- after", out.toString());

		InlineCache.storeFragment("$(PROVIDER{mock})/inline-cache", new Date(System.currentTimeMillis() - 10L * 1000L), false, null, "---expired inline cache item---");
		out = new StringWriter();
		provider.addResource("/inline-cache", "---fetched inline cache item---");
		tested.render(request, page, out);
		assertEquals("before ---fetched inline cache item--- after", out.toString());
	}

	public void testIncludeInlineElement() throws IOException, HttpErrorPage {
		String page = "before <esi:include src='$(PROVIDER{mock})/inline-cache' /> middle "
				+ "<esi:inline name='$(PROVIDER{mock})/inline-cache' fetchable='false'>---inline cache item---</esi:inline>" + "<esi:include src='$(PROVIDER{mock})/inline-cache' /> after";
		provider.addResource("/inline-cache", "---fetched inline cache item---");
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before ---fetched inline cache item--- middle ---inline cache item--- after", out.toString());
	}

	public void testIncludeReplaceElementFragment() throws IOException, HttpErrorPage {
		String page = "before <esi:include src='$(PROVIDER{mock})/include-replace' >" + "<esi:replace fragment='replaceable-fragment'>$(HTTP_COOKIE{cookieName})</esi:replace>"
				+ "</esi:include> after";
		String includedPage = "-incl-page-start" + " <esi:fragment name='replaceable-fragment'>replaced content</esi:fragment>" + " <esi:fragment name='untouched-fragment' />" + " incl-page-end-";
		TestUtils.addCookie(new BasicClientCookie("cookieName", "fragment replaced"), request);
		provider.addResource("/include-replace", includedPage);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before -incl-page-start fragment replaced  incl-page-end- after", out.toString());
	}

	public void testIncludeReplaceElementRegexp() throws IOException, HttpErrorPage {
		String page = "before <esi:include src='$(PROVIDER{mock})/include-replace' >" + "<esi:replace regexp='replaceable-regexp'>$(HTTP_COOKIE{cookieName})</esi:replace>" + "</esi:include> after";
		String includedPage = "-incl-page-start" + " <esi:fragment name='untouched-fragment'>zzz</esi:fragment>" + " replaceable-regexp" + " incl-page-end-";
		TestUtils.addCookie(new BasicClientCookie("cookieName", "regexp replaced"), request);
		provider.addResource("/include-replace", includedPage);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before -incl-page-start zzz regexp replaced incl-page-end- after", out.toString());
	}

	public void testIncludeReplaceElementExpression() throws IOException, HttpErrorPage {
		String page = "before <esi:include src='$(PROVIDER{mock})/include-replace' >" + "<esi:replace expression='replaceable-regexp'>$(HTTP_COOKIE{cookieName})</esi:replace>" + "</esi:include> after";
		String includedPage = "-incl-page-start" + " <esi:fragment name='untouched-fragment'>zzz</esi:fragment>" + " replaceable-regexp" + " incl-page-end-";
		TestUtils.addCookie(new BasicClientCookie("cookieName", "regexp replaced"), request);
		provider.addResource("/include-replace", includedPage);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before -incl-page-start zzz regexp replaced incl-page-end- after", out.toString());
	}

	public void testIncludeXpath() throws IOException, HttpErrorPage {
		String page = "before " + "<esi:include src='$(PROVIDER{mock})/inline-xpath' xpath='//html:body/text()' />" + " after";
		provider.addResource("/inline-xpath", "<html><title>The header</title><body>-the body-<br><ul><li>list item</li></ul></body></html>");
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before -the body- after", out.toString());
	}

	public void testIncludeXpathSeveralMatchingNodes() throws IOException, HttpErrorPage {
		String page = "before " + "<esi:include src='$(PROVIDER{mock})/inline-xpath' xpath='//html:body/html:ul/html:li/text()' />" + " after";
		provider.addResource("/inline-xpath", "<html><title>The header</title><body>-the body-<br><ul><li>list item 1</li><li>list item 2</li></ul></body></html>");
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before list item 1list item 2 after", out.toString());
	}

	public void testIncludeXSLT() throws IOException, HttpErrorPage {
		String page = "before " + "<esi:include src='$(PROVIDER{mock})/inline-xslt' stylesheet=\"http://www.foo.com/test.xsl\" />" + " after";
		provider.addResource("/inline-xslt", "<html><body>The body<br></body></html>");
		provider.addResource("http://www.foo.com/test.xsl", "<?xml version=\"1.0\"?>"
				+ "<xsl:stylesheet version=\"1.0\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:html=\"http://www.w3.org/1999/xhtml\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
				+ "<xsl:output method=\"xml\" omit-xml-declaration=\"yes\"/> indent=\"no\"" + "<xsl:template match=\"//html:body\">" + "<xsl:copy-of select=\".\"/>" + "</xsl:template>"
				+ "</xsl:stylesheet>");
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before <body>The body<br /></body> after", out.toString());
	}

	public void testIncludeAlt1() throws IOException, HttpErrorPage {
		String page = "before " + "<esi:include src='$(PROVIDER{mock})/alt-url' alt=\"http://www.foo.com/test\" />" + " after";
		provider.addResource("/alt-url", "---fetched alt url---");
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before ---fetched alt url--- after", out.toString());
	}

	public void testIncludeAlt2() throws IOException, HttpErrorPage {
		String page = "before " + "<esi:include src='$(PROVIDER{mock})/not-found' alt=\"http://www.foo.com/test\" />" + " after";
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before test after", out.toString());
	}

	public void testOnError() throws IOException {
		String page = "before <esi:include src=\"http://www.foo.com/test-onerror\" /> after";
		StringWriter out = new StringWriter();
		try {
			tested.render(request, page, out);
			fail("should throw HttpErrorPage");
		} catch (HttpErrorPage e) {
			assertEquals(404, e.getHttpResponse().getStatusLine().getStatusCode());
		}
	}

	public void testOnErrorContinue() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"http://www.foo.com/test-onerror\" onerror=\"continue\"/> after";
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before  after", out.toString());
	}

	public void testIncludeReplaceAbsolute() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"http://www.foo.com/test-rewriteUrl\" rewriteabsoluteurl=\"true\"  /> after";
		String defaultBaseUrl = "http://www.foo.com/context/";
		String visibleBaseURL = "http://www.foo.com/contextExt/";

		Properties defaultProps = new Properties();
		defaultProps.setProperty(Parameters.REMOTE_URL_BASE.name, defaultBaseUrl);
		defaultProps.setProperty("visibleUrlBase", visibleBaseURL);
		defaultProps.setProperty("fixResources", "true");

		provider = new MockDriver("mock", defaultProps);
		provider.addResource("http://www.foo.com/test-rewriteUrl", "<IMG src=\"http://www.foo.com/context/~miko/counter.gif?name=idocsguide\">" + "<a href=\"http://www.foo.com/test\">"
				+ "<a href=\"http://www.foo.com/context/test\">");
		request = TestUtils.createRequest();
		provider.initHttpRequestParams(request, null);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before <IMG src=\"/contextExt/~miko/counter.gif?name=idocsguide\">" + "<a href=\"http://www.foo.com/test\"><a href=\"/contextExt/test\"> after", out.toString());
	}

	public void testIncludeReplaceAbsoluteBaseUrl() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"http://www.foo.com/test-rewriteUrl\" rewriteabsoluteurl=\"true\"  /> after";
		String defaultBaseUrl = "http://www.foo.com/context";

		Properties defaultProps = new Properties();
		defaultProps.setProperty(Parameters.REMOTE_URL_BASE.name, defaultBaseUrl);

		provider = new MockDriver("mock", defaultProps);
		provider.addResource("http://www.foo.com/test-rewriteUrl", "<IMG src=\"http://www.foo.com/context/~miko/counter.gif?name=idocsguide\">" + "<a href=\"http://www.foo.com/test\">"
				+ "<a href=\"http://www.foo.com/context/test\">");
		request = TestUtils.createRequest();
		provider.initHttpRequestParams(request, null);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before <IMG src=\"/context/~miko/counter.gif?name=idocsguide\">" + "<a href=\"http://www.foo.com/test\"><a href=\"/context/test\"> after", out.toString());
	}

	public void testIncludeTagContentShouldBeRemoved() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"$(PROVIDER{mock})/testFragment\" fragment =\"myFragment\">Content to be removed</esi:include> after";
		provider.addResource("/testFragment", "before fragment <esi:fragment name=\"myFragment\">---fragment content---</esi:fragment> after fragment");
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before ---fragment content--- after", out.toString());
	}

	public void testIncludeFragmentNested() throws IOException, HttpErrorPage {
		String page = "before <esi:include src='$(PROVIDER{mock})/fragment' fragment='test' /> after";
		String includedPage = "-incl-page-start <esi:fragment name='test'>-<esi:fragment name='test'>content</esi:fragment>-</esi:fragment> incl-page-end-";
		provider.addResource("/fragment", includedPage);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before -content- after", out.toString());
	}

	public void testIncludeFragmentReplaceNested() throws IOException, HttpErrorPage {
		String page = "before <esi:include src='$(PROVIDER{mock})/fragment'><esi:replace fragment='test'>replacement</esi:replace></esi:include> after";
		String includedPage = "-incl-page-start <esi:fragment name='test'>-<esi:fragment name='test'>content</esi:fragment>-</esi:fragment> incl-page-end-";
		provider.addResource("/fragment", includedPage);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before -incl-page-start replacement incl-page-end- after", out.toString());
	}
	
	public void testIncludeFragmentReplaceFragment() throws IOException, HttpErrorPage {
		String page = "before <esi:include src='$(PROVIDER{mock})/fragment' fragment='toInclude'><esi:replace fragment='toReplace'>replacement</esi:replace></esi:include> after";
		String includedPage = "-incl-page-start <esi:fragment name='toInclude'>begin include <esi:fragment name='toReplace'>content to replace</esi:fragment> end include</esi:fragment> incl-page-end-";
		provider.addResource("/fragment", includedPage);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("before begin include replacement end include after", out.toString());
	}

}
