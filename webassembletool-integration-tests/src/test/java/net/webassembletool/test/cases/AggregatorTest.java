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
	private final static String APPLICATION_PATH = "/webassembletool-app-aggregator/";
	
    public void testBlock() throws Exception {
        doGet(APPLICATION_PATH + "block.html");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile(APPLICATION_PATH + "block.html");
    }

    public void testESIInclude() throws Exception {
        doGet(APPLICATION_PATH + "esi-include.html");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile(APPLICATION_PATH + "esi-include.html");
    }

    public void testPost() throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        // e with acute accent in UTF-8 is C3A9
        // putting this explicitly avoids issues related to source code
        // encoding.
        String eacute = new String(new byte[] { (byte) 0xC3, (byte) 0xA9 },
                "UTF-8");
        params.put("myField", eacute);
        doPost(APPLICATION_PATH + "post.jsp", params, "UTF-8");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile(APPLICATION_PATH + "post.jsp");
    }

    public void testGet() throws Exception {
        doGet(APPLICATION_PATH + "get.jsp?myField=%C3%A9&send=Post+this+form");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile(APPLICATION_PATH + "get.jsp");
    }

    public void testAg2Index() throws Exception {
        doGet(APPLICATION_PATH + "ag2/index.html");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile(APPLICATION_PATH + "ag2/index.html");
    }

    public void testRedirect() throws Exception {
        doGet(APPLICATION_PATH + "redirect.jsp");
        assertStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        assertHeaderEquals("location", "http://localhost:8080/webassembletool-app-aggregator/redirected.jsp");
    }

    public void testTemplate() throws Exception {
        doGet(APPLICATION_PATH + "template.html");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile(APPLICATION_PATH + "template.html");
    }

    public void testTemplateWithParams() throws Exception {
        /*
         * Ensure aggregator "template" params are not forwarded to the backend template like it is the case for master/provider since these params are processed aggregator-side.
         */
        doGet(APPLICATION_PATH + "templatewithparams.html");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile(APPLICATION_PATH + "templatewithparams.html");
    }

    public void testUtf8() throws Exception {
        doGet(APPLICATION_PATH + "utf8.jsp");
        assertStatus(HttpServletResponse.SC_OK);
        assertBodyEqualsLocalFile(APPLICATION_PATH + "utf8.jsp");
    }

    public void testRawPost() throws Exception {
        doPost(APPLICATION_PATH + "post_raw.jsp", "Hello smile!".getBytes());
        assertBodyMatch("^Posted body data : Hello smile!$");
    }

    public void testRawPostWithQueryString() throws Exception {
        doPost(APPLICATION_PATH + "post_raw.jsp?param=smile", "Hello !".getBytes());
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
        doGet(APPLICATION_PATH + "raw/rawrequest");
        rhs.join();
        assertStatus(200);
        assertBodyMatch("^Bonjour Monde !$");
    }

    public void testZipDownload() throws Exception {
        doGet(APPLICATION_PATH + "export_ContentContainer_55.zip");
        assertBodyEqualsLocalFile(APPLICATION_PATH + "export_ContentContainer_55.zip");
    }
    
    public void testSpaceInUrl() throws Exception {
        doGet(APPLICATION_PATH + "ressource%20with%20spaces.html");
        assertStatus(200);
        assertBodyEqualsLocalFile(APPLICATION_PATH + "ressource_with_spaces.html");
    }
    
    public void testBarInUrlParam() throws Exception {
        doGet(APPLICATION_PATH + "ressource%20with%20spaces.html?bar=foo%7Cfoo%26bar");
        assertStatus(200);
        assertBodyEqualsLocalFile(APPLICATION_PATH + "ressource_with_spaces.html");
    }

    public void testCircularRedirect() throws Exception {
        doGet(APPLICATION_PATH + "blockwithredirect.jsp?count=10");
        assertStatus(200);
        assertBodyEqualsLocalFile(APPLICATION_PATH + "blockwithredirect.jsp.html");
    }
}
