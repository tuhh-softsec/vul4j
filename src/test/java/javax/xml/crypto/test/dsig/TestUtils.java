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
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
package javax.xml.crypto.test.dsig;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;

import javax.crypto.SecretKey;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.spec.*;
import javax.xml.crypto.dom.*;
import javax.xml.crypto.*;

import java.math.BigInteger;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.parsers.DocumentBuilder;

import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/*
 * @author Sean Mullan
 * @author Valerie Peng
 */
public class TestUtils {
    
    private static final String DSA_Y = 
        "07066284216756577193658833512863439617178933165631848358445549382240081120085333137303066923542492834619027404463194956043802393462371310375123430985057160";
    private static final String DSA_P = 
        "013232376895198612407547930718267435757728527029623408872245156039757713029036368719146452186041204237350521785240337048752071462798273003935646236777459223";
    private static final String DSA_Q = 
        "0857393771208094202104259627990318636601332086981";
    private static final String DSA_G = 
        "05421644057436475141609648488325705128047428394380474376834667300766108262613900542681289080713724597310673074119355136085795982097390670890367185141189796";
    private static final String DSA_X = 
        "0527140396812450214498055937934275626078768840117";
    private static final String RSA_MOD = 
        "010800185049102889923150759252557522305032794699952150943573164381936603255999071981574575044810461362008102247767482738822150129277490998033971789476107463";
    private static final String RSA_PUB = "065537";
    private static final String RSA_PRIV = 
        "0161169735844219697954459962296126719476357984292128166117072108359155865913405986839960884870654387514883422519600695753920562880636800379454345804879553";

    private TestUtils() {}
    
    public static PublicKey getPublicKey(String algo) 
        throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory kf = KeyFactory.getInstance(algo);
        KeySpec kspec;
        if (algo.equalsIgnoreCase("DSA")) {
            kspec = new DSAPublicKeySpec(new BigInteger(DSA_Y), 
                                         new BigInteger(DSA_P), 
                                         new BigInteger(DSA_Q), 
                                         new BigInteger(DSA_G));
        } else if (algo.equalsIgnoreCase("RSA")) {
            kspec = new RSAPublicKeySpec(new BigInteger(RSA_MOD), 
                                         new BigInteger(RSA_PUB));
        } else throw new RuntimeException("Unsupported key algorithm " + algo);
        return kf.generatePublic(kspec);
    }

    public static void validateSecurityOrEncryptionElement(Node toValidate) throws SAXException, IOException {
        
        Schema schema = XMLSecurityConstants.getJaxbSchemas();
        Validator validator = schema.newValidator();
        DOMSource source = new DOMSource(toValidate);
        validator.validate(source);
    }
    
    public static PrivateKey getPrivateKey(String algo) 
        throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory kf = KeyFactory.getInstance(algo);
        KeySpec kspec;
        if (algo.equalsIgnoreCase("DSA")) {
            kspec = new DSAPrivateKeySpec
                (new BigInteger(DSA_X), new BigInteger(DSA_P), 
                 new BigInteger(DSA_Q), new BigInteger(DSA_G));
        } else if (algo.equalsIgnoreCase("RSA")) {
            kspec = new RSAPrivateKeySpec
                (new BigInteger(RSA_MOD), new BigInteger(RSA_PRIV));
        } else throw new RuntimeException("Unsupported key algorithm " + algo);
        return kf.generatePrivate(kspec);
    }

    public static SecretKey getSecretKey(final byte[] secret) {
        return new SecretKey() {
            private static final long serialVersionUID = 5629454124145851381L;
            
            public String getFormat()	{ return "RAW"; }
            public byte[] getEncoded()	{ return secret; }
            public String getAlgorithm(){ return "SECRET"; }
        };
    }
    
    public static Document newDocument() {
        try {
            DocumentBuilder docBuilder = XMLUtils.createDocumentBuilder(false);
            return docBuilder.newDocument();
        } catch (Exception ex) {
            return null;
        }
    }

    public static class MyOwnC14nParameterSpec implements C14NMethodParameterSpec {}
    
    public static class MyOwnDigestMethodParameterSpec 
        implements DigestMethodParameterSpec {}
    
    public static class MyOwnSignatureMethodParameterSpec 
        implements SignatureMethodParameterSpec {}

    public static XMLValidateContext getXMLValidateContext(String type, 
                                                       File input, 
                                                       String tag) 
        throws Exception {
        if (type.equalsIgnoreCase("dom")) {
            DocumentBuilder docBuilder = XMLUtils.createDocumentBuilder(false);
            Document doc = docBuilder.parse(input);
            if (tag == null) {
                return new DOMValidateContext
                    (TestUtils.getPublicKey("RSA"), doc.getDocumentElement());
            } else {
                NodeList list = doc.getElementsByTagName(tag);
                return new DOMValidateContext
                    (TestUtils.getPublicKey("RSA"), list.item(0));
            }
        } else {
            throw new Exception("Unsupported XMLValidateContext type: " + 
                                type);
        }
    }

    public static class MyOwnDOMReference extends DOMStructure 
        implements Reference {
        private String id;
        private boolean status;
        private byte[] digest;
        private static MessageDigest MD;
        private static DigestMethod DIG_METHOD;
        private Data derefData;
        private InputStream dis;
        static {
            try {
                MD = MessageDigest.getInstance("SHA");
                XMLSignatureFactory factory = XMLSignatureFactory.getInstance
                    ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
                DIG_METHOD = 
                    factory.newDigestMethod(DigestMethod.SHA1, null);
            } catch (Exception ex) {
                // should never be thrown
            }
        }
        
        public MyOwnDOMReference(String id, boolean status) {
            super(newDocument());
            this.id = id;
            this.status = status;
            digest = null;
        }

        public byte[] getDigestValue() {
            if (digest == null) {
                byte[] inBytes = id.getBytes();
                digest = new byte[20];
                if (status) {
                    digest = MD.digest(inBytes);
                }
            }
            return digest;
        }
        
        public byte[] getCalculatedDigestValue() {
            return null;
        }
        
        public DigestMethod getDigestMethod() { return DIG_METHOD; }
        
        public String getId() {
            return id;
        }
        
        public String getType() {
            return null;
        }
        
        public String getURI() {
            return null;
        }
        
        public List<?> getTransforms() {
            return Collections.EMPTY_LIST;
        }
        
        public boolean validate(XMLValidateContext vCtx) 
            throws XMLSignatureException {
            this.dis = new ByteArrayInputStream(id.getBytes());
            this.derefData = new OctetStreamData(this.dis);
            return status;
        }
        
        public Data getDereferencedData() {
            return derefData;
        }
        
        public InputStream getDigestInputStream() {
            return dis;
        }
    }

    public static class MyOwnXMLStructure implements XMLStructure {
        
        public boolean isFeatureSupported(String feature) 
            throws NullPointerException {
            if (feature == null) throw new NullPointerException();
            return false;
        }
    }

    public static class OctetStreamURIDereferencer implements URIDereferencer {
        
        private byte[] data = null;
        
        public OctetStreamURIDereferencer(byte[] in) {
            data = (byte[]) in.clone();
        }
        
        public Data dereference(URIReference ref, XMLCryptoContext ctxt) {
            return new OctetStreamData(new ByteArrayInputStream(data));
        }
        
        public byte[] getData() {
            return data;
        }
        
        public boolean equals(Object obj) {
            if (obj instanceof OctetStreamURIDereferencer) {
                return Arrays.equals
                    (((OctetStreamURIDereferencer) obj).getData(), data);
            } else {
                return false;
            }
        }
        
        public int hashCode() {
            return 5678;
        }
    }

    public static class NodeSetURIDereferencer implements URIDereferencer {
        
        private Node data = null;
        
        public NodeSetURIDereferencer(Node node) {
            data = node;
        }
        
        public Data dereference(URIReference ref, XMLCryptoContext ctxt) {
            return new NodeSetData() {
                public Iterator<?> iterator() {
                    return Collections.singletonList(data).iterator();
                }
            };
        }
    }

    public static void dumpDocument(Document doc, String outName) throws Exception {
        DOMSource source = new DOMSource(doc);
        File path = new File(System.getProperty("test.dir"), outName);
        Result result = new StreamResult(new FileOutputStream(path));
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.transform(source, result);
    }
    
}
