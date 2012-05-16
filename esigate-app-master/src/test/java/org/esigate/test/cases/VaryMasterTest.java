package org.esigate.test.cases;

import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.xml.sax.SAXException;

/**
 * "Vary" header testing
 * 
 * @author Nicolas Richeton
 */
public class VaryMasterTest extends TestCase {
	private final static String APPLICATION_PATH = "http://localhost:8080/esigate-app-master/";
	private static String PAGE_CACHE_AUTO = "vary-auto.jsp";
	private static String PAGE_CACHE_DELAY = "vary.jsp";
	private String sessionId = null;

	/**
	 * Send a request with a Cookie "test-cookie" to vary.jsp (which will get
	 * content from provider) and ensure the result is valid.
	 * 
	 * @param addCookie
	 * @param value
	 * @param refresh
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws SAXException
	 */
	private String doCookieRequest(String page, String cookieValue,
			boolean forceRefresh) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);
		HttpGet request = new HttpGet(APPLICATION_PATH + page);
		if (cookieValue != null) {
			BasicClientCookie cookie = new BasicClientCookie("test-cookie",
					cookieValue);
			cookie.setDomain("localhost");
			cookie.setPath("/");
			client.getCookieStore().addCookie(cookie);
		}
		if (forceRefresh) {
			request.addHeader("Cache-Control", "no-cache");
		}
		HttpResponse response = client.execute(request);
		// Ensure content is valid.
		String text = IOUtils.toString(response.getEntity().getContent());
		assertNotNull(text);
		if (cookieValue != null) {
			assertTrue("no value '" + cookieValue + "' found",
					text.contains(cookieValue));
		} else {
			assertTrue("no cookie found", text.contains("no cookie"));
		}

		// Ensure vary and Cache-Control header were forwarded
		assertEquals("toto", response.getFirstHeader("Vary").getValue());
		assertEquals("public, max-age=3600",
				response.getFirstHeader("Cache-Control").getValue());

		client.getConnectionManager().shutdown();

		// Return page timestamp. Can be used to detect cache hits.
		return text.substring(text.indexOf("stime") + 5, text.indexOf("etime"));
	}

	/**
	 * Reset session between tests.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		sessionId = null;
	}

	/**
	 * Call test page with different cookie values and ensure cache use and
	 * content are valid.
	 * 
	 * @throws Exception
	 */
	public void testBlockVary(String page) throws Exception {
		// FIXME HttpClient cache does not support Vary: Cookie. See
		// https://issues.apache.org/jira/browse/HTTPCLIENT-1190
		// // This is for initialization : get JSESSIONID
		// doCookieRequest(page, "init", false);
		//
		// // Cache test
		// String value1 = doCookieRequest(page, "value1", false);
		// String valueNull = doCookieRequest(page, null, false);
		// assertEquals(value1, doCookieRequest(page, "value1", false));
		// String value2 = doCookieRequest(page, "value2", false);
		// assertEquals(valueNull, doCookieRequest(page, null, false));
		// assertEquals(value1, doCookieRequest(page, "value1", false));
		// assertEquals(value2, doCookieRequest(page, "value2", false));
		// String value3 = doCookieRequest(page, "value3", false);
		// assertEquals(valueNull, doCookieRequest(page, null, false));
		// assertEquals(value3, doCookieRequest(page, "value3", false));
		//
		// // Test refresh
		// String value1Refresh = doCookieRequest(page, "value1", true);
		// // assertFalse(value1.equals(value1Refresh));
		// assertEquals(value1Refresh, doCookieRequest(page, "value1", false));

	}

	/**
	 * Run all tests with auto caching based on response.
	 * 
	 * @throws Exception
	 */
	public void testBlockVaryCacheAuto() throws Exception {
		testBlockVary(PAGE_CACHE_AUTO);
	}

	/**
	 * Run all tests with caching based on configuration.
	 * 
	 * @throws Exception
	 */
	public void testBlockVaryCacheDelay() throws Exception {
		testBlockVary(PAGE_CACHE_DELAY);

	}

	public void testHttpHeadersNoCacheFromHttpUnit() {

	}

}
