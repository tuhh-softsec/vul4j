package net.webassembletool.test.cases;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.test.junit.HttpTestCase;

/**
 * Junit tests using the "/master" webapp
 * 
 * @author Omar BENHAMID
 */
public class MasterTest extends HttpTestCase {

    public void testBase() throws Exception {
	doGet("/master/base.jsp");
	assertStatus(HttpServletResponse.SC_OK);
	assertBodyEqualsLocalFile("/master/base.jsp");
    }

    public void testBlock() throws Exception {
	doGet("/master/block.jsp");
	assertStatus(HttpServletResponse.SC_OK);
	assertBodyEqualsLocalFile("/master/block.jsp");
    }

    public void testError500() throws Exception {
	doGet("/master/error500.jsp");
	assertStatus(HttpServletResponse.SC_OK);
	assertBodyEqualsLocalFile("/master/error500.jsp");
    }

    public void testProxy() throws Exception {
	doGet("/master/proxy.jsp");
	assertStatus(HttpServletResponse.SC_OK);
	assertBodyEqualsLocalFile("/master/proxy.jsp");
    }

    public void testReplaceBlock() throws Exception {
	doGet("/master/replaceblock.jsp");
	assertStatus(HttpServletResponse.SC_OK);
	assertBodyEqualsLocalFile("/master/replaceblock.jsp");
    }

    public void testReplaceTemplate() throws Exception {
	doGet("/master/replacetemplate.jsp");
	assertStatus(HttpServletResponse.SC_OK);
	assertBodyEqualsLocalFile("/master/replacetemplate.jsp");
    }

    public void testTemplate() throws Exception {
	doGet("/master/template.jsp");
	assertStatus(HttpServletResponse.SC_OK);
	assertBodyEqualsLocalFile("/master/template.jsp");
    }

    public void testTemplateMultipleParams() throws Exception {
	doGet("/master/templatemultipleparams.jsp");
	assertStatus(HttpServletResponse.SC_OK);
	assertBodyEqualsLocalFile("/master/templatemultipleparams.jsp");
    }

    public void testTimeout() throws Exception {
	Date before = new Date();
	doGet("/master/timeout.jsp");
	Date after = new Date();
	assertStatus(HttpServletResponse.SC_OK);
	long duration = after.getTime() - before.getTime();
	assertTrue("Request should take more than 2 s as it is not in cache",
		duration >= 1500);
	assertBodyEqualsLocalFile("/master/timeout.jsp");
	before = new Date();
	doGet("/master/timeout.jsp");
	after = new Date();
	assertStatus(HttpServletResponse.SC_OK);
	duration = after.getTime() - before.getTime();
	assertTrue("Request should take less than 5 s as it is in cache",
		duration < 1000);
	assertBodyEqualsLocalFile("/master/timeout.jsp");
    }

    public void testUser() throws Exception {
	doGet("/master/user.jsp");
	assertStatus(HttpServletResponse.SC_OK);
	assertBodyEqualsLocalFile("/master/user.jsp");
    }

}
