/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.test.dom.transforms.implementations;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.implementations.TransformBase64Decode;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Unit test for {@link org.apache.xml.security.transforms.implementations.TransformBase64Decode}
 *
 * @author Christian Geuer-Pollmann
 */
public class TransformBase64DecodeTest extends org.junit.Assert {

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(TransformBase64DecodeTest.class);

    static {
        org.apache.xml.security.Init.init();
    }
    
    @org.junit.Test
    public void test1() throws Exception {
        // base64 encoded
        String s1 =
            "VGhlIFVSSSBvZiB0aGUgdHJhbnNmb3JtIGlzIGh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1s\n"
            + "ZHNpZyNiYXNlNjQ=";

        Document doc = TransformBase64DecodeTest.createDocument();
        Transforms t = new Transforms(doc);
        doc.appendChild(t.getElement());
        t.addTransform(TransformBase64Decode.implementedTransformURI);

        XMLSignatureInput in =
            new XMLSignatureInput(new ByteArrayInputStream(s1.getBytes()));
        XMLSignatureInput out = t.performTransforms(in);
        String result = new String(out.getBytes());

        assertTrue(
            result.equals("The URI of the transform is http://www.w3.org/2000/09/xmldsig#base64")
        );
    }

    @org.junit.Test
    public void test2() throws Exception {
        // base64 encoded twice
        String s2 =
            "VkdobElGVlNTU0J2WmlCMGFHVWdkSEpoYm5ObWIzSnRJR2x6SUdoMGRIQTZMeTkzZDNjdWR6TXVi\n"
            + "M0puTHpJd01EQXZNRGt2ZUcxcwpaSE5wWnlOaVlYTmxOalE9";
        Document doc = TransformBase64DecodeTest.createDocument();
        Transforms t = new Transforms(doc);
        doc.appendChild(t.getElement());

        t.addTransform(TransformBase64Decode.implementedTransformURI);

        XMLSignatureInput in =
            new XMLSignatureInput(new ByteArrayInputStream(s2.getBytes()));
        XMLSignatureInput out = t.performTransforms(t.performTransforms(in));
        String result = new String(out.getBytes());

        assertTrue(
            result.equals("The URI of the transform is http://www.w3.org/2000/09/xmldsig#base64")
        );
    }

    @org.junit.Test
    public void test3() throws Exception {
        //J-
        String input = ""
            + "<Object xmlns:signature='http://www.w3.org/2000/09/xmldsig#'>\n"
            + "<signature:Base64>\n"
            + "VGhlIFVSSSBvZiB0aGU   gdHJhbn<RealText>Nmb  3JtIGlzIG<test/>h0dHA6</RealText>Ly93d3cudzMub3JnLzIwMDAvMDkveG1s\n"
            + "ZHNpZyNiYXNlNjQ=\n"
            + "</signature:Base64>\n"
            + "</Object>\n"
            ;
        //J+
        DocumentBuilder db = XMLUtils.createDocumentBuilder(false);

        db.setErrorHandler(new org.apache.xml.security.utils.IgnoreAllErrorHandler());

        Document doc = db.parse(new ByteArrayInputStream(input.getBytes()));
        //XMLUtils.circumventBug2650(doc);
        
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//ds:Base64";
        Node base64Node = 
            (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);

        XMLSignatureInput xmlinput = new XMLSignatureInput(base64Node);

        Document doc2 = TransformBase64DecodeTest.createDocument();
        Transforms t = new Transforms(doc2);
        doc2.appendChild(t.getElement());
        t.addTransform(Transforms.TRANSFORM_BASE64_DECODE);

        XMLSignatureInput out = t.performTransforms(xmlinput);
        String result = new String(out.getBytes());

        assertTrue(
            "\"" + result + "\"", 
            result.equals("The URI of the transform is http://www.w3.org/2000/09/xmldsig#base64")
        );
    }

    private static Document createDocument() throws ParserConfigurationException {
        DocumentBuilder db = XMLUtils.createDocumentBuilder(false);
        Document doc = db.newDocument();

        if (doc == null) {
            throw new RuntimeException("Could not create a Document");
        } else {
            log.debug("I could create the Document");
        }
        return doc;
    }
    
}
