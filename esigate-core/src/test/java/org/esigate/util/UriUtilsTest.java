package org.esigate.util;

import junit.framework.TestCase;

public class UriUtilsTest extends TestCase {

	public void testCreateUri() {
		assertEquals("http://foo.com/%E9?q=%E0", UriUtils.createURI("http", "foo.com", 0, "%E9", "q=%E0", null).toString());
	}

}
