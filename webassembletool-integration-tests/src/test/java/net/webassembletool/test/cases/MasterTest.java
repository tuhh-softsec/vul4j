package net.webassembletool.test.cases;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.test.junit.HttpTestCase;

/**
 * Junit tests using the "/master" webapp
 * 
 * @author Omar BENHAMID
 */
public class MasterTest extends HttpTestCase {
	private final static String APPLICATION_PATH = "/webassembletool-app-master/";

	// TODO This test does not work with cargo maven plugin as the version of
	// Xerces is too old. Needed to add a new version of Xerces to the
	// app-master project. But it works fine without it in a real servlet
	// container.
	public void testXml() throws Exception {
		doGet(APPLICATION_PATH + "xml.jsp");
		assertStatus(HttpServletResponse.SC_OK);
		assertBodyEqualsLocalFile(APPLICATION_PATH + "xml.jsp");
	}

	public void testBase() throws Exception {
		doGet(APPLICATION_PATH + "base.jsp");
		assertStatus(HttpServletResponse.SC_OK);
		assertBodyEqualsLocalFile(APPLICATION_PATH + "base.jsp");
	}

	public void testBlock() throws Exception {
		doGet(APPLICATION_PATH + "block.jsp");
		assertStatus(HttpServletResponse.SC_OK);
		assertBodyEqualsLocalFile(APPLICATION_PATH + "block.jsp");
	}

	public void testError500() throws Exception {
		doGet(APPLICATION_PATH + "error500.jsp");
		assertStatus(HttpServletResponse.SC_OK);
		assertBodyEqualsLocalFile(APPLICATION_PATH + "error500.jsp");
	}

	public void testProxy() throws Exception {
		doGet(APPLICATION_PATH + "images/smile.jpg");
		assertStatus(HttpServletResponse.SC_OK);
		assertHeaderEquals("Content-type", "image/jpeg");
	}

	public void testProxyIfmodifiedsince() throws Exception {
		doGet(APPLICATION_PATH + "images/smile.jpg");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("if-modified-since", getResponseHeader("Last-modified"));
		doGet(APPLICATION_PATH + "images/smile.jpg", headers);
		assertStatus(HttpServletResponse.SC_NOT_MODIFIED);
	}

	public void testReplaceBlock() throws Exception {
		doGet(APPLICATION_PATH + "replaceblock.jsp");
		assertStatus(HttpServletResponse.SC_OK);
		assertBodyEqualsLocalFile(APPLICATION_PATH + "replaceblock.jsp");
	}

	public void testReplaceTemplate() throws Exception {
		doGet(APPLICATION_PATH + "replacetemplate.jsp");
		assertStatus(HttpServletResponse.SC_OK);
		assertBodyEqualsLocalFile(APPLICATION_PATH + "replacetemplate.jsp");
	}

	public void testTemplate() throws Exception {
		doGet(APPLICATION_PATH + "template.jsp");
		assertStatus(HttpServletResponse.SC_OK);
		assertBodyEqualsLocalFile(APPLICATION_PATH + "template.jsp");
	}

	public void testTemplateMultipleParams() throws Exception {
		doGet(APPLICATION_PATH + "templatemultipleparams.jsp");
		assertStatus(HttpServletResponse.SC_OK);
		assertBodyEqualsLocalFile(APPLICATION_PATH
				+ "templatemultipleparams.jsp");
	}

	public void testTimeout() throws Exception {
		Date before = new Date();
		doGet(APPLICATION_PATH + "timeout.jsp");
		Date after = new Date();
		assertStatus(HttpServletResponse.SC_OK);
		long duration = after.getTime() - before.getTime();
		assertTrue("Request should take more than 2 s as it is not in cache",
				duration >= 1500);
		assertBodyEqualsLocalFile(APPLICATION_PATH + "timeout.jsp");
		before = new Date();
		doGet(APPLICATION_PATH + "timeout.jsp");
		after = new Date();
		assertStatus(HttpServletResponse.SC_OK);
		duration = after.getTime() - before.getTime();
		assertTrue("Request should take less than 5 s as it is in cache",
				duration < 1000);
		assertBodyEqualsLocalFile(APPLICATION_PATH + "timeout.jsp");
	}

	public void testUser() throws Exception {
		doGet(APPLICATION_PATH + "user.jsp");
		assertStatus(HttpServletResponse.SC_OK);
		assertBodyEqualsLocalFile(APPLICATION_PATH + "user.jsp");
	}

}
