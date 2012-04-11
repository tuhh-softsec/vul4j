package org.esigate.test.cases;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.lang3.StringUtils;
import org.esigate.DriverConfiguration;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * Response headers forwarding or discarding tests. Standard HTTP headers are
 * defined in:
 * <ul>
 * <li><a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html">RFC
 * 2616</a></li>
 * <li><a href="http://www.ietf.org/rfc/rfc2817.txt">RFC 2817</a></li>
 * </ul>
 * 
 * @author Francois-Xavier Bonnet
 */
public class ResponseHeadersTest extends TestCase {
	private final static String APPLICATION_PATH = "http://localhost:8080/esigate-app-aggregator/";

	private void assertHeaderDiscarded(String name) throws Exception {
		assertHeaderDiscarded(name, "dummy");
	}

	private void assertHeaderDiscarded(String name, String value)
			throws Exception {
		String resp = sendRequestAndExpectResponseHeader(name, value);
		assertNull("HTTP header " + name + " should not be forwarded", resp);
	}

	private void assertHeaderForwarded(String name) throws Exception {
		assertHeaderForwarded(name, "dummy");
	}

	private void assertHeaderForwarded(String name, String value)
			throws Exception {
		String resp = sendRequestAndExpectResponseHeader(name, value);
		assertEquals("HTTP header " + name + " should be forwarded", value,
				resp);
	}

	/**
	 * Location header should be rewritten
	 * 
	 * @throws Exception
	 */
	private void assertUriInHeaderIsRewritten(String name) throws Exception {
		String resp = sendRequestAndExpectResponseHeader(name,
				APPLICATION_PATH.replaceFirst("aggregator", "aggregated1")
						+ "dummy");
		assertEquals(
				name
						+ " header should be rewritten ('aggregator' replaced with 'aggregated1')",
				APPLICATION_PATH + "dummy", resp);
	}

	private String sendRequestAndExpectResponseHeader(String name, String value)
			throws Exception {
		WebConversation webConversation = new WebConversation();
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH
				+ "response-headers.jsp");
		req.setHeaderField("X-response-header-" + name, value);
		WebResponse resp = webConversation.getResponse(req);
		return resp.getHeaderField(name);
	}

	public void testAge() throws Exception {
		// FIXME Age header should be recalculated as described in HTTP
		// specification. Probably the age of the cache entry + the age header
		// received in the response
		assertHeaderDiscarded("Age");
	}

	public void testAllow() throws Exception {
		assertHeaderForwarded("Allow");
	}

	public void testCacheControl() throws Exception {
		assertHeaderForwarded("Cache-Control");
	}

	public void testContentDisposition() throws Exception {
		assertHeaderForwarded("Content-Disposition");
	}

	/**
	 * Content-encoding should not be forwarded as EsiGate will decompress gzip
	 * responses
	 * 
	 * @throws Exception
	 */
	public void testContentEncoding() throws Exception {
		assertHeaderDiscarded("Content-Encoding", "gzip");
	}

	public void testContentLanguage() throws Exception {
		assertHeaderForwarded("Content-Language");
	}

	/**
	 * Keep-alive is managed by the servlet container, we must not try to set it
	 * 
	 * @throws Exception
	 */
	public void testContentLength() throws Exception {
		Properties props = new Properties();
		DriverConfiguration driverConfiguration = new DriverConfiguration(
				"dummy", props);
		assertFalse(
				"Content-Length is managed by the servlet container, we must not try to change",
				driverConfiguration.isForwardedResponseHeader("Content-Length"));
	}

	/**
	 * Content-Location header should be rewritten
	 * 
	 * @throws Exception
	 */
	public void testContentLocation() throws Exception {
		assertUriInHeaderIsRewritten("Content-Location");
	}

	public void testContentMD5() throws Exception {
		assertHeaderDiscarded("Content-MD5");
	}

	public void testContentRange() throws Exception {
		assertHeaderForwarded("Content-Range");
	}

	/**
	 * Content-type is often modified by tha application server as it
	 * automatically sets it if not defined and it is case-insensitive so we can
	 * have differences depending on the server vendor and version. So we just
	 * test that 'text/plain' was forwarded, no matter the charset.
	 * 
	 * @throws Exception
	 */
	public void testContentType() throws Exception {
		String resp = sendRequestAndExpectResponseHeader("Content-Type",
				"text/plain");
		if (!StringUtils.startsWithIgnoreCase(resp, "text/plain")) {
			fail("HTTP header Content-Type should be forwarded, expected 'text/plain', got '"
					+ resp + "'");
		}
	}

	public void testDate() throws Exception {
		// FIXME Date header set automatically by application server, cannot
		// override it on Tomcat 7.0 -> problem with cache validation
		// on Jetty 6 you can override it only with setDateHeader method
		// setHeader method will not work !
		// assertHeaderForwarded("Date", "Fri, 06 Apr 2012 15:18:12 GMT");
	}

	public void testETag() throws Exception {
		assertHeaderForwarded("E-tag");
	}

	public void testExpires() throws Exception {
		assertHeaderForwarded("Expires");
	}

	/**
	 * Keep-alive is managed by the servlet container, we must not try to change
	 * it
	 * 
	 * @throws Exception
	 */
	public void testKeepAlive() throws Exception {
		Properties props = new Properties();
		DriverConfiguration driverConfiguration = new DriverConfiguration(
				"dummy", props);
		assertFalse(
				"Keep-alive is managed by the servlet container, we must not try to change",
				driverConfiguration.isForwardedResponseHeader("Keep-Alive"));
	}

	public void testLastModified() throws Exception {
		assertHeaderForwarded("Last-Modified");
	}

	/**
	 * Link header should be rewritten
	 * 
	 * @throws Exception
	 */
	public void testLink() throws Exception {
		assertUriInHeaderIsRewritten("Link");
	}

	/**
	 * Location header should be rewritten
	 * 
	 * @throws Exception
	 */
	public void testLocation() throws Exception {
		assertUriInHeaderIsRewritten("Location");
	}

	/**
	 * P3p header should be rewritten
	 * 
	 * @throws Exception
	 */
	public void testP3p() throws Exception {
		assertUriInHeaderIsRewritten("P3p");
	}

	/**
	 * Default ignored, see authentication
	 * 
	 * @throws Exception
	 */
	public void testProxyAuthenticate() throws Exception {
		assertHeaderDiscarded("Proxy-Authenticate");
	}

	public void testRefresh() throws Exception {
		assertHeaderForwarded("Refresh");
	}

	public void testRetryAfter() throws Exception {
		assertHeaderForwarded("Retry-After");
	}

	public void testServer() throws Exception {
		assertHeaderForwarded("Server");
	}

	/**
	 * Trailer Ignored (chunked encoding managed by the container)
	 * 
	 * @throws Exception
	 */
	public void testTrailer() throws Exception {
		assertHeaderDiscarded("Trailer");
	}

	/**
	 * Transfer-Encoding Ignored (chunked encoding managed by the container). We
	 * cannot really test it live as setting this header generates invalid
	 * responses.
	 * 
	 * @throws Exception
	 */
	public void testTransferEncoding() throws Exception {
		Properties props = new Properties();
		DriverConfiguration driverConfiguration = new DriverConfiguration(
				"dummy", props);
		assertFalse(
				"Transfer-Encoding is managed by the servlet container, we must not try to change",
				driverConfiguration
						.isForwardedResponseHeader("Transfer-Encoding"));
	}

	public void testVary() throws Exception {
		assertHeaderForwarded("Vary");
	}

	public void testVia() throws Exception {
		assertHeaderForwarded("Via");
	}

	public void testWarning() throws Exception {
		assertHeaderForwarded("Warning");
	}

	public void testWWWAuthenticate() throws Exception {
		// Default ignored, see authentication
		assertHeaderDiscarded("WWW-Authenticate");
	}

	public void testXPoweredBy() throws Exception {
		assertHeaderForwarded("X-Powered-By");
	}

}
