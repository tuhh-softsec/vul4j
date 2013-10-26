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
package org.esigate.servlet;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.servlet.impl.DriverSelector;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests the url/driver mapping feature.
 * 
 * <p>
 * Ensure that legacy methods from web.xml still work.
 * 
 * @author Nicolas Richeton
 * 
 */
public class DriverSelectorTest extends TestCase {

	/**
	 * Test setting a unique Driver instance for a servlet (web.xml)
	 * 
	 * @throws HttpErrorPage
	 */
	@Test
	public void testWebXmlProviderSelection() throws HttpErrorPage {
		// Setup default
		Properties properties = new Properties();
		properties.setProperty("default." + Parameters.REMOTE_URL_BASE.name, "http://example2.com");
		DriverFactory.configure(properties);

		DriverSelector ds = new DriverSelector();
		ds.setWebXmlProvider("default");

		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getHeader("Host")).thenReturn("sub2.domain.com:8080");
		Mockito.when(request.getContextPath()).thenReturn("/");
		Mockito.when(request.getRequestURI()).thenReturn("test/");
		Assert.assertEquals("default", ds.selectProvider(request, true).getLeft().getConfiguration().getInstanceName());
	}

	/**
	 * Test setting a host-based mapping for a servlet. (web.xml)
	 * 
	 * @throws HttpErrorPage
	 */
	@Test
	public void testWebXmlProvidersSelection() throws HttpErrorPage {
		Properties properties = new Properties();
		properties.setProperty("default." + Parameters.REMOTE_URL_BASE.name, "http://example2.com");
		properties.setProperty("aggregated1." + Parameters.REMOTE_URL_BASE.name, "http://example2.com");
		properties.setProperty("aggregated2." + Parameters.REMOTE_URL_BASE.name, "http://example2.com");
		DriverFactory.configure(properties);

		DriverSelector ds = new DriverSelector();
		ds.setWebXmlProviders("sub1.domain.com=aggregated1,sub2.domain.com:8080=aggregated2");
		ds.setWebXmlProvider("default");

		HttpServletRequest request1 = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request1.getHeader("Host")).thenReturn("sub2.domain.com:8080");
		Mockito.when(request1.getContextPath()).thenReturn("/");
		Mockito.when(request1.getRequestURI()).thenReturn("test/");
		Assert.assertEquals("aggregated2", ds.selectProvider(request1, true).getLeft().getConfiguration().getInstanceName());

		HttpServletRequest request2 = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request2.getHeader("Host")).thenReturn("sub1.domain.com");
		Mockito.when(request2.getContextPath()).thenReturn("/");
		Mockito.when(request2.getRequestURI()).thenReturn("test/");
		Assert.assertEquals("aggregated1", ds.selectProvider(request2, true).getLeft().getConfiguration().getInstanceName());

		HttpServletRequest request3 = Mockito.mock(HttpServletRequest.class);
		// Other port
		Mockito.when(request3.getHeader("Host")).thenReturn("sub2.domain.com:8082");
		Mockito.when(request3.getContextPath()).thenReturn("/");
		Mockito.when(request3.getRequestURI()).thenReturn("test/");
		Assert.assertEquals("default", ds.selectProvider(request3, true).getLeft().getConfiguration().getInstanceName());
	}

}
