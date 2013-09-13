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
package org.apache.xml.security.test.stax.c14n;

import org.apache.xml.security.stax.impl.transformer.canonicalizer.Canonicalizer20010315_Excl;
import org.junit.Before;
import org.junit.Test;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.transformer.canonicalizer.Canonicalizer20010315_ExclOmitCommentsTransformer;
import org.apache.xml.security.stax.impl.transformer.canonicalizer.Canonicalizer20010315_ExclWithCommentsTransformer;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class Canonicalizer20010315ExclusiveTest extends org.junit.Assert {

    private XMLInputFactory xmlInputFactory;

    @Before
    public void setUp() throws Exception {
        this.xmlInputFactory = XMLInputFactory.newInstance();
        this.xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());
    }

    @Test
    public void test221excl() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Canonicalizer20010315_ExclWithCommentsTransformer c = new Canonicalizer20010315_ExclWithCommentsTransformer();
        c.setOutputStream(baos);
        XMLEventReader xmlSecEventReader = xmlInputFactory.createXMLEventReader(
                this.getClass().getClassLoader().getResourceAsStream(
                    "org/apache/xml/security/c14n/inExcl/example2_2_1.xml")
        );

        XMLSecEvent xmlSecEvent = null;
        while (xmlSecEventReader.hasNext()) {
            xmlSecEvent = (XMLSecEvent) xmlSecEventReader.nextEvent();
            if (xmlSecEvent.isStartElement() && xmlSecEvent.asStartElement().getName().equals(new QName("http://example.net", "elem2"))) {
                break;
            }
        }
        while (xmlSecEventReader.hasNext()) {

            c.transform(xmlSecEvent);

            if (xmlSecEvent.isEndElement() && xmlSecEvent.asEndElement().getName().equals(new QName("http://example.net", "elem2"))) {
                break;
            }
            xmlSecEvent = (XMLSecEvent) xmlSecEventReader.nextEvent();
        }

        byte[] reference = 
            getBytesFromResource(this.getClass().getClassLoader().getResource(
                "org/apache/xml/security/c14n/inExcl/example2_2_c14nized_exclusive.xml"));
        boolean equals = java.security.MessageDigest.isEqual(reference, baos.toByteArray());

        if (!equals) {
            System.out.println("Expected:\n" + new String(reference, "UTF-8"));
            System.out.println("");
            System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        }

        assertTrue(equals);
    }

    @Test
    public void test222excl() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Canonicalizer20010315_ExclWithCommentsTransformer c = new Canonicalizer20010315_ExclWithCommentsTransformer();
        c.setOutputStream(baos);

        canonicalize(c,
                this.getClass().getClassLoader().getResourceAsStream(
                    "org/apache/xml/security/c14n/inExcl/example2_2_2.xml"),
                new QName("http://example.net", "elem2")
        );

        byte[] reference = 
            getBytesFromResource(this.getClass().getClassLoader().getResource(
                "org/apache/xml/security/c14n/inExcl/example2_2_c14nized_exclusive.xml"));
        boolean equals = java.security.MessageDigest.isEqual(reference, baos.toByteArray());

        if (!equals) {
            System.out.println("Expected:\n" + new String(reference, "UTF-8"));
            System.out.println("");
            System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        }

        assertTrue(equals);
    }

    @Test
    public void test24excl() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Canonicalizer20010315_ExclWithCommentsTransformer c = new Canonicalizer20010315_ExclWithCommentsTransformer();
        c.setOutputStream(baos);

        canonicalize(c,
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/apache/xml/security/c14n/inExcl/example2_4.xml"),
                new QName("http://example.net", "elem2")
        );

        byte[] reference = 
            getBytesFromResource(this.getClass().getClassLoader().getResource(
                "org/apache/xml/security/c14n/inExcl/example2_4_c14nized.xml"));
        boolean equals = java.security.MessageDigest.isEqual(reference, baos.toByteArray());

        if (!equals) {
            System.out.println("Expected:\n" + new String(reference, "UTF-8"));
            System.out.println("");
            System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        }

        assertTrue(equals);
    }

    @Test
    public void testComplexDocexcl() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Canonicalizer20010315_ExclWithCommentsTransformer c = new Canonicalizer20010315_ExclWithCommentsTransformer();
        c.setOutputStream(baos);

        canonicalize(c,
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/apache/xml/security/c14n/inExcl/plain-soap-1.1.xml"),
                new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body", "env")
        );

        byte[] reference = 
            getBytesFromResource(this.getClass().getClassLoader().getResource(
                "org/apache/xml/security/c14n/inExcl/plain-soap-c14nized.xml"));
        boolean equals = java.security.MessageDigest.isEqual(reference, baos.toByteArray());

        if (!equals) {
            System.out.println("Expected:\n" + new String(reference, "UTF-8"));
            System.out.println("");
            System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        }
        assertTrue(equals);
    }

    @Test
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<String> inclusiveNamespaces = new ArrayList<String>();
        inclusiveNamespaces.add("env");
        inclusiveNamespaces.add("ns0");
        inclusiveNamespaces.add("xsi");
        inclusiveNamespaces.add("wsu");
        Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
        c.setList(inclusiveNamespaces);
        c.setOutputStream(baos);

        canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

        assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML);
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
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        t.transform(new DOMSource(doc), streamResult);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<String> inclusiveNamespaces = new ArrayList<String>();
        Canonicalizer20010315_ExclWithCommentsTransformer c = 
                new Canonicalizer20010315_ExclWithCommentsTransformer();
        c.setList(inclusiveNamespaces);
        c.setOutputStream(baos);

        canonicalize(c, new StringReader(stringWriter.toString()), new QName("http://example.net", "elem2"));

        byte[] reference =
                getBytesFromResource(this.getClass().getClassLoader().getResource(
                    "org/apache/xml/security/c14n/inExcl/example2_4_c14nized.xml"));
        boolean equals = java.security.MessageDigest.isEqual(reference, baos.toByteArray());

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

        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            List<String> inclusiveNamespaces = new ArrayList<String>();
            inclusiveNamespaces.add("#default");
            inclusiveNamespaces.add("xsi");
            Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
            c.setList(inclusiveNamespaces);
            c.setOutputStream(baos);
            canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

            assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML);
        }

        {
            //exactly the same outcome is expected if #default is not set:
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            List<String> inclusiveNamespaces = new ArrayList<String>();
            inclusiveNamespaces.add("xsi");
            Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
            c.setList(inclusiveNamespaces);
            c.setOutputStream(baos);
            canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

            assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML);
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

        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            List<String> inclusiveNamespaces = new ArrayList<String>();
            inclusiveNamespaces.add("#default");
            inclusiveNamespaces.add("xsi");
            Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
            c.setList(inclusiveNamespaces);
            c.setOutputStream(baos);
            canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

            assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML1);
        }
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            List<String> inclusiveNamespaces = new ArrayList<String>();
            inclusiveNamespaces.add("xsi");
            Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
            c.setList(inclusiveNamespaces);
            c.setOutputStream(baos);
            canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

            assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML2);
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

        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            List<String> inclusiveNamespaces = new ArrayList<String>();
            inclusiveNamespaces.add("#default");
            inclusiveNamespaces.add("xsi");
            Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
            c.setList(inclusiveNamespaces);
            c.setOutputStream(baos);
            canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

            assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML);
        }
        {
            //exactly the same outcome is expected if #default is not set:
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            List<String> inclusiveNamespaces = new ArrayList<String>();
            inclusiveNamespaces.add("xsi");
            Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
            c.setList(inclusiveNamespaces);
            c.setOutputStream(baos);
            canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

            assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML);
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


        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            List<String> inclusiveNamespaces = new ArrayList<String>();
            inclusiveNamespaces.add("#default");
            inclusiveNamespaces.add("xsi");
            Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
            c.setList(inclusiveNamespaces);
            c.setOutputStream(baos);
            canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

            assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML);
        }
        {
            //exactly the same outcome is expected if #default is not set:
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            List<String> inclusiveNamespaces = new ArrayList<String>();
            inclusiveNamespaces.add("xsi");
            Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
            c.setList(inclusiveNamespaces);
            c.setOutputStream(baos);
            canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

            assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML);
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<String> inclusiveNamespaces = new ArrayList<String>();
        inclusiveNamespaces.add("#default");
        Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
        c.setList(inclusiveNamespaces);
        c.setPropagateDefaultNamespace(true);
        c.setOutputStream(baos);
        canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

        assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML);
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<String> inclusiveNamespaces = new ArrayList<String>();
        inclusiveNamespaces.add("#default");
        Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
        c.setList(inclusiveNamespaces);
        c.setPropagateDefaultNamespace(true);
        c.setOutputStream(baos);
        canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

        assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML);
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<String> inclusiveNamespaces = new ArrayList<String>();
        inclusiveNamespaces.add("#default");
        Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
        c.setList(inclusiveNamespaces);
        c.setPropagateDefaultNamespace(true);
        c.setOutputStream(baos);
        canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

        assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML);
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<String> inclusiveNamespaces = new ArrayList<String>();
        inclusiveNamespaces.add("#default");
        Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
        c.setList(inclusiveNamespaces);
        c.setPropagateDefaultNamespace(true);
        c.setOutputStream(baos);
        canonicalize(c, new StringReader(XML), new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body"));

        assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML);
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<String> inclusiveNamespaces = new ArrayList<String>();
        inclusiveNamespaces.add("#default");
        Canonicalizer20010315_ExclOmitCommentsTransformer c = new Canonicalizer20010315_ExclOmitCommentsTransformer();
        c.setList(inclusiveNamespaces);
        c.setPropagateDefaultNamespace(true);
        c.setOutputStream(baos);
        canonicalize(c, new StringReader(XML), new QName("http://xmlsoap.org/Ping", "Ping"));

        assertEquals(new String(baos.toByteArray(), "UTF-8"), c14nXML);
    }

    private void canonicalize(
            Canonicalizer20010315_Excl c, InputStream inputStream, QName elementName)
            throws XMLStreamException {
        canonicalize(c, xmlInputFactory.createXMLEventReader(inputStream), elementName);
    }

    private void canonicalize(
            Canonicalizer20010315_Excl c, Reader reader, QName elementName)
            throws XMLStreamException {
        canonicalize(c, xmlInputFactory.createXMLEventReader(reader), elementName);
    }

    private void canonicalize(
            Canonicalizer20010315_Excl c, XMLEventReader xmlEventReader, QName elementName)
            throws XMLStreamException {

        XMLSecEvent xmlSecEvent = null;
        while (xmlEventReader.hasNext()) {
            xmlSecEvent = (XMLSecEvent) xmlEventReader.nextEvent();
            if (xmlSecEvent.isStartElement() && xmlSecEvent.asStartElement().getName().equals(elementName)) {
                break;
            }
        }

        while (xmlEventReader.hasNext()) {
            c.transform(xmlSecEvent);
            if (xmlSecEvent.isEndElement() && xmlSecEvent.asEndElement().getName().equals(elementName)) {
                break;
            }
            xmlSecEvent = (XMLSecEvent) xmlEventReader.nextEvent();
        }
    }

    public static byte[] getBytesFromResource(URL resource) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream inputStream = resource.openStream();
        try {
            byte buf[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }

            return baos.toByteArray();
        } finally {
            inputStream.close();
        }
    }
}