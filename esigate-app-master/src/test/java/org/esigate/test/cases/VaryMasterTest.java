package org.esigate.test.cases;

import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

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
	private String doCookieRequest(String page, boolean addCookie,
			String value, boolean refresh) throws MalformedURLException,
			IOException, SAXException {

		WebConversation webConversation = new WebConversation();
		webConversation.getClientProperties().setAutoRedirect(false);

		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + page);

		// Httpunit's default is to force reload. Set Pragma and cache-control
		// to anything to disable.
		if (!refresh) {
			req.setHeaderField("Pragma", "enable-cache");
			req.setHeaderField("Cache-Control", "enable-cache");
		}

		// Add session to ensure cache will be used. (changing session changes
		// page with Vary: Cookie)
		if (sessionId != null) {
			webConversation.putCookie("JSESSIONID", sessionId);
		}
		if (addCookie) {
			webConversation.putCookie("test-cookie", value);
		}

		// Ensure content is valid.
		WebResponse resp = webConversation.getResponse(req);
		if (addCookie) {
			assertTrue(resp.getText().contains(value));
		} else {
			assertTrue(resp.getText().contains("no cookie"));
		}

		// Save session id
		if (webConversation.getCookieValue("JSESSIONID") != null) {
			sessionId = webConversation.getCookieValue("JSESSIONID");
		}

		// Return page timestamp. Can be used to detect cache hits.
		return resp.getText().substring(resp.getText().indexOf("stime") + 5,
				resp.getText().indexOf("etime"));
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

		// This is for initialization : get JSESSIONID
		doCookieRequest(page, true, "init", false);

		// Cache test
		String value1 = doCookieRequest(page, true, "value1", false);
		String valueNull = doCookieRequest(page, false, null, false);
		assertEquals(value1, doCookieRequest(page, true, "value1", false));
		String value2 = doCookieRequest(page, true, "value2", false);
		assertEquals(valueNull, doCookieRequest(page, false, null, false));
		assertEquals(value1, doCookieRequest(page, true, "value1", false));
		assertEquals(value2, doCookieRequest(page, true, "value2", false));
		String value3 = doCookieRequest(page, true, "value3", false);
		assertEquals(valueNull, doCookieRequest(page, false, null, false));
		assertEquals(value3, doCookieRequest(page, true, "value3", false));

		// Test refresh
		String value1Refresh = doCookieRequest(page, true, "value1", true);
		// assertFalse(value1.equals(value1Refresh));
		assertEquals(value1Refresh,
				doCookieRequest(page, true, "value1", false));

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

}
