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
package org.apache.xml.security.test.dom.c14n.implementations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315Excl;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315WithComments;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * @author Christian Geuer-Pollmann
 */
public class Canonicalizer20010315ExclusiveTest extends org.junit.Assert {

    static {
        org.apache.xml.security.Init.init();
    }

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(
            Canonicalizer20010315ExclusiveTest.class
        );

    /** Field db */
    DocumentBuilder db;

    public Canonicalizer20010315ExclusiveTest() throws ParserConfigurationException {
        this.db = XMLUtils.createDocumentBuilder(false);
    }

    /**
     * Method testA
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws XMLSecurityException
     * @throws XMLSignatureException
     * @throws org.apache.xml.security.keys.keyresolver.KeyResolverException
     */
    @org.junit.Test
    public void testA()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException,
        XMLSignatureException, XMLSecurityException,
        org.apache.xml.security.keys.keyresolver.KeyResolverException {

        File fileIn = new File(getAbsolutePath(
            "src/test/resources/ie/baltimore/merlin-examples/ec-merlin-iaikTests-two/signature.xml"));

        // File fileIn = new File("signature.xml");
        assertTrue("file exists", fileIn.exists());

        Document doc = this.db.parse(fileIn);
        Element signatureElement =
            (Element) doc.getElementsByTagNameNS(
                Constants.SignatureSpecNS, Constants._TAG_SIGNATURE).item(0);
        XMLSignature xmlSignature = new XMLSignature(signatureElement,
                                                     fileIn.toURI().toURL().toString());
        boolean verify =
            xmlSignature.checkSignatureValue(xmlSignature.getKeyInfo().getPublicKey());
        int length = xmlSignature.getSignedInfo().getLength();
        int numberOfPositiveReferences = 0;

        for (int i = 0; i < length; i++) {
            boolean singleResult =
                xmlSignature.getSignedInfo().getVerificationResult(i);

            if (singleResult) {
                numberOfPositiveReferences++;
            }
        }

        assertTrue("Verification failed; only " + numberOfPositiveReferences
                   + "/" + length + " matched", verify);
    }

    /**
     * Method test221
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws XMLSecurityException
     * @throws XMLSignatureException
     */
    @org.junit.Test
    public void test221()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException,
        XMLSignatureException, XMLSecurityException {

        Document doc =
            this.db.parse(
                getAbsolutePath("src/test/resources/org/apache/xml/security/c14n/inExcl/example2_2_1.xml")
            );
        Node root = doc.getElementsByTagNameNS("http://example.net", "elem2").item(0);
        Canonicalizer20010315 c = new Canonicalizer20010315WithComments();
        byte[] reference = JavaUtils.getBytesFromFile(getAbsolutePath(
            "src/test/resources/org/apache/xml/security/c14n/inExcl/example2_2_1_c14nized.xml"));
        byte[] result = c.engineCanonicalizeSubTree(root);
        boolean equals = java.security.MessageDigest.isEqual(reference, result);

        assertTrue(equals);
    }

    /**
     * Method test222
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws XMLSecurityException
     * @throws XMLSignatureException
     */
    @org.junit.Test
    public void test222()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException,
        XMLSignatureException, XMLSecurityException {
        Document doc =
            this.db.parse(getAbsolutePath(
                "src/test/resources/org/apache/xml/security/c14n/inExcl/example2_2_2.xml")
            );
        Node root = doc.getElementsByTagNameNS("http://example.net", "elem2").item(0);
        Canonicalizer20010315 c = new Canonicalizer20010315WithComments();
        byte[] reference = JavaUtils.getBytesFromFile(getAbsolutePath(
            "src/test/resources/org/apache/xml/security/c14n/inExcl/example2_2_2_c14nized.xml"));
        byte[] result = c.engineCanonicalizeSubTree(root);
        boolean equals = java.security.MessageDigest.isEqual(reference, result);

        assertTrue(equals);
    }

    /**
     * Method test221excl
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws XMLSecurityException
     * @throws XMLSignatureException
     */
    @org.junit.Test
    public void test221excl()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException,
        XMLSignatureException, XMLSecurityException {
        Document doc =
            this.db.parse(getAbsolutePath(
                "src/test/resources/org/apache/xml/security/c14n/inExcl/example2_2_1.xml"));
        Node root = doc.getElementsByTagNameNS("http://example.net", "elem2").item(0);
        Canonicalizer20010315Excl c = new Canonicalizer20010315ExclWithComments();
        byte[] reference = JavaUtils.getBytesFromFile(getAbsolutePath(
            "src/test/resources/org/apache/xml/security/c14n/inExcl/example2_2_c14nized_exclusive.xml"));
        byte[] result = c.engineCanonicalizeSubTree(root);
        boolean equals = java.security.MessageDigest.isEqual(reference, result);

        assertTrue(equals);
    }

    /**
     * Method test222excl
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws XMLSecurityException
     * @throws XMLSignatureException
     */
    @org.junit.Test
    public void test222excl()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException,
        XMLSignatureException, XMLSecurityException {
        Document doc =
            this.db.parse(getAbsolutePath(
                "src/test/resources/org/apache/xml/security/c14n/inExcl/example2_2_2.xml"));
        Node root = doc.getElementsByTagNameNS("http://example.net", "elem2").item(0);
        Canonicalizer20010315Excl c = new Canonicalizer20010315ExclWithComments();
        byte[] reference = JavaUtils.getBytesFromFile(getAbsolutePath(
            "src/test/resources/org/apache/xml/security/c14n/inExcl/example2_2_c14nized_exclusive.xml") );
        byte[] result = c.engineCanonicalizeSubTree(root);
        boolean equals = java.security.MessageDigest.isEqual(reference, result);

        assertTrue(equals);
    }

    /**
     * Method test223excl
     *
     * Provided by Gabriel McGoldrick - see e-mail of 21/11/03
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws XMLSecurityException
     * @throws XMLSignatureException
     * @throws XPathExpressionException 
     */
    @org.junit.Test
    public void test223excl()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException,
        XMLSignatureException, XMLSecurityException, XPathExpressionException {
        Document doc =
            this.db.parse(getAbsolutePath(
                "src/test/resources/org/apache/xml/security/c14n/inExcl/example2_2_3.xml"));

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "(//. | //@* | //namespace::*)[ancestor-or-self::p]";
        NodeList nodes = 
            (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

        Canonicalizer20010315Excl c = new Canonicalizer20010315ExclWithComments();
        byte[] reference = JavaUtils.getBytesFromFile(
            getAbsolutePath(
                "src/test/resources/org/apache/xml/security/c14n/inExcl/example2_2_3_c14nized_exclusive.xml")
            );
        byte[] result = c.engineCanonicalizeXPathNodeSet(nodes);
        assertEquals(new String(reference), new String(result));
    }

    /**
     * Tests node-set as input. See bug 37708. 
     * Provided by Pete Hendry.
     */
    @org.junit.Test
    public void testNodeSet() throws Exception {
        final String XML =
            "<env:Envelope"
            + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:ns0=\"http://xmlsoap.org/Ping\""
            + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
            + "<env:Body wsu:Id=\"body\">"
            + "<ns0:Ping xsi:type=\"ns0:ping\">"
            + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
            + "</ns0:Ping>"
            + "</env:Body>"
            + "</env:Envelope>";

        final String c14nXML =
            "<env:Body"
            + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
            + " xmlns:ns0=\"http://xmlsoap.org/Ping\""
            + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " wsu:Id=\"body\">"
            + "<ns0:Ping xsi:type=\"ns0:ping\">"
            + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
            + "</ns0:Ping>"
            + "</env:Body>";

        Document doc = this.db.parse(new InputSource(new StringReader(XML)));
        Canonicalizer20010315ExclOmitComments c14n = 
            new Canonicalizer20010315ExclOmitComments();
        Set<Node> nodeSet = new HashSet<Node>();
        XMLUtils.getSet(doc.getDocumentElement().getFirstChild(), nodeSet, null, false);
        XMLSignatureInput input = new XMLSignatureInput(nodeSet);
        byte[] bytes = c14n.engineCanonicalize(input, "env ns0 xsi wsu");
        assertEquals(c14nXML, new String(bytes));
    }
    
    /**
     * Method test24excl - a testcase for SANTUARIO-263 
     * "Canonicalizer can't handle dynamical created DOM correctly"
     * https://issues.apache.org/jira/browse/SANTUARIO-263
     */
    @org.junit.Test
    public void test24excl() throws Exception {
        Document doc =
            this.db.parse(
                getAbsolutePath(
                    "src/test/resources/org/apache/xml/security/c14n/inExcl/example2_4.xml"));
        Node root = 
            doc.getElementsByTagNameNS("http://example.net", "elem2").item(0);
        Canonicalizer20010315Excl c = new Canonicalizer20010315ExclWithComments();
        byte[] reference = 
            JavaUtils.getBytesFromFile(getAbsolutePath(
                "src/test/resources/org/apache/xml/security/c14n/inExcl/example2_4_c14nized.xml"));
        byte[] result = c.engineCanonicalizeSubTree(root);
        boolean equals = java.security.MessageDigest.isEqual(reference, result);

        assertTrue(equals);
    }

    /**
     * Method test24Aexcl - a testcase for SANTUARIO-263 
     * "Canonicalizer can't handle dynamical created DOM correctly"
     * https://issues.apache.org/jira/browse/SANTUARIO-263
     */
    @org.junit.Test
    public void test24Aexcl() throws Exception {
        Document doc = XMLUtils.createDocumentBuilder(false).newDocument();
        Element local = doc.createElementNS("foo:bar", "dsig:local");
        Element test = doc.createElementNS("http://example.net", "etsi:test");
        Element elem2 = doc.createElementNS("http://example.net", "etsi:elem2");
        Element stuff = doc.createElementNS("foo:bar", "dsig:stuff");
        elem2.appendChild(stuff);
        test.appendChild(elem2);
        local.appendChild(test);
        doc.appendChild(local);

        Node root = doc.getElementsByTagNameNS("http://example.net", "elem2").item(0);
        Canonicalizer20010315Excl c = new Canonicalizer20010315ExclWithComments();
        byte[] reference = 
            JavaUtils.getBytesFromFile(getAbsolutePath(
                "src/test/resources/org/apache/xml/security/c14n/inExcl/example2_4_c14nized.xml"));
        byte[] result = c.engineCanonicalizeSubTree(root);
        boolean equals = java.security.MessageDigest.isEqual(reference, result);

        assertTrue(equals);
    }

    /**
     * Test default namespace behavior if its in the InclusiveNamespace prefix list.
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testDefaultNSInInclusiveNamespacePrefixList1() throws Exception {
        final String XML =
                "<env:Envelope"
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns:ns0=\"http://xmlsoap.org/Ping\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
                        + "<env:Body wsu:Id=\"body\">"
                        + "<ns0:Ping xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>"
                        + "</env:Envelope>";

        final String c14nXML =
                "<env:Body"
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " wsu:Id=\"body\">"
                        + "<ns0:Ping xmlns:ns0=\"http://xmlsoap.org/Ping\" xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>";

        Document doc = this.db.parse(new InputSource(new StringReader(XML)));
        {
            Canonicalizer20010315ExclOmitComments c14n =
                    new Canonicalizer20010315ExclOmitComments();
            XMLSignatureInput input = new XMLSignatureInput(doc.getDocumentElement().getFirstChild());
            byte[] bytes = c14n.engineCanonicalize(input, "#default xsi");
            assertEquals(c14nXML, new String(bytes));
        }
        {
            //exactly the same outcome is expected if #default is not set:
            Canonicalizer20010315ExclOmitComments c14n =
                    new Canonicalizer20010315ExclOmitComments();
            XMLSignatureInput input = new XMLSignatureInput(doc.getDocumentElement().getFirstChild());
            byte[] bytes = c14n.engineCanonicalize(input, "xsi");
            assertEquals(c14nXML, new String(bytes));
        }
    }

    /**
     * Test default namespace behavior if its in the InclusiveNamespace prefix list.
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testDefaultNSInInclusiveNamespacePrefixList2() throws Exception {
        final String XML =
                "<env:Envelope"
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns=\"http://example.com\""
                        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns:ns0=\"http://xmlsoap.org/Ping\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
                        + "<env:Body wsu:Id=\"body\">"
                        + "<ns0:Ping xsi:type=\"ns0:ping\">"
                        + "<ns0:text xmlns=\"\" xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>"
                        + "</env:Envelope>";

        final String c14nXML1 =
                "<env:Body"
                        + " xmlns=\"http://example.com\""
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " wsu:Id=\"body\">"
                        + "<ns0:Ping xmlns:ns0=\"http://xmlsoap.org/Ping\" xsi:type=\"ns0:ping\">"
                        + "<ns0:text xmlns=\"\" xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>";

        final String c14nXML2 =
                "<env:Body"
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " wsu:Id=\"body\">"
                        + "<ns0:Ping xmlns:ns0=\"http://xmlsoap.org/Ping\" xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>";

        Document doc = this.db.parse(new InputSource(new StringReader(XML)));
        {
            Canonicalizer20010315ExclOmitComments c14n =
                    new Canonicalizer20010315ExclOmitComments();
            XMLSignatureInput input = new XMLSignatureInput(doc.getDocumentElement().getFirstChild());
            byte[] bytes = c14n.engineCanonicalize(input, "#default xsi");
            assertEquals(c14nXML1, new String(bytes));
        }
        {
            Canonicalizer20010315ExclOmitComments c14n =
                    new Canonicalizer20010315ExclOmitComments();
            XMLSignatureInput input = new XMLSignatureInput(doc.getDocumentElement().getFirstChild());
            byte[] bytes = c14n.engineCanonicalize(input, "xsi");
            assertEquals(c14nXML2, new String(bytes));
        }
    }

    /**
     * Test default namespace behavior if its in the InclusiveNamespace prefix list.
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testDefaultNSInInclusiveNamespacePrefixList3() throws Exception {
        final String XML =
                "<env:Envelope"
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns=\"\""
                        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns:ns0=\"http://xmlsoap.org/Ping\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
                        + "<env:Body wsu:Id=\"body\">"
                        + "<ns0:Ping xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>"
                        + "</env:Envelope>";

        final String c14nXML =
                "<env:Body"
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " wsu:Id=\"body\">"
                        + "<ns0:Ping xmlns:ns0=\"http://xmlsoap.org/Ping\" xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>";

        Document doc = this.db.parse(new InputSource(new StringReader(XML)));
        {
            Canonicalizer20010315ExclOmitComments c14n =
                    new Canonicalizer20010315ExclOmitComments();
            XMLSignatureInput input = new XMLSignatureInput(doc.getDocumentElement().getFirstChild());
            byte[] bytes = c14n.engineCanonicalize(input, "#default xsi");
            assertEquals(c14nXML, new String(bytes));
        }
        {
            //exactly the same outcome is expected if #default is not set:
            Canonicalizer20010315ExclOmitComments c14n =
                    new Canonicalizer20010315ExclOmitComments();
            XMLSignatureInput input = new XMLSignatureInput(doc.getDocumentElement().getFirstChild());
            byte[] bytes = c14n.engineCanonicalize(input, "xsi");
            assertEquals(c14nXML, new String(bytes));
        }
    }

    /**
     * Test default namespace behavior if its in the InclusiveNamespace prefix list.
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testDefaultNSInInclusiveNamespacePrefixList4() throws Exception {
        final String XML =
                "<env:Envelope"
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns:ns0=\"http://xmlsoap.org/Ping\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
                        + "<env:Body wsu:Id=\"body\">"
                        + "<ns0:Ping xsi:type=\"ns0:ping\">"
                        + "<ns0:text xmlns=\"\" xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>"
                        + "</env:Envelope>";

        final String c14nXML =
                "<env:Body"
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " wsu:Id=\"body\">"
                        + "<ns0:Ping xmlns:ns0=\"http://xmlsoap.org/Ping\" xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>";

        Document doc = this.db.parse(new InputSource(new StringReader(XML)));
        {
            Canonicalizer20010315ExclOmitComments c14n =
                    new Canonicalizer20010315ExclOmitComments();
            XMLSignatureInput input = new XMLSignatureInput(doc.getDocumentElement().getFirstChild());
            byte[] bytes = c14n.engineCanonicalize(input, "#default xsi");
            assertEquals(c14nXML, new String(bytes));
        }
        {
            //exactly the same outcome is expected if #default is not set:
            Canonicalizer20010315ExclOmitComments c14n =
                    new Canonicalizer20010315ExclOmitComments();
            XMLSignatureInput input = new XMLSignatureInput(doc.getDocumentElement().getFirstChild());
            byte[] bytes = c14n.engineCanonicalize(input, "xsi");
            assertEquals(c14nXML, new String(bytes));
        }
    }

    /**
     * Test default namespace behavior if its in the InclusiveNamespace prefix list.
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testPropagateDefaultNs1() throws Exception {
        final String XML =
                "<env:Envelope"
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns:ns0=\"http://xmlsoap.org/Ping\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
                        + "<env:Body wsu:Id=\"body\">"
                        + "<ns0:Ping xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>"
                        + "</env:Envelope>";

        final String c14nXML =
                "<env:Body"
                        + " xmlns=\"\""
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""
                        + " wsu:Id=\"body\">"
                        + "<ns0:Ping xmlns:ns0=\"http://xmlsoap.org/Ping\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>";

        Document doc = this.db.parse(new InputSource(new StringReader(XML)));
        Canonicalizer20010315ExclOmitComments c14n =
                new Canonicalizer20010315ExclOmitComments();
        byte[] bytes = c14n.engineCanonicalizeSubTree(doc.getDocumentElement().getFirstChild(), "#default", true);
        assertEquals(c14nXML, new String(bytes));
    }

    @org.junit.Test
    public void testPropagateDefaultNs2() throws Exception {
        final String XML =
                "<env:Envelope"
                        + " xmlns=\"http://example.com\""
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns:ns0=\"http://xmlsoap.org/Ping\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
                        + "<env:Body wsu:Id=\"body\">"
                        + "<ns0:Ping xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>"
                        + "</env:Envelope>";

        final String c14nXML =
                "<env:Body"
                        + " xmlns=\"http://example.com\""
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""
                        + " wsu:Id=\"body\">"
                        + "<ns0:Ping xmlns:ns0=\"http://xmlsoap.org/Ping\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>";

        Document doc = this.db.parse(new InputSource(new StringReader(XML)));
        Canonicalizer20010315ExclOmitComments c14n =
                new Canonicalizer20010315ExclOmitComments();
        byte[] bytes = c14n.engineCanonicalizeSubTree(doc.getDocumentElement().getFirstChild(), "#default", true);
        assertEquals(c14nXML, new String(bytes));
    }

    @org.junit.Test
    public void testPropagateDefaultNs3() throws Exception {
        final String XML =
                "<Envelope"
                        + " xmlns=\"http://example.com\""
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns:ns0=\"http://xmlsoap.org/Ping\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
                        + "<env:Body wsu:Id=\"body\">"
                        + "<ns0:Ping xsi:type=\"ns0:ping\">"
                        + "<ns0:text xmlns=\"\" xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>"
                        + "</Envelope>";

        final String c14nXML =
                "<env:Body"
                        + " xmlns=\"http://example.com\""
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""
                        + " wsu:Id=\"body\">"
                        + "<ns0:Ping xmlns:ns0=\"http://xmlsoap.org/Ping\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns0:ping\">"
                        + "<ns0:text xmlns=\"\" xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>";

        Document doc = this.db.parse(new InputSource(new StringReader(XML)));
        Canonicalizer20010315ExclOmitComments c14n =
                new Canonicalizer20010315ExclOmitComments();
        byte[] bytes = c14n.engineCanonicalizeSubTree(doc.getDocumentElement().getFirstChild(), "#default", true);
        assertEquals(c14nXML, new String(bytes));
    }

    @org.junit.Test
    public void testPropagateDefaultNs4() throws Exception {
        final String XML =
                "<Envelope"
                        + " xmlns=\"\""
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns:ns0=\"http://xmlsoap.org/Ping\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
                        + "<env:Body wsu:Id=\"body\">"
                        + "<ns0:Ping xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>"
                        + "</Envelope>";

        final String c14nXML =
                "<env:Body"
                        + " xmlns=\"\""
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""
                        + " wsu:Id=\"body\">"
                        + "<ns0:Ping xmlns:ns0=\"http://xmlsoap.org/Ping\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>";

        Document doc = this.db.parse(new InputSource(new StringReader(XML)));
        Canonicalizer20010315ExclOmitComments c14n =
                new Canonicalizer20010315ExclOmitComments();
        byte[] bytes = c14n.engineCanonicalizeSubTree(doc.getDocumentElement().getFirstChild(), "#default", true);
        assertEquals(c14nXML, new String(bytes));
    }

    @org.junit.Test
    public void testPropagateDefaultNs5() throws Exception {
        final String XML =
                "<env:Envelope"
                        + " xmlns=\"http://example.com\""
                        + " xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""
                        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns:ns0=\"http://xmlsoap.org/Ping\""
                        + " xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
                        + "<env:Body xmlns=\"\" wsu:Id=\"body\">"
                        + "<ns0:Ping xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>"
                        + "</env:Body>"
                        + "</env:Envelope>";

        final String c14nXML =
                "<ns0:Ping xmlns=\"\" xmlns:ns0=\"http://xmlsoap.org/Ping\" " +
                        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns0:ping\">"
                        + "<ns0:text xsi:type=\"xsd:string\">hello</ns0:text>"
                        + "</ns0:Ping>";

        Document doc = this.db.parse(new InputSource(new StringReader(XML)));
        Canonicalizer20010315ExclOmitComments c14n =
                new Canonicalizer20010315ExclOmitComments();
        byte[] bytes = c14n.engineCanonicalizeSubTree(doc.getDocumentElement().getFirstChild().getFirstChild(), "#default", true);
        assertEquals(c14nXML, new String(bytes));
    }

    private String getAbsolutePath(String path) {
        String basedir = System.getProperty("basedir");
        if (basedir != null && !"".equals(basedir)) {
            path = basedir + "/" + path;
        }
        return path;
    }

}
