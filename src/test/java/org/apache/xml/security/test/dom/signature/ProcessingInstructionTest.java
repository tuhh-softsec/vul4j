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
package org.apache.xml.security.test.dom.signature;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;

/**
 * A test-case for Bugzilla bug 45744 - "XPath transform and xml-stylesheet".
 */
public class ProcessingInstructionTest extends org.junit.Assert {

    static {
        org.apache.xml.security.Init.init();
    }

    private static String dir;

    public ProcessingInstructionTest() {
        String base = System.getProperty("basedir") == null 
            ? "./" : System.getProperty("basedir");
        String fs = System.getProperty("file.separator");
        dir = base + fs + "src/test/resources" + fs + "org" + fs + "apache" + fs + "xml" + 
            fs + "security" + fs + "testcases" + fs;
    }
    
    @org.junit.Test
    public void testProcessingInstruction() throws Exception {
        String signatureFileName = dir + "upp_sign.xml";
        DocumentBuilder db = XMLUtils.createDocumentBuilder(false);
        File f = new File(signatureFileName);
        Document doc = db.parse(new java.io.FileInputStream(f));
        
        Node obj = 
            doc.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", "QualifyingProperties").item(0);
        while (obj != null) {
            if (obj instanceof Element) {
                Attr attr = ((Element)obj).getAttributeNode("Id");
                if (attr != null) {
                    ((Element)obj).setIdAttributeNode(attr, true);
                }
            }
            obj = obj.getFirstChild();
        }

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//ds:Signature[1]";
        Element sigElement = 
            (Element) xpath.evaluate(expression, doc, XPathConstants.NODE);

        String baseUri = new File(".").toURI().toURL().toString();
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
    
        @Override
        public XMLSignatureInput engineResolveURI(ResourceResolverContext context)
            throws ResourceResolverException {
            try {
                URI uriNew = getNewURI(context.uriToResolve, context.baseUri);
                
                FileInputStream inputStream = 
                    new FileInputStream(dir + "out.xml");
                XMLSignatureInput result = new XMLSignatureInput(inputStream);

                result.setSourceURI(uriNew.toString());

                return result;
            } catch (Exception ex) {
                throw new ResourceResolverException(
                    "generic.EmptyMessage", ex, context.uriToResolve, context.baseUri
                );
            }
        }
        
        @Override
        public boolean engineCanResolveURI(ResourceResolverContext context) {
            if (context.uriToResolve == null || !"out.xml".equals(context.uriToResolve)) {
                return false;
            }
            return true;
        }
        
        private static URI getNewURI(String uri, String baseURI) throws URISyntaxException {
            URI newUri = null;
            if (baseURI == null || "".equals(baseURI)) {
                newUri = new URI(uri);
            } else {
                newUri = new URI(baseURI).resolve(uri);
            }
            
            // if the URI contains a fragment, ignore it
            if (newUri.getFragment() != null) {
                URI uriNewNoFrag = 
                    new URI(newUri.getScheme(), newUri.getSchemeSpecificPart(), null);
                return uriNewNoFrag;
            }
            return newUri;
        }
    }

}
