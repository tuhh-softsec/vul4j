package net.webassembletool.test.cases;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.test.junit.HttpTestCase;
import net.webassembletool.test.junit.RawHttpServer;

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
        // e with acute accent in UTF-8 is C3A9
        // putting this explicitly avoids issues related to source code
        // encoding.
        String eacute = new String(new byte[] { (byte) 0xC3, (byte) 0xA9 },
                "UTF-8");
        params.put("myField", eacute);
        doPost("aggregated1/post.jsp", params, "UTF-8");
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
        assertHeaderEquals("location", "http://localhost:8080/aggregator/redirected.jsp");
    }

    public void testTemplate() throws Exception {
        doGet("aggregator/template.html");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile("aggregator/template.html");
    }

    public void testTemplateWithParams() throws Exception {
        /*
         * Ensure aggregator "template" params are not forwarded to the backend template like it is the case for master/provider since these params are processed aggregator-side.
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

    public void testChunkedEncoding() throws Exception {
        /* Start raw http server to serve chunked content */
        HashMap<String, String> h = new HashMap<String, String>();
        h.put("Transfer-Encoding", "chunked");
        RawHttpServer rhs = new RawHttpServer(RawHttpServer.buildHTTPBody(200, h, "8;\r\nBonjour \r\n" + "7;\r\nMonde !\r\n" + "0;\r\n\r\n" +
        // This is "end of transfer" trailing content should be ignored
                "4;\r\nSome\r\n"), 8888);
        rhs.start();
        doGet("aggregator/raw/rawrequest");
        rhs.join();
        assertStatus(200);
        assertBodyMatch("^Bonjour Monde !$");
    }

    public void testZipDownload() throws Exception {
        doGet("/aggregator/export_ContentContainer_55.zip");
        assertBodyEqualsLocalFile("aggregator/export_ContentContainer_55.zip");
    }
}
