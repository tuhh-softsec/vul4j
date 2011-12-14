package org.esigate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import junit.framework.TestCase;

public class DriverFactoryTest extends TestCase {

	@Override
	protected void setUp() {
		DriverFactory.configure(new Properties());
		DriverFactory.configure("some", new Properties());
	}

	public void testConfigureStringProperties1() {
		String id = DriverFactoryTest.class.getName();
		try {
			DriverFactory.getInstance(id);
			fail("should throw ConfigurationException as there should be no provider named "
					+ id);
		} catch (ConfigurationException e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains(id));
		}

		Properties props = new Properties();
		props.setProperty("remoteUrlBase", "http://base.url");
		DriverFactory.configure(id, props);
		Driver instance = DriverFactory.getInstance(id);
		assertNotNull(instance);
		assertEquals("http://base.url", instance.getConfiguration()
				.getBaseURL());
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
		assertEquals("http://base.url", instance.getConfiguration()
				.getBaseURL());
	}

	public void testMergeProperties() {
		String deafultBaseUrl = "http://basedefault.url";
		String extendedBaseUrl = "http://baseextended.url";

		Properties defaultProps = new Properties();
		defaultProps.setProperty("remoteUrlBase", deafultBaseUrl);
		defaultProps.setProperty("filter",
				"org.esigate.filter.CookieForwardingFilter");
		defaultProps.setProperty("cookieStore",
				"org.esigate.cookie.FilteringCookieStore");
		defaultProps.setProperty("forwardCookies", "test");

		Properties extendedProps = new Properties();
		extendedProps.setProperty("remoteUrlBase", extendedBaseUrl);
		extendedProps.setProperty("cookieStore", "test-cookie");

		URL dir = this.getClass().getResource("DriverFactoryTest.class");
		File file = new File(dir.getPath());
		File classPathFile = file.getParentFile();
		File extFolder = classPathFile.getParentFile().getParentFile();

		File driverPropsFile = new File(classPathFile + File.separator
				+ "driver.properties");
		File extendedPropsFile = new File(extFolder + File.separator
				+ "driver-ext.properties");

		try {
			FileOutputStream defaultOutputStream = new FileOutputStream(
					driverPropsFile);
			FileOutputStream extOutputStream = new FileOutputStream(extendedPropsFile);
			defaultProps.store(defaultOutputStream, "driver.properties");
			extendedProps.store(extOutputStream, "driver.properties");

			defaultOutputStream.close();
			extOutputStream.close();

		} catch (IOException e) {
			fail(e.getMessage());
		}

		try {
			DriverFactory.configure();
			Driver driver = DriverFactory.getInstance();

			assertEquals(extendedBaseUrl, driver.getConfiguration()
					.getBaseURL());

			assertEquals("org.esigate.filter.CookieForwardingFilter",
					driver.getFilter().getClass().getCanonicalName());
			assertEquals("test-cookie", driver.getConfiguration()
					.getCookieStore());
			assertEquals(
					"test",
					driver.getConfiguration().getProperties()
							.get("forwardCookies"));

		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			driverPropsFile.deleteOnExit();
			extendedPropsFile.deleteOnExit();
			DriverFactory.configure();
		}

	}
}
