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

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nu.validator.htmlparser.common.DoctypeExpectation;
import nu.validator.htmlparser.dom.Dom2Sax;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.Renderer;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML renderer.
 * <p>
 * Applies optional XPath evaluation and XSLT transformation to the retrieved data.
 * 
 * @author Stanislav Bernatskyi
 */
public class XpathRenderer implements Renderer {
    private static final HtmlNamespaceContext HTML_NAMESPACE_CONTEXT = new HtmlNamespaceContext();
    private static final XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();
    private final XPathExpression expr;

    public XpathRenderer(String xpath) {
        try {
            XPath xpathObj = X_PATH_FACTORY.newXPath();
            xpathObj.setNamespaceContext(HTML_NAMESPACE_CONTEXT);
            this.expr = xpathObj.compile(xpath);
        } catch (XPathExpressionException e) {
            throw new ProcessingFailedException("failed to compile XPath expression", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void render(HttpEntityEnclosingRequest httpRequest, String src, Writer out) throws IOException {
        try {
            HtmlDocumentBuilder htmlDocumentBuilder = new HtmlDocumentBuilder();
            htmlDocumentBuilder.setDoctypeExpectation(DoctypeExpectation.NO_DOCTYPE_ERRORS);
            Document document = htmlDocumentBuilder.parse(new InputSource(new StringReader(src)));
            NodeList matchingNodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            XhtmlSerializer serializer = new XhtmlSerializer(out);
            Dom2Sax dom2Sax = new Dom2Sax(serializer, serializer);
            for (int i = 0; i < matchingNodes.getLength(); i++) {
                dom2Sax.parse(matchingNodes.item(i));
            }
        } catch (XPathExpressionException e) {
            throw new ProcessingFailedException("Failed to evaluate XPath expression", e);
        } catch (SAXException e) {
            throw new ProcessingFailedException("Unable to parse source", e);
        }
    }
}
