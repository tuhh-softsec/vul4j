/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate.test.cases;

import junit.framework.TestCase;

import org.apache.commons.lang3.StringUtils;

import com.meterware.httpunit.AuthorizationRequiredException;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * Response headers forwarding or discarding tests. Standard HTTP headers are defined in:
 * <ul>
 * <li><a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html">RFC 2616</a></li>
 * <li><a href="http://www.ietf.org/rfc/rfc2817.txt">RFC 2817</a></li>
 * </ul>
 * 
 * @author Francois-Xavier Bonnet
 */
public class ResponseHeadersTest extends TestCase {
    private final static String APPLICATION_PATH = "http://localhost:8080/esigate-app-aggregator/";

    private void assertHeaderDiscarded(String name) throws Exception {
        assertHeaderDiscarded(name, "dummy");
    }

    private void assertHeaderDiscarded(String name, String value) throws Exception {
        String resp = sendRequestAndExpectResponseHeader(name, value);
        assertEquals("HTTP header " + name + " should not be forwarded", "", resp);
    }

    private void assertHeaderForwarded(String name) throws Exception {
        assertHeaderForwarded(name, "dummy");
    }

    private void assertHeaderForwarded(String name, String value) throws Exception {
        String resp = sendRequestAndExpectResponseHeader(name, value);
        if (resp.contains("\n")) {
            // In case of multiple headers
            String[] responses = StringUtils.split(resp, "\n");
            boolean found = false;
            for (String r : responses) {
                if (value.equals(r)) {
                    found = true;
                }
            }
            assertTrue("HTTP header " + name + " should be forwarded (multiple values)", found);
        } else {
            assertEquals("HTTP header " + name + " should be forwarded", value, resp);
        }
    }

    /**
     * Location header should be rewritten
     * 
     * @throws Exception
     */
    private void assertUriInHeaderIsRewritten(String name) throws Exception {
        String resp = sendRequestAndExpectResponseHeader(name,
                APPLICATION_PATH.replaceFirst("aggregator", "aggregated1") + "dummy");
        assertEquals(name + " header should be rewritten ('aggregator' replaced with 'aggregated1')", APPLICATION_PATH
                + "nocache/ag1/dummy", resp);
    }

    private String sendRequestAndExpectResponseHeader(String name, String value) throws Exception {
        WebConversation webConversation = new WebConversation();
        WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "nocache/ag1/response-headers.jsp");
        req.setHeaderField("X-response-header-" + name, value);
        WebResponse resp = webConversation.getResponse(req);
        String[] responseHeader = resp.getHeaderFields(name);
        if (responseHeader == null || responseHeader.length == 0) {
            return "";
        }
        String result = responseHeader[0];
        for (int i = 1; i < responseHeader.length; i++) {
            result += "\n" + responseHeader[i];
        }
        return result;
    }

    public void testAllow() throws Exception {
        assertHeaderForwarded("Allow");
    }

    public void testCacheControl() throws Exception {
        assertHeaderForwarded("Cache-Control");
    }

    public void testContentDisposition() throws Exception {
        assertHeaderForwarded("Content-Disposition");
    }

    /**
     * Content-encoding should not be forwarded as EsiGate will decompress gzip responses
     * 
     */
    public void testContentEncoding() {
        // FIXME not easy to test as adding this header without really gzipping
        // response body makes the response invalid
        // assertHeaderDiscarded("Content-Encoding", "gzip");
    }

    public void testContentLanguage() throws Exception {
        assertHeaderForwarded("Content-Language");
    }

    /**
     * Keep-alive is managed by the servlet container, we must not try to set it
     * 
     */
    public void testContentLength() {
        // Cannot test it
    }

    /**
     * Content-Location header should be rewritten
     * 
     * @throws Exception
     */
    public void testContentLocation() throws Exception {
        assertUriInHeaderIsRewritten("Content-Location");
    }

    public void testContentMD5() throws Exception {
        assertHeaderDiscarded("Content-MD5");
    }

    public void testContentRange() throws Exception {
        assertHeaderForwarded("Content-Range");
    }

    /**
     * Content-type is often modified by tha application server as it automatically sets it if not defined and it is
     * case-insensitive so we can have differences depending on the server vendor and version. So we just test that
     * 'text/plain' was forwarded, no matter the charset.
     * 
     * @throws Exception
     */
    public void testContentType() throws Exception {
        // FIXME not easy to test with arbitrary values as application servers
        // automatically set this header.
        // String resp = sendRequestAndExpectResponseHeader("Content-Type",
        // "text/plain");
        // if (!StringUtils.startsWithIgnoreCase(resp, "text/plain")) {
        // fail("HTTP header Content-Type should be forwarded, expected 'text/plain', got '"
        // + resp + "'");
        // }
        WebConversation webConversation = new WebConversation();
        WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "nocache/ag1/response-headers.jsp");
        WebResponse resp = webConversation.getResponse(req);
        String[] responseHeader = resp.getHeaderFields("Content-type");
        if (responseHeader == null || responseHeader.length != 1) {
            fail("There should be one and only one Content-type header in the response, found " + responseHeader.length);
        }
    }

    public void testDate() throws Exception {
        // Note: Date header set automatically by most application servers,
        // cannot override it on Tomcat 7.0 -> problem with cache validation
        // on Jetty 6 you can override it only with setDateHeader method
        // setHeader method will not work !
        // assertHeaderForwarded("Date", "Fri, 06 Apr 2012 15:18:12 GMT");
        WebConversation webConversation = new WebConversation();
        WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "nocache/ag1/response-headers.jsp");
        WebResponse resp = webConversation.getResponse(req);
        String[] responseHeader = resp.getHeaderFields("Date");
        if (responseHeader == null || responseHeader.length > 1) {
            fail("There should be one and only one Date header in the response, found " + responseHeader.length);
        }
    }

    public void testETag() throws Exception {
        assertHeaderForwarded("E-tag");
    }

    public void testExpires() throws Exception {
        assertHeaderForwarded("Expires");
    }

    /**
     * Keep-alive is managed by the servlet container, we must not try to change it
     * 
     */
    public void testKeepAlive() {
        // Cannot test it
    }

    public void testLastModified() throws Exception {
        assertHeaderForwarded("Last-Modified");
    }

    /**
     * Link header should be rewritten.
     * <p>
     * 
     * Link headers have the following syntax
     * <p>
     * <code>Link: &lt;/feed&gt;; rel="alternate"</code>
     * 
     * <p>
     * See
     * <ul>
     * <li>
     * https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=129</li>
     * <li>http://en.wikipedia.org/wiki/List_of_HTTP_header_fields</li>
     * </ul>
     * 
     * @throws Exception
     */
    public void testLink() throws Exception {

        String resp = sendRequestAndExpectResponseHeader("Link",
                "<" + APPLICATION_PATH.replaceFirst("aggregator", "aggregated1") + "dummy" + ">; rel=\"shortlink\"");

        // Assert link is rewritten correctly.
        assertEquals("Link" + " header should be rewritten ('aggregated1' replaced with 'aggregator')", "<"
                + APPLICATION_PATH + "nocache/ag1/dummy" + ">; rel=\"shortlink\"", resp);

    }

    /**
     * Location header should be rewritten
     * 
     * @throws Exception
     */
    public void testLocation() throws Exception {
        assertUriInHeaderIsRewritten("Location");
    }

    /**
     * P3p header should be forwarded. (Rewritting not implemented yet)
     * 
     * @throws Exception
     */
    public void testP3p() throws Exception {

        assertHeaderForwarded("P3P");
    }

    /**
     * Default ignored, see authentication
     * 
     * @throws Exception
     */
    public void testProxyAuthenticate() throws Exception {
        assertHeaderDiscarded("Proxy-Authenticate");
    }

    public void testRefresh() throws Exception {
        String resp = sendRequestAndExpectResponseHeader("Refresh",
                "5; url=" + APPLICATION_PATH.replaceFirst("aggregator", "aggregated1") + "dummy");

        // Assert link is rewritten correctly.
        assertEquals("Refresh" + " header should be rewritten ('aggregated1' replaced with 'aggregator')", "5; url="
                + APPLICATION_PATH + "nocache/ag1/dummy", resp);
    }

    public void testRetryAfter() throws Exception {
        assertHeaderForwarded("Retry-After");
    }

    public void testServer() throws Exception {
        assertHeaderForwarded("Server");
    }

    /**
     * Trailer Ignored (chunked encoding managed by the container)
     * 
     * @throws Exception
     */
    public void testTrailer() throws Exception {
        assertHeaderDiscarded("Trailer");
    }

    /**
     * Transfer-Encoding Ignored (chunked encoding managed by the container). We cannot really test it live as setting
     * this header generates invalid responses.
     * 
     */
    public void testTransferEncoding() {
        // Cannot test it
    }

    public void testVary() throws Exception {
        assertHeaderForwarded("Vary");
    }

    public void testVia() throws Exception {
        // HttpCache adds its own Via header but according to RFC2616 sec 4.2 it
        // is valid to append a new header instead of combining it with the
        // existing header.
        String resp = sendRequestAndExpectResponseHeader("Via", "1.1 EsiGate");
        if (!StringUtils.startsWithIgnoreCase(resp, "1.1 EsiGate")) {
            fail("HTTP header Via should be forwarded, expected 'Via: 1.1 EsiGate...', got '" + resp + "'");
        }
    }

    public void testWarning() throws Exception {
        assertHeaderForwarded("Warning");
    }

    public void testWWWAuthenticate() throws Exception {
        try {
            assertHeaderForwarded("WWW-Authenticate");
        } catch (AuthorizationRequiredException e) {
            // What we expected
            return;
        }
        fail("Header WWW-Authenticate should be forwarded");
    }

    public void testXPoweredBy() throws Exception {
        assertHeaderForwarded("X-Powered-By");
    }

}
