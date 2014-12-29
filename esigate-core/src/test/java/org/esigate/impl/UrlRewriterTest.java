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

/**
 * Tests on UrlRewriter.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class UrlRewriterTest extends TestCase {

    private UrlRewriter urlRewriter;
    private String visibleUrlBase;
    private boolean absolute;
    private String requestUrl;
    private String baseUrl;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        urlRewriter = null;
        visibleUrlBase = null;
        absolute = false;
        requestUrl = null;
        baseUrl = null;
    }

    private void createUrlRewriter() {
        Properties properties = new Properties();
        urlRewriter = new UrlRewriter(properties);
    }

    private String rewriteUrl(String url) {
        return urlRewriter.rewriteUrl(url, requestUrl, baseUrl, visibleUrlBase, absolute);
    }

    private String rewriteHtml(String html) {
        return urlRewriter.rewriteHtml(html, requestUrl, baseUrl, visibleUrlBase, absolute).toString();
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
        absolute = false;
        String input = pattern.replace("{}", "test");
        String expectedOutput = pattern.replace("{}", "/context/path/test");
        createUrlRewriter();
        String result = rewriteHtml(input);
        assertEquals(expectedOutput, result);
    }

    private void assertPatternDoesNotMatch(String pattern) {
        baseUrl = "http://backend/context";
        visibleUrlBase = "http://backend/context";
        requestUrl = "path/page.html";
        absolute = false;
        String input = pattern.replace("{}", "test");
        createUrlRewriter();
        String result = rewriteHtml(input);
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

        absolute = false;
        assertRewrites("images/logo.png", "/context/path/images/logo.png");
        assertRewrites("/context/page/page1.htm", "/context/page/page1.htm");
        assertDoesNotRewrite("http://www.google.com/logo.com");

        absolute = true;
        assertRewrites("images/logo.png", "http://backend/context/path/images/logo.png");
        assertRewrites("/context/page/page1.htm", "http://backend/context/page/page1.htm");
        assertDoesNotRewrite("http://www.google.com/logo.com");
    }

    public void testUrlRewriteReplaceContext() {
        baseUrl = "http://backend/context/";
        visibleUrlBase = "http://backend/visiblecontext/";
        requestUrl = "path/file.html";

        absolute = false;
        assertRewrites("images/logo.png", "/visiblecontext/path/images/logo.png");
        assertRewrites("/context/page/page1.htm", "/visiblecontext/page/page1.htm");
        assertDoesNotRewrite("http://www.google.com/logo.com");

        absolute = true;
        assertRewrites("images/logo.png", "http://backend/visiblecontext/path/images/logo.png");
        assertRewrites("/context/page/page1.htm", "http://backend/visiblecontext/page/page1.htm");
        assertDoesNotRewrite("http://www.google.com/logo.com");
    }

    public void testUrlRewriteSpecialChars() {
        baseUrl = "http://backend/context/";
        visibleUrlBase = "http://visibleservername/";
        requestUrl = "/page/";

        absolute = true;
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

        absolute = false;
        assertRewrites("?p=services", "/path/page?p=services");

        absolute = true;
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

        absolute = false;
        assertRewrites("/path/controller", "/controller");

        absolute = true;
        assertRewrites("/path/controller", "http://backend/controller");
    }

    public void testUrlRewritePort() {
        baseUrl = "http://backend:1234/path/";
        visibleUrlBase = "http://backend:888/";
        requestUrl = "/";

        absolute = true;
        assertRewrites("/path/controller", "http://backend:888/controller");
    }

    public void testUrlRewriteProtocol() {
        baseUrl = "http://backend/path/";
        visibleUrlBase = "https://backend/";
        requestUrl = "/";

        absolute = true;
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

        absolute = false;
        assertDoesNotRewrite("//domain.com/applicationPath/controller");

        absolute = true;
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

        absolute = false;
        assertRewrites("test", "/test");

        absolute = true;
        assertRewrites("test", "http://backend/test");
    }

    public void testUrlSanitizing() {
        baseUrl = "http://backend/context/";
        visibleUrlBase = "http://visibleservername/";
        requestUrl = "/page/";

        absolute = false;
        assertRewrites("../styles/style.css", "/styles/style.css");

        absolute = true;
        assertRewrites("../styles/style.css", "http://visibleservername/styles/style.css");
    }

    public void testUrlRewriteEmptyUrl() {
        baseUrl = "http://backend";
        visibleUrlBase = "http://backend";
        requestUrl = "/test";

        absolute = false;

        createUrlRewriter();
        assertEquals("<a href=\"\">a</a>", rewriteHtml("<a href=\"\">a</a>"));

        absolute = true;
        assertEquals("<a href=\"\">a</a>", rewriteHtml("<a href=\"\">a</a>"));
    }

    /**
     * Generation of relative links is not correct. https://github.com/esigate/esigate/issues/59
     */
    public void testUrlSanitizingWithDot() {
        baseUrl = "http://backend/context/";
        visibleUrlBase = "http://visibleservername/";
        requestUrl = "/page";

        absolute = false;
        assertRewrites("./test", "/test");

        absolute = true;
        assertRewrites("./test", "http://visibleservername/test");
    }

    public void testRewriteReferer() {
        baseUrl = "http://backend/context/";
        visibleUrlBase = "http://visibleservername/test/";
        absolute = false;

        createUrlRewriter();

        String referer = "http://visibleservername/test/page.html";

        assertEquals("http://backend/context/page.html", urlRewriter.rewriteReferer(referer, baseUrl, visibleUrlBase));
    }

    /**
     * Test for Issue 83: UrlRewriter does not trim urls.
     * 
     * @see "https://github.com/esigate/esigate/issues/83"
     */
    public void testUrlRewriteTrim() {
        baseUrl = "http://backend";
        visibleUrlBase = "http://visible/context/";
        requestUrl = "/";

        absolute = false;
        assertRewrites(" \t\n/test", "/context/test");

        absolute = true;
        assertRewrites(" \t\n/test", "http://visible/context/test");
    }

}
