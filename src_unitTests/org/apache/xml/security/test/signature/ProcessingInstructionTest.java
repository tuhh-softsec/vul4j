/*
 * Copyright  1999-2009 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.test.signature;

import java.io.File;
import java.io.FileInputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.utils.URI;

/**
 * A test-case for Bugzilla bug 45744 - "XPath transform and xml-stylesheet".
 */
public class ProcessingInstructionTest extends TestCase {

    static {
        org.apache.xml.security.Init.init();
    }

    private static String dir;

    public static Test suite() {
        return new TestSuite(ProcessingInstructionTest.class);
    }
    
    public ProcessingInstructionTest(String name) {
        super(name);
        String base = System.getProperty("basedir") == null 
            ? "./" : System.getProperty("basedir");
        String fs = System.getProperty("file.separator");
        dir = base + fs + "data" + fs + "org" + fs + "apache" + fs + "xml" + 
            fs + "security" + fs + "testcases" + fs;
    }
    
    public void testProcessingInstruction() throws Exception {
        String signatureFileName = dir + "upp_sign.xml";
        DocumentBuilderFactory dbf = 
            DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        File f = new File(signatureFileName);
        Document doc = db.parse(new java.io.FileInputStream(f));

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {

            public String getNamespaceURI(String arg0) {
                if (!arg0.equals("ds"))
                    throw new RuntimeException();
                return "http://www.w3.org/2000/09/xmldsig#";
            }

            public String getPrefix(String arg0) {
                return "ds";
            }

            public Iterator getPrefixes(String arg0) {
                List al = new ArrayList();
                al.add("ds");
                return al.iterator();
            }

        });

        String expression = "//ds:Signature[1]";
        Element sigElement = 
            (Element) xpath.evaluate(expression, doc, XPathConstants.NODE);

        String baseUri = new File(".").toURL().toString();
        XMLSignature signature = new XMLSignature(sigElement, baseUri);
        signature.addResourceResolver(FileResolver.getInstance());
        X509Certificate cert = signature.getKeyInfo().getX509Certificate();
        if (!signature.checkSignatureValue(cert)) {
            throw new Exception("Signature is invalid!");
        }
    }

    
    /**
     * This class resolves "out.xml" on the local filesystem.
     */
    private static class FileResolver extends ResourceResolverSpi {

        private static FileResolver resolver = null;
    
        public synchronized static ResourceResolverSpi getInstance() {
            if (resolver == null) {
                resolver = new FileResolver();
            }
            return resolver;
        }
    
        private FileResolver() {
        }
    
        public XMLSignatureInput engineResolve(Attr uri, String baseURI)
            throws ResourceResolverException {
    
            try {
                URI uriNew = new URI(uri.getNodeValue(), baseURI);
                
                FileInputStream inputStream = 
                    new FileInputStream(dir + "out.xml");
                XMLSignatureInput result = new XMLSignatureInput(inputStream);

                result.setSourceURI(uriNew.toString());

                return result;
            } catch (Exception ex) {
                throw new ResourceResolverException(
                    "generic.EmptyMessage", ex, uri, baseURI
                );
            }
        }
        
        public boolean engineCanResolve(Attr uri, String BaseURI) {
            if (uri == null || !"out.xml".equals(uri.getNodeValue())) {
                return false;
            }
            return true;
        }
    }

}
