package org.esigate.test.cases;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * Tests for aggregator webapp
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
public class AggregatorTest extends BaseAggregatorTest {

    private void doCookieSimpleTest(String page, String resultResource) throws Exception {
        WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + page);
        WebResponse resp = webConversation.getResponse(req);

        assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp.getResponseCode());
        assertEqualsIgnoreWhiteSpaces(getResource(resultResource).replaceAll("JSESSIONID=[^;]+;", ""), resp.getText()
                .replaceAll("JSESSIONID=[^;]+;", ""));

        String[] setcookies = resp.getHeaderFields("Set-Cookie");
        boolean containsHttpOnlyCookie = false;
        for (String setcookie : setcookies) {
            if (setcookie.contains("test0")) {
                assertTrue(setcookie.contains("HttpOnly"));
                containsHttpOnlyCookie = true;
            }
        }
        assertTrue("Response should contains an HttpOnly cookie test0", containsHttpOnlyCookie);

    }

    public void testAg2Index() throws Exception {
        doSimpleTest("ag2/index.html");
    }

    public void testBarInUrlParam() throws Exception {
        doSimpleTest("resource%20with%20spaces.html?bar=foo%7Cfoo%26bar", "resource_with_spaces.html");
    }

    public void testBinaryGzip() throws Exception {
        WebResponse webResponse = doSimpleTest("binary-gzip.jsp");
        assertEquals("gzip", webResponse.getHeaderField("content-encoding"));
    }

    public void testBlock() throws Exception {
        doSimpleTest("block.html");
    }

    public void testBlockGzip() throws Exception {
        doSimpleTest("block-gzip.html", "block.html");
    }

    public void testBlocks2Drivers() throws Exception {
        doSimpleTest("blocks.html", "blocks.html");
    }

    public void testBlockWithRedirect() throws Exception {
        webConversation.getClientProperties().setAutoRedirect(true);
        doSimpleTest("/blockwithredirect.jsp?count=1", "blockwithredirect.jsp");
    }

    public void testBlockWithVariables() throws Exception {
        doSimpleTest("blockwithvariables.html", "block.html");
    }

    public void testChunkedEncoding() throws Exception {
        WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "ChunkedEncodingServlet");
        WebResponse resp = webConversation.getResponse(req);
        assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp.getResponseCode());
        assertEquals("Response body did not match", "Bonjour Monde !", resp.getText());
    }

    public void testCircularRedirect() throws Exception {
        webConversation.getClientProperties().setAutoRedirect(true);
        doSimpleTest("blockwithredirect.jsp?count=10", "blockwithredirect.jsp");
    }

    public void testCookies() throws Exception {
        doCookieSimpleTest("nocache/ag1/cookies.jsp", "cookies-firstcall.html");
        doCookieSimpleTest("nocache/ag1/cookies.jsp", "cookies.html");
        webConversation.getCookieDetails("test0");
    }

    /**
     * Ensure esi include tag is processed correclty if enclosed by esi comments.
     * 
     * @throws Exception
     */
    public void testESIComments() throws Exception {
        doSimpleTest("esi-comments.html");
    }

    public void testESIInclude() throws Exception {
        doSimpleTest("esi-include.html");
    }

    public void testEsiIncludeUtf8Iso88591NoCharsetSpecifiedInHeaders() throws Exception {
        doSimpleTest("include-mixed-encodings-no-charset.jsp");
    }

    public void testGet() throws Exception {
        doSimpleTest("get.jsp?myField=%C3%A9&send=Post+this+form", "get.jsp");
    }

    public void testLocalPageWithEsiInclude() throws Exception {
        doSimpleTest("local/local.jsp");
    }

    public void testLocalPageWithEsiIncludeCrossContext() throws Exception {
        doSimpleTest("local/local-crosscontext.jsp");
    }

    /**
     * Test for a post on a local page
     * 
     * @see <a href="https://github.com/esigate/esigate/issues/97">POST requests do not work with local providers
     *      #97</a>
     * 
     * @throws Exception
     */
    public void testLocalPost() throws Exception {
        // Post request with a e-acute urlencoded using UTF-8 charset
        PostMethodWebRequest req =
                new PostMethodWebRequest(APPLICATION_PATH + "local/post.jsp", new ByteArrayInputStream(
                        "myField=%C3%A9".getBytes("UTF-8")), "application/x-www-form-urlencoded");
        WebResponse resp = webConversation.getResponse(req);
        assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp.getResponseCode());
        assertEqualsIgnoreCarriageReturn(getResource("post.jsp"), resp.getText());
    }

    public void testMixedEncodings() throws Exception {
        doSimpleTest("template-mixed-encodings.jsp");
    }

    /**
     * Test for a nested include : aggregated1 includes a block from aggregated2 and the block from aggregated2 includes
     * a block from aggregated1
     * 
     * @throws Exception
     */
    public void testNestedInclude() throws Exception {
        doSimpleTest("nestedinclude.html");
    }

    public void testPageGzip() throws Exception {
        doSimpleTest("page-gzip.jsp");
    }

    public void testPost() throws Exception {
        // Post request with a e-acute urlencoded using UTF-8 charset
        PostMethodWebRequest req =
                new PostMethodWebRequest(APPLICATION_PATH + "post.jsp", new ByteArrayInputStream(
                        "myField=%C3%A9".getBytes("UTF-8")), "application/x-www-form-urlencoded");
        WebResponse resp = webConversation.getResponse(req);
        assertEquals("Status should be 200", HttpServletResponse.SC_OK, resp.getResponseCode());
        assertEquals(getResource("post.jsp"), resp.getText());
    }

    public void testRawPost() throws Exception {
        PostMethodWebRequest req =
                new PostMethodWebRequest(APPLICATION_PATH + "post_raw.jsp", new ByteArrayInputStream(
                        "Hello smile!".getBytes("UTF-8")), "raw/post-data");
        WebResponse resp = webConversation.getResponse(req);
        assertEquals("Response body did not match", "Posted body data : Hello smile!", resp.getText());
    }

    public void testRawPostWithQueryString() throws Exception {
        PostMethodWebRequest req =
                new PostMethodWebRequest(APPLICATION_PATH + "post_raw.jsp?param=smile", new ByteArrayInputStream(
                        "Hello !".getBytes("UTF-8")), "raw/post-data");
        WebResponse resp = webConversation.getResponse(req);
        assertEquals("Response body did not match", "Posted body data : Hello !smile", resp.getText());
    }

    public void testRedirect() throws Exception {
        WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "redirect.jsp");
        webConversation.getClientProperties().setAutoRedirect(false);
        WebResponse resp = webConversation.getResponse(req);
        assertEquals("Status should be " + HttpServletResponse.SC_MOVED_TEMPORARILY,
                HttpServletResponse.SC_MOVED_TEMPORARILY, resp.getResponseCode());
        String[] locations = resp.getHeaderFields("Location");
        assertNotNull(locations);
        assertEquals("should be only one location: " + Arrays.asList(locations), 1, locations.length);
        assertEquals("Redirect header did not match", "http://localhost:8080/esigate-app-aggregator/redirected.jsp",
                locations[0]);
    }

    public void testRemotePageWithCrosscontextEsiInclude() throws Exception {
        doSimpleTest("crosscontext/include-crosscontext.jsp");
    }

    public void testRemotePageWithLocalEsiInclude() throws Exception {
        doSimpleTest("include-local.jsp");
    }

    public void testSpaceInUrl() throws Exception {
        doSimpleTest("resource%20with%20spaces.html", "resource_with_spaces.html");
    }

    public void testSpecial404Error() throws Exception {
        WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "whatever");
        WebResponse resp = webConversation.getResponse(req);
        // Should get a 404 error, not a 200 !
        assertEquals("Status should be 404", HttpServletResponse.SC_NOT_FOUND, resp.getResponseCode());
    }

    public void testSpecialCharactersInUrl() throws Exception {
        WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "%5B");
        WebResponse resp = webConversation.getResponse(req);
        // Should get a 404 error but if the character is decoded to "[", it
        // will generate an invalid URL and a 500 error
        assertEquals("Status should be 404", HttpServletResponse.SC_NOT_FOUND, resp.getResponseCode());
    }

    public void testTemplate() throws Exception {
        doSimpleTest("template.html");
    }

    public void testTemplateWithParams() throws Exception {
        /*
         * Ensure aggregator "template" params are not forwarded to the backend template like it is the case for
         * master/provider since these params are processed aggregator-side.
         */
        doSimpleTest("templatewithparams.html");
    }

    public void testTemplateWithVariables() throws Exception {
        doSimpleTest("templatewithvariables.html", "template.html");
    }

    public void testUtf8() throws Exception {
        doSimpleTest("utf8.jsp");
    }

    public void testZipDownload() throws Exception {
        WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "test.zip");
        WebResponse resp = webConversation.getResponse(req);
        InputStream inputStream = this.getClass().getResourceAsStream(RESOURCES_PATH + "test.zip");
        byte[] expected = IOUtils.toByteArray(inputStream);
        inputStream.close();
        byte[] result = IOUtils.toByteArray(resp.getInputStream());
        inputStream.close();
        assertTrue("Response body did not match", Arrays.equals(expected, result));
    }

}
