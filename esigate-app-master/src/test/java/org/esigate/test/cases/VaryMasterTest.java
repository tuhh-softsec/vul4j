package org.esigate.test.cases;

import junit.framework.TestCase;

/**
 * "Vary" header testing
 * 
 * @author Nicolas Richeton
 */
public class VaryMasterTest extends TestCase {
	private static String PAGE_CACHE_AUTO = "vary-auto.jsp";
	private static String PAGE_CACHE_DELAY = "vary.jsp";

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
