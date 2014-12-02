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

package org.esigate.xml;

import junit.framework.TestCase;

import org.apache.commons.io.output.StringBuilderWriter;

public class XpathRendererTest extends TestCase {

    /**
     * Tests xpath expression evaluation for an html document.
     * 
     * @throws Exception
     */
    public void testXpathHtml() throws Exception {
        String src = "<html><title>The header</title><body>The body<br></body></html>";
        StringBuilderWriter out = new StringBuilderWriter();
        XpathRenderer tested = new XpathRenderer("/html:html/html:body");
        tested.render(null, src, out);
        assertEquals("<body>The body<br /></body>", out.toString());
    }

    /**
     * Tests xpath expression evaluation for an xhtml document.
     * 
     * @throws Exception
     */
    public void testXpathXhtml() throws Exception {
        String src =
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                        + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
                        + "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
                        + "<head><title>The header</title></head><body>The body<br/><b></b></body></html>";
        StringBuilderWriter out = new StringBuilderWriter();
        XpathRenderer tested = new XpathRenderer("//html:body");
        tested.render(null, src, out);
        assertEquals("<body>The body<br /><b></b></body>", out.toString());
    }

    /**
     * Tests xpath expression targetting an attribute.
     * 
     * @throws Exception
     */
    public void testXpathAttribute() throws Exception {
        String src =
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                        + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
                        + "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + "<head><title>The header</title></head>"
                        + "<body class=\"test\">The body<br/><b></b></body>" + "</html>";
        StringBuilderWriter out = new StringBuilderWriter();
        XpathRenderer tested = new XpathRenderer("//html:body/@class");
        tested.render(null, src, out);
        assertEquals("test", out.toString());
    }
}
