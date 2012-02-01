package org.esigate;

import java.util.Collection;
import java.util.Properties;

import junit.framework.TestCase;

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

	private void validateParsableContentTypes(Collection<String> actual, String ... expected) {
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

		validateHeader(config, null, true);
		validateHeader(config, "", true);

		validateHeader(config, "Content-Length", true);
		validateHeader(config, "Content-Length".toUpperCase(), true);
		validateHeader(config, "Content-Length".toLowerCase(), true);
		validateHeader(config, "Content-Encoding", true);
		validateHeader(config, "Transfer-Encoding", true);
		validateHeader(config, "Set-Cookie", true);
		validateHeader(config, "Cookie", true);
		validateHeader(config, "Connection", true);
		validateHeader(config, "Keep-Alive", true);
		validateHeader(config, "Proxy-Authenticate", true);
		validateHeader(config, "Proxy-Authorization", true);
		validateHeader(config, "TE", true);
		validateHeader(config, "Trailers", true);
		validateHeader(config, "Upgrade", true);

		// blacklisted headers are specified via 'blackListedHeaders' property
		properties = new Properties();
		properties.setProperty("blackListedHeaders", "header");
		config = new DriverConfiguration("test-parsable", properties);

		validateHeader(config, null, true);
		validateHeader(config, "", true);
		validateHeader(config, "Content-Length", false);
		validateHeader(config, "header", true);
		validateHeader(config, "header".toUpperCase(), true);
	}

	private void validateHeader(DriverConfiguration config, String header, boolean blacklisted) {
		assertEquals("'" + header + "' header should " + (blacklisted ? "" : "not ") + "be blacklisted",
				config.isBlackListed(header), blacklisted);
	}
}
