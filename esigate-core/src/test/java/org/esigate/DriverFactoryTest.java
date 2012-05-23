/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.esigate.api.HttpRequest;
import org.esigate.http.CookieForwardingFilter;

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
			fail("should throw ConfigurationException as there should be no provider named " + id);
		} catch (ConfigurationException e) {
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().contains(id));
		}

		Properties props = new Properties();
		props.setProperty(Parameters.REMOTE_URL_BASE.name, "http://base.url");
		DriverFactory.configure(id, props);
		Driver instance = DriverFactory.getInstance(id);
		assertNotNull(instance);

		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		ResourceContext resourceContext = new ResourceContext(instance, "/test", null, request, null);

		assertEquals("http://base.url", resourceContext.getBaseURL());
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
		props.setProperty(Parameters.REMOTE_URL_BASE.name, "http://base.url");
		DriverFactory.configure(null, props);
		Driver instance = DriverFactory.getInstance();
		assertNotNull(instance);

		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		ResourceContext resourceContext = new ResourceContext(instance, "/test", null, request, null);

		assertEquals("http://base.url", resourceContext.getBaseURL());
	}

	public void testMergeProperties() {
		String deafultBaseUrl = "http://basedefault.url";
		String extendedBaseUrl = "http://baseextended.url";

		Properties defaultProps = new Properties();
		defaultProps.setProperty(Parameters.REMOTE_URL_BASE.name, deafultBaseUrl);
		defaultProps.setProperty(Parameters.FILTER.name, CookieForwardingFilter.class.getName());
		defaultProps.setProperty(Parameters.COOKIE_STORE.name, "org.esigate.cookie.FilteringCookieStore");
		defaultProps.setProperty(Parameters.FORWARD_COOKIES.name, "test");

		Properties extendedProps = new Properties();
		extendedProps.setProperty(Parameters.REMOTE_URL_BASE.name, extendedBaseUrl);
		extendedProps.setProperty(Parameters.COOKIE_STORE.name, "test-cookie");

		URL dir = this.getClass().getResource("DriverFactoryTest.class");
		File file = new File(dir.getPath());
		File classPathFile = file.getParentFile();
		File extFolder = classPathFile.getParentFile().getParentFile();

		File driverPropsFile = new File(classPathFile + File.separator + "driver.properties");
		File extendedPropsFile = new File(extFolder + File.separator + "driver-ext.properties");

		try {
			FileOutputStream defaultOutputStream = new FileOutputStream(driverPropsFile);
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

			HttpRequest request = EasyMock.createMock(HttpRequest.class);
			ResourceContext resourceContext = new ResourceContext(driver, "/test", null, request, null);

			assertEquals(extendedBaseUrl, resourceContext.getBaseURL());

			assertEquals(CookieForwardingFilter.class.getName(), driver.getFilter().getClass().getName());
			assertEquals("test-cookie", driver.getConfiguration().getCookieStore());
			assertEquals("test", driver.getConfiguration().getProperties().get(Parameters.FORWARD_COOKIES.name));

		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			driverPropsFile.deleteOnExit();
			extendedPropsFile.deleteOnExit();
			DriverFactory.configure();
		}

	}
}
