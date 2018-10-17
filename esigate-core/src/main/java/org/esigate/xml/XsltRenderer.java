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
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.XMLConstants;

import nu.validator.htmlparser.common.DoctypeExpectation;
import nu.validator.htmlparser.dom.Dom2Sax;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.http.HttpResponseUtils;
import org.esigate.impl.DriverRequest;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Applies an XSLT transformation to the retrieved data.
 * 
 * If no namespace is specified in the document to transform, it is asumed as:<br />
 * xmlns="http://www.w3.org/1999/xhtml"
 * 
 * @author Stanislav Bernatskyi
 * @author Francois-Xavier Bonnet
 */
public class XsltRenderer implements Renderer {
    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
    private Transformer transformer;

    /**
     * @param template
     *            The path to the xsl template, relative to the context root
     * @param driver
     *            driver
     * @param originalRequest
     * @throws IOException
     *             If an error occurs while writing to the output
     * @throws HttpErrorPage
     */
    public XsltRenderer(String template, Driver driver, DriverRequest originalRequest) throws IOException,
            HttpErrorPage {
        StringBuilder templateStringBuilder = new StringBuilder();
        CloseableHttpResponse response = driver.render(template, originalRequest.getOriginalRequest());
        templateStringBuilder.append(HttpResponseUtils.toString(response));
        transformer = createTransformer(IOUtils.toInputStream(templateStringBuilder));
    }

    /**
     * @param xsl
     *            The xsl template to apply as a String
     * @throws IOException
     *             If an error occurs while writing to the output
     */
    public XsltRenderer(String xsl) throws IOException {
        InputStream templateStream = IOUtils.toInputStream(xsl);
        transformer = createTransformer(templateStream);
    }

    private static Transformer createTransformer(InputStream templateStream) throws IOException {
        try {
            // Ensure XSLT cannot use advanced extensions during processing.
            TRANSFORMER_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            return TRANSFORMER_FACTORY.newTransformer(new StreamSource(templateStream));
        } catch (TransformerConfigurationException e) {
            throw new ProcessingFailedException("Failed to create XSLT template", e);
        } finally {
            templateStream.close();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void render(DriverRequest httpRequest, String src, Writer out) throws IOException {
        try {
            HtmlDocumentBuilder htmlDocumentBuilder = new HtmlDocumentBuilder();
            htmlDocumentBuilder.setDoctypeExpectation(DoctypeExpectation.NO_DOCTYPE_ERRORS);
            Document document = htmlDocumentBuilder.parse(new InputSource(new StringReader(src)));
            Source source = new DOMSource(document);
            DOMResult result = new DOMResult();
            transformer.transform(source, result);
            XhtmlSerializer serializer = new XhtmlSerializer(out);
            Dom2Sax dom2Sax = new Dom2Sax(serializer, serializer);
            dom2Sax.parse(result.getNode());
        } catch (TransformerException e) {
            throw new ProcessingFailedException("Failed to transform source", e);
        } catch (SAXException e) {
            throw new ProcessingFailedException("Failed serialize transformation result", e);
        }
    }
}
