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

package org.esigate.renderers;

import java.io.IOException;
import java.io.Writer;

import junit.framework.TestCase;

import org.apache.commons.io.output.StringBuilderWriter;
import org.esigate.impl.UrlRewriter;
import org.esigate.impl.UrlRewriterTest;

/**
 * Tests on ResourceFixupRenderer.
 * 
 * @author Nicolas Richeton
 * 
 */
public class ResourceFixupRendererTest extends TestCase {

    public void testRenderBlock1() throws IOException {
        String base = "http://myapp/context";
        String page = "templates/template1.html";
        final String input = "  <img src=\"images/logo.png\"/> <a href=\"/context/page/page1.htm\">link</a> "
                + "<img src=\"http://www.google.com/logo.com\"/>";
        final String expectedOutputRelative = "  <img src=\"/context/templates/images/logo.png\"/> "
                + "<a href=\"/context/page/page1.htm\">link</a> <img src=\"http://www.google.com/logo.com\"/>";
        final String expectedOutputAbsolute = "  <img src=\"http://myapp/context/templates/images/logo.png\"/> "
                + "<a href=\"http://myapp/context/page/page1.htm\">link</a> "
                + "<img src=\"http://www.google.com/logo.com\"/>";

        Writer out = new StringBuilderWriter();
        UrlRewriter urlRewriter = UrlRewriterTest.createUrlRewriter(base, "absolute");
        ResourceFixupRenderer tested = new ResourceFixupRenderer(base, page, urlRewriter);
        tested.render(null, input, out);
        assertEquals(expectedOutputAbsolute, out.toString());

        out = new StringBuilderWriter();
        urlRewriter = UrlRewriterTest.createUrlRewriter(base, "relative");
        tested = new ResourceFixupRenderer(base, page, urlRewriter);
        tested.render(null, input, out);
        assertEquals(expectedOutputRelative, out.toString());
    }
}
