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
package org.apache.xml.security.stax.ext;

import org.apache.xml.security.stax.impl.util.ConcreteLSInput;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * XMLSecurityConstants for global use
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityConstants {

    public static final SecureRandom secureRandom;
    private static JAXBContext jaxbContext;
    private static Schema schema;

    public static final DatatypeFactory datatypeFactory;

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }

        try {
            setJaxbContext(JAXBContext.newInstance("org.apache.xml.security.binding.xmlenc:org.apache.xml.security.binding.xmldsig:org.apache.xml.security.binding.xmldsig11:org.apache.xml.security.binding.excc14n"));
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setResourceResolver(new LSResourceResolver() {
                @Override
                public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
                    if ("http://www.w3.org/2001/XMLSchema.dtd".equals(systemId)) {
                        ConcreteLSInput concreteLSInput = new ConcreteLSInput();
                        concreteLSInput.setByteStream(XMLSecurityConstants.class.getClassLoader().getResourceAsStream("bindings/schemas/XMLSchema.dtd"));
                        return concreteLSInput;
                    } else if ("XMLSchema.dtd".equals(systemId)) {
                        ConcreteLSInput concreteLSInput = new ConcreteLSInput();
                        concreteLSInput.setByteStream(XMLSecurityConstants.class.getClassLoader().getResourceAsStream("bindings/schemas/XMLSchema.dtd"));
                        return concreteLSInput;
                    } else if ("datatypes.dtd".equals(systemId)) {
                        ConcreteLSInput concreteLSInput = new ConcreteLSInput();
                        concreteLSInput.setByteStream(XMLSecurityConstants.class.getClassLoader().getResourceAsStream("bindings/schemas/datatypes.dtd"));
                        return concreteLSInput;
                    } else if ("http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd".equals(systemId)) {
                        ConcreteLSInput concreteLSInput = new ConcreteLSInput();
                        concreteLSInput.setByteStream(XMLSecurityConstants.class.getClassLoader().getResourceAsStream("bindings/schemas/xmldsig-core-schema.xsd"));
                        return concreteLSInput;
                    } else if ("http://www.w3.org/2001/xml.xsd".equals(systemId)) {
                        ConcreteLSInput concreteLSInput = new ConcreteLSInput();
                        concreteLSInput.setByteStream(XMLSecurityConstants.class.getClassLoader().getResourceAsStream("bindings/schemas/xml.xsd"));
                        return concreteLSInput;
                    }
                    return null;
                }
            });
            Schema schema = schemaFactory.newSchema(
                    new Source[]{
                            new StreamSource(XMLSecurityConstants.class.getClassLoader().getResourceAsStream("bindings/schemas/exc-c14n.xsd")),
                            new StreamSource(XMLSecurityConstants.class.getClassLoader().getResourceAsStream("bindings/schemas/xmldsig-core-schema.xsd")),
                            new StreamSource(XMLSecurityConstants.class.getClassLoader().getResourceAsStream("bindings/schemas/xenc-schema.xsd")),
                            new StreamSource(XMLSecurityConstants.class.getClassLoader().getResourceAsStream("bindings/schemas/xmldsig11-schema.xsd")),
                    }
            );
            setJaxbSchemas(schema);

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    protected XMLSecurityConstants() {
    }

    protected static synchronized void setJaxbContext(JAXBContext jaxbContext) {
        XMLSecurityConstants.jaxbContext = jaxbContext;
    }

    public static synchronized void setJaxbSchemas(Schema schema) {
        XMLSecurityConstants.schema = schema;
    }

    public static Unmarshaller getJaxbUnmarshaller(boolean disableSchemaValidation) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        if (!disableSchemaValidation) {
            unmarshaller.setSchema(schema);
        }
        return unmarshaller;
    }

    public enum Phase {
        PREPROCESSING,
        PROCESSING,
        POSTPROCESSING,
    }

    public static final String XMLINPUTFACTORY = "XMLInputFactory";

    public static final String NS_XML = "http://www.w3.org/2000/xmlns/";
    public static final String NS_XMLENC = "http://www.w3.org/2001/04/xmlenc#";
    public static final String NS_DSIG = "http://www.w3.org/2000/09/xmldsig#";
    public static final String NS_DSIG11 = "http://www.w3.org/2009/xmldsig11#";
    public static final String NS_WSSE11 = "http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd";

    public static final String PREFIX_XENC = "xenc";
    public static final QName TAG_xenc_EncryptedKey = new QName(NS_XMLENC, "EncryptedKey", PREFIX_XENC);
    public static final QName ATT_NULL_Id = new QName(null, "Id");
    public static final QName ATT_NULL_Type = new QName(null, "Type");
    public static final QName ATT_NULL_MimeType = new QName(null, "MimeType");
    public static final QName ATT_NULL_Encoding = new QName(null, "Encoding");

    public static final QName TAG_xenc_EncryptionMethod = new QName(NS_XMLENC, "EncryptionMethod", PREFIX_XENC);
    public static final QName ATT_NULL_Algorithm = new QName(null, "Algorithm");

    public static final String PREFIX_DSIG = "dsig";
    public static final QName TAG_dsig_KeyInfo = new QName(NS_DSIG, "KeyInfo", PREFIX_DSIG);

    public static final QName TAG_xenc_EncryptionProperties = new QName(NS_XMLENC, "EncryptionProperties", PREFIX_XENC);
    public static final QName TAG_xenc_CipherData = new QName(NS_XMLENC, "CipherData", PREFIX_XENC);
    public static final QName TAG_xenc_CipherValue = new QName(NS_XMLENC, "CipherValue", PREFIX_XENC);
    public static final QName TAG_xenc_ReferenceList = new QName(NS_XMLENC, "ReferenceList", PREFIX_XENC);
    public static final QName TAG_xenc_DataReference = new QName(NS_XMLENC, "DataReference", PREFIX_XENC);
    public static final QName ATT_NULL_URI = new QName(null, "URI");

    public static final QName TAG_xenc_EncryptedData = new QName(NS_XMLENC, "EncryptedData", PREFIX_XENC);

    public static final String PREFIX_WSSE11 = "wsse11";
    public static final QName TAG_wsse11_EncryptedHeader = new QName(NS_WSSE11, "EncryptedHeader", PREFIX_WSSE11);

    public static final QName TAG_dsig_Signature = new QName(NS_DSIG, "Signature", PREFIX_DSIG);
    public static final QName TAG_dsig_SignedInfo = new QName(NS_DSIG, "SignedInfo", PREFIX_DSIG);
    public static final QName TAG_dsig_CanonicalizationMethod = new QName(NS_DSIG, "CanonicalizationMethod", PREFIX_DSIG);
    public static final QName TAG_dsig_SignatureMethod = new QName(NS_DSIG, "SignatureMethod", PREFIX_DSIG);
    public static final QName TAG_dsig_HMACOutputLength = new QName(NS_DSIG, "HMACOutputLength", PREFIX_DSIG);
    public static final QName TAG_dsig_Reference = new QName(NS_DSIG, "Reference", PREFIX_DSIG);
    public static final QName TAG_dsig_Transforms = new QName(NS_DSIG, "Transforms", PREFIX_DSIG);
    public static final QName TAG_dsig_Transform = new QName(NS_DSIG, "Transform", PREFIX_DSIG);
    public static final QName TAG_dsig_DigestMethod = new QName(NS_DSIG, "DigestMethod", PREFIX_DSIG);
    public static final QName TAG_dsig_DigestValue = new QName(NS_DSIG, "DigestValue", PREFIX_DSIG);
    public static final QName TAG_dsig_SignatureValue = new QName(NS_DSIG, "SignatureValue", PREFIX_DSIG);
    public static final QName TAG_dsig_Manifest = new QName(NS_DSIG, "Manifest", PREFIX_DSIG);

    public static final QName TAG_dsig_X509Data = new QName(NS_DSIG, "X509Data", PREFIX_DSIG);
    public static final QName TAG_dsig_X509IssuerSerial = new QName(NS_DSIG, "X509IssuerSerial", PREFIX_DSIG);
    public static final QName TAG_dsig_X509IssuerName = new QName(NS_DSIG, "X509IssuerName", PREFIX_DSIG);
    public static final QName TAG_dsig_X509SerialNumber = new QName(NS_DSIG, "X509SerialNumber", PREFIX_DSIG);
    public static final QName TAG_dsig_X509SKI = new QName(NS_DSIG, "X509SKI", PREFIX_DSIG);
    public static final QName TAG_dsig_X509Certificate = new QName(NS_DSIG, "X509Certificate", PREFIX_DSIG);
    public static final QName TAG_dsig_X509SubjectName = new QName(NS_DSIG, "X509SubjectName", PREFIX_DSIG);

    public static final QName TAG_dsig_KeyValue = new QName(NS_DSIG, "KeyValue", PREFIX_DSIG);
    public static final QName TAG_dsig_RSAKeyValue = new QName(NS_DSIG, "RSAKeyValue", PREFIX_DSIG);
    public static final QName TAG_dsig_Modulus = new QName(NS_DSIG, "Modulus", PREFIX_DSIG);
    public static final QName TAG_dsig_Exponent = new QName(NS_DSIG, "Exponent", PREFIX_DSIG);

    public static final QName TAG_dsig_DSAKeyValue = new QName(NS_DSIG, "DSAKeyValue", PREFIX_DSIG);
    public static final QName TAG_dsig_P = new QName(NS_DSIG, "P", PREFIX_DSIG);
    public static final QName TAG_dsig_Q = new QName(NS_DSIG, "Q", PREFIX_DSIG);
    public static final QName TAG_dsig_G = new QName(NS_DSIG, "G", PREFIX_DSIG);
    public static final QName TAG_dsig_Y = new QName(NS_DSIG, "Y", PREFIX_DSIG);
    public static final QName TAG_dsig_J = new QName(NS_DSIG, "J", PREFIX_DSIG);
    public static final QName TAG_dsig_Seed = new QName(NS_DSIG, "Seed", PREFIX_DSIG);
    public static final QName TAG_dsig_PgenCounter = new QName(NS_DSIG, "PgenCounter", PREFIX_DSIG);

    public static final String PREFIX_DSIG11 = "dsig11";
    public static final QName TAG_dsig11_ECKeyValue = new QName(NS_DSIG11, "ECKeyValue", PREFIX_DSIG11);
    public static final QName TAG_dsig11_ECParameters = new QName(NS_DSIG11, "ECParameters", PREFIX_DSIG11);
    public static final QName TAG_dsig11_NamedCurve = new QName(NS_DSIG11, "NamedCurve", PREFIX_DSIG11);
    public static final QName TAG_dsig11_PublicKey = new QName(NS_DSIG11, "PublicKey", PREFIX_DSIG11);

    public static final String NS_C14N_EXCL = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String NS_XMLDSIG_FILTER2 = "http://www.w3.org/2002/06/xmldsig-filter2";
    public static final String NS_XMLDSIG_ENVELOPED_SIGNATURE = NS_DSIG + "enveloped-signature";
    public static final String NS_XMLDSIG_SHA1 = NS_DSIG + "sha1";
    public static final String NS_XMLDSIG_HMACSHA1 = NS_DSIG + "hmac-sha1";
    public static final String NS_XMLDSIG_RSASHA1 = NS_DSIG + "rsa-sha1";

    public static final String NS_XENC_TRIBLE_DES = NS_XMLENC + "tripledes-cbc";
    public static final String NS_XENC_AES128 = NS_XMLENC + "aes128-cbc";
    public static final String NS_XENC_AES256 = NS_XMLENC + "aes256-cbc";

    public static final String PREFIX_C14N_EXCL = "c14nEx";
    public static final QName ATT_NULL_PrefixList = new QName(null, "PrefixList");
    public static final QName TAG_c14nExcl_InclusiveNamespaces = new QName(NS_C14N_EXCL, "InclusiveNamespaces", PREFIX_C14N_EXCL);

    public static final String PROP_USE_THIS_TOKEN_ID_FOR_SIGNATURE = "PROP_USE_THIS_TOKEN_ID_FOR_SIGNATURE";
    public static final String PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION = "PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION";
    public static final String PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTED_KEY = "PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTED_KEY";

    public static final String PROP_APPEND_SIGNATURE_ON_THIS_ID = "PROP_APPEND_SIGNATURE_ON_THIS_ID";

    public static final String SIGNATURE_PARTS = "signatureParts";
    public static final String ENCRYPTION_PARTS = "encryptionParts";

    public static final Action SIGNATURE = new Action("SIGNATURE");
    public static final Action ENCRYPT = new Action("ENCRYPT");
    
    public interface KeyIdentifierType {
        
    }
    
    public enum XMLKeyIdentifierType implements KeyIdentifierType {
        KEY_VALUE,
        X509_ISSUER_SERIAL,
        X509_SKI,
        X509_CERTIFICATE,
        X509_SUBJECT_NAME,
        NO_KEY_INFO
    }

    public static class Action implements Comparable<Action> {
        private final String name;

        protected Action(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Action) {
                Action otherAction = (Action) obj;
                if (this.toString().equals(otherAction.toString())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public int compareTo(Action o) {
            return this.toString().compareTo(o.toString());
        }
    }

    public static final KeyUsage Sym_Sig = new KeyUsage("Sym_Sig");
    public static final KeyUsage Asym_Sig = new KeyUsage("Asym_Sig");
    public static final KeyUsage Enc = new KeyUsage("Enc");
    public static final KeyUsage Dig = new KeyUsage("Dig");
    public static final KeyUsage C14n = new KeyUsage("C14n");

    public static class KeyUsage implements Comparable<KeyUsage> {
        private final String name;

        public KeyUsage(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof KeyUsage) {
                KeyUsage otherKeyUsage = (KeyUsage) obj;
                if (this.toString().equals(otherKeyUsage.toString())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public int compareTo(KeyUsage o) {
            return this.toString().compareTo(o.toString());
        }
    }

    public static final TokenType X509V3Token = new TokenType("X509V3Token");
    public static final TokenType X509V1Token = new TokenType("X509V1Token");
    public static final TokenType X509Pkcs7Token = new TokenType("X509Pkcs7Token");
    public static final TokenType X509PkiPathV1Token = new TokenType("X509PkiPathV1Token");
    public static final TokenType KeyValueToken = new TokenType("KeyValueToken");
    public static final TokenType DefaultToken = new TokenType("DefaultToken");
    
    public static class TokenType implements Comparable<TokenType> {
        private final String name;

        public TokenType(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof TokenType) {
                TokenType otherTokenType = (TokenType) obj;
                if (this.toString().equals(otherTokenType.toString())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public int compareTo(TokenType o) {
            return this.toString().compareTo(o.toString());
        }
    }

    public enum ContentType {
        PLAIN,
        SIGNATURE,
        ENCRYPTION
    }
}
