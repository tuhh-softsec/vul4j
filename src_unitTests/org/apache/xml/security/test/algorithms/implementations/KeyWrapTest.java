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
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.MessageDigest;
import junit.framework.*;


/**
 * Test cases for the KeyWrap algorithms
 *
 * @author $Author$
 */
public class KeyWrapTest extends TestCase {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(KeyWrapTest.class.getName());

   /** Field FAILURE_OK */
   static final int FAILURE_OK = 0;

   /** Field FAILURE_WRAP */
   static final int FAILURE_WRAP = 1;

   /** Field FAILURE_UNWRAP */
   static final int FAILURE_UNWRAP = 2;

   static final int FAILURE_USABLE_ENCRYPTION = 3;

   /** Field doc */
   Document doc;

   /**
    * Method suite
    *
    *
    */
   public static Test suite() {
      return new TestSuite(KeyWrapTest.class);
   }

   /**
    * Constructor KeyWrapTest
    *
    * @param Name_
    */
   public KeyWrapTest(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading", KeyWrapTest.class.getName() };

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

      this.doc.appendChild(doc.createElementNS(null, "root"));
   }

   /**
    * Method wrapUnwrap
    *
    * @param KEKstr
    * @param KEKtype
    * @param CEKstr
    * @param CEKtype
    * @param WrappedStr
    *
    * @throws Exception
    */
   int wrapUnwrap(
           String KEKstr, String KEKtype, String CEKstr, String CEKtype, String WrappedStr)
              throws Exception {
      return this.wrapUnwrap(KEKstr, KEKtype, CEKstr, CEKtype, WrappedStr,
                             null);
   }

   /**
    * Method wrapUnwrap
    *
    * @param KEKstr
    * @param KEKtype
    * @param CEKstr
    * @param CEKtype
    * @param WrappedStr
    * @param IV
    *
    * @throws Exception
    */
   int wrapUnwrap(
           String KEKstr, String KEKtype, String CEKstr, String CEKtype, String WrappedStr, byte IV[])
              throws Exception {

      byte CEK[] = HexDump.hexStringToByteArray(CEKstr);
      SecretKeySpec cek = new SecretKeySpec(CEK, CEKtype);
      byte KEK[] = HexDump.hexStringToByteArray(KEKstr);
      SecretKeySpec kek = new SecretKeySpec(KEK, KEKtype);
      String kekURI =
         JCEMapper.getURIfromKey(kek, JCEMapper.KEYTYPE_SYMMETRIC_KEY_WRAP);
      EncryptionMethod kwa = new EncryptionMethod(doc, kekURI);
      byte result[] = kwa.wrap(cek, kek, IV);

      if (!MessageDigest.isEqual(result,
                                 HexDump.hexStringToByteArray(WrappedStr))) {
         cat.info("Wrap failed: " + HexDump.byteArrayToHexString(result));
         return FAILURE_WRAP;
      }

      String cekURI =
         JCEMapper.getURIfromKey(cek, JCEMapper.KEYTYPE_BLOCK_ENCRYPTION);
      SecretKeySpec unwrapped = (SecretKeySpec) kwa.unwrap(result, kek, cekURI);

      if (!MessageDigest.isEqual(unwrapped.getEncoded(), CEK)) {
         cat.info("Unwrap failed: " + HexDump.byteArrayToHexString(unwrapped.getEncoded()));
         return FAILURE_UNWRAP;
      }

      boolean exceptionGot = false;
      try {
         kwa.encrypt(".sfsd".getBytes(), kek);
      } catch (Exception ex) {
         exceptionGot = true;
      }

      if (!exceptionGot) {
         return FAILURE_USABLE_ENCRYPTION;
      }

      return FAILURE_OK;
   }

   /**
    * Method test_AES_41
    *
    * @throws Exception
    */
   public void test_AES_41() throws Exception {
      //J-
       assertTrue("Wrap 128 bits of Key Data with a 128-bit KEK", FAILURE_OK ==

       wrapUnwrap("0001020304050607 08090A0B0C0D0E0F", "AES",
                  "0011223344556677 8899AABBCCDDEEFF", "AES",
                  "1FA68B0A8112B447 AEF34BD8FB5A7B82 9D3E862371D2CFE5"));
       //J+
   }

   /**
    * Method test_AES_42
    *
    * @throws Exception
    */
   public void test_AES_42() throws Exception {
      //J-
       assertTrue("4.2 Wrap 128 bits of Key Data with a 192-bit KEK",FAILURE_OK ==
       wrapUnwrap("00010203040506070 8090A0B0C0D0E0F 1011121314151617","AES",
                  "0011223344556677 8899AABBCCDDEEFF","AES",
                  "96778B25AE6CA435 F92B5B97C050AED2 468AB8A17AD84E5D"));

       //J+
   }

   /**
    * Method test_AES_43
    *
    * @throws Exception
    */
   public void test_AES_43() throws Exception {
      //J-
       assertTrue("4.3 Wrap 128 bits of Key Data with a 256-bit KEK",FAILURE_OK ==
       wrapUnwrap("0001020304050607 08090A0B0C0D0E0F 1011121314151617 18191A1B1C1D1E1F","AES",
                  "0011223344556677 8899AABBCCDDEEFF","AES",
                  "64E8C3F9CE0F5BA2 63E9777905818A2A 93C8191E7D6E8AE7"));

       //J+
   }

   /**
    * Method test_AES_44
    *
    * @throws Exception
    */
   public void test_AES_44() throws Exception {
      //J-
       assertTrue("4.4 Wrap 192 bits of Key Data with a 192-bit KEK",FAILURE_OK ==
       wrapUnwrap("0001020304050607 08090A0B0C0D0E0F 1011121314151617","AES",
                  "0011223344556677 8899AABBCCDDEEFF 0001020304050607","AES",
                  "031D33264E15D332 68F24EC260743EDC E1C6C7DDEE725A93 6BA814915C6762D2"));

       //J+
   }

   /**
    * Method test_AES_45
    *
    * @throws Exception
    */
   public void test_AES_45() throws Exception {
      //J-
       assertTrue("4.5 Wrap 192 bits of Key Data with a 256-bit KEK",FAILURE_OK ==
       wrapUnwrap("0001020304050607 08090A0B0C0D0E0F 1011121314151617 18191A1B1C1D1E1F","AES",
                  "0011223344556677 8899AABBCCDDEEFF 0001020304050607","AES",
                  "A8F9BC1612C68B3F F6E6F4FBE30E71E4 769C8B80A32CB895 8CD5D17D6B254DA1"));

       //J+
   }

   /**
    * Method test_AES_46
    *
    * @throws Exception
    */
   public void test_AES_46() throws Exception {
      //J-
       assertTrue("4.6 Wrap 256 bits of Key Data with a 256-bit KEK",FAILURE_OK ==
       wrapUnwrap("0001020304050607 08090A0B0C0D0E0F 1011121314151617 18191A1B1C1D1E1F","AES",
                  "0011223344556677 8899AABBCCDDEEFF 0001020304050607 08090A0B0C0D0E0F","AES",
                  "28C9F404C4B810F4 CBCCB35CFB87F826 3F5786E2D80ED326 CBC7F0E71A99F43B FB988B9B7A02DD21"));

       //J+
   }

   /**
    * Method test_3DES
    *
    * @throws Exception
    */
   public void test_3DES() throws Exception {
      //J-
       assertTrue("TripleDES Wrap",FAILURE_OK ==
       wrapUnwrap("255e 0d1c 07b6 46df b313 4cc8 43ba 8aa7 1f02 5b7c 0838 251f", "DESEDE",
                  "2923 bf85 e06d d6ae 5291 49f1 f1ba e9ea b3a7 da3d 860d 3e98", "DESEDE",
                  "6901 0761 8ef0 92b3 b48c a179 6b23 4ae9 fa33 ebb4 1596 0403 " +
                  "7db5 d6a8 4eb3 aac2 768c 6327 75a4 67d4",
                  HexDump.hexStringToByteArray("5dd4 cbfc 96f5 453b")));

       //J+
   }
}
