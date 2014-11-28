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

package org.esigate.impl;

import java.util.Properties;

import junit.framework.TestCase;

import org.esigate.Parameters;

/**
 * Tests on UrlRewriter.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class UrlRewriterTest extends TestCase {

    private UrlRewriter urlRewriter;
    private String visibleUrlBase;
    private String fixMode;
    private String requestUrl;
    private String baseUrl;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        urlRewriter = null;
        visibleUrlBase = null;
        fixMode = null;
        requestUrl = null;
        baseUrl = null;
    }

    private void createUrlRewriter() {
        Properties properties = new Properties();
        properties.put(Parameters.FIX_MODE, fixMode);
        urlRewriter = new UrlRewriter(properties);
    }

    private String rewriteUrl(String url) {
        return urlRewriter.rewriteUrl(url, requestUrl, baseUrl, visibleUrlBase);
    }

    private void assertRewrites(String sourceUrl, String rewrittenUrl) {
        createUrlRewriter();
        assertEquals(rewrittenUrl, rewriteUrl(sourceUrl));
    }

    private void assertDoesNotRewrite(String sourceUrl) {
        createUrlRewriter();
        assertEquals(sourceUrl, rewriteUrl(sourceUrl));
    }

    private void assertPatternMatches(String pattern) {
        baseUrl = "http://backend/context";
        visibleUrlBase = "http://backend/context";
        requestUrl = "path/page.html";
        fixMode = "relative";
        String input = pattern.replace("{}", "test");
        String expectedOutput = pattern.replace("{}", "/context/path/test");
        createUrlRewriter();
        String result = urlRewriter.rewriteHtml(input, requestUrl, baseUrl, visibleUrlBase).toString();
        assertEquals(expectedOutput, result);
    }

    private void assertPatternDoesNotMatch(String pattern) {
        baseUrl = "http://backend/context";
        visibleUrlBase = "http://backend/context";
        requestUrl = "path/page.html";
        fixMode = "relative";
        String input = pattern.replace("{}", "test");
        createUrlRewriter();
        String result = urlRewriter.rewriteHtml(input, requestUrl, baseUrl, visibleUrlBase).toString();
        assertEquals(input, result);
    }

    public void testSrcPatternMatches() {
        assertPatternMatches("<img src=\"{}\"/>");
    }

    public void testHrefPatternMatches() {
        assertPatternMatches("<a href=\"{}\"/>link</a>");
    }

    public void testActionPatternMatches() {
        assertPatternMatches("<form name=\"test\" action=\"{}\"/> test </a>");
    }

    public void testBackgroundPatternMatches() {
        assertPatternMatches("<img background=\"{}\"/>");
    }

    public void testPatternsAreCaseInsensitive() {
        assertPatternMatches("<a HREF=\"{}\"/>  </a>");
        assertPatternMatches("<img SrC=\"{}\"/>");
    }

    /**
     * Ensure CDATA does not match replacement rules.
     * 
     * @see "http://www.esigate.org/mantisbt/view.php?id=120"
     */
    public void testCdataPatternDoesNotMatch() {
        assertPatternDoesNotMatch("<![CDATA[   var src=\"test\" ]]>");
    }

    public void testEsiIncludePatternDoesNotMatch() {
        assertPatternDoesNotMatch("<esi:include src=\"$(PROVIDER{provider})/\" />");
    }

    public void testUrlRewrite() {
        baseUrl = "http://backend/context";
        visibleUrlBase = "http://backend/context";
        requestUrl = "path/file.html";

        fixMode = "relative";
        assertRewrites("images/logo.png", "/context/path/images/logo.png");
        assertRewrites("/context/page/page1.htm", "/context/page/page1.htm");
        assertDoesNotRewrite("http://www.google.com/logo.com");

        fixMode = "absolute";
        assertRewrites("images/logo.png", "http://backend/context/path/images/logo.png");
        assertRewrites("/context/page/page1.htm", "http://backend/context/page/page1.htm");
        assertDoesNotRewrite("http://www.google.com/logo.com");
    }

    public void testUrlRewriteReplaceContext() {
        baseUrl = "http://backend/context/";
        visibleUrlBase = "http://backend/visiblecontext/";
        requestUrl = "path/file.html";

        fixMode = "relative";
        assertRewrites("images/logo.png", "/visiblecontext/path/images/logo.png");
        assertRewrites("/context/page/page1.htm", "/visiblecontext/page/page1.htm");
        assertDoesNotRewrite("http://www.google.com/logo.com");

        fixMode = "absolute";
        assertRewrites("images/logo.png", "http://backend/visiblecontext/path/images/logo.png");
        assertRewrites("/context/page/page1.htm", "http://backend/visiblecontext/page/page1.htm");
        assertDoesNotRewrite("http://www.google.com/logo.com");
    }

    public void testUrlRewriteSpecialChars() {
        baseUrl = "http://backend/context/";
        visibleUrlBase = "http://visibleservername/";
        requestUrl = "/page/";

        fixMode = "absolute";
        assertRewrites("images/logo$.png", "http://visibleservername/page/images/logo$.png");
        assertRewrites("images/logo 1.png", "http://visibleservername/page/images/logo%201.png");
    }

    /**
     * Ensures links like &lt;a href="?test=true">link&lt;a/> are correctly fixed, with both RELATIVE and ABSOLUTE
     * settings.
     * 
     */
    public void testUrlRewriteRawQueryString() {
        baseUrl = "http://backend/";
        visibleUrlBase = "http://backend/";
        requestUrl = "/path/page";

        fixMode = "relative";
        assertRewrites("?p=services", "/path/page?p=services");

        fixMode = "absolute";
        assertRewrites("?p=services", "http://backend/path/page?p=services");
    }

    /**
     * Test for 0000186: ResourceFixup : StringIndexOutOfBoundsException: String index out of range: -1.
     * 
     * @see "http://www.esigate.org/mantisbt/view.php?id=186"
     * 
     */
    public void testUrlRewriteSlash() {
        baseUrl = "http://backend/path/";
        visibleUrlBase = "http://backend/";
        requestUrl = "/";

        fixMode = "relative";
        assertRewrites("/path/controller", "/controller");

        fixMode = "absolute";
        assertRewrites("/path/controller", "http://backend/controller");
    }

    public void testUrlRewritePort() {
        baseUrl = "http://backend:1234/path/";
        visibleUrlBase = "http://backend:888/";
        requestUrl = "/";

        fixMode = "absolute";
        assertRewrites("/path/controller", "http://backend:888/controller");
    }

    public void testUrlRewriteProtocol() {
        baseUrl = "http://backend/path/";
        visibleUrlBase = "https://backend/";
        requestUrl = "/";

        fixMode = "absolute";
        assertRewrites("/path/controller", "https://backend/controller");
    }

    /**
     * Test for 0000238: ResourceFixUp does not support the protocol-relative urls.
     * <p>
     * protocol-relative urls should be considered as absolute urls.
     * 
     * @see "http://www.esigate.org/mantisbt/view.php?id=238"
     * 
     */
    public void testUrlRewriteProtocolRelativeUrl() {
        baseUrl = "http://backend/path/";
        visibleUrlBase = "http://backend/";
        requestUrl = "/";

        fixMode = "relative";
        assertDoesNotRewrite("//domain.com/applicationPath/controller");

        fixMode = "absolute";
        assertDoesNotRewrite("//domain.com/applicationPath/controller");
    }

    /**
     * Test for 0000286: ResourceFixupRenderer StringIndexOutOfBoundsException
     * <p>
     * Index out of range if page parameter is empty.
     * 
     * @see "http://www.esigate.org/mantisbt/view.php?id=286"
     * 
     */
    public void testUrlRewriteEmptyRequestUrl() {
        baseUrl = "http://backend";
        visibleUrlBase = "http://backend";
        requestUrl = "";

        fixMode = "relative";
        assertRewrites("test", "/test");

        fixMode = "absolute";
        assertRewrites("test", "http://backend/test");
    }

    public void testUrlSanitizing() {
        baseUrl = "http://backend/context/";
        visibleUrlBase = "http://visibleservername/";
        requestUrl = "/page/";

        fixMode = "relative";
        assertRewrites("../styles/style.css", "/styles/style.css");

        fixMode = "absolute";
        assertRewrites("../styles/style.css", "http://visibleservername/styles/style.css");
    }

    public void testUrlRewriteEmptyUrl() {
        baseUrl = "http://backend";
        visibleUrlBase = "http://backend";
        requestUrl = "/test";

        fixMode = "relative";
        assertRewrites("", "");

        fixMode = "absolute";
        assertRewrites("", "");
    }

    /**
     * generation of relative links not correct https://github.com/esigate/esigate/issues/59
     */
    public void testUrlSanitizingWithDot() {
        baseUrl = "http://backend/context/";
        visibleUrlBase = "http://visibleservername/";
        requestUrl = "/page";

        fixMode = "relative";
        assertRewrites("./test", "/test");

        fixMode = "absolute";
        assertRewrites("./test", "http://visibleservername/test");
    }

    public void testRewriteReferer() {
        baseUrl = "http://backend/context/";
        visibleUrlBase = "http://visibleservername/test/";
        fixMode = "relative";

        createUrlRewriter();

        String referer = "http://visibleservername/test/page.html";

        assertEquals("http://backend/context/page.html", urlRewriter.rewriteReferer(referer, baseUrl, visibleUrlBase));
    }

}
