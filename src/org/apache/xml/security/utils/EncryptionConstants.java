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

/**
 *
 * @author $Author$
 */
public class EncryptionConstants {
   //J-
   public static final String _ATT_ID                     = Constants._ATT_ID;
   public static final String _ATT_TYPE                   = Constants._ATT_TYPE;
   public static final String _ATT_NONCE                  = "Nonce";
   public static final String _ATT_CarriedKeyName         = "CarriedKeyName";
   public static final String _ATT_Recipient              = "Recipient";
   public static final String _ATT_ALGORITHM              = Constants._ATT_ALGORITHM;
   public static final String _ATT_URI                    = Constants._ATT_URI;
   public static final String _ATT_ENCODING               = "Encoding";

   public static final String _TAG_CIPHERDATA             = "CipherData";
   public static final String _TAG_CIPHERREFERENCE        = "CipherReference";
   public static final String _TAG_DATAREFERENCE          = "DataReference";
   public static final String _TAG_ENCRYPTIONMETHOD       = "EncryptionMethod";
   public static final String _TAG_ENCRYPTIONPROPERTIES   = "EncryptionProperties";
   public static final String _TAG_KEYREFERENCE           = "KeyReference";
   public static final String _TAG_KEYSIZE                = "KeySize";
   public static final String _TAG_OAEPPARAMS             = "OAEPparams";
   public static final String _TAG_REFERENCELIST          = "ReferenceList";
   public static final String _TAG_TRANSFORMS             = "Transforms";

   /** Field ENCRYPTIONSPECIFICATION_URL */
   public static final String ENCRYPTIONSPECIFICATION_URL = "http://www.w3.org/TR/2001/WD-xmlenc-core-20010626/";

   /** The namespace of the <A HREF="http://www.w3.org/TR/2001/WD-xmlenc-core-20010626/">XML Encryption Syntax and Processing</A> */
   public static final String EncryptionSpecNS = "http://www.w3.org/2001/04/xmlenc#";

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
   public static final String ALGO_ID_KEYAGREEMENT_SH = EncryptionConstants.EncryptionSpecNS + "dh";

   // Symmetric Key Wrap - REQUIRED TRIPLEDES KeyWrap
   public static final String ALGO_ID_KEYWRAP_TRIPLEDES = EncryptionConstants.EncryptionSpecNS + "kw-tripledes";
   // Symmetric Key Wrap - REQUIRED AES-128 KeyWrap
   public static final String ALGO_ID_KEYWRAP_AES128 = EncryptionConstants.EncryptionSpecNS + "kw-aes128";
   // Symmetric Key Wrap - REQUIRED AES-256 KeyWrap
   public static final String ALGO_ID_KEYWRAP_AES256 = EncryptionConstants.EncryptionSpecNS + "kw-aes256";
   // Symmetric Key Wrap - OPTIONAL AES-192 KeyWrap
   public static final String ALGO_ID_KEYWRAP_AES192 = EncryptionConstants.EncryptionSpecNS + "kw-aes192";

   // Message Digest - REQUIRED SHA1
   public static final String ALGO_ID_DIGEST_SHA160 = Constants.ALGO_ID_DIGEST_SHA1;
   // Message Digest - RECOMMENDED SHA256
   public static final String ALGO_ID_DIGEST_SHA256 = EncryptionConstants.EncryptionSpecNS + "sha256";
   // Message Digest - OPTIONAL SHA512
   public static final String ALGO_ID_DIGEST_SHA512 = EncryptionConstants.EncryptionSpecNS + "sha512";
   // Message Digest - OPTIONAL RIPEMD-160
   public static final String ALGO_ID_DIGEST_RIPEMD160 = EncryptionConstants.EncryptionSpecNS + "ripemd160";

   // Message Authentication - RECOMMENDED XML Digital Signature
   public static final String ALGO_ID_AUTHENTICATION_XMLSIGNATURE = "http://www.w3.org/TR/2001/CR-xmldsig-core-20010419/";

   // Canonicalization - OPTIONAL Canonical XML with Comments
   public static final String ALGO_ID_C14N_WITHCOMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
   // Canonicalization - OPTIONAL Canonical XML (omits comments)
   public static final String ALGO_ID_C14N_OMITCOMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

   // Encoding - REQUIRED base64
   public static final String ALGO_ID_ENCODING_BASE64 = "http://www.w3.org/2000/09/xmldsig#base64";
   //J+

   /**
    * The namespace prefix which is used by default is <CODE>xenc</CODE>.
    */
   static final String DEFAULTENCRYPTIONNSPREFIX = "xenc";

   /** Field SignatureSpecNSprefix */
   static String _encryptionSpecNSprefix = DEFAULTENCRYPTIONNSPREFIX;

   /**
    * Method setEncryptionSpecNSprefix
    *
    * @param newPrefix
    */
   public static void setEncryptionSpecNSprefix(String newPrefix) {
      EncryptionConstants._encryptionSpecNSprefix = newPrefix;
   }

   /**
    * Method getEncryptionSpecNSprefix
    *
    * @return
    */
   public static String getEncryptionSpecNSprefix() {
      return EncryptionConstants._encryptionSpecNSprefix;
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
