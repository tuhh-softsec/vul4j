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
package org.apache.xml.security.test.stax.signature;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.crypto.SecretKey;
import javax.xml.stream.XMLInputFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.junit.Assert;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class AbstractSignatureCreationTest extends org.junit.Assert {

    protected static String BASEDIR;

    protected XMLInputFactory xmlInputFactory;

    @Before
    public void setUp() throws Exception {

        BASEDIR = System.getProperty("basedir");
        if (BASEDIR == null) {
            BASEDIR = new File(".").getCanonicalPath();
        }

        org.apache.xml.security.Init.init();

        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());
    }

    /**
     * Verify the document using DOM
     */
    protected void verifyUsingDOM(
            Document document,
            X509Certificate cert,
            List<SecurePart> secureParts
    ) throws Exception {
        verifyUsingDOM(document, cert, secureParts, null);
    }
    
    /**
     * Verify the document using DOM
     */
    protected void verifyUsingDOM(
            Document document,
            X509Certificate cert,
            List<SecurePart> secureParts,
            ResourceResolverSpi resourceResolverSpi
    ) throws Exception {
        verifyUsingDOM(document, cert, secureParts, resourceResolverSpi, true);
    }

    /**
     * Verify the document using DOM
     */
    protected void verifyUsingDOM(
            Document document,
            X509Certificate cert,
            List<SecurePart> secureParts,
            ResourceResolverSpi resourceResolverSpi,
            boolean keyInfoRequired
    ) throws Exception {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//dsig:Signature[1]";
        Element sigElement =
                (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
        Assert.assertNotNull(sigElement);

        for (SecurePart securePart : secureParts) {
            if (securePart.getName() == null) {
                continue;
            }
            expression = "//*[local-name()='" + securePart.getName().getLocalPart() + "']";
            Element signedElement =
                    (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
            Assert.assertNotNull(signedElement);
            signedElement.setIdAttributeNS(null, "Id", true);
        }

        XMLSignature signature = new XMLSignature(sigElement, "");
        if (resourceResolverSpi != null) {
            signature.addResourceResolver(resourceResolverSpi);
        }
        if (keyInfoRequired) {
            KeyInfo ki = signature.getKeyInfo();
            Assert.assertNotNull(ki);
        }

        Assert.assertTrue(signature.checkSignatureValue(cert));
    }

    /**
     * Verify the document using DOM
     */
    protected void verifyUsingDOM(
            Document document,
            SecretKey secretKey,
            List<SecurePart> secureParts
    ) throws Exception {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//dsig:Signature[1]";
        Element sigElement =
                (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
        Assert.assertNotNull(sigElement);

        for (SecurePart securePart : secureParts) {
            expression = "//*[local-name()='" + securePart.getName().getLocalPart() + "']";
            Element signedElement =
                    (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
            Assert.assertNotNull(signedElement);
            signedElement.setIdAttributeNS(null, "Id", true);
        }

        XMLSignature signature = new XMLSignature(sigElement, "");
        Assert.assertTrue(signature.checkSignatureValue(secretKey));
    }
}
