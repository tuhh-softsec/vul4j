package org.esigate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;

public class DriverTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		MockDriver provider = new MockDriver("mock");
		provider.addResource("/testBlock",
				"abc some<!--$beginblock$A$-->some text goes here<!--$endblock$A$--> cdf hello");
		provider.addResource(
				"/testTemplateFullPage",
				"some <!--$beginparam$key$-->some hidden text goes here<!--$endparam$key$--> printed");
		provider.addResource(
				"/testTemplate",
				"abc some<!--$begintemplate$A$-->some text goes here<!--$endtemplate$A$--> cdf hello");
	}

	public void testRenderBlock() throws IOException, HttpErrorPage {
		Writer out = new StringWriter();
		DriverFactory.getInstance("mock").renderBlock("/testBlock", "A", out,
				null, null, new HashMap<String, String>(), null, false);
		assertEquals("some text goes here", out.toString());

		out = new StringWriter();
		DriverFactory.getInstance("mock").renderBlock("$(vartestBlock)", "A",
				out, null, null, new HashMap<String, String>(), null, false);
		assertEquals("some text goes here", out.toString());

		out = new StringWriter();
		DriverFactory.getInstance("mock").renderBlock("/$(vartest)$(varBlock)",
				"A", out, null, null, new HashMap<String, String>(), null,
				false);
		assertEquals("some text goes here", out.toString());

	}

	public void testRenderTemplateFullPage() throws IOException, HttpErrorPage {
		StringWriter out = new StringWriter();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "'value'");
		params.put("some other key", "'another value'");
		DriverFactory.getInstance("mock").renderTemplate(
				"/testTemplateFullPage", null, out, null, null, params, null,
				null, false);
		assertFalse(out.toString().contains("key"));
		assertTrue(out.toString().contains("'value'"));
		assertFalse(out.toString().contains("some other key"));
		assertEquals("some 'value' printed", out.toString());
	}

	public void testRenderTemplate() throws IOException, HttpErrorPage {
		StringWriter out = new StringWriter();
		DriverFactory.getInstance("mock").renderTemplate("/testTemplate", "A",
				out, null, null, null, null, null, false);
		assertEquals("some text goes here", out.toString());

		out = new StringWriter();
		DriverFactory.getInstance("mock").renderTemplate("/test$(varTemplate)",
				"A", out, null, null, null, null, null, false);
		assertEquals("some text goes here", out.toString());

	}

	public void testProxyEmpty() throws Exception {
		String relUrl = "/test.html";
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		EasyMock.expect(request.getCharacterEncoding()).andReturn("ISO-8859-1")
				.anyTimes();
		request.setCharacterEncoding("ISO-8859-1");
		EasyMock.expect(request.getQueryString()).andReturn("?test=56")
				.anyTimes();
		EasyMock.expect(request.getSession(false)).andReturn(null).anyTimes();
		EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
		EasyMock.expect(request.getHeader("Host")).andReturn("localhost")
				.anyTimes();
		EasyMock.expect(request.getHeaderNames())
				.andReturn(Collections.singleton("Host"))
				.anyTimes();

		HttpResponse response = EasyMock.createMock(HttpResponse.class);
		response.setStatus(404);
		EasyMock.expect(response.getOutputStream()).andReturn(out);

		EasyMock.replay(request, response);
		try {
			DriverFactory.getInstance("mock").proxy(relUrl, request, response);
		} catch (HttpErrorPage e) {
			assertEquals(404, e.getStatusCode());
		}
		EasyMock.verify(request, response);
	}

	public void testProxy() throws Exception {
		Properties props = new Properties();
		props.setProperty("remoteUrlBase", "http://localhost:8080");
		props.setProperty("putInCache", "true");
		File localBase = new File("./target/localBase/");
		localBase.mkdirs();
		props.setProperty("localBase", localBase.getCanonicalPath());

		MockDriver provider = new MockDriver("mockTestProxy", props);
		provider.addResource("/wiki/", "<h1>Hello</h1>");

		String relUrl = "/wiki/";
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		EasyMock.expect(request.getCharacterEncoding()).andReturn("ISO-8859-1")
				.anyTimes();
		request.setCharacterEncoding("ISO-8859-1");
		EasyMock.expect(request.getQueryString()).andReturn("text=tt&lr=143")
				.anyTimes();
		EasyMock.expect(request.getSession(false)).andReturn(null).anyTimes();
		EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
		EasyMock.expect(request.getHeader("Host")).andReturn("localhost")
				.anyTimes();
		EasyMock.expect(request.getHeader("Cache-Control")).andReturn(null)
				.anyTimes();
		EasyMock.expect(request.getHeader("Pragma")).andReturn(null).anyTimes();
		EasyMock.expect(request.getHeader("Cookie")).andReturn(null).anyTimes();
		EasyMock.expect(request.getHeader("Accept"))
				.andReturn(
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.anyTimes();
		EasyMock.expect(request.getHeader("Accept-Encoding"))
				.andReturn("gzip,deflate").anyTimes();
		EasyMock.expect(request.getHeader("Accept-Language"))
				.andReturn("ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3").anyTimes();
		EasyMock.expect(request.getHeader("Accept-Charset"))
				.andReturn("ISO-8859-1").anyTimes();

		EasyMock.expect(request.getHeader("User-Agent"))
				.andReturn(
						"Mozilla/5.0 (Windows; U; Windows NT 6.1; ru; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13")
				.anyTimes();
		EasyMock.expect(request.getHeader("If-None-Match"))
				.andReturn("\"some_string\"").anyTimes();
		EasyMock.expect(request.getHeader("If-Modified-Since")).andReturn(null)
				.anyTimes();
		EasyMock.expect(
				request.getAttribute("org.esigate.UserContext#mockTestProxy"))
				.andReturn(null).anyTimes();
		request.setAttribute(
				EasyMock.eq("org.esigate.UserContext#mockTestProxy"),
				EasyMock.anyObject(UserContext.class));
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(request.getRemoteUser()).andReturn(null).anyTimes();

		EasyMock.expect(request.getHeaderNames())
				.andReturn(Arrays.asList(
						"Host", "User-Agent", "If-None-Match", "Accept", "Accept-Encoding", "Accept-Language",
						"Accept-Charset", "User-Agent", "If-None-Match"))
				.anyTimes();

		HttpResponse response = EasyMock.createMock(HttpResponse.class);
		response.setStatus(200);
		response.addHeader(EasyMock.isA(String.class),
				EasyMock.isA(String.class));
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(response.getOutputStream()).andReturn(out);

		EasyMock.replay(request, response);
		DriverFactory.getInstance("mockTestProxy").proxy(relUrl, request, response);
		EasyMock.verify(request, response);

		// Retrieve from cache
		EasyMock.reset(request, response);
		EasyMock.expect(request.getCharacterEncoding()).andReturn("ISO-8859-1")
				.anyTimes();
		request.setCharacterEncoding("ISO-8859-1");
		EasyMock.expect(request.getQueryString()).andReturn("text=tt&lr=143")
				.anyTimes();
		EasyMock.expect(request.getSession(false)).andReturn(null).anyTimes();
		EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
		EasyMock.expect(request.getHeader("Host")).andReturn("localhost")
				.anyTimes();
		EasyMock.expect(request.getHeader("Cache-Control")).andReturn(null)
				.anyTimes();
		EasyMock.expect(request.getHeader("Cache-control")).andReturn(null)
				.anyTimes();
		EasyMock.expect(request.getHeader("Pragma")).andReturn(null).anyTimes();
		EasyMock.expect(request.getHeader("Cookie")).andReturn(null).anyTimes();
		EasyMock.expect(request.getHeader("Accept"))
				.andReturn(
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.anyTimes();
		EasyMock.expect(request.getHeader("Accept-Encoding"))
				.andReturn("gzip,deflate").anyTimes();
		EasyMock.expect(request.getHeader("Accept-Language"))
				.andReturn("ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3").anyTimes();
		EasyMock.expect(request.getHeader("Accept-Charset"))
				.andReturn("ISO-8859-1").anyTimes();

		EasyMock.expect(request.getHeader("User-Agent"))
				.andReturn(
						"Mozilla/5.0 (Windows; U; Windows NT 6.1; ru; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13")
				.anyTimes();
		EasyMock.expect(request.getHeader("If-None-Match"))
				.andReturn("\"some_string\"").anyTimes();
		EasyMock.expect(request.getHeader("If-Modified-Since")).andReturn(null)
				.anyTimes();
		EasyMock.expect(
				request.getAttribute("org.esigate.UserContext#mockTestProxy"))
				.andReturn(null).anyTimes();
		request.setAttribute(
				EasyMock.eq("org.esigate.UserContext#mockTestProxy"),
				EasyMock.anyObject(UserContext.class));
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(request.getRemoteUser()).andReturn(null).anyTimes();

		EasyMock.expect(request.getHeaderNames())
				.andReturn(Arrays.asList("Host", "User-Agent", "If-None-Match", "Accept", "Accept-Encoding",
						"Accept-Language", "Accept-Charset","User-Agent", "If-None-Match"))
				.anyTimes();

		response.setStatus(200);
		response.addHeader(EasyMock.isA(String.class),
				EasyMock.isA(String.class));
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(response.getOutputStream()).andReturn(out);
		EasyMock.replay(request, response);
		DriverFactory.getInstance("mockTestProxy").proxy(relUrl, request, response);
		EasyMock.verify(request, response);
	}

	public void testProxyWithCacheRefreshDelay() throws Exception {
		Properties props = new Properties();
		props.setProperty("remoteUrlBase", "http://localhost:8080");
		props.setProperty("cacheRefreshDelay", "1");

		MockDriver provider = new MockDriver(
				"mockTestProxyWithCacheRefreshDelay", props);
		provider.addResource("/wiki/", "<h1>Hello</h1>");
		String relUrl = "/wiki/";
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		EasyMock.expect(request.getCharacterEncoding()).andReturn("ISO-8859-1")
				.anyTimes();
		request.setCharacterEncoding("ISO-8859-1");
		EasyMock.expect(request.getQueryString()).andReturn("text=tt&lr=143")
				.anyTimes();
		EasyMock.expect(request.getSession(false)).andReturn(null).anyTimes();
		EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
		EasyMock.expect(request.getHeader("Host")).andReturn("localhost")
				.anyTimes();
		EasyMock.expect(request.getHeader("Cache-Control")).andReturn(null)
				.anyTimes();
		EasyMock.expect(request.getHeader("Pragma")).andReturn(null).anyTimes();
		EasyMock.expect(request.getHeader("Cookie")).andReturn(null).anyTimes();
		EasyMock.expect(request.getHeader("Accept"))
				.andReturn(
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.anyTimes();
		EasyMock.expect(request.getHeader("Accept-Encoding"))
				.andReturn("gzip,deflate").anyTimes();
		EasyMock.expect(request.getHeader("Accept-Language"))
				.andReturn("ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3").anyTimes();
		EasyMock.expect(request.getHeader("Accept-Charset"))
				.andReturn("ISO-8859-1").anyTimes();

		EasyMock.expect(request.getHeader("User-Agent"))
				.andReturn(
						"Mozilla/5.0 (Windows; U; Windows NT 6.1; ru; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13")
				.anyTimes();
		EasyMock.expect(request.getHeader("If-None-Match"))
				.andReturn("\"some_string\"").anyTimes();
		EasyMock.expect(request.getHeader("If-Modified-Since")).andReturn(null)
				.anyTimes();
		EasyMock.expect(
				request.getAttribute("org.esigate.UserContext#mockTestProxyWithCacheRefreshDelay"))
				.andReturn(null).anyTimes();
		request.setAttribute(
				EasyMock.eq("org.esigate.UserContext#mockTestProxyWithCacheRefreshDelay"),
				EasyMock.anyObject(UserContext.class));
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(request.getRemoteUser()).andReturn(null).anyTimes();

		EasyMock.expect(request.getHeaderNames())
				.andReturn(Arrays.asList("Host", "User-Agent", "If-None-Match", "Accept", "Accept-Encoding",
						"Accept-Language", "Accept-Charset", "User-Agent", "If-None-Match"))
				.anyTimes();

		HttpResponse response = EasyMock.createMock(HttpResponse.class);
		response.setStatus(200);
		response.addHeader(EasyMock.isA(String.class),
				EasyMock.isA(String.class));
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(response.getOutputStream()).andReturn(out);

		EasyMock.replay(request, response);
		DriverFactory.getInstance("mockTestProxyWithCacheRefreshDelay").proxy(relUrl, request, response);
		EasyMock.verify(request, response);

		Thread.sleep(1000);
		// Retrieve from cache
		EasyMock.reset(request, response);
		EasyMock.expect(request.getCharacterEncoding()).andReturn("ISO-8859-1")
				.anyTimes();
		request.setCharacterEncoding("ISO-8859-1");
		EasyMock.expect(request.getQueryString()).andReturn("text=tt&lr=143")
				.anyTimes();
		EasyMock.expect(request.getSession(false)).andReturn(null).anyTimes();
		EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
		EasyMock.expect(request.getHeader("Host")).andReturn("localhost")
				.anyTimes();
		EasyMock.expect(request.getHeader("Cache-Control")).andReturn(null)
				.anyTimes();
		EasyMock.expect(request.getHeader("Cache-control")).andReturn(null)
				.anyTimes();
		EasyMock.expect(request.getHeader("Pragma")).andReturn(null).anyTimes();
		EasyMock.expect(request.getHeader("Cookie")).andReturn(null).anyTimes();
		EasyMock.expect(request.getHeader("Accept"))
				.andReturn(
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.anyTimes();
		EasyMock.expect(request.getHeader("Accept-Encoding"))
				.andReturn("gzip,deflate").anyTimes();
		EasyMock.expect(request.getHeader("Accept-Language"))
				.andReturn("ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3").anyTimes();
		EasyMock.expect(request.getHeader("Accept-Charset"))
				.andReturn("ISO-8859-1").anyTimes();

		EasyMock.expect(request.getHeader("User-Agent"))
				.andReturn(
						"Mozilla/5.0 (Windows; U; Windows NT 6.1; ru; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13")
				.anyTimes();
		EasyMock.expect(request.getHeader("If-None-Match"))
				.andReturn("\"some_string\"").anyTimes();
		EasyMock.expect(request.getHeader("If-Modified-Since")).andReturn(null)
				.anyTimes();
		EasyMock.expect(
				request.getAttribute("org.esigate.UserContext#mockTestProxyWithCacheRefreshDelay"))
				.andReturn(null).anyTimes();
		request.setAttribute(
				EasyMock.eq("org.esigate.UserContext#mockTestProxyWithCacheRefreshDelay"),
				EasyMock.anyObject(UserContext.class));
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(request.getRemoteUser()).andReturn(null).anyTimes();

		EasyMock.expect(request.getHeaderNames())
				.andReturn(Arrays.asList("Host", "User-Agent", "If-None-Match", "Accept", "Accept-Encoding",
						"Accept-Language", "Accept-Charset", "User-Agent", "If-None-Match"))
				.anyTimes();

		response.setStatus(200);
		response.addHeader(EasyMock.isA(String.class),
				EasyMock.isA(String.class));
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(response.getOutputStream()).andReturn(out);
		EasyMock.replay(request, response);
		DriverFactory.getInstance("mockTestProxyWithCacheRefreshDelay").proxy(relUrl, request, response);
		EasyMock.verify(request, response);

		// Retrieve from cache
		EasyMock.reset(request, response);
		EasyMock.expect(request.getCharacterEncoding()).andReturn("ISO-8859-1")
				.anyTimes();
		request.setCharacterEncoding("ISO-8859-1");
		EasyMock.expect(request.getQueryString()).andReturn("text=tt&lr=143")
				.anyTimes();
		EasyMock.expect(request.getSession(false)).andReturn(null).anyTimes();
		EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
		EasyMock.expect(request.getHeader("Host")).andReturn("localhost")
				.anyTimes();
		EasyMock.expect(request.getHeader("Cache-Control")).andReturn(null)
				.anyTimes();
		EasyMock.expect(request.getHeader("Cache-control")).andReturn(null)
				.anyTimes();
		EasyMock.expect(request.getHeader("Pragma")).andReturn(null).anyTimes();
		EasyMock.expect(request.getHeader("Cookie")).andReturn(null).anyTimes();
		EasyMock.expect(request.getHeader("Accept"))
				.andReturn(
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.anyTimes();
		EasyMock.expect(request.getHeader("Accept-Encoding"))
				.andReturn("gzip,deflate").anyTimes();
		EasyMock.expect(request.getHeader("Accept-Language"))
				.andReturn("ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3").anyTimes();
		EasyMock.expect(request.getHeader("Accept-Charset"))
				.andReturn("ISO-8859-1").anyTimes();

		EasyMock.expect(request.getHeader("User-Agent"))
				.andReturn(
						"Mozilla/5.0 (Windows; U; Windows NT 6.1; ru; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13")
				.anyTimes();
		EasyMock.expect(request.getHeader("If-None-Match"))
				.andReturn("\"some_string\"").anyTimes();
		EasyMock.expect(request.getHeader("If-Modified-Since")).andReturn(null)
				.anyTimes();

		response.setStatus(200);
		response.addHeader(EasyMock.isA(String.class),
				EasyMock.isA(String.class));
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(response.getOutputStream()).andReturn(out);
		EasyMock.replay(request, response);
		DriverFactory.getInstance("mockTestProxyWithCacheRefreshDelay").proxy(relUrl, request, response);
		EasyMock.verify(request, response);
	}

	public void testProxyWithoutCache() throws Exception {
		Properties props = new Properties();
		props.setProperty("remoteUrlBase", "http://localhost:8080");
		props.setProperty("useCache", "false");
		new MockDriver("mockTestProxyWithoutCache", props);
		MockDriver provider = new MockDriver("mockTestProxyWithoutCache", props);
		provider.addResource("/wiki/Portal:Contents", "<h1>Hello</h1>");
		String relUrl = "/wiki/Portal:Contents";
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		EasyMock.expect(request.getCharacterEncoding()).andReturn("ISO-8859-1")
				.anyTimes();
		request.setCharacterEncoding("ISO-8859-1");
		EasyMock.expect(request.getQueryString()).andReturn("?test=56")
				.anyTimes();
		EasyMock.expect(request.getSession(false)).andReturn(null).anyTimes();
		EasyMock.expect(request.getMethod()).andReturn("GET").anyTimes();
		EasyMock.expect(request.getHeader("Host")).andReturn("localhost")
				.anyTimes();
		EasyMock.expect(request.getHeader("Cache-Control")).andReturn(null)
				.anyTimes();
		EasyMock.expect(request.getHeader("Pragma")).andReturn(null).anyTimes();
		EasyMock.expect(request.getHeader("Accept"))
				.andReturn(
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.anyTimes();
		EasyMock.expect(request.getHeader("Accept-Encoding"))
				.andReturn("gzip,deflate").anyTimes();
		EasyMock.expect(request.getHeader("Accept-Language"))
				.andReturn("ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3").anyTimes();
		EasyMock.expect(request.getHeader("Accept-Charset"))
				.andReturn("ISO-8859-1").anyTimes();

		EasyMock.expect(request.getHeader("User-Agent"))
				.andReturn(
						"Mozilla/5.0 (Windows; U; Windows NT 6.1; ru; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13")
				.anyTimes();
		EasyMock.expect(request.getHeader("If-None-Match"))
				.andReturn("\"some_string\"").anyTimes();
		EasyMock.expect(request.getHeader("If-Modified-Since")).andReturn(null)
				.anyTimes();
		EasyMock.expect(
				request.getAttribute("org.esigate.UserContext#mockTestProxyWithoutCache"))
				.andReturn(null).anyTimes();
		request.setAttribute(EasyMock
				.eq("org.esigate.UserContext#mockTestProxyWithoutCache"),
				EasyMock.anyObject(UserContext.class));
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(request.getRemoteUser()).andReturn(null).anyTimes();

		HttpResponse response = EasyMock.createMock(HttpResponse.class);
		response.setStatus(200);
		response.addHeader(EasyMock.isA(String.class),
				EasyMock.isA(String.class));
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(response.getOutputStream()).andReturn(out);

		EasyMock.replay(request, response);
		DriverFactory.getInstance("mockTestProxyWithoutCache").proxy(relUrl, request, response);
		EasyMock.verify(request, response);
	}
}
