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
package org.apache.xml.security.algorithms.encryption.helper;



import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.*;


/**
 *
 * @author $Author$
 */
public class TripleDESWrapper {

   /** Field digest */
   byte[] digest = new byte[20];

   /** Field iv */
   byte[] iv;

   /** Field IV2 */
   private static final byte[] IV2 = { (byte) 0x4a, (byte) 0xdd, (byte) 0xa2,
                                       (byte) 0x2c, (byte) 0x79, (byte) 0xe8,
                                       (byte) 0x21, (byte) 0x05 };

   /** Field _cipher */
   Cipher _cipher;

   /** Field _digest */
   MessageDigest _digest;

   /**
    * Constructor TripleDESWrapper
    *
    * @param tripleDesECBCipher
    * @param sha1
    */
   public TripleDESWrapper(Cipher tripleDesECBCipher, MessageDigest sha1) {
      this._cipher = tripleDesECBCipher;
      this._digest = sha1;
   }

   /**
    * Method wrap
    *
    * @param keyToBeWrapped
    * @param wrapKey
    * @param IV
    * @return
    * @throws IllegalBlockSizeException
    * @throws InvalidAlgorithmParameterException
    * @throws InvalidKeyException
    * @throws XMLSecurityException
    */
   public byte[] wrap(byte[] keyToBeWrapped, Key wrapKey, byte[] IV)
           throws InvalidKeyException, InvalidAlgorithmParameterException,
                  IllegalBlockSizeException, XMLSecurityException {

      try {

         // Compute the CMS Key Checksum, (section 5.6.1), call this CKS.
         byte[] CKS = calculateCMSKeyChecksum(keyToBeWrapped);

         // Let WKCKS = WK || CKS where || is concatenation.
         byte[] WKCKS = new byte[keyToBeWrapped.length + CKS.length];

         System.arraycopy(keyToBeWrapped, 0, WKCKS, 0, keyToBeWrapped.length);
         System.arraycopy(CKS, 0, WKCKS, keyToBeWrapped.length, CKS.length);

         // Encrypt WKCKS in CBC mode using KEK as the key and IV as the
         // initialization vector. Call the results TEMP1.
         byte TEMP1[] = new byte[WKCKS.length];

         System.arraycopy(WKCKS, 0, TEMP1, 0, WKCKS.length);

         int extraBytes = WKCKS.length % this._cipher.getBlockSize();

         if (extraBytes != 0) {
            throw new IllegalStateException("Not multiple of block length: "
                                            + WKCKS.length + " % "
                                            + this._cipher.getBlockSize()
                                            + " = " + extraBytes);
         }

         if (IV == null) {
            IV = PRNG.createBytes(8);
         }

         IvParameterSpec ivParam = new IvParameterSpec(IV);

         this._cipher.init(Cipher.ENCRYPT_MODE, wrapKey, ivParam);

         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] t;

            t = this._cipher.update(TEMP1);

            if (t != null) {
               baos.write(t);
            }

            t = this._cipher.doFinal();

            if (t != null) {
               baos.write(t);
            }

            System.arraycopy(baos.toByteArray(), 0, TEMP1, 0, TEMP1.length);
         }

         // Left TEMP2 = IV || TEMP1.
         byte[] TEMP2 = new byte[IV.length + TEMP1.length];

         System.arraycopy(IV, 0, TEMP2, 0, IV.length);
         System.arraycopy(TEMP1, 0, TEMP2, IV.length, TEMP1.length);

         // Reverse the order of the octets in TEMP2 and call the result TEMP3.
         byte[] TEMP3 = new byte[TEMP2.length];

         for (int i = 0; i < TEMP2.length; i++) {
            TEMP3[i] = TEMP2[TEMP2.length - (i + 1)];
         }

         // Encrypt TEMP3 in CBC mode using the KEK and an initialization vector
         // of 0x 4a dd a2 2c 79 e8 21 05. The resulting cipher text is the desired
         // result. It is 40 octets long if a 168 bit key is being wrapped.
         IvParameterSpec iv2Param = new IvParameterSpec(IV2);

         this._cipher.init(Cipher.ENCRYPT_MODE, wrapKey, iv2Param);

         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] t;

            t = this._cipher.update(TEMP3);

            if (t != null) {
               baos.write(t);
            }

            t = this._cipher.doFinal();

            if (t != null) {
               baos.write(t);
            }

            System.arraycopy(baos.toByteArray(), 0, TEMP3, 0, TEMP3.length);
         }

         return TEMP3;
      } catch (IOException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (BadPaddingException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method unwrap
    *
    * @param wrappedKey
    * @param wrapKey
    * @return
    * @throws InvalidCipherTextException
    * @throws XMLSecurityException
    */
   public byte[] unwrap(byte[] wrappedKey, Key wrapKey)
           throws InvalidCipherTextException, XMLSecurityException {

      try {
         if (wrappedKey == null) {
            throw new InvalidCipherTextException("Null pointer as ciphertext");
         }

         if (wrappedKey.length % this._cipher.getBlockSize() != 0) {
            throw new InvalidCipherTextException("Ciphertext not multiple of "
                                                 + this._cipher.getBlockSize());
         }

         /*
         // Check if the length of the cipher text is reasonable given the key
         // type. It must be 40 bytes for a 168 bit key and either 32, 40, or
         // 48 bytes for a 128, 192, or 256 bit key. If the length is not supported
         // or inconsistent with the algorithm for which the key is intended,
         // return error.
         //
         // we do not accept 168 bit keys. it has to be 192 bit.
         int lengthA = (estimatedKeyLengthInBit / 8) + 16;
         int lengthB = estimatedKeyLengthInBit % 8;

         if ((lengthA != keyToBeUnwrapped.length) || (lengthB != 0)) {
            throw new XMLSecurityException("empty");
         }
         */

         // Decrypt the cipher text with TRIPLedeS in CBC mode using the KEK
         // and an initialization vector (IV) of 0x4adda22c79e82105. Call the output TEMP3.
         IvParameterSpec iv2Param = new IvParameterSpec(IV2);

         this._cipher.init(Cipher.DECRYPT_MODE, wrapKey, iv2Param);

         byte TEMP3[] = new byte[wrappedKey.length];

         System.arraycopy(wrappedKey, 0, TEMP3, 0, wrappedKey.length);

         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] t;

            t = this._cipher.update(TEMP3);

            if (t != null) {
               baos.write(t);
            }

            t = this._cipher.doFinal();

            if (t != null) {
               baos.write(t);
            }

            System.arraycopy(baos.toByteArray(), 0, TEMP3, 0, TEMP3.length);
         }

         // Reverse the order of the octets in TEMP3 and call the result TEMP2.
         byte[] TEMP2 = new byte[TEMP3.length];

         for (int i = 0; i < TEMP3.length; i++) {
            TEMP2[i] = TEMP3[TEMP3.length - (i + 1)];
         }

         // Decompose TEMP2 into IV, the first 8 octets, and TEMP1, the remaining octets.
         IvParameterSpec ivParam = new IvParameterSpec(TEMP2, 0, 8);
         byte[] TEMP1 = new byte[TEMP2.length - 8];

         System.arraycopy(TEMP2, 8, TEMP1, 0, TEMP2.length - 8);

         // Decrypt TEMP1 using TRIPLedeS in CBC mode using the KEK and the IV
         // found in the previous step. Call the result WKCKS.
         this._cipher.init(Cipher.DECRYPT_MODE, wrapKey, ivParam);

         byte[] WKCKS = new byte[TEMP1.length];

         System.arraycopy(TEMP1, 0, WKCKS, 0, TEMP1.length);

         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] t;

            t = this._cipher.update(WKCKS);

            if (t != null) {
               baos.write(t);
            }

            t = this._cipher.doFinal();

            if (t != null) {
               baos.write(t);
            }

            System.arraycopy(baos.toByteArray(), 0, WKCKS, 0, WKCKS.length);
         }

         // Decompose WKCKS. CKS is the last 8 octets and WK, the wrapped key, are
         // those octets before the CKS.
         byte[] result = new byte[WKCKS.length - 8];
         byte[] CKStoBeVerified = new byte[8];

         System.arraycopy(WKCKS, 0, result, 0, WKCKS.length - 8);
         System.arraycopy(WKCKS, WKCKS.length - 8, CKStoBeVerified, 0, 8);

         // Calculate a CMS Key Checksum, (section 5.6.1), over the WK and compare
         // with the CKS extracted in the above step. If they are not equal, return error.
         if (!checkCMSKeyChecksum(result, CKStoBeVerified)) {
            throw new InvalidCipherTextException(
               "Checksum inside ciphertext is corrupted");
         }

         // WK is the wrapped key, now extracted for use in data decryption.
         return result;
      } catch (InvalidCipherTextException ex) {
         throw ex;
      } catch (XMLSecurityException ex) {
         throw ex;
      } catch (Exception ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Some key wrap algorithms make use of the Key Checksum defined
    * in CMS [CMS-Algorithms]. This is used to provide an integrity
    * check value for the key being wrapped. The algorithm is
    *
    * - Compute the 20 octet SHA-1 hash on the key being wrapped.
    * - Use the first 8 octets of this hash as the checksum value.
    *
    * @param key
    * @return
    * @see http://www.w3.org/TR/xmlenc-core/#sec-CMSKeyChecksum
    */
   private byte[] calculateCMSKeyChecksum(byte[] key) {

      this._digest.reset();
      this._digest.update(key, 0, key.length);

      byte result[] = new byte[8];
      byte digest[] = this._digest.digest();

      System.arraycopy(digest, 0, result, 0, 8);

      return result;
   }

   /**
    * @param key
    * @param checksum
    * @return
    * @see http://www.w3.org/TR/xmlenc-core/#sec-CMSKeyChecksum
    */
   private boolean checkCMSKeyChecksum(byte[] key, byte[] checksum) {

      byte[] calculatedChecksum = calculateCMSKeyChecksum(key);

      if (checksum.length != calculatedChecksum.length) {
         return false;
      }

      for (int i = 0; i != checksum.length; i++) {
         if (checksum[i] != calculatedChecksum[i]) {
            return false;
         }
      }

      return true;
   }
}
