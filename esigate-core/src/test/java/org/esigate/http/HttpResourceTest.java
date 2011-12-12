package org.esigate.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;
import org.easymock.EasyMock;
import org.esigate.Driver;
import org.esigate.ResourceContext;
import org.esigate.output.Output;
import org.esigate.test.MockOutput;

public class HttpResourceTest extends TestCase {

	public void testEmpty() {
		// reserved only to hide warnings about test cases without tests
	}

	public void doNotTestRedirect() throws Exception {
		HttpClient httpClient = EasyMock.createMock(HttpClient.class);
		org.esigate.api.HttpRequest originalRequest = EasyMock.createMock(org.esigate.api.HttpRequest.class);
		org.esigate.api.HttpResponse originalResponse = EasyMock.createMock(org.esigate.api.HttpResponse.class);
		HttpResponse httpResponse = EasyMock.createMock(HttpResponse.class);
		HttpEntity entity = EasyMock.createMock(HttpEntity.class);
		StatusLine statusLine = EasyMock.createMock(StatusLine.class);
		Header header = EasyMock.createMock(Header.class);

		Properties props = new Properties();
		props.setProperty("remoteUrlBase",
				"http://localhost:8080/esigate-app-aggregated1/");
		props.setProperty("uriEncoding", "UTF-8");
		props.setProperty("timeout", "5000");
		props.setProperty("useCache", "false");

		Driver driver = new Driver("test", props);
		ResourceContext resourceContext = new ResourceContext(driver,
				"/redirect.jsp", null, originalRequest, originalResponse);

		EasyMock.expect(originalRequest.getCharacterEncoding())
				.andReturn("UTF-8").anyTimes();
		EasyMock.expect(originalRequest.getQueryString())
				.andReturn(
						"http://localhost:8080/esigate-app-aggregator/redirect.jsp")
				.anyTimes();
		EasyMock.expect(originalRequest.getRequestURL())
				.andReturn(
						new String("http://localhost:8080/esigate-app-aggregator/redirect.jsp"))
				.anyTimes();
		EasyMock.expect(originalRequest.getSession(false)).andReturn(null)
				.anyTimes();
		EasyMock.expect(originalRequest.getRemoteUser()).andReturn(null)
				.anyTimes();
		EasyMock.expect(originalRequest.getHeader("User-Agent"))
				.andReturn(
						"Mozilla/5.0 (Windows; U; Windows NT 6.1; ru; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13")
				.anyTimes();
		EasyMock.expect(originalRequest.getHeader("Accept"))
				.andReturn(
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				.anyTimes();
		EasyMock.expect(originalRequest.getHeader("Accept-Encoding"))
				.andReturn("gzip,deflate").anyTimes();
		EasyMock.expect(originalRequest.getHeader("Accept-Language"))
				.andReturn("ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3").anyTimes();
		EasyMock.expect(originalRequest.getHeader("Accept-Charset"))
				.andReturn("windows-1251,utf-8;q=0.7,*;q=0.7").anyTimes();
		EasyMock.expect(originalRequest.getHeader("Cache-Control"))
				.andReturn(null).anyTimes();
		EasyMock.expect(originalRequest.getHeader("Pragma")).andReturn(null)
				.anyTimes();

		EasyMock.expect(
				httpClient.execute((HttpHost) EasyMock.anyObject(),
						(HttpRequest) EasyMock.anyObject(),
						(HttpContext) EasyMock.anyObject()))
				.andReturn(httpResponse).anyTimes();

		EasyMock.expect(httpResponse.getStatusLine()).andReturn(statusLine)
				.anyTimes();
		EasyMock.expect(httpResponse.getEntity()).andReturn(entity).anyTimes();
		EasyMock.expect(entity.getContentType()).andReturn(new Header() {

			public String getValue() {
				return "text/html";
			}

			public String getName() {
				return "Content-Type";
			}

			public HeaderElement[] getElements() throws ParseException {
				return new HeaderElement[] {};
			}
		}).anyTimes();
		EasyMock.expect(entity.getContent()).andReturn(
				new ByteArrayInputStream(new byte[1024]));

		EasyMock.expect(statusLine.getStatusCode())
				.andReturn(HttpServletResponse.SC_MOVED_TEMPORARILY).anyTimes();
		EasyMock.expect(statusLine.getReasonPhrase())
				.andReturn("Moved Permanently").anyTimes();
		EasyMock.expect(httpResponse.getFirstHeader("Location"))
				.andReturn(header).anyTimes();
		EasyMock.expect(httpResponse.getAllHeaders()).andReturn(new Header[0])
				.anyTimes();
		EasyMock.expect(
				httpResponse.getFirstHeader((String) EasyMock.anyObject()))
				.andReturn(null).anyTimes();

		EasyMock.expect(header.getValue()).andReturn(
				"http://localhost:8080/esigate-app-aggregated1/redirected.jsp");
		EasyMock.expect(header.getValue()).andReturn(
				"http://localhost:8080/esigate-app-aggregated1/redirected.jsp");

		EasyMock.expect(header.getValue()).andReturn("http://google.com");

		EasyMock.replay(httpClient, originalRequest, originalResponse,
				httpResponse, statusLine, header, entity);

		HttpResource httpResource = new HttpResource(httpClient,
				resourceContext);
		assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY,
				httpResource.getStatusCode());
		assertEquals(false, httpResource.hasResponseBody());

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Output output = new MockOutput(outputStream);
		httpResource.render(output);

		assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY,
				output.getStatusCode());
		assertEquals(
				"http://localhost:8080/esigate-app-aggregator/redirected.jsp",
				output.getHeader("Location"));

		try {
			httpResource = new HttpResource(httpClient, resourceContext);
			fail();
		} catch (IOException e) {
			assertNotNull(e);
			assertTrue(e.getMessage().contains(
					driver.getConfiguration().getBaseURL()));
		}

	}

}
