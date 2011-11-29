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
package org.apache.xml.security.test.utils;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.utils.IdResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class IdResolverTest extends org.junit.Assert {
    
    @org.junit.Test
    public void testIdSoap() throws Exception {
        String s="<env:Envelope xmlns:SOAP-SEC=\"http://schemas.xmlsoap.org/soap/security/2000-12\" xmlns:env=\"http://www.w3.org/2001/12/soap-envelope\" actor=\"some-uri\" mustUnderstand=\"1\">\r\n" 
            + "<env:Header><SOAP-SEC:Signature>xxxx</SOAP-SEC:Signature></env:Header>\r\n"
            + "<env:Body SOAP-SEC:id=\"Body\">This is signed together with it\'s Body ancestor</env:Body>\r\n" 
            + "</env:Envelope>";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(s.getBytes()));
        
        Node body = doc.getElementsByTagNameNS("http://www.w3.org/2001/12/soap-envelope", "Body").item(0);
        Attr attr = 
            ((Element)body).getAttributeNodeNS("http://schemas.xmlsoap.org/soap/security/2000-12", "id");
        if (attr != null) {
            IdResolver.registerElementById((Element)body, attr.getValue());
        }
        
        Element el = IdResolver.getElementById(doc, "Body");
        assertNotNull(el);
        assertEquals("Body", el.getLocalName());
    }
    
    @org.junit.Test
    public void testIdWithOtherIdSoap() throws Exception {
        String s="<env:Envelope xmlns:SOAP-SEC=\"http://schemas.xmlsoap.org/soap/security/2000-12\" xmlns:env=\"http://www.w3.org/2001/12/soap-envelope\" actor=\"some-uri\" mustUnderstand=\"1\">\r\n" 
            + "<env:Header><SOAP-SEC:Signature>xxxx</SOAP-SEC:Signature></env:Header>\r\n"
            + "<a id=\"Body\"/><env:Body SOAP-SEC:id=\"Body\">This is signed together with it\'s Body ancestor</env:Body>\r\n" 
            + "</env:Envelope>";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(s.getBytes()));
        
        Node body = doc.getElementsByTagNameNS("http://www.w3.org/2001/12/soap-envelope", "Body").item(0);
        Attr attr = 
            ((Element)body).getAttributeNodeNS("http://schemas.xmlsoap.org/soap/security/2000-12", "id");
        if (attr != null) {
            IdResolver.registerElementById((Element)body, attr.getValue());
        }
        
        Element el = IdResolver.getElementById(doc, "Body");
        assertNotNull(el);
        assertEquals("Body", el.getLocalName());
    }
    
    @org.junit.Test
    public void testANoId() throws Exception {
        String s = "<env:Envelope xmlns:SOAP-SEC=\"http://schemas.xmlsoap.org/soap/security/2000-12\" xmlns:env=\"http://www.w3.org/2001/12/soap-envelope\" actor=\"some-uri\" mustUnderstand=\"1\">\r\n"
            + "<env:Header><SOAP-SEC:Signature>xxxx</SOAP-SEC:Signature></env:Header>\r\n"
            + "<a id=\"Body\"/>"
            + "</env:Envelope>";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(s.getBytes()));
        
        Node node = doc.getElementsByTagName("a").item(0);
        Attr attr = 
            ((Element)node).getAttributeNodeNS(null, "id");
        if (attr != null) {
            IdResolver.registerElementById((Element)node, attr.getValue());
        }
        
        Element el = IdResolver.getElementById(doc, "Body");
        assertNotNull(el);
        assertEquals("a", el.getLocalName());
    }
    
}
