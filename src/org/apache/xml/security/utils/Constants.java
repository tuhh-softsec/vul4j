/*
 * Copyright  1999-2004 The Apache Software Foundation.
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
package org.apache.xml.security.utils;



import org.apache.xml.security.exceptions.XMLSecurityException;


/**
 * Provides all constants and some translation functions for i18n.
 *
 * For the used Algorithm identifiers and Namespaces, look at the
 * <A HREF="http://www.w3.org/TR/xmldsig-core/#sec-TransformAlg">XML
 * Signature specification</A>.
 *
 * @author $Author$
 */
public class Constants {

   /** {@link org.apache.commons.logging} logging facility */
   static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(Constants.class.getName());

   /** Field configurationFile */
   public static String configurationFile = "data/websig.conf";

   /** Field configurationFileNew */
   public static final String configurationFileNew = ".xmlsecurityconfig";

   /** Field exceptionMessagesResourceBundleDir */
   public static final String exceptionMessagesResourceBundleDir =
      "org/apache/xml/security/resource";

   /** Field exceptionMessagesResourceBundleBase is the location of the <CODE>ResourceBundle</CODE> */
   public static final String exceptionMessagesResourceBundleBase =
      exceptionMessagesResourceBundleDir + "/" + "xmlsecurity";
   //J-
   /**
    * The URL of the <A HREF="http://www.w3.org/TR/2001/CR-xmldsig-core-20010419/">XML Signature specification</A>
    */
   public static final String SIGNATURESPECIFICATION_URL = "http://www.w3.org/TR/2001/CR-xmldsig-core-20010419/";

   /**
    * The namespace of the <A HREF="http://www.w3.org/TR/2001/CR-xmldsig-core-20010419/">XML Signature specification</A>
    */
   public static final String SignatureSpecNS   = "http://www.w3.org/2000/09/xmldsig#";
   public static final String MoreAlgorithmsSpecNS   = "http://www.w3.org/2001/04/xmldsig-more#";

   public static final String XML_LANG_SPACE_SpecNS = "http://www.w3.org/XML/1998/namespace";
   public static final String NamespaceSpecNS = "http://www.w3.org/2000/xmlns/";

   public static final String _ATT_ALGORITHM              = "Algorithm";
   public static final String _ATT_URI                    = "URI";
   public static final String _ATT_TYPE                   = "Type";
   public static final String _ATT_ID                     = "Id";
   public static final String _ATT_MIMETYPE               = "MimeType";
   public static final String _ATT_ENCODING               = "Encoding";
   public static final String _ATT_TARGET                 = "Target";

   // KeyInfo (KeyName|KeyValue|RetrievalMethod|X509Data|PGPData|SPKIData|MgmtData)
   // KeyValue (DSAKeyValue|RSAKeyValue)
   // DSAKeyValue (P, Q, G, Y, J?, (Seed, PgenCounter)?)
   // RSAKeyValue (Modulus, Exponent)
   // RetrievalMethod (Transforms?)
   // X509Data ((X509IssuerSerial | X509SKI | X509SubjectName | X509Certificate)+ | X509CRL)
   // X509IssuerSerial (X509IssuerName, X509SerialNumber)
   // PGPData ((PGPKeyID, PGPKeyPacket?) | (PGPKeyPacket))
   // SPKIData (SPKISexp)

   public static final String _TAG_CANONICALIZATIONMETHOD = "CanonicalizationMethod";
   public static final String _TAG_DIGESTMETHOD           = "DigestMethod";
   public static final String _TAG_DIGESTVALUE            = "DigestValue";
   public static final String _TAG_MANIFEST               = "Manifest";
   public static final String _TAG_METHODS                = "Methods";
   public static final String _TAG_OBJECT                 = "Object";
   public static final String _TAG_REFERENCE              = "Reference";
   public static final String _TAG_SIGNATURE              = "Signature";
   public static final String _TAG_SIGNATUREMETHOD        = "SignatureMethod";
   public static final String _TAG_HMACOUTPUTLENGTH       = "HMACOutputLength";
   public static final String _TAG_SIGNATUREPROPERTIES    = "SignatureProperties";
   public static final String _TAG_SIGNATUREPROPERTY      = "SignatureProperty";
   public static final String _TAG_SIGNATUREVALUE         = "SignatureValue";
   public static final String _TAG_SIGNEDINFO             = "SignedInfo";
   public static final String _TAG_TRANSFORM              = "Transform";
   public static final String _TAG_TRANSFORMS             = "Transforms";
   public static final String _TAG_XPATH                  = "XPath";

   public static final String _TAG_KEYINFO                = "KeyInfo";

   public static final String _TAG_KEYNAME                = "KeyName";
   public static final String _TAG_KEYVALUE               = "KeyValue";
   public static final String _TAG_RETRIEVALMETHOD        = "RetrievalMethod";
   public static final String _TAG_X509DATA               = "X509Data";
   public static final String _TAG_PGPDATA                = "PGPData";
   public static final String _TAG_SPKIDATA               = "SPKIData";
   public static final String _TAG_MGMTDATA               = "MgmtData";

   public static final String _TAG_RSAKEYVALUE            = "RSAKeyValue";
   public static final String _TAG_EXPONENT               = "Exponent";
   public static final String _TAG_MODULUS                = "Modulus";

   public static final String _TAG_DSAKEYVALUE            = "DSAKeyValue";
   public static final String _TAG_P                      = "P";
   public static final String _TAG_Q                      = "Q";
   public static final String _TAG_G                      = "G";
   public static final String _TAG_Y                      = "Y";
   public static final String _TAG_J                      = "J";
   public static final String _TAG_SEED                   = "Seed";
   public static final String _TAG_PGENCOUNTER            = "PgenCounter";

   public static final String _TAG_RAWX509CERTIFICATE     = "rawX509Certificate";
   public static final String _TAG_X509ISSUERSERIAL       = "X509IssuerSerial";
   public static final String _TAG_X509SKI                = "X509SKI";
   public static final String _TAG_X509SUBJECTNAME        = "X509SubjectName";
   public static final String _TAG_X509CERTIFICATE        = "X509Certificate";
   public static final String _TAG_X509CRL                = "X509CRL";
   public static final String _TAG_X509ISSUERNAME         = "X509IssuerName";
   public static final String _TAG_X509SERIALNUMBER       = "X509SerialNumber";
   public static final String _TAG_PGPKEYID               = "PGPKeyID";
   public static final String _TAG_PGPKEYPACKET           = "PGPKeyPacket";
   public static final String _TAG_SPKISEXP               = "SPKISexp";

   // Digest - Required SHA1
   public static final String ALGO_ID_DIGEST_SHA1        = SignatureSpecNS + "sha1";

   /**
    * @see <A HREF="http://www.ietf.org/internet-drafts/draft-blake-wilson-xmldsig-ecdsa-02.txt">
    *  draft-blake-wilson-xmldsig-ecdsa-02.txt</A>
    */
   public static final String ALGO_ID_SIGNATURE_ECDSA_CERTICOM = "http://www.certicom.com/2000/11/xmlecdsig#ecdsa-sha1";
   //J+

   private Constants() {
     // we don't allow instantiation
   }

   /**
    * Sets the namespace prefix which will be used to identify elements in the
    * XML Signature Namespace.
    *
    * <pre>
    * Constants.setSignatureSpecNSprefix("dsig");
    * </pre>
    *
    * @param newPrefix is the new namespace prefix.
    * @see org.apache.xml.security.utils.Constants#getSignatureSpecNSprefix
    * $todo$ Add consistency checking for valid prefix
    */
   public static void setSignatureSpecNSprefix(String newPrefix) throws XMLSecurityException {
      ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, newPrefix);
   }

   /**
    * Returns the XML namespace prefix which is used for elements in the XML
    * Signature namespace.
    *
    * It is defaulted to <code>dsig</code>, but can be changed using the
    * {@link #setSignatureSpecNSprefix} function.
    *
    * @return the current used namespace prefix
    * @see #setSignatureSpecNSprefix
    */
   public static String getSignatureSpecNSprefix() {
      return ElementProxy.getDefaultPrefix(Constants.SignatureSpecNS);
   }
}
