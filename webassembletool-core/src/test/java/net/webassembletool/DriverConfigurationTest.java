package net.webassembletool;

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
}
