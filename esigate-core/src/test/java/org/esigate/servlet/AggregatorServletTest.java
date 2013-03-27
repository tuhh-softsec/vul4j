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
import org.junit.Test;
import org.mockito.Mockito;

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
		@SuppressWarnings("rawtypes")
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

	@Test
	public void testConfig() throws ServletException {
		AggregatorServlet servlet = new AggregatorServlet();
		
		// Init driver before calling the servlet 
		Properties p= 		new Properties();
		p.setProperty("remoteUrlBase", "test");
		DriverFactory.put("single", new Driver( "single", p));
		
		servlet.init(new TestServletConfig());
		Assert.assertEquals("provider1",
				servlet.getProviderMappings().get("sub.domain.com"));
		Assert.assertEquals("provider2",
				servlet.getProviderMappings().get("sub2.domain.com"));

	}

	@Test
	public void testProviderSelection() throws ServletException, IOException {
		AggregatorServlet servlet = new AggregatorServlet();
		servlet.init(new TestServletConfig());

		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getContextPath()).thenReturn("test");
		Mockito.when(request.getRequestURI())
				.thenReturn("test/servlet/request");
		Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
		Mockito.when(request.getMethod()).thenReturn("GET");
		Mockito.when(request.getServletPath()).thenReturn("servlet");

		Mockito.when(request.getHeader("Host")).thenReturn("sub2.domain.com");
		Assert.assertEquals("provider2", servlet.selectProvider(request));

		request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getContextPath()).thenReturn("test");
		Mockito.when(request.getRequestURI())
				.thenReturn("test/servlet/request");
		Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
		Mockito.when(request.getMethod()).thenReturn("GET");
		Mockito.when(request.getServletPath()).thenReturn("servlet");

		Mockito.when(request.getHeader("Host")).thenReturn("sub.domain.com");
		Assert.assertEquals("provider1", servlet.selectProvider(request));

		request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getContextPath()).thenReturn("test");
		Mockito.when(request.getRequestURI())
				.thenReturn("test/servlet/request");
		Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
		Mockito.when(request.getMethod()).thenReturn("GET");
		Mockito.when(request.getServletPath()).thenReturn("servlet");
		Assert.assertEquals("single", servlet.selectProvider(request));

	}

}
