package org.esigate.resource;

import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.esigate.Driver;
import org.esigate.DriverConfiguration;
import org.esigate.ResourceContext;
import org.esigate.api.HttpRequest;

public class ResourceUtilsTest extends TestCase {

	public void testGetHttpUrlWithQueryString() {
		Properties props = new Properties();
		props.put("remoteUrlBase", "http://www.foo.com/");
		props.put("localBase", "/temp/");
		Driver driver = new Driver("test", props);
		HttpRequest request = EasyMock
				.createMock(HttpRequest.class);
		EasyMock.expect(request.getCharacterEncoding()).andStubReturn(
				"ISO-8859-1");
		EasyMock.expect(request.getQueryString()).andReturn(null);
		EasyMock.expect(request.getSession(false)).andReturn(null);
		ResourceContext resourceContext = new ResourceContext(driver, "/test",
				null, request, null);
		resourceContext.setProxy(true);
		EasyMock.replay(request);
		assertEquals("http://www.foo.com/test",
				ResourceUtils.getHttpUrlWithQueryString(resourceContext));
	}

	public void testGetHttpUrlWithQueryStringAbsoluteurl() {
		Properties props = new Properties();
		props.put("remoteUrlBase", "http://www.foo.com/");
		props.put("localBase", "/temp/");
		Driver driver = new Driver("test", props);
		HttpRequest request = EasyMock
				.createMock(HttpRequest.class);
		EasyMock.expect(request.getCharacterEncoding()).andStubReturn(
				"ISO-8859-1");
		EasyMock.expect(request.getQueryString()).andReturn(null);
		EasyMock.expect(request.getSession(false)).andReturn(null);
		ResourceContext resourceContext = new ResourceContext(driver,
				"http://www.bar.com/test", null, request, null);
		resourceContext.setProxy(true);
		EasyMock.replay(request);
		assertEquals("http://www.bar.com/test",
				ResourceUtils.getHttpUrlWithQueryString(resourceContext));
	}

	public void testIsTextContentType() {
		DriverConfiguration config = new DriverConfiguration("test",
				new Properties());
		assertTrue(ResourceUtils.isTextContentType("text/html",
				config.getParsableContentTypes()));

		assertTrue(ResourceUtils.isTextContentType("application/xhtml+xml",
				config.getParsableContentTypes()));

		assertFalse(ResourceUtils.isTextContentType("text/plain",
				config.getParsableContentTypes()));

		Properties properties = new Properties();
		properties.setProperty("parsableContentTypes", "text/plain");

		config = new DriverConfiguration("test", properties);
		assertTrue(ResourceUtils.isTextContentType("text/plain",
				config.getParsableContentTypes()));
		assertFalse(ResourceUtils.isTextContentType("text/html",
				config.getParsableContentTypes()));

	}
}
