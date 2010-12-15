package net.webassembletool.resource;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;
import net.webassembletool.Driver;
import net.webassembletool.DriverConfiguration;
import net.webassembletool.ResourceContext;

import org.easymock.EasyMock;

public class ResourceUtilsTest extends TestCase {

	public void testGetHttpUrlWithQueryString() {
		Properties props = new Properties();
		props.put("remoteUrlBase", "http://www.foo.com/");
		props.put("localBase", "/temp/");
		Driver driver = new Driver("test", props);
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
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
		HttpServletRequest request = EasyMock
				.createMock(HttpServletRequest.class);
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
