/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate.resource;

import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.esigate.Driver;
import org.esigate.DriverConfiguration;
import org.esigate.Parameters;
import org.esigate.ResourceContext;
import org.esigate.api.HttpRequest;

public class ResourceUtilsTest extends TestCase {

	public void testGetHttpUrlWithQueryString() {
		Properties props = new Properties();
		props.put(Parameters.REMOTE_URL_BASE.name, "http://www.foo.com/");
		Driver driver = new Driver("test", props);
		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		EasyMock.expect(request.getCharacterEncoding()).andStubReturn("ISO-8859-1");
		EasyMock.expect(request.getQueryString()).andReturn(null);
		EasyMock.expect(request.getSession(false)).andReturn(null);
		ResourceContext resourceContext = new ResourceContext(driver, "/test", null, request, null);
		resourceContext.setProxy(true);
		EasyMock.replay(request);
		assertEquals("http://www.foo.com/test", ResourceUtils.getHttpUrlWithQueryString(resourceContext));
	}

	public void testGetHttpUrlWithQueryStringAbsoluteurl() {
		Properties props = new Properties();
		props.put(Parameters.REMOTE_URL_BASE, "http://www.foo.com/");
		Driver driver = new Driver("test", props);
		HttpRequest request = EasyMock.createMock(HttpRequest.class);
		EasyMock.expect(request.getCharacterEncoding()).andStubReturn("ISO-8859-1");
		EasyMock.expect(request.getQueryString()).andReturn(null);
		EasyMock.expect(request.getSession(false)).andReturn(null);
		ResourceContext resourceContext = new ResourceContext(driver, "http://www.bar.com/test", null, request, null);
		resourceContext.setProxy(true);
		EasyMock.replay(request);
		assertEquals("http://www.bar.com/test", ResourceUtils.getHttpUrlWithQueryString(resourceContext));
	}

	public void testIsTextContentType() {
		DriverConfiguration config = new DriverConfiguration("test", new Properties());
		assertTrue(ResourceUtils.isTextContentType("text/html", config.getParsableContentTypes()));

		assertTrue(ResourceUtils.isTextContentType("application/xhtml+xml", config.getParsableContentTypes()));

		assertFalse(ResourceUtils.isTextContentType("text/plain", config.getParsableContentTypes()));

		Properties properties = new Properties();
		properties.setProperty(Parameters.PARSABLE_CONTENT_TYPES.name, "text/plain");

		config = new DriverConfiguration("test", properties);
		assertTrue(ResourceUtils.isTextContentType("text/plain", config.getParsableContentTypes()));
		assertFalse(ResourceUtils.isTextContentType("text/html", config.getParsableContentTypes()));

	}
}
