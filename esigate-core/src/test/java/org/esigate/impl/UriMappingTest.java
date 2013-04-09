package org.esigate.impl;

import javax.naming.ConfigurationException;

import junit.framework.TestCase;

public class UriMappingTest extends TestCase {

	public void testParseMapping() throws Exception {
		UriMapping uriMapping = UriMapping.create("http://foo.com:80/a*.jsp");
		assertEquals("http://foo.com:80", uriMapping.host);
		assertEquals("/a", uriMapping.path);
		assertEquals(".jsp", uriMapping.extension);

		uriMapping = UriMapping.create("http://foo.com:80/aaa.jsp");
		assertEquals("http://foo.com:80", uriMapping.host);
		assertEquals("/aaa.jsp", uriMapping.path);
		assertNull(uriMapping.extension);

		uriMapping = UriMapping.create("https://foo.com/aaa.jsp");
		assertEquals("https://foo.com", uriMapping.host);
		assertEquals("/aaa.jsp", uriMapping.path);
		assertNull(uriMapping.extension);

		uriMapping = UriMapping.create("/aaa.jsp");
		assertNull(uriMapping.host);
		assertEquals("/aaa.jsp", uriMapping.path);
		assertNull(uriMapping.extension);

		uriMapping = UriMapping.create("*.jsp");
		assertNull(uriMapping.host);
		assertNull(uriMapping.path);
		assertEquals(".jsp", uriMapping.extension);

		uriMapping = UriMapping.create("/test/*");
		assertNull(uriMapping.host);
		assertEquals("/test/", uriMapping.path);
		assertNull(uriMapping.extension);
	}

	public void testParseInvalidMapping() throws Exception {
		try {
			UriMapping.create("aaa");
			fail("The mapping is invalid, we should get an exception");
		} catch (ConfigurationException e) {
			// This is exactly what we expect
		}
		try {
			UriMapping.create("*.jsp*");
			fail("The mapping is invalid, we should get an exception");
		} catch (ConfigurationException e) {
			// This is exactly what we expect
		}
		try {
			UriMapping.create("ftp://foo.com");
			fail("The mapping is invalid, we should get an exception");
		} catch (ConfigurationException e) {
			// This is exactly what we expect
		}
	}

}
