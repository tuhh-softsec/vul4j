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



import java.security.Provider;
import java.security.Security;
import java.security.MessageDigest;
import java.security.Key;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.KeyGenerator;
import javax.crypto.Cipher;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Wrap Triple-DES keys according to
 * <A HREF="http://www.ietf.org/internet-drafts/draft-ietf-smime-key-wrap-01.txt">
 * draft-ietf-smime-key-wrap-01.txt</A>.
 *
 * @author $Author$
 */
public class TripleDESWrapperTest extends TestCase {

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {
      return new TestSuite(TripleDESWrapperTest.class);
   }

   /**
    * Constructor TripleDESWrapperTest
    *
    * @param Name_
    */
   public TripleDESWrapperTest(String Name_) {
      super(Name_);
   }

   /**
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                TripleDESWrapperTest.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method setUp
    *
    */
   public void setUp() {

      try {
         Provider provider =
            (Provider) Class
               .forName("org.bouncycastle.jce.provider.BouncyCastleProvider")
                  .newInstance();

         Security.addProvider(provider);
      } catch (ClassNotFoundException ex) {
         throw new RuntimeException(ex.getMessage());
      } catch (IllegalAccessException ex) {
         throw new RuntimeException(ex.getMessage());
      } catch (InstantiationException ex) {
         throw new RuntimeException(ex.getMessage());
      }
   }

   /**
    * Method wrapUnwrap
    *
    * @param KEK
    * @param keyData
    * @param ciphertext
    * @param IVStr
    * @return
    * @throws Exception
    */
   private boolean wrapUnwrap(
           String KEK, String keyData, String ciphertext, String IVStr)
              throws Exception {

      byte KEKbytes[] = AESWrapperTest.hexStringToByteArray(KEK);
      byte keyDataBytes[] = AESWrapperTest.hexStringToByteArray(keyData);
      byte expectedCiphertextBytes[] =
         AESWrapperTest.hexStringToByteArray(ciphertext);
      byte IV[] = AESWrapperTest.hexStringToByteArray(IVStr);
      IvParameterSpec ivParamSpec = new IvParameterSpec(IV);
      Cipher tripleDESWrapper = Cipher.getInstance("DESEDEWrap", "BC");

      tripleDESWrapper.init(Cipher.WRAP_MODE,
                            new SecretKeySpec(KEKbytes, "DESEDE"), ivParamSpec);

      Key keyDataKey = new SecretKeySpec(keyDataBytes, "DESEDE");
      byte realCipherTextBytes[] = tripleDESWrapper.wrap(keyDataKey);

      if (!MessageDigest.isEqual(realCipherTextBytes,
                                 expectedCiphertextBytes)) {
         return false;
      }

      tripleDESWrapper.init(Cipher.UNWRAP_MODE,
                            new SecretKeySpec(KEKbytes, "DESEDE"));

      Key plaintextKey = tripleDESWrapper.unwrap(realCipherTextBytes, "DESEDE",
                                                 Cipher.SECRET_KEY);
      byte realPlainTextBytes[] = plaintextKey.getEncoded();

      if (!MessageDigest.isEqual(realPlainTextBytes, keyDataBytes)) {
         return false;
      }

      return true;
   }

   /**
    * Method test34
    *
    * @throws Exception
    */
   public void test34() throws Exception {
      //J-
       String CEK    = "2923 bf85 e06d d6ae 5291 49f1 f1ba e9ea b3a7 da3d 860d 3e98";
       String KEK    = "255e 0d1c 07b6 46df b313 4cc8 43ba 8aa7 1f02 5b7c 0838 251f";
       String IV     = "5dd4 cbfc 96f5 453b";
       String RESULT = "6901 0761 8ef0 92b3 b48c a179 6b23 4ae9 fa33 ebb4 1596 0403 " +
                       "7db5 d6a8 4eb3 aac2 768c 6327 75a4 67d4";

       assertTrue("3.4  Triple-DES Key Wrap Example", wrapUnwrap(KEK, CEK, RESULT, IV));
       //J+
   }
}
