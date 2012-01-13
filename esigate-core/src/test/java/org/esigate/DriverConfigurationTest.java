package org.esigate;

import java.util.Properties;

import junit.framework.TestCase;

/**
 * DriverConfiguration test case.
 * 
 * @author Alexis Thaveau
 * 
 */
public class DriverConfigurationTest extends TestCase {

	DriverConfiguration defaultConfig;

	@Override
	protected void setUp() throws Exception {
		defaultConfig = new DriverConfiguration("test", new Properties());
	}

	/**
	 * Test default configuration
	 */
	public void testDefaultConfig() {
		// Parsable contentTypes
		assertTrue("text/html is not in default parsable cotent-types",
				defaultConfig.getParsableContentTypes().contains("text/html"));
		assertTrue(
				"application/xhtml+xml is not in default parsable cotent-types",
				defaultConfig.getParsableContentTypes().contains(
						"application/xhtml+xml"));

	}

	/**
	 * Test property parsableContentTypes
	 */
	public void testParsableContentTypes() {
		Properties properties = new Properties();
		properties.put("parsableContentTypes", "text/plain");
		DriverConfiguration config = new DriverConfiguration("test-parsable",
				properties);
		assertEquals("parsableContentTypes should contains only 1 element", 1,
				config.getParsableContentTypes().size());
		assertEquals("parsableContentTypes should contains text/plain",
				"text/plain", config.getParsableContentTypes().get(0));

		properties = new Properties();
		properties.put("parsableContentTypes", "text/plain, text/html");
		config = new DriverConfiguration("test-parsable", properties);
		assertEquals("parsableContentTypes should contains only 2 elements", 2,
				config.getParsableContentTypes().size());
		assertEquals("parsableContentTypes should contains text/plain",
				"text/plain", config.getParsableContentTypes().get(0));

		assertEquals("parsableContentTypes should contains text/html",
				"text/html", config.getParsableContentTypes().get(1));

		properties = new Properties();
		properties.put("parsableContentTypes",
				"text/plain, text/html,application/x");
		config = new DriverConfiguration("test-parsable", properties);
		assertEquals("parsableContentTypes should contains only 3 elements", 3,
				config.getParsableContentTypes().size());
		assertEquals("parsableContentTypes should contains text/plain",
				"text/plain", config.getParsableContentTypes().get(0));
		assertEquals("parsableContentTypes should contains text/html",
				"text/html", config.getParsableContentTypes().get(1));
		assertEquals("parsableContentTypes should contains application/x",
				"application/x", config.getParsableContentTypes().get(2));

	}

	public void testIsBlackListed() {
		// by default only 'Content-Length,Content-Encoding,Transfer-Encoding,Set-Cookie,Cookie' are blacklisted
		Properties properties = new Properties();
		DriverConfiguration config = new DriverConfiguration("test-parsable",
				properties);

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
