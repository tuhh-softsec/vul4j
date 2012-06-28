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
package org.apache.xml.security.test.dom.utils;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OldApiTest extends org.junit.Assert {

    public static class OldResourceResolverSpi extends ResourceResolverSpi {
        Attr uriCompare;
        String baseCompare;
        
        public boolean engineCanResolve(Attr uri, String BaseURI) {
            if (uri.getValue().indexOf("!!!test=") != 0) {
                return false;
            }
            uriCompare = uri;
            baseCompare = BaseURI;
            return true;
        }

        public XMLSignatureInput engineResolve(
            Attr uri, String BaseURI
        ) throws ResourceResolverException {
            assertEquals(uriCompare, uri);
            assertEquals(baseCompare,BaseURI);
            return null;
        }
    }
    
    static class PublicKeyMock implements PublicKey {
        private static final long serialVersionUID = 2072239246582775281L;

        public String getAlgorithm() {
            // TODO Auto-generated method stub
            return null;
        }

        public byte[] getEncoded() {
            // TODO Auto-generated method stub
            return null;
        }

        public String getFormat() {
            // TODO Auto-generated method stub
            return null;
        }                  
    }
    
    static public class OldKeyResolverSpi extends KeyResolverSpi {
        static int number = 0;
        PublicKey pk = null;
        
        public OldKeyResolverSpi() {
            number++;
        }
        
        public boolean engineCanResolve(
            Element element, String BaseURI, StorageResolver storage
        ) {
            if ("!!!testUri".equals(BaseURI)) {
                return true;
            }
            return false;
        }
        
        public PublicKey engineResolvePublicKey(
            Element element, String BaseURI, StorageResolver storage
        ) throws KeyResolverException {
            if (pk == null) {
                pk = new PublicKeyMock();
            }
            return pk;
        }

        public X509Certificate engineResolveX509Certificate(
            Element element, String BaseURI, StorageResolver storage
        ) throws KeyResolverException {
            return null;
        };

        public SecretKey engineResolveSecretKey(
            Element element, String BaseURI, StorageResolver storage
        ) throws KeyResolverException{
            return null;
        }
    }
    
    static public class OldKeyResolverNoPublicConsSpi extends OldKeyResolverSpi {
        
        protected OldKeyResolverNoPublicConsSpi() {
            //
        }
        
        public OldKeyResolverNoPublicConsSpi(PublicKey pk) {
            this.pk = pk;
        }
    }

    @org.junit.Test
    public void testOldResourceResolverSpi() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();		
        Attr uri = doc.createAttribute("id");
        uri.setNodeValue("!!!test=1");
        ((Element)doc.createElement("test")).setAttributeNode(uri);
        Attr uri1 = doc.createAttribute("id");
        uri1.setNodeValue("!!!test=2");
        doc.createElement("test1").setAttributeNode(uri1);
        ResourceResolver.registerAtStart(OldResourceResolverSpi.class.getName());
        ResourceResolver resolver = ResourceResolver.getInstance(uri, "test");
        ResourceResolver resolver1 = ResourceResolver.getInstance(uri1, "test1");
        ResourceResolver resolver2 = ResourceResolver.getInstance(uri1, "test2");

        resolver2.resolve(uri1, "test2");		
        resolver.resolve(uri, "test");
        resolver1.resolve(uri1, "test1");
    }
    
    @org.junit.Test
    public void testOldKeyResolverSpi() throws Exception {
        KeyResolver.register(OldKeyResolverSpi.class.getName(), true);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();				
        Element el = ((Element)doc.createElement("test"));
        PublicKey pk = KeyResolver.getPublicKey(el, "!!!testUri", null);
        assertNotNull(pk);
        assertTrue(pk instanceof PublicKeyMock);
        assertEquals(2, OldKeyResolverSpi.number);
        PublicKey pk1 = KeyResolver.getPublicKey(el, "!!!testUri", null);
        assertNotSame(pk, pk1);
        assertEquals(3, OldKeyResolverSpi.number);
    }

    @org.junit.Test
    public void testOldKeyResolverSpiInKeyInfo() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element el = (Element)doc.createElementNS("http://www.w3.org/2000/09/xmldsig#","KeyInfo");
        el.appendChild((Element)doc.createElementNS("http://www.w3.org/2000/09/xmldsig#","KeyInfo"));
        KeyInfo ki = new KeyInfo(el,"!!!testUri");
        PublicKey pk = new PublicKeyMock();
        ki.registerInternalKeyResolver(new OldKeyResolverNoPublicConsSpi(pk));
        assertNotNull(ki.getPublicKey());

    }
    
}
