package net.webassembletool.test.cases;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

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
	private final static String APPLICATION_PATH = "http://localhost:8080/webassembletool-app-master/";
	private final static String RESOURCES_PATH = "/";
	private WebConversation webConversation;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		webConversation = new WebConversation();
		webConversation.getClientProperties().setAutoRedirect(false);
	}

	private String getResource(String file) throws IOException {
		InputStream inputStream = this.getClass().getResourceAsStream(
				RESOURCES_PATH + file);
		String result = IOUtils.toString(inputStream, "ISO-8859-1");
		inputStream.close();
		return result;
	}

	private void doSimpleTest(String page) throws Exception {
		// We assume the file name is the name of the resource
		doSimpleTest(page, page);
	}

	private void doSimpleTest(String page, String resultResource)
			throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + page);
		WebResponse resp = webConversation.getResponse(req);
		assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp
				.getResponseCode());
		assertEquals(getResource(resultResource), resp.getText());
	}

	// TODO This test does not work with cargo maven plugin as the version of
	// Xerces is too old. Needed to add a new version of Xerces to the
	// app-master project. But it works fine without it in a real servlet
	// container.
	public void testXml() throws Exception {
		doSimpleTest("xml.jsp");
	}

	public void testBase() throws Exception {
		doSimpleTest("base.jsp");
	}

	public void testBlock() throws Exception {
		doSimpleTest("block.jsp");
	}

	public void testError500() throws Exception {
		doSimpleTest("error500.jsp");
	}

	public void testProxy() throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH
				+ "images/smile.jpg");
		WebResponse resp = webConversation.getResponse(req);
		assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp
				.getResponseCode());
		assertEquals("image/jpeg", resp.getContentType().toLowerCase());
	}

	public void testProxyIfmodifiedsince() throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH
				+ "images/smile.jpg");
		WebResponse resp = webConversation.getResponse(req);
		req = new GetMethodWebRequest(APPLICATION_PATH + "images/smile.jpg");
		req.setHeaderField("if-modified-since", resp
				.getHeaderField("Last-modified"));
		resp = webConversation.getResponse(req);
		assertEquals("Status should be 200",
				HttpServletResponse.SC_NOT_MODIFIED, resp.getResponseCode());
	}

	public void testReplaceBlock() throws Exception {
		doSimpleTest("replaceblock.jsp");
	}

	public void testReplaceTemplate() throws Exception {
		doSimpleTest("replacetemplate.jsp");
	}

	public void testTemplate() throws Exception {
		doSimpleTest("template.jsp");
	}

	public void testTemplateMultipleParams() throws Exception {
		doSimpleTest("templatemultipleparams.jsp");
	}

	public void testTimeout() throws Exception {
		Date before = new Date();
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH
				+ "timeout.jsp");
		WebResponse resp = webConversation.getResponse(req);
		Date after = new Date();
		assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp
				.getResponseCode());
		long duration = after.getTime() - before.getTime();
		assertTrue("Request should take more than 2 s as it is not in cache",
				duration >= 1500);
		assertEquals(getResource("timeout.jsp"), resp.getText());
		before = new Date();
		req = new GetMethodWebRequest(APPLICATION_PATH + "timeout.jsp");
		// Allow caching
		req.setHeaderField("Pragma", "can-be-cached");
		req.setHeaderField("Cache-control", "can-be-cached");
		resp = webConversation.getResponse(req);
		after = new Date();
		assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp
				.getResponseCode());
		duration = after.getTime() - before.getTime();
		assertTrue("Request should take less than 2 s as it is in cache",
				duration < 1500);
		assertEquals(getResource("timeout.jsp"), resp.getText());
	}

	public void testUser() throws Exception {
		doSimpleTest("user.jsp");
	}

	public void testPreserveHost() throws Exception {
		WebRequest req = new GetMethodWebRequest(
				"http://127.0.0.1:8080/webassembletool-app-master/images/host.jsp");
		WebResponse resp = webConversation.getResponse(req);
		assertEquals(
				"Host name and port should be preserved",
				"You should see here the same hostname and port as in the browser=>127.0.0.1:8080",
				resp.getText());
	}

}
