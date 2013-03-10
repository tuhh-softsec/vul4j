package org.esigate.test.cases;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.esigate.http.HttpResponseUtils;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * Junit tests using the "/master" webapp
 * 
 * @author Omar BENHAMID
 * @author Francois-Xavier Bonnet
 */
public class MasterTest extends TestCase {
	private final static String APPLICATION_PATH = "http://localhost:8080/esigate-app-master/";
	private final static String RESOURCES_PATH = "/";
	private WebConversation webConversation;

	private void doSimpleTest(String page) throws Exception {
		// We assume the file name is the name of the resource
		doSimpleTest(page, page);
	}

	private void doSimpleTest(String page, String resultResource) throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + page);
		WebResponse resp = webConversation.getResponse(req);
		assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp.getResponseCode());
		String strResponse = resp.getText().replaceAll("\r", "");
		String strResource = getResource(resultResource).replaceAll("\r", "");
		assertEquals(strResource, strResponse);
	}

	private String getResource(String file) throws IOException {
		InputStream inputStream = this.getClass().getResourceAsStream(RESOURCES_PATH + file);
		String result = IOUtils.toString(inputStream, "ISO-8859-1");
		inputStream.close();
		return result;
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		webConversation = new WebConversation();
		webConversation.getClientProperties().setAutoRedirect(false);
	}

	public void testBase() throws Exception {
		doSimpleTest("base.jsp");
	}

	public void testBlock() throws Exception {
		doSimpleTest("block.jsp");
		doSimpleTest("blocks.jsp");
		doSimpleTest("blockvariables.jsp", "block.jsp");
	}

	public void testBlockAbsolute() throws Exception {
		doSimpleTest("blockabsolute.jsp", "block.jsp");
	}

	public void testError500() throws Exception {
		doSimpleTest("error500.jsp");
	}

	public void testPreserveHost() throws Exception {
		WebRequest req = new GetMethodWebRequest("http://127.0.0.1:8080/esigate-app-master/images/host.jsp");
		WebResponse resp = webConversation.getResponse(req);
		assertEquals("Host name and port should be preserved", "You should see here the same hostname and port as in the browser=>127.0.0.1:8080", resp.getText());
	}

	public void testProxy() throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "images/smile.jpg");
		WebResponse resp = webConversation.getResponse(req);
		assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp.getResponseCode());
		assertEquals("image/jpeg", resp.getContentType().toLowerCase());
	}

	public void testProxyIfmodifiedsince() throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "images/smile.jpg");
		WebResponse resp = webConversation.getResponse(req);
		req = new GetMethodWebRequest(APPLICATION_PATH + "images/smile.jpg");
		req.setHeaderField("if-modified-since", resp.getHeaderField("Last-modified"));
		resp = webConversation.getResponse(req);
		assertEquals("Status should be 304", HttpServletResponse.SC_NOT_MODIFIED, resp.getResponseCode());
	}

	public void testReplaceBlock() throws Exception {
		doSimpleTest("replaceblock.jsp");
	}

	public void testReplaceTemplate() throws Exception {
		doSimpleTest("replacetemplate.jsp");
	}

	public void testReplaceXml() throws Exception {
		doSimpleTest("replaceXml.jsp");
	}

	public void testTemplate() throws Exception {
		doSimpleTest("template.jsp");
	}

	public void testTemplateMultipleParams() throws Exception {
		doSimpleTest("templatemultipleparams.jsp");
	}

	public void testTimeout() throws Exception {
		// First request to this URL
		long before = System.currentTimeMillis();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(APPLICATION_PATH + "timeout.jsp");
		HttpResponse response = client.execute(request);
		long after = System.currentTimeMillis();
		assertEquals("Status should be 200", HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
		assertTrue("Request should take more than 2 s as it is not in cache", after - before >= 1500);
		assertEquals(getResource("timeout.jsp"), HttpResponseUtils.toString(response, null));
		// Second request: should use cache
		// FIXME Errors are not in cache anymore since HttpClient cache does not
		// support error caching
		// before = System.currentTimeMillis();
		// request = new HttpGet(APPLICATION_PATH + "timeout.jsp");
		// response = client.execute(request);
		// after = System.currentTimeMillis();
		// assertEquals("Status should be 200", HttpServletResponse.SC_OK,
		// response.getStatusLine().getStatusCode());
		// assertTrue("Request should take less than 2 s as it is in cache",
		// after
		// - before < 1500);
		// assertEquals(getResource("timeout.jsp"),
		// IOUtils.toString(response.getEntity().getContent()));
	}

	public void testUser() throws Exception {
		doSimpleTest("user.jsp");
	}

	// This test does not work with cargo maven plugin as the version of
	// Xerces is too old. Needed to add a new version of Xerces to the
	// app-master project. But it works fine without it in a real servlet
	// container.
	public void testXml() throws Exception {
		doSimpleTest("xml.jsp");
	}

}
