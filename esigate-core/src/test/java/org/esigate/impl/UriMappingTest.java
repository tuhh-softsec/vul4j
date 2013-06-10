package org.esigate.impl;


import junit.framework.TestCase;

import org.esigate.ConfigurationException;

public class UriMappingTest extends TestCase {

	public void testParseMapping() throws Exception {
		UriMapping uriMapping = UriMapping.create("http://foo.com:80/a*.jsp");
		assertEquals("http://foo.com:80", uriMapping.getHost());
		assertEquals("/a", uriMapping.getPath());
		assertEquals(".jsp", uriMapping.getExtension());

		uriMapping = UriMapping.create("http://foo.com:80/aaa.jsp");
		assertEquals("http://foo.com:80", uriMapping.getHost());
		assertEquals("/aaa.jsp", uriMapping.getPath());
		assertNull(uriMapping.getExtension());

		uriMapping = UriMapping.create("https://foo.com/aaa.jsp");
		assertEquals("https://foo.com", uriMapping.getHost());
		assertEquals("/aaa.jsp", uriMapping.getPath());
		assertNull(uriMapping.getExtension());

		uriMapping = UriMapping.create("/aaa.jsp");
		assertNull(uriMapping.getHost());
		assertEquals("/aaa.jsp", uriMapping.getPath());
		assertNull(uriMapping.getExtension());

		uriMapping = UriMapping.create("*.jsp");
		assertNull(uriMapping.getHost());
		assertNull(uriMapping.getPath());
		assertEquals(".jsp", uriMapping.getExtension());

		uriMapping = UriMapping.create("/test/*");
		assertNull(uriMapping.getHost());
		assertEquals("/test/", uriMapping.getPath());
		assertNull(uriMapping.getExtension());
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
