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
package org.apache.xml.security.test.c14n.implementations;


import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.utils.Constants;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This is a test for Santuario-273:
 * 
 * https://issues.apache.org/jira/browse/SANTUARIO-273
 * "xml:base attribute not processed correctly in C14N11 canonicalization"
 */
public class Santuario273Test extends org.junit.Assert {
    static String input = ""
        + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\" xml:base=\"http://www.acme.com/resources/\">\n"
        + "  <SignedInfo xml:base=\"subresources/\"><!-- comment inside -->\n"
        + "    <CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\" />\n"
        + "    <SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" />\n"
        + "    <Reference URI=\"http://www.w3.org/TR/xml-stylesheet\">\n"
        + "      <DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" />\n"
        + "      <DigestValue>60NvZvtdTB+7UnlLp/H24p7h4bs=</DigestValue>\n"
        + "    </Reference>\n"
        + "  </SignedInfo>\n"
        + "  <SignatureValue>\n"
        + "    fKMmy9GYF2s8rLFrZdVugTOFuWx19ccX7jh5HqFd4vMOY7LWAj52ykjSdvtW3fNY\n"
        + "    PPYGC4MFL19oPSId5GEsMtFMpGXB3XaCtoKjMCHQsN3+kom8YnGf7Ge1JNRcGty5\n"
        + "    0UsoP6Asj47+QR7QECT64uoziha4WRDVyXjDrg24W+U=\n"
        + "  </SignatureValue>\n"
        + "  <KeyInfo>\n"
        + "    <KeyName>Lugh</KeyName>\n"
        + "  </KeyInfo>\n"
        + "</Signature>\n"
        ;
    
    static {
        Init.init();
    }

    @org.junit.Test
    public void testC14n11Base() throws Exception {
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        dfactory.setNamespaceAware(true);
        dfactory.setValidating(true);
        DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();

        documentBuilder.setErrorHandler(new org.apache.xml.security.utils.IgnoreAllErrorHandler());
        byte inputBytes[] = input.getBytes();
        Document doc =
            documentBuilder.parse(new ByteArrayInputStream(inputBytes));
        
        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS);
        Element nscontext = createDSctx(doc, "ds", Constants.SignatureSpecNS);

        Node signedInfo = XPathAPI.selectSingleNode(doc, "//ds:SignedInfo", nscontext);
        c14n.canonicalizeSubtree(signedInfo);

        NamedNodeMap attributes = signedInfo.getAttributes();
        boolean foundBase = false;
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            if ("base".equals(attribute.getLocalName())
                && "http://www.acme.com/resources/subresources/".equals(attribute.getNodeValue())) {
                foundBase = true;
                break;
            }
        }
        if (!foundBase) {
            fail("The base attribute was not found or was incorrect");
        }
    }

    /**
     * Method createDSctx
     *
     * @param doc
     * @param prefix
     * @param namespace
     * @return the element.
     */
    public static Element createDSctx(Document doc, String prefix, String namespace) {
        if ((prefix == null) || (prefix.trim().length() == 0)) {
            throw new IllegalArgumentException("You must supply a prefix");
        }

        Element ctx = doc.createElementNS(null, "namespaceContext");

        ctx.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:" + prefix.trim(), namespace);

        return ctx;
    }
}
