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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test the aggregator servlet for driver selection (url mapping) for both
 * web.xml configuration (legacy) and esigate.properties.
 * 
 * @author Nicolas Richeton
 * @deprecated Remove when {@link AggregatorServlet} is removed.
 */
@Deprecated
public class AggregatorServletTest {

	protected class TestServletConfig implements ServletConfig {
		@Override
		public String getServletName() {
			return "aggregator";
		}

		@Override
		public ServletContext getServletContext() {
			return null;
		}

		@Override
		public Enumeration getInitParameterNames() {
			return null;
		}

		@Override
		public String getInitParameter(String name) {
			if (name.equals("provider"))
				return "single";

			if (name.equals("providers"))
				return "suB.domaiN.com=provider1,sub2.domAin.com=provider2";

			return null;
		}
	}

	/**
	 * Test Reading configuration from web.xml
	 * 
	 * @throws ServletException
	 */
	@Test
	public void testConfig() throws ServletException {
		AggregatorServlet servlet = new AggregatorServlet();

		// Setup Esigate
		Properties p = new Properties();
		p.setProperty("remoteUrlBase", "test");
		DriverFactory.put("single", Driver.builder().setName("single").setProperties(p).build());

		// Init servlet
		servlet.init(new TestServletConfig());

		// Ensure config is loaded
		Assert.assertEquals("provider1", servlet.getDriverSelector().getWebXmlProviderMappings().get("sub.domain.com"));
		Assert.assertEquals("provider2", servlet.getDriverSelector().getWebXmlProviderMappings().get("sub2.domain.com"));

	}

	/**
	 * Test provider selection based on web.xml configuration.
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	@Test
	public void testProviderSelectionWebXml() throws ServletException, IOException, HttpErrorPage {

		// Setup Esigate
		Properties p = new Properties();
		p.setProperty("provider1." + Parameters.REMOTE_URL_BASE, "test");
		p.setProperty("provider2." + Parameters.REMOTE_URL_BASE, "test");
		p.setProperty("single." + Parameters.REMOTE_URL_BASE, "test");
		DriverFactory.configure(p);

		// Init servlet
		AggregatorServlet servlet = new AggregatorServlet();
		servlet.init(new TestServletConfig());

		// Do testing
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getContextPath()).thenReturn("/test");
		Mockito.when(request.getRequestURI()).thenReturn("test/servlet/request");
		Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
		Mockito.when(request.getMethod()).thenReturn("GET");
		Mockito.when(request.getServletPath()).thenReturn("servlet");
		Mockito.when(request.getHeader("Host")).thenReturn("sub2.domain.com");
		Mockito.when(request.getScheme()).thenReturn("http");
		Assert.assertEquals("provider2", servlet.getDriverSelector().selectProvider(request, true).getLeft().getConfiguration().getInstanceName());

		request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getContextPath()).thenReturn("/test");
		Mockito.when(request.getRequestURI()).thenReturn("test/servlet/request");
		Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
		Mockito.when(request.getMethod()).thenReturn("GET");
		Mockito.when(request.getServletPath()).thenReturn("servlet");
		Mockito.when(request.getHeader("Host")).thenReturn("sub.domain.com");
		Mockito.when(request.getScheme()).thenReturn("http");
		Assert.assertEquals("provider1", servlet.getDriverSelector().selectProvider(request, true).getLeft().getConfiguration().getInstanceName());

		request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getContextPath()).thenReturn("/test");
		Mockito.when(request.getRequestURI()).thenReturn("test/servlet/request");
		Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
		Mockito.when(request.getMethod()).thenReturn("GET");
		Mockito.when(request.getServletPath()).thenReturn("servlet");
		Mockito.when(request.getScheme()).thenReturn("http");
		Assert.assertEquals("single", servlet.getDriverSelector().selectProvider(request, true).getLeft().getConfiguration().getInstanceName());

	}

	/**
	 * Test provider selection based on esigate.properties configuration.
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	@Test
	public void testProviderSelectionEsigate() throws ServletException, IOException, HttpErrorPage {

		// Setup Esigate
		Properties p = new Properties();
		p.setProperty("provider1." + Parameters.REMOTE_URL_BASE, "test");
		p.setProperty("provider1." + Parameters.MAPPINGS, "http://sub.domain.com/*");
		p.setProperty("provider2." + Parameters.REMOTE_URL_BASE, "test");
		p.setProperty("provider2." + Parameters.MAPPINGS, "http://sub2.domain.com/*");
		p.setProperty("single." + Parameters.REMOTE_URL_BASE, "test");
		p.setProperty("single." + Parameters.MAPPINGS, "*");
		DriverFactory.configure(p);

		AggregatorServlet servlet = new AggregatorServlet();
		ServletConfig conf = Mockito.mock(ServletConfig.class);
		Mockito.when(conf.getInitParameter("useMappings")).thenReturn("true");
		servlet.init(conf);

		// Do testing
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getContextPath()).thenReturn("/test");
		Mockito.when(request.getRequestURI()).thenReturn("/test/servlet/request");
		Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
		Mockito.when(request.getMethod()).thenReturn("GET");
		Mockito.when(request.getServletPath()).thenReturn("servlet");
		Mockito.when(request.getHeader("Host")).thenReturn("sub2.domain.com");
		Mockito.when(request.getScheme()).thenReturn("http");
		Assert.assertEquals("provider2", servlet.getDriverSelector().selectProvider(request, true).getLeft().getConfiguration().getInstanceName());

		request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getContextPath()).thenReturn("/test");
		Mockito.when(request.getRequestURI()).thenReturn("/test/servlet/request");
		Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
		Mockito.when(request.getMethod()).thenReturn("GET");
		Mockito.when(request.getServletPath()).thenReturn("servlet");
		Mockito.when(request.getHeader("Host")).thenReturn("sub.domain.com");
		Mockito.when(request.getScheme()).thenReturn("http");
		Assert.assertEquals("provider1", servlet.getDriverSelector().selectProvider(request, true).getLeft().getConfiguration().getInstanceName());

		request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getContextPath()).thenReturn("/test");
		Mockito.when(request.getRequestURI()).thenReturn("/test/servlet/request");
		Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
		Mockito.when(request.getMethod()).thenReturn("GET");
		Mockito.when(request.getServletPath()).thenReturn("servlet");
		Mockito.when(request.getScheme()).thenReturn("http");
		Assert.assertEquals("single", servlet.getDriverSelector().selectProvider(request, true).getLeft().getConfiguration().getInstanceName());

	}

}
