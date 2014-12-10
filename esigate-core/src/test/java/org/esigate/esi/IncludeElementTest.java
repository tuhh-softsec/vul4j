/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate.esi;

import java.io.IOException;
import java.util.Date;

import org.apache.http.HttpStatus;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.esigate.HttpErrorPage;

public class IncludeElementTest extends AbstractElementTest {
    private static final long TEN_SECONDS = 10L * 1000L;

    @Override
    protected void setUp() {
        super.setUp();
        addResource("/test", "test");
        addResource("http://www.foo.com/test", "test");
    }

    public void testIncludeProvider() throws IOException, HttpErrorPage {
        String page = "before <esi:include src=\"$(PROVIDER{mock})/test\" /> after";
        String result = render(page);
        assertEquals("before test after", result);
    }

    public void testIncludeProvider10() throws IOException, HttpErrorPage {
        int nb = 10;

        String page = "before ";
        for (int i = 0; i < nb; i++) {
            page = page + "<esi:include src=\"$(PROVIDER{mock})/test\" />";
        }
        page = page + " after";

        String result = render(page);

        String expected = "before ";
        for (int i = 0; i < nb; i++) {
            expected = expected + "test";
        }
        expected = expected + " after";
        assertEquals(expected, result);
    }

    public void testIncludeProviderLegacy() throws IOException, HttpErrorPage {
        String page = "before <esi:include src=\"$PROVIDER({mock})/test\" /> after";
        String result = render(page);
        assertEquals("before test after", result);
    }

    public void testIncludeAbsolute() throws IOException, HttpErrorPage {
        String page = "before <esi:include src=\"http://www.foo.com/test\" /> after";
        String result = render(page);
        assertEquals("before test after", result);
    }

    public void testIncludeFragment() throws IOException, HttpErrorPage {
        String page = "before <esi:include src=\"$(PROVIDER{mock})/testFragment\" fragment =\"myFragment\" /> after";
        addResource("/testFragment", "before fragment "
                + "<esi:fragment name=\"myFragment\">---fragment content---</esi:fragment>" + " after fragment");
        String result = render(page);
        assertEquals("before ---fragment content--- after", result);
    }

    public void testIncludeQueryString() throws IOException, HttpErrorPage {
        incomingRequest("http://localhost/test?queryparameter1=test&queryparameter2=test2");
        String page = "before <esi:include src=\"$(PROVIDER{mock})/test?$(QUERY_STRING)\" /> after";
        addResource("/test?queryparameter1=test&queryparameter2=test2", "query OK");
        String result = render(page);
        assertEquals("before query OK after", result);
    }

    public void testIncludeQueryStringParameter() throws IOException, HttpErrorPage {
        incomingRequest("http://localhost/test?queryparameter1=test&queryparameter2=test2");
        String page = "before <esi:include src=\"$(PROVIDER{mock})/$(QUERY_STRING{queryparameter2})\" /> after";
        addResource("/test2", "queryparameter2 OK");
        String result = render(page);
        assertEquals("before queryparameter2 OK after", result);
    }

    public void testIncludeInlineCache() throws IOException, HttpErrorPage {
        String page = "before <esi:include src='$(PROVIDER{mock})/inline-cache' /> after";
        InlineCache.storeFragment("$(PROVIDER{mock})/inline-cache", null, false, null, "---inline cache item---");
        String result = render(page);
        assertEquals("before ---inline cache item--- after", result);

        InlineCache.storeFragment("$(PROVIDER{mock})/inline-cache", new Date(System.currentTimeMillis() + TEN_SECONDS),
                false, null, "---updated inline cache item---");
        result = render(page);
        assertEquals("before ---updated inline cache item--- after", result);

        InlineCache.storeFragment("$(PROVIDER{mock})/inline-cache", new Date(System.currentTimeMillis() - TEN_SECONDS),
                false, null, "---expired inline cache item---");
        addResource("/inline-cache", "---fetched inline cache item---");
        result = render(page);
        assertEquals("before ---fetched inline cache item--- after", result);
    }

    public void testIncludeInlineElement() throws IOException, HttpErrorPage {
        String page =
                "before <esi:include src='$(PROVIDER{mock})/inline-cache' /> middle "
                        + "<esi:inline name='$(PROVIDER{mock})/inline-cache' "
                        + "fetchable='false'>---inline cache item---</esi:inline>"
                        + "<esi:include src='$(PROVIDER{mock})/inline-cache' /> after";
        addResource("/inline-cache", "---fetched inline cache item---");
        String result = render(page);
        assertEquals("before ---fetched inline cache item--- middle ---inline cache item--- after", result);
    }

    public void testIncludeReplaceElementFragment() throws IOException, HttpErrorPage {
        String page =
                "before <esi:include src='$(PROVIDER{mock})/include-replace' >"
                        + "<esi:replace fragment='replaceable-fragment'>"
                        + "<esi:vars>$(HTTP_COOKIE{cookieName})</esi:vars>" + "</esi:replace>" + "</esi:include> after";
        String includedPage =
                "-incl-page-start" + " <esi:fragment name='replaceable-fragment'>replaced content</esi:fragment>"
                        + " <esi:fragment name='untouched-fragment' />" + " incl-page-end-";
        getRequestBuilder().addCookie(new BasicClientCookie("cookieName", "fragment replaced"));
        addResource("/include-replace", includedPage);
        String result = render(page);
        assertEquals("before -incl-page-start fragment replaced  incl-page-end- after", result);
    }

    public void testIncludeReplaceElementRegexp() throws IOException, HttpErrorPage {
        String page =
                "before <esi:include src='$(PROVIDER{mock})/include-replace' >"
                        + "<esi:replace regexp='replaceable-regexp'>"
                        + "<esi:vars>$(HTTP_COOKIE{cookieName})</esi:vars>" + "</esi:replace>" + "</esi:include> after";
        String includedPage =
                "-incl-page-start" + " <esi:fragment name='untouched-fragment'>zzz</esi:fragment>"
                        + " replaceable-regexp" + " incl-page-end-";
        getRequestBuilder().addCookie(new BasicClientCookie("cookieName", "regexp replaced"));
        addResource("/include-replace", includedPage);
        String result = render(page);
        assertEquals("before -incl-page-start zzz regexp replaced incl-page-end- after", result);
    }

    public void testIncludeReplaceElementExpression() throws IOException, HttpErrorPage {
        String page =
                "before <esi:include src='$(PROVIDER{mock})/include-replace' >"
                        + "<esi:replace expression='replaceable-regexp'>"
                        + "<esi:vars>$(HTTP_COOKIE{cookieName})</esi:vars>" + "</esi:replace>" + "</esi:include> after";
        String includedPage =
                "-incl-page-start" + " <esi:fragment name='untouched-fragment'>zzz</esi:fragment>"
                        + " replaceable-regexp" + " incl-page-end-";
        getRequestBuilder().addCookie(new BasicClientCookie("cookieName", "regexp replaced"));
        addResource("/include-replace", includedPage);
        String result = render(page);
        assertEquals("before -incl-page-start zzz regexp replaced incl-page-end- after", result);
    }

    public void testIncludeXpath() throws IOException, HttpErrorPage {
        String page =
                "before " + "<esi:include src='$(PROVIDER{mock})/inline-xpath' xpath='//html:body/text()' />"
                        + " after";
        addResource("/inline-xpath",
                "<html><title>The header</title><body>-the body-<br><ul><li>list item</li></ul></body></html>");
        String result = render(page);
        assertEquals("before -the body- after", result);
    }

    public void testIncludeXpathSeveralMatchingNodes() throws IOException, HttpErrorPage {
        String page =
                "before <esi:include src='$(PROVIDER{mock})/inline-xpath' "
                        + "xpath='//html:body/html:ul/html:li/text()' />" + " after";
        addResource("/inline-xpath", "<html><title>The header</title><body>-the body-<br>"
                + "<ul><li>list item 1</li><li>list item 2</li></ul></body></html>");
        String result = render(page);
        assertEquals("before list item 1list item 2 after", result);
    }

    public void testIncludeXSLT() throws IOException, HttpErrorPage {
        String page =
                "before <esi:include src='$(PROVIDER{mock})/inline-xslt' "
                        + "stylesheet=\"http://www.foo.com/test.xsl\" />" + " after";
        addResource("/inline-xslt", "<html><body>The body<br></body></html>");
        addResource("http://www.foo.com/test.xsl", "<?xml version=\"1.0\"?>"
                + "<xsl:stylesheet version=\"1.0\" xmlns=\"http://www.w3.org/1999/xhtml\" "
                + "xmlns:html=\"http://www.w3.org/1999/xhtml\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
                + "<xsl:output method=\"xml\" omit-xml-declaration=\"yes\"/>" + "<xsl:template match=\"//html:body\">"
                + "<xsl:copy-of select=\".\"/>" + "</xsl:template>" + "</xsl:stylesheet>");
        String result = render(page);
        assertEquals("before <body>The body<br /></body> after", result);
    }

    public void testIncludeAlt1() throws IOException, HttpErrorPage {
        String page =
                "before " + "<esi:include src='$(PROVIDER{mock})/alt-url' alt=\"http://www.foo.com/test\" />"
                        + " after";
        addResource("/alt-url", "---fetched alt url---");
        String result = render(page);
        assertEquals("before ---fetched alt url--- after", result);
    }

    public void testIncludeAlt2() throws IOException, HttpErrorPage {
        String page =
                "before " + "<esi:include src='$(PROVIDER{mock})/not-found' alt=\"http://www.foo.com/test\" />"
                        + " after";
        String result = render(page);
        assertEquals("before test after", result);
    }

    /**
     * Test src + alt + onerror combined.
     * <p>
     * See 0000262: Unable to use both alt and onerror in esi:include
     * <p>
     * https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=262
     * <p>
     * See http://www.akamai.com/dl/technical_publications/esi_faq.pdf
     * <p>
     * What happens if the src and alt tags cannot be retrieved? If the edge server can fetch neither the src object nor
     * the alt object, it returns a 404 HTTP error with a simple error message—unless the onerror attribute is present.
     * The onerror attribute can be used with an src only or with both an src and alt attempt. If onerror=“continue” is
     * specified and the src and alt fail to fetch the object, ESI deletes the include tag and serves the page without
     * any object replacing the include statement.
     * <p>
     * When onerror=“continue” is set and the fetch fails, the edge server does not serve a default object. Without the
     * onerror attribute, the edge server attempts to fetch a default object if one is specified in the configuration
     * file. The default object can be processed by ESI. However, if anything goes wrong during ESI processing, the
     * result is that the edge server will send an error to the client. If you choose to use ESI in default objects, the
     * ESI code should be very simple and well-tested. For more information on error handling, see “Exception and Error
     * Handling” in the ESI Developer’s Guide. For information on using onerror inside ESI’s explicit exception handling
     * method, see the try block documentation in the same document.
     * 
     * @throws IOException
     * @throws HttpErrorPage
     */
    public void testIncludeAltOnError() throws IOException, HttpErrorPage {
        String page =
                "before " + "<esi:include src='$(PROVIDER{mock})/not-found' "
                        + "alt=\"$(PROVIDER{mock})/not-found2\"  onerror=\"continue\"/>" + " after";
        String result = render(page);
        assertEquals("before  after", result);
    }

    public void testOnError() throws IOException {
        String page = "before <esi:include src=\"http://www.foo.com/test-onerror\" /> after";
        try {
            render(page);
            fail("should throw HttpErrorPage");
        } catch (HttpErrorPage e) {
            assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpResponse().getStatusLine().getStatusCode());
        }
    }

    public void testOnErrorContinue() throws IOException, HttpErrorPage {
        String page = "before <esi:include src=\"http://www.foo.com/test-onerror\" onerror=\"continue\"/> after";
        String result = render(page);
        assertEquals("before  after", result);
    }

    /**
     * JQuery selector interpreted like a ESIgate variable during an esi:include
     * https://github.com/esigate/esigate/issues/35
     * 
     * @throws IOException
     * @throws HttpErrorPage
     */
    public void testIncludeReplaceJquerySelector() throws IOException, HttpErrorPage {
        String page =
                "<esi:include src=\"$(PROVIDER{mock})/test\"><esi:replace fragment=\"fragment\">"
                        + "<script type=\"text/javascript\">$(document).ready(function () {...});</script>"
                        + "</esi:replace></esi:include>";
        addResource("/test", "<esi:fragment name=\"fragment\">---fragment content---</esi:fragment>");
        String result = render(page);
        assertEquals("<script type=\"text/javascript\">$(document).ready(function () {...});</script>", result);
    }

    public void testIncludeTagContentShouldBeRemoved() throws IOException, HttpErrorPage {
        String page =
                "before <esi:include src=\"$(PROVIDER{mock})/testFragment\" "
                        + "fragment =\"myFragment\">Content to be removed</esi:include> after";
        addResource("/testFragment", "before fragment "
                + "<esi:fragment name=\"myFragment\">---fragment content---</esi:fragment>" + " after fragment");
        String result = render(page);
        assertEquals("before ---fragment content--- after", result);
    }

    public void testIncludeFragmentNested() throws IOException, HttpErrorPage {
        String page = "before <esi:include src='$(PROVIDER{mock})/fragment' fragment='test' /> after";
        String includedPage =
                "-incl-page-start <esi:fragment name='test'>-" + "<esi:fragment name='test'>content</esi:fragment>"
                        + "-</esi:fragment> incl-page-end-";
        addResource("/fragment", includedPage);
        String result = render(page);
        assertEquals("before -content- after", result);
    }

    public void testIncludeFragmentReplaceNested() throws IOException, HttpErrorPage {
        String page =
                "before <esi:include src='$(PROVIDER{mock})/fragment'>"
                        + "<esi:replace fragment='test'>replacement</esi:replace" + "></esi:include> after";
        String includedPage =
                "-incl-page-start <esi:fragment name='test'>-" + "<esi:fragment name='test'>content</esi:fragment>"
                        + "-</esi:fragment> incl-page-end-";
        addResource("/fragment", includedPage);
        String result = render(page);
        assertEquals("before -incl-page-start replacement incl-page-end- after", result);
    }

    public void testIncludeFragmentReplaceFragment() throws IOException, HttpErrorPage {
        String page =
                "before <esi:include src='$(PROVIDER{mock})/fragment' fragment='toInclude'>"
                        + "<esi:replace fragment='toReplace'>replacement</esi:replace>" + "</esi:include> after";
        String includedPage =
                "-incl-page-start <esi:fragment name='toInclude'>begin include "
                        + "<esi:fragment name='toReplace'>content to replace</esi:fragment>"
                        + " end include</esi:fragment> incl-page-end-";
        addResource("/fragment", includedPage);
        String result = render(page);
        assertEquals("before begin include replacement end include after", result);
    }

    public void testIncludeProviderWithCharactersBeforeProvider() throws IOException, HttpErrorPage {

        String page = "<esi:include src=\"somechars$(PROVIDER{mock})/test\" />";
        addResource("/test", "code fragment");
        String result = render(page);
        String expected = "code fragment";
        assertEquals(expected, result);

        page = "<esi:include src=\"somechars$PROVIDER({mock})/test\" />";
        result = render(page);
        expected = "code fragment";
        assertEquals(expected, result);
    }
}
