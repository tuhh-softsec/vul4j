package org.esigate.test.cases;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * "Vary" header testing
 * 
 * @author Nicolas Richeton
 */
public class VaryMasterTest extends TestCase {
	private static final Logger LOG = LoggerFactory.getLogger(VaryMasterTest.class);

	/**
	 * Send a request with a Cookie "test-cookie" to vary.jsp (which will get
	 * content from provider) and ensure the result is valid.
	 * 
	 * @param cookieValue
	 * @param forceRefresh
	 * @return Page timestamp. Can be used to detect cache hits.
	 * @throws Exception
	 */
	private String doCookieRequest(String cookieValue, boolean forceRefresh) throws Exception {
		CookieStore cookieStore = new BasicCookieStore();
		HttpClientContext context = new HttpClientContext();
		context.setCookieStore(cookieStore);
		CloseableHttpClient client = HttpClients.createDefault();
		RequestConfig config = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();
		HttpGet request = new HttpGet("http://localhost:8080/esigate-app-master/vary.jsp");
		request.setConfig(config);

		if (cookieValue != null) {
			BasicClientCookie cookie = new BasicClientCookie("test-cookie", cookieValue);
			cookie.setDomain("localhost");
			cookie.setPath("/");
			cookieStore.addCookie(cookie);
		}
		if (forceRefresh) {
			request.addHeader("Cache-Control", "no-cache");
		}
		HttpResponse response = client.execute(request, context);
		// Ensure content is valid.
		String text = IOUtils.toString(response.getEntity().getContent());
		assertNotNull(text);
		LOG.debug("----- Request with cookie " + cookieValue + " and forceRefresh=" + forceRefresh + " -----> \n" + text);
		if (cookieValue != null) {
			assertTrue("no value '" + cookieValue + "' found", text.contains(cookieValue));
		} else {
			assertTrue("no cookie found", text.contains("no cookie"));
		}

		client.close();

		//
		return text.substring(text.indexOf("stime") + 5, text.indexOf("etime"));
	}

	/**
	 * Call test page with different cookie values and ensure cache use and
	 * content are valid.
	 * 
	 * @throws Exception
	 */
	public void testBlockVary() throws Exception {
		// FIXME HttpClient cache does not support Vary: Cookie. See
		// https://issues.apache.org/jira/browse/HTTPCLIENT-1190

		doCookieRequest("init", false);

		// Cache test
		String value1 = doCookieRequest("value1", false);
		String valueNull = doCookieRequest(null, false);
		assertEquals(value1, doCookieRequest("value1", false));
		String value2 = doCookieRequest("value2", false);
		assertEquals(valueNull, doCookieRequest(null, false));
		assertEquals(value1, doCookieRequest("value1", false));
		assertEquals(value2, doCookieRequest("value2", false));
		String value3 = doCookieRequest("value3", false);
		assertEquals(valueNull, doCookieRequest(null, false));
		assertEquals(value3, doCookieRequest("value3", false));

		// Test refresh but in fact there should be no refresh in an include
		String value1Refresh = doCookieRequest("value1", true);
		// assertFalse(value1.equals(value1Refresh));
		assertEquals(value1Refresh, doCookieRequest("value1", false));

	}

}
