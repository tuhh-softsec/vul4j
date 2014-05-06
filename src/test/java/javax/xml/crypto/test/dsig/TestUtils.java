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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.crypto.SecretKey;
import javax.xml.crypto.Data;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
    private static final String DSA_2048_Y =
        "15119007057343785981993995134621348945077524760182795513668325877793414638620983617627033248732235626178802906346261435991040697338468329634416089753032362617771631199351767336660070462291411472735835843440140283101463231807789628656218830720378705090795271104661936237385140354825159080766174663596286149653433914842868551355716015585570827642835307073681358328172009941968323702291677280809277843998510864653406122348712345584706761165794179850728091522094227603562280855104749858249588234915206290448353957550635709520273178475097150818955098638774564910092913714625772708285992586894795017709678223469405896699928";
    private static final String DSA_2048_P =
        "18111848663142005571178770624881214696591339256823507023544605891411707081617152319519180201250440615163700426054396403795303435564101919053459832890139496933938670005799610981765220283775567361483662648340339405220348871308593627647076689407931875483406244310337925809427432681864623551598136302441690546585427193224254314088256212718983105131138772434658820375111735710449331518776858786793875865418124429269409118756812841019074631004956409706877081612616347900606555802111224022921017725537417047242635829949739109274666495826205002104010355456981211025738812433088757102520562459649777989718122219159982614304359";
    private static final String DSA_2048_Q =
        "19689526866605154788513693571065914024068069442724893395618704484701";
    private static final String DSA_2048_G =
        "2859278237642201956931085611015389087970918161297522023542900348087718063098423976428252369340967506010054236052095950169272612831491902295835660747775572934757474194739347115870723217560530672532404847508798651915566434553729839971841903983916294692452760249019857108409189016993380919900231322610083060784269299257074905043636029708121288037909739559605347853174853410208334242027740275688698461842637641566056165699733710043802697192696426360843173620679214131951400148855611740858610821913573088059404459364892373027492936037789337011875710759208498486908611261954026964574111219599568903257472567764789616958430";
    private static final String DSA_2048_X =
        "14562787764977288900757387442281559936279834964901963465277698843172";
    private static final String RSA_MOD = 
        "010800185049102889923150759252557522305032794699952150943573164381936603255999071981574575044810461362008102247767482738822150129277490998033971789476107463";
    private static final String RSA_PUB = "065537";
    private static final String RSA_PRIV = 
        "0161169735844219697954459962296126719476357984292128166117072108359155865913405986839960884870654387514883422519600695753920562880636800379454345804879553";

    private TestUtils() {}
    
    public static PublicKey getPublicKey(String algo)
        throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (algo.equalsIgnoreCase("DSA")) {
            return getPublicKey("DSA", 1024);
        } else if (algo.equalsIgnoreCase("RSA")) {
            return getPublicKey("RSA", 512);
        } else throw new RuntimeException("Unsupported key algorithm " + algo);
    }
        
    public static PublicKey getPublicKey(String algo, int keysize)
        throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory kf = KeyFactory.getInstance(algo);
        KeySpec kspec;
        if (algo.equalsIgnoreCase("DSA")) {
            if (keysize == 1024) {
                kspec = new DSAPublicKeySpec(new BigInteger(DSA_Y), 
                                             new BigInteger(DSA_P), 
                                             new BigInteger(DSA_Q), 
                                             new BigInteger(DSA_G));
            } else if (keysize == 2048) {
                kspec = new DSAPublicKeySpec(new BigInteger(DSA_2048_Y), 
                                             new BigInteger(DSA_2048_P), 
                                             new BigInteger(DSA_2048_Q), 
                                             new BigInteger(DSA_2048_G));
            } else throw new RuntimeException("Unsupported keysize:" + keysize);
        } else if (algo.equalsIgnoreCase("RSA")) {
            if (keysize == 512) {
                kspec = new RSAPublicKeySpec(new BigInteger(RSA_MOD), 
                                             new BigInteger(RSA_PUB));
            } else throw new RuntimeException("Unsupported keysize:" + keysize);
        } else throw new RuntimeException("Unsupported key algorithm " + algo);
        return kf.generatePublic(kspec);
    }

    public static void validateSecurityOrEncryptionElement(Node toValidate) throws SAXException, IOException {
        XMLSec.init();
        Schema schema = XMLSecurityConstants.getJaxbSchemas();
        Validator validator = schema.newValidator();
        DOMSource source = new DOMSource(toValidate);
        validator.validate(source);
    }
    
    public static PrivateKey getPrivateKey(String algo)
        throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (algo.equalsIgnoreCase("DSA")) {
            return getPrivateKey("DSA", 1024);
        } else if (algo.equalsIgnoreCase("RSA")) {
            return getPrivateKey("RSA", 512);
        } else throw new RuntimeException("Unsupported key algorithm " + algo);
    }

    public static PrivateKey getPrivateKey(String algo, int keysize) 
        throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory kf = KeyFactory.getInstance(algo);
        KeySpec kspec;
        if (algo.equalsIgnoreCase("DSA")) {
            if (keysize == 1024) {
                kspec = new DSAPrivateKeySpec
                    (new BigInteger(DSA_X), new BigInteger(DSA_P), 
                     new BigInteger(DSA_Q), new BigInteger(DSA_G));
            } else if (keysize == 2048) {
                kspec = new DSAPrivateKeySpec
                    (new BigInteger(DSA_2048_X), new BigInteger(DSA_2048_P), 
                     new BigInteger(DSA_2048_Q), new BigInteger(DSA_2048_G));
            } else throw new RuntimeException("Unsupported keysize:" + keysize);
        } else if (algo.equalsIgnoreCase("RSA")) {
            if (keysize == 512) {
                kspec = new RSAPrivateKeySpec
                    (new BigInteger(RSA_MOD), new BigInteger(RSA_PRIV));
            } else throw new RuntimeException("Unsupported keysize:" + keysize);
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
            DocumentBuilder docBuilder = XMLUtils.createDocumentBuilder(false, false);
            Document doc = docBuilder.parse(input);
            if (tag == null) {
                return new DOMValidateContext
                    (TestUtils.getPublicKey("RSA", 512),
                     doc.getDocumentElement());
            } else {
                NodeList list = doc.getElementsByTagName(tag);
                return new DOMValidateContext
                    (TestUtils.getPublicKey("RSA", 512), list.item(0));
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
