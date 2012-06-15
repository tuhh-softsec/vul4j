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

package org.esigate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Properties;

import junit.framework.TestCase;

public class DriverTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		MockDriver provider = new MockDriver("mock");
		provider.addResource("/testBlock", "abc some<!--$beginblock$A$-->some text goes here<!--$endblock$A$--> cdf hello");
		provider.addResource("/testTemplateFullPage", "some <!--$beginparam$key$-->some hidden text goes here<!--$endparam$key$--> printed");
		provider.addResource("/testTemplate", "abc some<!--$begintemplate$A$-->some text goes here<!--$endtemplate$A$--> cdf hello");
	}

	public void testRenderBlock() throws IOException, HttpErrorPage {
		Writer out = new StringWriter();
		DriverFactory.getInstance("mock").renderBlock("/testBlock", "A", out, null, null, new HashMap<String, String>(), null, false);
		assertEquals("some text goes here", out.toString());

		out = new StringWriter();
		DriverFactory.getInstance("mock").renderBlock("$(vartestBlock)", "A", out, null, null, new HashMap<String, String>(), null, false);
		assertEquals("some text goes here", out.toString());

		out = new StringWriter();
		DriverFactory.getInstance("mock").renderBlock("/$(vartest)$(varBlock)", "A", out, null, null, new HashMap<String, String>(), null, false);
		assertEquals("some text goes here", out.toString());

	}

	public void testRenderTemplateFullPage() throws IOException, HttpErrorPage {
		StringWriter out = new StringWriter();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "'value'");
		params.put("some other key", "'another value'");
		DriverFactory.getInstance("mock").renderTemplate("/testTemplateFullPage", null, out, null, null, params, null, null, false);
		assertFalse(out.toString().contains("key"));
		assertTrue(out.toString().contains("'value'"));
		assertFalse(out.toString().contains("some other key"));
		assertEquals("some 'value' printed", out.toString());
	}

	public void testRenderTemplate() throws IOException, HttpErrorPage {
		StringWriter out = new StringWriter();
		DriverFactory.getInstance("mock").renderTemplate("/testTemplate", "A", out, null, null, null, null, null, false);
		assertEquals("some text goes here", out.toString());

		out = new StringWriter();
		DriverFactory.getInstance("mock").renderTemplate("/test$(varTemplate)", "A", out, null, null, null, null, null, false);
		assertEquals("some text goes here", out.toString());

	}


	/** Test default configuration */
	public void testDefaultConfig() {
		Properties properties = new Properties();
		Driver driver = new Driver("dummy", properties);
		// Parsable contentTypes
		assertParsableContentType(driver, "text/html; charset=utf-8");
		assertParsableContentType(driver, "application/xhtml+xml; charset=iso-8859-1");
		assertNotParsableContentType(driver, "application/octet-stream");
	}

	private void assertParsableContentType(Driver driver, String contentType) {
		assertTrue("Content-type should be considered as text", driver.isTextContentType(contentType));
	}

	private void assertNotParsableContentType(Driver driver, String contentType) {
		assertFalse("Content-type should be considered as binary", driver.isTextContentType(contentType));
	}

	/**
	 * Test property parsableContentTypes
	 */
	public void testParsableContentTypes() {
		Properties properties = new Properties();
		properties.put(Parameters.PARSABLE_CONTENT_TYPES.name, "text/plain");
		Driver driver = new Driver("dummy", properties);
		assertParsableContentType(driver, "text/plain");

		properties = new Properties();
		properties.put(Parameters.PARSABLE_CONTENT_TYPES.name, "text/plain, text/html");
		driver = new Driver("dummy", properties);
		assertParsableContentType(driver, "text/plain");
		assertParsableContentType(driver, "text/html");

		properties = new Properties();
		properties.put(Parameters.PARSABLE_CONTENT_TYPES.name, "text/plain, text/html,application/x");
		driver = new Driver("dummy", properties);
		assertParsableContentType(driver, "text/plain");
		assertParsableContentType(driver, "text/html");
		assertParsableContentType(driver, "application/x");
	}

}
