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
 *
 * @author $Author$
 */
public class EncryptionConstants {
   //J-
   // Attributes that exist in XML Signature in the same way
   public static final String _ATT_ALGORITHM              = Constants._ATT_ALGORITHM;
   public static final String _ATT_ID                     = Constants._ATT_ID;
   public static final String _ATT_TARGET                 = Constants._ATT_TARGET;
   public static final String _ATT_TYPE                   = Constants._ATT_TYPE;
   public static final String _ATT_URI                    = Constants._ATT_URI;

   // Attributes new in XML Encryption
   public static final String _ATT_ENCODING               = "Encoding";
   public static final String _ATT_RECIPIENT              = "Recipient";
   public static final String _ATT_MIMETYPE               = "MimeType";

   public static final String _TAG_CARRIEDKEYNAME         = "CarriedKeyName";
   public static final String _TAG_CIPHERDATA             = "CipherData";
   public static final String _TAG_CIPHERREFERENCE        = "CipherReference";
   public static final String _TAG_CIPHERVALUE            = "CipherValue";
   public static final String _TAG_DATAREFERENCE          = "DataReference";
   public static final String _TAG_ENCRYPTEDDATA          = "EncryptedData";
   public static final String _TAG_ENCRYPTEDKEY           = "EncryptedKey";
   public static final String _TAG_ENCRYPTIONMETHOD       = "EncryptionMethod";
   public static final String _TAG_ENCRYPTIONPROPERTIES   = "EncryptionProperties";
   public static final String _TAG_ENCRYPTIONPROPERTY     = "EncryptionProperty";
   public static final String _TAG_KEYREFERENCE           = "KeyReference";
   public static final String _TAG_KEYSIZE                = "KeySize";
   public static final String _TAG_OAEPPARAMS             = "OAEPparams";
   public static final String _TAG_REFERENCELIST          = "ReferenceList";
   public static final String _TAG_TRANSFORMS             = "Transforms";
   public static final String _TAG_AGREEMENTMETHOD        = "AgreementMethod";
   public static final String _TAG_KA_NONCE               = "KA-Nonce";
   public static final String _TAG_ORIGINATORKEYINFO      = "OriginatorKeyInfo";
   public static final String _TAG_RECIPIENTKEYINFO       = "RecipientKeyInfo";

   /** Field ENCRYPTIONSPECIFICATION_URL */
   public static final String ENCRYPTIONSPECIFICATION_URL = "http://www.w3.org/TR/2001/WD-xmlenc-core-20010626/";

   /** The namespace of the <A HREF="http://www.w3.org/TR/2001/WD-xmlenc-core-20010626/">XML Encryption Syntax and Processing</A> */
   public static final String EncryptionSpecNS = "http://www.w3.org/2001/04/xmlenc#";

   public static final String TYPE_CONTENT                = EncryptionSpecNS + "Content";
   public static final String TYPE_ELEMENT                = EncryptionSpecNS + "Element";
   public static final String TYPE_MEDIATYPE              = "http://www.isi.edu/in-notes/iana/assignments/media-types/"; // + "*/*";

   // Block Encryption - REQUIRED TRIPLEDES
   public static final String ALGO_ID_BLOCKCIPHER_TRIPLEDES = EncryptionConstants.EncryptionSpecNS + "tripledes-cbc";
   // Block Encryption - REQUIRED AES-128
   public static final String ALGO_ID_BLOCKCIPHER_AES128 = EncryptionConstants.EncryptionSpecNS + "aes128-cbc";
   // Block Encryption - REQUIRED AES-256
   public static final String ALGO_ID_BLOCKCIPHER_AES256 = EncryptionConstants.EncryptionSpecNS + "aes256-cbc";
   // Block Encryption - OPTIONAL AES-192
   public static final String ALGO_ID_BLOCKCIPHER_AES192 = EncryptionConstants.EncryptionSpecNS + "aes192-cbc";

   // Key Transport - REQUIRED RSA-v1.5
   public static final String ALGO_ID_KEYTRANSPORT_RSA15 = EncryptionConstants.EncryptionSpecNS + "rsa-1_5";
   // Key Transport - REQUIRED RSA-OAEP
   public static final String ALGO_ID_KEYTRANSPORT_RSAOAEP = EncryptionConstants.EncryptionSpecNS + "rsa-oaep-mgf1p";

   // Key Agreement - OPTIONAL Diffie-Hellman
   public static final String ALGO_ID_KEYAGREEMENT_DH = EncryptionConstants.EncryptionSpecNS + "dh";

   // Symmetric Key Wrap - REQUIRED TRIPLEDES KeyWrap
   public static final String ALGO_ID_KEYWRAP_TRIPLEDES = EncryptionConstants.EncryptionSpecNS + "kw-tripledes";
   // Symmetric Key Wrap - REQUIRED AES-128 KeyWrap
   public static final String ALGO_ID_KEYWRAP_AES128 = EncryptionConstants.EncryptionSpecNS + "kw-aes128";
   // Symmetric Key Wrap - REQUIRED AES-256 KeyWrap
   public static final String ALGO_ID_KEYWRAP_AES256 = EncryptionConstants.EncryptionSpecNS + "kw-aes256";
   // Symmetric Key Wrap - OPTIONAL AES-192 KeyWrap
   public static final String ALGO_ID_KEYWRAP_AES192 = EncryptionConstants.EncryptionSpecNS + "kw-aes192";

   /*
   // Message Digest - REQUIRED SHA1
   public static final String ALGO_ID_DIGEST_SHA160 = Constants.ALGO_ID_DIGEST_SHA1;
   // Message Digest - RECOMMENDED SHA256
   public static final String ALGO_ID_DIGEST_SHA256 = EncryptionConstants.EncryptionSpecNS + "sha256";
   // Message Digest - OPTIONAL SHA512
   public static final String ALGO_ID_DIGEST_SHA512 = EncryptionConstants.EncryptionSpecNS + "sha512";
   // Message Digest - OPTIONAL RIPEMD-160
   public static final String ALGO_ID_DIGEST_RIPEMD160 = EncryptionConstants.EncryptionSpecNS + "ripemd160";
   */

   // Message Authentication - RECOMMENDED XML Digital Signature
   public static final String ALGO_ID_AUTHENTICATION_XMLSIGNATURE = "http://www.w3.org/TR/2001/CR-xmldsig-core-20010419/";

   // Canonicalization - OPTIONAL Canonical XML with Comments
   public static final String ALGO_ID_C14N_WITHCOMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
   // Canonicalization - OPTIONAL Canonical XML (omits comments)
   public static final String ALGO_ID_C14N_OMITCOMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

   // Encoding - REQUIRED base64
   public static final String ALGO_ID_ENCODING_BASE64 = "http://www.w3.org/2000/09/xmldsig#base64";
   //J+

   private EncryptionConstants() {
     // we don't allow instantiation
   }

   /**
    * Method setEncryptionSpecNSprefix
    *
    * @param newPrefix
    * @throws XMLSecurityException
    */
   public static void setEncryptionSpecNSprefix(String newPrefix)
           throws XMLSecurityException {
      ElementProxy.setDefaultPrefix(EncryptionConstants.EncryptionSpecNS,
                                    newPrefix);
   }

   /**
    * Method getEncryptionSpecNSprefix
    *
    *
    */
   public static String getEncryptionSpecNSprefix() {
      return ElementProxy
         .getDefaultPrefix(EncryptionConstants.EncryptionSpecNS);
   }
}
