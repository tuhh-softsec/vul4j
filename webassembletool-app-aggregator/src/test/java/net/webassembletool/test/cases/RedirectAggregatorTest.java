package net.webassembletool.test.cases;

import junit.framework.TestCase;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * Redirect testing.
 * 
 * @author Nicolas Richeton
 */
public class RedirectAggregatorTest extends TestCase {
	private final static String APPLICATION_PATH = "http://localhost:8080/webassembletool-app-aggregator/";
	private static String PAGE_REQUEST = "encoding/utf-8éèà/redirect.jsp";
	private static String PAGE_REQUEST_ENCODED = "encoding/utf-8%C3%A9%C3%A8%C3%A0/redirect.jsp";

	/**
	 * Ensure redirect an utf-8 and encoded urls are correctly parsed.
	 * 
	 * @throws Exception
	 */
	public void testUTF8Redirection() throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH
				+ PAGE_REQUEST_ENCODED);
		WebConversation webConversation = new WebConversation();
		webConversation.getClientProperties().setAutoRedirect(true);
		WebResponse resp = webConversation.getResponse(req);

		assertTrue(resp.getText().contains("successfuly"));
	}

}
