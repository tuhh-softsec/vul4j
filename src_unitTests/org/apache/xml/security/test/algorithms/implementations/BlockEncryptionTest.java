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
package org.apache.xml.security.test.algorithms.implementations;



import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xml.security.algorithms.encryption.*;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.HexDump;
import java.security.Key;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.MessageDigest;
import junit.framework.*;


/**
 *
 * @author $Author$
 */
public class BlockEncryptionTest extends TestCase {
   //J-
   static final String AES_NULL_IV        = "0000000000000000 0000000000000000";
   static final String AES_PLAIN_TEXT     = "0011223344556677 8899aabbccddeeff";
   static final int FAILURE_OK = 0;
   static final int FAILURE_ENCRYPT = 1;
   static final int FAILURE_DECRYPT = 2;
   //J+

   /** Field doc */
   Document doc;

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {
      return new TestSuite(BlockEncryptionTest.class);
   }

   /**
    * Constructor BlockEncryptionTest
    *
    * @param Name_
    */
   public BlockEncryptionTest(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                BlockEncryptionTest.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method setUp
    *
    * @throws Exception
    */
   public void setUp() throws Exception {

      org.apache.xml.security.Init.init();

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();

      this.doc = db.newDocument();

      this.doc.appendChild(doc.createElement("root"));
   }

   /**
    * Method encryptDecrypt
    *
    * @param keyStr
    * @param keyType
    * @param plainStr
    * @param cipherStr
    * @param IVStr
    * @return
    * @throws Exception
    */
   int encryptDecrypt(
           String keyStr, String keyType, String plainStr, String cipherStr, String IVStr)
              throws Exception {
      //J-
      byte keyBytes[]   = HexDump.hexStringToByteArray(keyStr);
      byte plainText[]  = HexDump.hexStringToByteArray(plainStr);
      byte cipherText[] = HexDump.hexStringToByteArray(cipherStr);
      byte IV[]         = HexDump.hexStringToByteArray(IVStr);
      //J+
      SecretKeySpec key = new SecretKeySpec(keyBytes, keyType);

      String keyURI = JCEMapper.getURIfromKey(key, JCEMapper.KEYTYPE_BLOCK_ENCRYPTION);
      EncryptionMethod bea = new EncryptionMethod(doc, keyURI);

      byte realciphertext[] = bea.encrypt(plainText, key, IV);
      byte decrypted[] = bea.decrypt(realciphertext, key);

      // we cannot compare the whole ciphertext because the padding bytes always look different.
      int beaBlockSize = bea.getBlockSize();

      if (realciphertext.length - beaBlockSize != cipherText.length) {
         return FAILURE_ENCRYPT;
      }

      int iMax = realciphertext.length - beaBlockSize;
      for (int i = 0; i < iMax; i++) {
         if (realciphertext[i] != cipherText[i]) {
            return FAILURE_ENCRYPT;
         }
      }

      if (!MessageDigest.isEqual(plainText, decrypted)) {
         return FAILURE_DECRYPT;
      }

      return FAILURE_OK;
   }

   /**
    * Method test_AES128
    *
    * @throws Exception
    */
   public void test_AES128() throws Exception {

      String AES128_KEY = "0001020304050607 08090a0b0c0d0e0f";
      String AES128_CIPHER_TEXT_SINGLE_BLOCK =
         "69c4e0d86a7b0430 d8cdb78070b4c55a";
      String AES128_CIPHER_TEXT_WITH_NULL_IV_WITHOUT_PADDING =
         AES_NULL_IV + AES128_CIPHER_TEXT_SINGLE_BLOCK;
      int result =
         encryptDecrypt(AES128_KEY, "AES", AES_PLAIN_TEXT,
                        AES128_CIPHER_TEXT_WITH_NULL_IV_WITHOUT_PADDING,
                        AES_NULL_IV);
      String errorStr = "";

      if (result == FAILURE_ENCRYPT) {
         errorStr += " error during encryption";
      } else if (result == FAILURE_DECRYPT) {
         errorStr += " error during decryption";
      }

      assertTrue(errorStr, result == FAILURE_OK);
   }

   /**
    * Method test_AES192
    *
    * @throws Exception
    */
   public void test_AES192() throws Exception {

      String AES192_KEY = "0001020304050607 08090a0b0c0d0e0f 1011121314151617";
      String AES192_CIPHER_TEXT_SINGLE_BLOCK =
         "dda97ca4864cdfe0 6eaf70a0ec0d7191";
      String AES192_CIPHER_TEXT_WITH_NULL_IV_WITHOUT_PADDING =
         AES_NULL_IV + AES192_CIPHER_TEXT_SINGLE_BLOCK;
      int result =
         encryptDecrypt(AES192_KEY, "AES", AES_PLAIN_TEXT,
                        AES192_CIPHER_TEXT_WITH_NULL_IV_WITHOUT_PADDING,
                        AES_NULL_IV);
      String errorStr = "";

      if (result == FAILURE_ENCRYPT) {
         errorStr += " error during encryption";
      } else if (result == FAILURE_DECRYPT) {
         errorStr += " error during decryption";
      }

      assertTrue(errorStr, result == FAILURE_OK);
   }

   /**
    * Method test_AES256
    *
    * @throws Exception
    */
   public void test_AES256() throws Exception {

      String AES256_KEY =
         "0001020304050607 08090a0b0c0d0e0f 1011121314151617 18191a1b1c1d1e1f";
      String AES256_CIPHER_TEXT_SINGLE_BLOCK =
         "8ea2b7ca516745bf eafc49904b496089";
      String AES256_CIPHER_TEXT_WITH_NULL_IV_WITHOUT_PADDING =
         AES_NULL_IV + AES256_CIPHER_TEXT_SINGLE_BLOCK;
      int result =
         encryptDecrypt(AES256_KEY, "AES", AES_PLAIN_TEXT,
                        AES256_CIPHER_TEXT_WITH_NULL_IV_WITHOUT_PADDING,
                        AES_NULL_IV);
      String errorStr = "";

      if (result == FAILURE_ENCRYPT) {
         errorStr += " error during encryption";
      } else if (result == FAILURE_DECRYPT) {
         errorStr += " error during decryption";
      }

      assertTrue(errorStr, result == FAILURE_OK);
   }

   /**
    * Method test_3DES
    *
    * @throws Exception
    * @todo find the normative test vector for 3DES
    */
   public void test_3DES() throws Exception {

      /*
      String TRIPLEDES_KEY =
         "2923 bf85 e06d d6ae 5291 49f1 f1ba e9ea b3a7 da3d 860d 3e98";
      int result = encryptDecrypt(TRIPLEDES_KEY, "DESEDE",
                                  "00 11 22 33 44 55 66 77", "",
                                  "00000000 00000000");
      String errorStr = "";

      if (result == FAILURE_ENCRYPT) {
         errorStr += " error during encryption";
      } else if (result == FAILURE_DECRYPT) {
         errorStr += " error during decryption";
      }

      assertTrue(errorStr, result == FAILURE_OK);
      */
   }
}
