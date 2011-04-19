package net.webassembletool.test.cases;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class MultipleHeadersTest extends TestCase {
	private final static String APPLICATION_PATH = "http://localhost:8080/webassembletool-app-aggregator/";
	private WebConversation webConversation;

	@Override
	protected void setUp() {
		webConversation = new WebConversation();
		webConversation.setExceptionsThrownOnErrorStatus(false);
	}

	@Override
	protected void tearDown() {
		webConversation = null;
	}

	public void testMultipleHeaders() throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "multiple-headers.jsp");
		WebResponse resp = webConversation.getResponse(req);
		assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp.getResponseCode());
		String[] actual = resp.getHeaderFields("header-name");
		assertNotNull("headers named 'header-name' should appear in response", actual);
		assertEquals("there should be 2 headers", 2, actual.length);
		Arrays.sort(actual);
		assertEquals("unexpected header value", "header-value-01", actual[0]);
		assertEquals("unexpected header value", "header-value-02", actual[1]);
	}
}
