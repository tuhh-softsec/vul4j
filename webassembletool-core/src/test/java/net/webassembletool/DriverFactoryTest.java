package net.webassembletool;

import java.util.Properties;

import junit.framework.TestCase;

public class DriverFactoryTest extends TestCase {

	protected void setUp() {
		DriverFactory.configure("some", new Properties());
	}

	public void testConfigureStringProperties1() {
		String id = DriverFactoryTest.class.getName();
		try {
			DriverFactory.getInstance(id);
			fail("should throw ConfigurationException as there should be no provider named " + id);
		} catch (ConfigurationException e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains(id));
		}

		Properties props = new Properties();
		props.setProperty("remoteUrlBase", "http://base.url");
		DriverFactory.configure(id, props);
		Driver instance = DriverFactory.getInstance(id);
		assertNotNull(instance);
		assertEquals("http://base.url", instance.getBaseURL());
	}

	public void testConfigureStringProperties2() {
		try {
			DriverFactory.getInstance();
			fail("should throw ConfigurationException as there should be no provider named default");
		} catch (ConfigurationException e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains("default"));
		}

		Properties props = new Properties();
		props.setProperty("remoteUrlBase", "http://base.url");
		DriverFactory.configure(null, props);
		Driver instance = DriverFactory.getInstance();
		assertNotNull(instance);
		assertEquals("http://base.url", instance.getBaseURL());
	}
}
