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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.ext.InboundXMLSec;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.test.stax.utils.StAX2DOM;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Interop test for exclusive canonical XML.
 */
public class ExclusiveC14NInteropTest extends org.junit.Assert {
    
    // Define the Keys
    private static final String DSA_Y = 
            "100608549353392247642909699559958629799239376887928526645894830026725196161145566849708733883990135426653044817911443739078414918200719335778010681793609319420004405842110519195451832486531732453606134760724262533773278050009694686068237740978670601469803011818937563585466331102373332041567195551433843399821";
    private static final String DSA_P =
            "168777965834506183316029234822049448824510746356579047085445137260615952005547237578049190106944051176874334502490044372001095187406578652983566270415497585188065671106321782044399345015635174214653019063633432488260846312454028091420079382316827654485991763360842518637782234847501432479186061030043532804049";
    private static final String DSA_Q = 
            "869728719104136791953389816626377255923310715629";
    private static final String DSA_G = 
            "92540127839696181660603196559927599444985269069164107041817893521317620801749269178254275499516782269048036232822187472000302495112526925817061777055402839811893512245205312429410597288465276425092799962454093795367121466944495977844096953319768287183660390415542263887700389610463126158583843139357076400271";
    
    private XMLInputFactory xmlInputFactory;
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();
    
    @Before
    public void setUp() throws Exception {
        Init.init(ExclusiveC14NInteropTest.class.getClassLoader().getResource("security-config.xml").toURI(),
                this.getClass());
        org.apache.xml.security.Init.init();
        
        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());
    }
    

    @Test
    public void test_Y1() throws Exception {
        // Read in plaintext document
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream("interop/c14n/Y1/exc-signature.xml");
        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(sourceDocument);
        
        // Set up the Key
        Key publicKey = getPublicKey();
        
        // XMLUtils.outputDOM(document, System.out);
        
        // Convert Document to a Stream Reader
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(baos));
        final XMLStreamReader xmlStreamReader = 
                xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
  
        // Verify signature
        XMLSecurityProperties properties = new XMLSecurityProperties();
        properties.setDisableSchemaValidation(true);
        properties.setSignatureVerificationKey(publicKey);
        InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);
        XMLStreamReader securityStreamReader = inboundXMLSec.processInMessage(xmlStreamReader);

        StAX2DOM.readDoc(XMLUtils.createDocumentBuilder(false), securityStreamReader);
    }
    
    private static PublicKey getPublicKey() 
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory kf = KeyFactory.getInstance("DSA");
        KeySpec kspec = new DSAPublicKeySpec(new BigInteger(DSA_Y), 
                        new BigInteger(DSA_P), 
                        new BigInteger(DSA_Q), 
                        new BigInteger(DSA_G));
        return kf.generatePublic(kspec);
    }

}
