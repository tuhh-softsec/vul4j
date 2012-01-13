package org.esigate.test.cases;

import java.io.IOException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * "Vary" header testing
 * 
 * @author Nicolas Richeton
 */
public class HeaderFilteringTest extends TestCase {
	private final static String APPLICATION_PATH = "http://localhost:8080/esigate-app-aggregator/";

	/** Send a request with a "Upgrade" header to headers-filtering.jsp and ensure the result is valid. */
	private void doRequest(String value) throws IOException, SAXException {

		WebConversation webConversation = new WebConversation();
		webConversation.getClientProperties().setAutoRedirect(false);

		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "headers-filtering.jsp");
		req.setHeaderField("Upgrade", value);
		req.setHeaderField("Upgrade-Response", value);

		// Ensure content is valid.
		WebResponse resp = webConversation.getResponse(req);
		assertNotNull(resp.getText());
		assertTrue("no value 'missing' found", resp.getText().contains("missing"));
		assertEquals("Upgrade-Response header value should pass through esigate", value, resp.getHeaderField("Upgrade-Response"));
	}

	/** Calls "headers-filtering.jsp" with filtered header value. */
	public void testHeadersFiltering() throws Exception {
		doRequest(getClass().getName());
	}

}
