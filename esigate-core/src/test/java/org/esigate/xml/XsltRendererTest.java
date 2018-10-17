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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Test;

public class XsltRendererTest {

    /**
     * Tests xpath expression evaluation.
     * 
     * @throws IOException
     */
    @Test
    public void testXslt() throws IOException {
        String src = "<html><body>The body<br></body></html>";
        String result = renderSimpleStyleSheet(src);
        assertEquals("<body>The body<br /></body>", result);
    }

    @Test(expected = ProcessingFailedException.class)
    public void testSecureRendering() throws IOException {
        String src = "<html><body>The body<br></body></html>";
        renderExtensionStyleSheet(src);
    }

    private String renderSimpleStyleSheet(String src) throws IOException {
        String template = "<?xml version=\"1.0\"?>";
        template +=
                "<xsl:stylesheet version=\"1.0\" xmlns=\"http://www.w3.org/1999/xhtml\" "
                        + "xmlns:html=\"http://www.w3.org/1999/xhtml\" "
                        + "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">";
        template += "<xsl:output method=\"xml\" omit-xml-declaration=\"yes\"/>";
        template += "<xsl:template match=\"//html:body\">";
        template += "<xsl:copy-of select=\".\"/>";
        template += "</xsl:template>";
        template += "<xsl:template match=\"text()\"/>";
        template += "</xsl:stylesheet>";
        StringBuilderWriter out = new StringBuilderWriter();
        XsltRenderer tested = new XsltRenderer(template);
        tested.render(null, src, out);
        return out.toString();
    }

	private String renderExtensionStyleSheet(String src) throws IOException {
		String template = "<?xml version=\"1.0\"?>";
		template += "<xsl:stylesheet version=\"1.0\" xmlns=\"http://www.w3.org/1999/xhtml\" "
				+ "xmlns:html=\"http://www.w3.org/1999/xhtml\" "
				+ "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">";
		template += "<xsl:output method=\"xml\" omit-xml-declaration=\"yes\"/>";
		template += "<xsl:template match=\"/\"  xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:rt=\"http://xml.apache.org/xalan/java/java.lang.Runtime\">";

		template += "<xsl:variable name=\"rtObj\" select=\"rt:getRuntime()\"/>";
		template += "<xsl:variable name=\"process\" select=\"rt:totalMemory($rtObj)\"/>";
		template += "Process: <xsl:value-of select=\"$process\"/>\n";

		template += "</xsl:template>";

		template += "</xsl:stylesheet>";
		StringBuilderWriter out = new StringBuilderWriter();
		XsltRenderer tested = new XsltRenderer(template);
		tested.render(null, src, out);
		return out.toString();
	}

    /**
     * Tests parser does not throw an Exception for an unescaped '&' character.
     * 
     * @throws Exception
     */
    @Test
    public void testParserSupportsUnescapedAmpersandCharacter() throws Exception {
        String src =
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                        + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
                        + "<html lang=\"fr\" xml:lang=\"fr\" xmlns=\"http://www.w3.org/1999/xhtml\">"
                        + "<head><title>The header</title></head><body>&x=</body></html>";
        String result = renderSimpleStyleSheet(src);
        assertEquals("<body>&amp;x=</body>", result);
    }

    /**
     * Tests parser does not throw an Exception for a duplicated id.
     * 
     * @throws Exception
     */
    @Test
    public void testParserSupportsDuplicatedId() throws Exception {
        String src =
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                        + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
                        + "<html lang=\"fr\" xml:lang=\"fr\" xmlns=\"http://www.w3.org/1999/xhtml\">"
                        + "<head><title>The header</title></head><body>"
                        + "<span id=\"test\">a</span><span id=\"test\">b</span></body></html>";
        String result = renderSimpleStyleSheet(src);
        assertEquals("<body><span id=\"test\">a</span><span id=\"test\">b</span></body>", result);
    }

}
