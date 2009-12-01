package net.webassembletool.test.cases;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * Tests for aggregator webapp
 * 
 * @author Francois-Xavier Bonnet
 */
public class AggregatorTest extends TestCase {
	private final static String APPLICATION_PATH = "http://localhost:8080/webassembletool-app-aggregator/";
	private final static String RESOURCES_PATH = "/";
	private WebConversation webConversation;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		webConversation = new WebConversation();
		webConversation.setExceptionsThrownOnErrorStatus(false);
	}

	private String getResource(String file) throws IOException {
		InputStream inputStream = this.getClass().getResourceAsStream(
				RESOURCES_PATH + file);
		String result = IOUtils.toString(inputStream, "UTF-8");
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

	public void testBlock() throws Exception {
		doSimpleTest("block.html");
	}

	public void testBlockWithRedirect() throws Exception {
		webConversation.getClientProperties().setAutoRedirect(true);
		doSimpleTest("/blockwithredirect.jsp?count=1", "blockwithredirect.jsp");
	}

	public void testESIInclude() throws Exception {
		doSimpleTest("esi-include.html");
	}

	public void testPost() throws Exception {
		// Post request with a e-acute urlencoded using UTF-8 charset
		PostMethodWebRequest req = new PostMethodWebRequest(APPLICATION_PATH
				+ "post.jsp", new ByteArrayInputStream("myField=%C3%A9"
				.getBytes("UTF-8")), "application/x-www-form-urlencoded");
		WebResponse resp = webConversation.getResponse(req);
		assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp
				.getResponseCode());
		assertEquals(getResource("post.jsp"), resp.getText());
	}

	public void testGet() throws Exception {
		doSimpleTest("get.jsp?myField=%C3%A9&send=Post+this+form", "get.jsp");
	}

	public void testAg2Index() throws Exception {
		doSimpleTest("ag2/index.html");
	}

	public void testRedirect() throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH
				+ "redirect.jsp");
		webConversation.getClientProperties().setAutoRedirect(false);
		WebResponse resp = webConversation.getResponse(req);
		assertEquals("Status should be "
				+ HttpServletResponse.SC_MOVED_TEMPORARILY,
				HttpServletResponse.SC_MOVED_TEMPORARILY, resp
						.getResponseCode());
		assertEquals(
				"Redirect header did not match",
				"http://localhost:8080/webassembletool-app-aggregator/redirected.jsp",
				resp.getHeaderField("Location"));
	}

	public void testTemplate() throws Exception {
		doSimpleTest("template.html");
	}

	public void testTemplateWithParams() throws Exception {
		/*
		 * Ensure aggregator "template" params are not forwarded to the backend
		 * template like it is the case for master/provider since these params
		 * are processed aggregator-side.
		 */
		doSimpleTest("templatewithparams.html");
	}

	public void testUtf8() throws Exception {
		doSimpleTest("utf8.jsp");
	}

	public void testRawPost() throws Exception {
		PostMethodWebRequest req = new PostMethodWebRequest(APPLICATION_PATH
				+ "post_raw.jsp", new ByteArrayInputStream("Hello smile!"
				.getBytes("UTF-8")), "raw/post-data");
		WebResponse resp = webConversation.getResponse(req);
		assertEquals("Response body did not match",
				"Posted body data : Hello smile!", resp.getText());
	}

	public void testRawPostWithQueryString() throws Exception {
		PostMethodWebRequest req = new PostMethodWebRequest(APPLICATION_PATH
				+ "post_raw.jsp?param=smile", new ByteArrayInputStream(
				"Hello !".getBytes("UTF-8")), "raw/post-data");
		WebResponse resp = webConversation.getResponse(req);
		assertEquals("Response body did not match",
				"Posted body data : Hello !smile", resp.getText());
	}

	public void testChunkedEncoding() throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH
				+ "ChunkedEncodingServlet");
		WebResponse resp = webConversation.getResponse(req);
		assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp
				.getResponseCode());
		assertEquals("Response body did not match", "Bonjour Monde !", resp
				.getText());
	}

	public void testZipDownload() throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "test.zip");
		WebResponse resp = webConversation.getResponse(req);
		InputStream inputStream = this.getClass().getResourceAsStream(
				RESOURCES_PATH + "test.zip");
		byte[] expected = IOUtils.toByteArray(inputStream);
		inputStream.close();
		byte[] result = IOUtils.toByteArray(resp.getInputStream());
		inputStream.close();
		assertTrue("Response body did not match", Arrays.equals(expected,
				result));
	}

	public void testSpaceInUrl() throws Exception {
		doSimpleTest("resource%20with%20spaces.html",
				"resource_with_spaces.html");
	}

	public void testBarInUrlParam() throws Exception {
		doSimpleTest("resource%20with%20spaces.html?bar=foo%7Cfoo%26bar",
				"resource_with_spaces.html");
	}

	public void testCircularRedirect() throws Exception {
		webConversation.getClientProperties().setAutoRedirect(true);
		doSimpleTest("blockwithredirect.jsp?count=10", "blockwithredirect.jsp");
	}

	public void testSpecialCharactersInUrl() throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "%5B");
		WebResponse resp = webConversation.getResponse(req);
		// Should get a 404 error but if the character is decoded to "[", it
		// will generate an invalid URL and a 500 error
		assertEquals("Status should be 404", HttpServletResponse.SC_NOT_FOUND,
				resp.getResponseCode());
	}

	public void testSpecial404Error() throws Exception {
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "whatever");
		WebResponse resp = webConversation.getResponse(req);
		// Should get a 404 error, not a 200 !
		assertEquals("Status should be 404", HttpServletResponse.SC_NOT_FOUND,
				resp.getResponseCode());
	}

	public void testMixedEncodings() throws Exception {
		doSimpleTest("template-mixed-encodings.jsp");
	}

	public void testBlockGzip() throws Exception {
		doSimpleTest("block-gzip.html", "block.html");
	}

	public void testPageGzip() throws Exception {
		doSimpleTest("page-gzip.jsp");
	}

	public void testBinaryGzip() throws Exception {
		doSimpleTest("binary-gzip.jsp");
	}

}
