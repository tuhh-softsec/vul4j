package net.webassembletool.test.cases;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class FaceletTest extends TestCase {
	private final static String APPLICATION_PATH = "http://localhost:8080/webassembletool-app-facelet/";
	private WebConversation webConversation;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		webConversation = new WebConversation();
		webConversation.getClientProperties().setAutoRedirect(false);
	}

	public void testTemplate() throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH);
		WebResponse resp = webConversation.getResponse(req);
		assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp
				.getResponseCode());
		assertEquals("Page title does not match",
				"Webassembletool facelet test", resp.getTitle());
		HTMLElement htmlElement = resp.getElementWithID("replacement");
		assertNotNull("\"replacement\" element is missing", htmlElement);
		assertEquals("Lorem ipsum should have been replaced in the template",
				"New value", htmlElement.getText());
		htmlElement = resp.getElementWithID("param1");
		assertNotNull("\"param1\" element is missing", htmlElement);
		assertEquals("param1 should have been replaced in the template",
				"Test body", htmlElement.getText());
		htmlElement = resp.getElementWithID("param2");
		assertNotNull("\"param2\" element is missing", htmlElement);
		assertEquals("param2 should have been replaced in the template",
				"Test body", resp.getElementWithID("param2").getText());
	}
}
