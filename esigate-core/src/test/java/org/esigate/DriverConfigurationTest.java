package org.esigate;

import java.util.Collection;
import java.util.Properties;

import junit.framework.TestCase;

import org.esigate.url.IpHashBaseUrlRetrieveStrategy;
import org.esigate.url.RoundRobinBaseUrlRetrieveStrategy;
import org.esigate.url.SingleBaseUrlRetrieveStrategy;
import org.esigate.url.StickySessionBaseUrlRetrieveStrategy;

/**
 * DriverConfiguration test case.
 * 
 * @author Alexis Thaveau
 * 
 */
public class DriverConfigurationTest extends TestCase {

	/** Test default configuration */
	public void testDefaultConfig() {
		DriverConfiguration defaultConfig = new DriverConfiguration("test", new Properties());
		// Parsable contentTypes
		validateParsableContentTypes(defaultConfig.getParsableContentTypes(), "text/html", "application/xhtml+xml");
	}

	/**
	 * Test property parsableContentTypes
	 */
	public void testParsableContentTypes() {
		Properties properties = new Properties();
		properties.put("parsableContentTypes", "text/plain");
		DriverConfiguration config = new DriverConfiguration("test-parsable", properties);
		validateParsableContentTypes(config.getParsableContentTypes(), "text/plain");

		properties = new Properties();
		properties.put("parsableContentTypes", "text/plain, text/html");
		config = new DriverConfiguration("test-parsable", properties);
		validateParsableContentTypes(config.getParsableContentTypes(), "text/plain", "text/html");

		properties = new Properties();
		properties.put("parsableContentTypes", "text/plain, text/html,application/x");
		config = new DriverConfiguration("test-parsable", properties);
		validateParsableContentTypes(config.getParsableContentTypes(), "text/plain", "text/html", "application/x");
	}

	private void validateParsableContentTypes(Collection<String> actual, String... expected) {
		assertNotNull("parsableContentTypes should not be null", actual);
		assertEquals("parsableContentTypes should contains only " + expected.length + " element(s)", expected.length, actual.size());
		for (String value : expected) {
			assertTrue("parsableContentTypes should contain " + value, actual.contains(value));
		}
	}

	public void testGetTimeout() {
		// default setup (1000 and 10000)
		Properties properties = new Properties();
		DriverConfiguration config = new DriverConfiguration("test-parsable", properties);
		assertEquals("default connectTimeout should be 1000", 1000, config.getConnectTimeout());
		assertEquals("default socketTimeout should be 10000", 1000 * 10, config.getSocketTimeout());

		// timeout property with no other ones
		properties = new Properties();
		properties.setProperty("timeout", "5000");
		config = new DriverConfiguration("test-parsable", properties);
		assertEquals("connectTimeout should be same as 'timeout' value", 5000, config.getConnectTimeout());
		assertEquals("socketTimeout should be 10x of 'timeout' value", 5000 * 10, config.getSocketTimeout());

		// connect only
		properties = new Properties();
		properties.setProperty("connectTimeout", "3000");
		config = new DriverConfiguration("test-parsable", properties);
		assertEquals("connectTimeout should be 3000", 3000, config.getConnectTimeout());
		assertEquals("default socketTimeout should be 10000", 1000 * 10, config.getSocketTimeout());

		// timeout+connect
		properties = new Properties();
		properties.setProperty("connectTimeout", "3000");
		properties.setProperty("timeout", "5000");
		config = new DriverConfiguration("test-parsable", properties);
		assertEquals("connectTimeout should be 3000", 3000, config.getConnectTimeout());
		assertEquals("socketTimeout should be 10x of 'timeout' value", 5000 * 10, config.getSocketTimeout());
	}

	public void testIsBlackListed() {
		// by default only DriverConfiguration#DEFAULT_BLACK_LISTED_HEADERS are blacklisted
		Properties properties = new Properties();
		DriverConfiguration config = new DriverConfiguration("test-parsable", properties);

		validateResponseHeaderIsBlacklisted(config, "Content-Encoding", true);
		validateRequestHeaderIsBlacklisted(config, "Content-Length", true);
		validateRequestHeaderIsBlacklisted(config, "Content-Length".toUpperCase(), true);
		validateRequestHeaderIsBlacklisted(config, "Content-Length".toLowerCase(), true);
		validateRequestHeaderIsBlacklisted(config, "Transfer-Encoding", true);
		validateResponseHeaderIsBlacklisted(config, "Set-Cookie", true);
		validateRequestHeaderIsBlacklisted(config, "Cookie", true);
		validateRequestHeaderIsBlacklisted(config, "Connection", true);
		validateResponseHeaderIsBlacklisted(config, "Keep-Alive", true);
		validateResponseHeaderIsBlacklisted(config, "Proxy-Authenticate", true);
		validateRequestHeaderIsBlacklisted(config, "Proxy-Authorization", true);
		validateRequestHeaderIsBlacklisted(config, "TE", true);
		validateResponseHeaderIsBlacklisted(config, "Trailer", true);
		validateRequestHeaderIsBlacklisted(config, "Upgrade", true);

		// blacklisted headers are specified via 'blackListedHeaders' property -> they are merged with default
		properties = new Properties();
		properties.setProperty("blackListedHeaders", "header");
		config = new DriverConfiguration("test-parsable", properties);

		validateRequestHeaderIsBlacklisted(config, "Content-Length", true);
		validateRequestHeaderIsBlacklisted(config, "Content-Length".toUpperCase(), true);
		validateRequestHeaderIsBlacklisted(config, "Content-Length".toLowerCase(), true);
		validateResponseHeaderIsBlacklisted(config, "Content-Encoding", true);
		validateRequestHeaderIsBlacklisted(config, "Transfer-Encoding", true);
		validateResponseHeaderIsBlacklisted(config, "Set-Cookie", true);
		validateRequestHeaderIsBlacklisted(config, "Cookie", true);
		validateRequestHeaderIsBlacklisted(config, "Connection", true);
		validateResponseHeaderIsBlacklisted(config, "Keep-Alive", true);
		validateResponseHeaderIsBlacklisted(config, "Proxy-Authenticate", true);
		validateRequestHeaderIsBlacklisted(config, "Proxy-Authorization", true);
		validateRequestHeaderIsBlacklisted(config, "TE", true);
		validateResponseHeaderIsBlacklisted(config, "Trailer", true);
		validateRequestHeaderIsBlacklisted(config, "Upgrade", true);
		validateRequestHeaderIsBlacklisted(config, "header", true);
		validateRequestHeaderIsBlacklisted(config, "header".toUpperCase(), true);
	}

	public void testBaseUrl() {
		Properties properties = new Properties();

		DriverConfiguration config = new DriverConfiguration("test-baseurl", properties);

		assertEquals(null, config.getBaseUrlRetrieveStrategy());

		properties.setProperty("remoteUrlBase", "http://example.com");
		config = new DriverConfiguration("test-baseurl", properties);
		assertTrue(config.getBaseUrlRetrieveStrategy() instanceof SingleBaseUrlRetrieveStrategy);

		properties.setProperty("remoteUrlBase", "http://example.com, http://example1.com");
		config = new DriverConfiguration("test-baseurl", properties);
		assertTrue(config.getBaseUrlRetrieveStrategy() instanceof RoundRobinBaseUrlRetrieveStrategy);

		properties.setProperty("remoteUrlBase", "http://example.com, http://example1.com");
		properties.setProperty("remoteUrlBaseStrategy", "roundrobin");
		config = new DriverConfiguration("test-baseurl", properties);
		assertTrue(config.getBaseUrlRetrieveStrategy() instanceof RoundRobinBaseUrlRetrieveStrategy);

		properties.setProperty("remoteUrlBase", "http://example.com, http://example1.com");
		properties.setProperty("remoteUrlBaseStrategy", "iphash");
		config = new DriverConfiguration("test-baseurl", properties);
		assertTrue(config.getBaseUrlRetrieveStrategy() instanceof IpHashBaseUrlRetrieveStrategy);

		properties.setProperty("remoteUrlBase", "http://example.com, http://example1.com");
		properties.setProperty("remoteUrlBaseStrategy", "stickysession");
		config = new DriverConfiguration("test-baseurl", properties);
		assertTrue(config.getBaseUrlRetrieveStrategy() instanceof StickySessionBaseUrlRetrieveStrategy);

		try {
			properties.setProperty("remoteUrlBase", "http://example.com, http://example1.com");
			properties.setProperty("remoteUrlBaseStrategy", "invalid_strategy");
			config = new DriverConfiguration("test-baseurl", properties);
			fail();
		} catch (ConfigurationException e) {
			assertTrue(e.getMessage().contains("invalid_strategy"));
		} catch (Exception e) {
			fail();
		}

		try {
			properties.setProperty("remoteUrlBase", "http://example.com, ://1.com");
			config = new DriverConfiguration("test-baseurl", properties);
			fail();
		} catch (ConfigurationException e) {

		} catch (Exception e) {
			fail();
		}

	}

	public void testProxy() {
		Properties properties = new Properties();
		properties.setProperty("proxyHost", "www-cache");
		properties.setProperty("proxyPort", "3128");
		properties.setProperty("proxyUser", "username");
		properties.setProperty("proxyPassword", "password");
		DriverConfiguration config = new DriverConfiguration("test-proxy", properties);
		assertEquals("proxyHost should be www-cache", "www-cache", config.getProxyHost());
		assertEquals("proxyPort should be 3128", 3128, config.getProxyPort());
		assertEquals("proxyUser should be username", "username", config.getProxyUser());
		assertEquals("proxyPassword should be 3128", "password", config.getProxyPassword());
	}

	private void validateRequestHeaderIsBlacklisted(DriverConfiguration config, String header, boolean blacklisted) {
		assertEquals("'" + header + "' header should " + (blacklisted ? "" : "not ") + "be blacklisted", !config.isForwardedRequestHeader(header), blacklisted);
	}

	private void validateResponseHeaderIsBlacklisted(DriverConfiguration config, String header, boolean blacklisted) {
		assertEquals("'" + header + "' header should " + (blacklisted ? "" : "not ") + "be blacklisted", !config.isForwardedResponseHeader(header), blacklisted);
	}

	public void testDiscardRequestHeader() {
		Properties props = new Properties();
		props.put("discardRequestHeaders", "dummy1,dummy2");
		DriverConfiguration driverConfiguration = new DriverConfiguration("dummy", props);
		assertFalse("Header should be discarded", driverConfiguration.isForwardedRequestHeader("dummy1"));
		assertFalse("Header should be discarded", driverConfiguration.isForwardedRequestHeader("dummy2"));
		assertTrue("Header should be forwarded", driverConfiguration.isForwardedRequestHeader("dummy3"));
	}

	public void testForwardRequestHeader() {
		Properties props = new Properties();
		props.put("forwardRequestHeaders", "Authorization");
		DriverConfiguration driverConfiguration = new DriverConfiguration("dummy", props);
		assertTrue("Header should be forwarded", driverConfiguration.isForwardedRequestHeader("Authorization"));
	}

	public void testDiscardResponseHeader() {
		Properties props = new Properties();
		props.put("discardRequestHeaders", "dummy1,dummy2");
		DriverConfiguration driverConfiguration = new DriverConfiguration("dummy", props);
		assertFalse("Header should be discarded", driverConfiguration.isForwardedRequestHeader("dummy1"));
		assertFalse("Header should be discarded", driverConfiguration.isForwardedRequestHeader("dummy2"));
		assertTrue("Header should be forwarded", driverConfiguration.isForwardedRequestHeader("dummy3"));
	}

	public void testForwardResponseHeader() {
		Properties props = new Properties();
		props.put("forwardRequestHeaders", "WWW-Authenticate");
		DriverConfiguration driverConfiguration = new DriverConfiguration("dummy", props);
		assertTrue("Header should be forwarded", driverConfiguration.isForwardedRequestHeader("WWW-Authenticate"));
	}
}
