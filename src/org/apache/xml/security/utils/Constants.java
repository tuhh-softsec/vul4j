/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.security.utils;



import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;


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

   /** Log4j logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(Constants.class.getName());

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
    * @todo Add consistency checking for valid prefix
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

   static {
      org.apache.xml.security.Init.init();
   }
}
