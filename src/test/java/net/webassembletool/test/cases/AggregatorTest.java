package net.webassembletool.test.cases;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.test.junit.HttpTestCase;

/**
 * Tests for aggregator webapp
 * 
 * @author FRBON
 */
public class AggregatorTest extends HttpTestCase {

    public void testBlock() throws Exception {
        doGet("aggregator/block.html");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile("aggregator/block.html");
    }

    public void testESIInclude() throws Exception {
        doGet("aggregator/esi-include.html");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile("aggregator/esi-include.html");
    }

    public void testPost() throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("myField", "é");
        doPost("aggregator/post.jsp", params, "UTF-8");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile("aggregator/post.jsp");
    }

    public void testGet() throws Exception {
        doGet("aggregator/get.jsp?myField=%C3%A9&send=Post+this+form");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile("aggregator/get.jsp");
    }

    public void testAg2Index() throws Exception {
        doGet("aggregator/ag2/index.html");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile("aggregator/ag2/index.html");
    }

    public void testRedirect() throws Exception {
        doGet("aggregator/redirect.jsp");
        assertStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        assertHeaderEquals("location",
                "http://localhost:8080/aggregator/redirected.jsp");
    }

    public void testTemplate() throws Exception {
        doGet("aggregator/template.html");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile("aggregator/template.html");
    }

    public void testTemplateWithParams() throws Exception {
        /*
         * Ensure aggregator "template" params are not forwarded to the backend
         * template like it is the case for master/provider since these params
         * are processed aggregator-side.
         */
        doGet("aggregator/templatewithparams.html");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile("aggregator/templatewithparams.html");
    }

    public void testUtf8() throws Exception {
        doGet("aggregator/utf8.jsp");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile("aggregator/utf8.jsp");
    }

    public void testRawPost() throws Exception {
        doPost("aggregator/post_raw.jsp", "Hello smile!".getBytes());
        assertBodyMatch("^Posted body data : Hello smile!$");
    }

    public void testRawPostWithQueryString() throws Exception {
        doPost("aggregator/post_raw.jsp?param=smile", "Hello !".getBytes());
        assertBodyMatch("^Posted body data : Hello !smile$");
    }
}
