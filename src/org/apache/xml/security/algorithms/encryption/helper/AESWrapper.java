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
public class AESWrapper {

   /** Field defaultIv */
   static final byte[] DEFAULT_IV = { (byte) 0xa6, (byte) 0xa6, (byte) 0xa6,
                                      (byte) 0xa6, (byte) 0xa6, (byte) 0xa6,
                                      (byte) 0xa6, (byte) 0xa6 };

   /** Field _cipher */
   Cipher _cipher;

   /**
    * Constructor AESWrapper
    *
    * @param aesCipher
    */
   public AESWrapper(Cipher aesCipher) {
      this._cipher = aesCipher;
   }

   /**
    * Method wrap
    *
    * @param keyToBeWrappedK
    * @param wrapKey
    * @param IV
    *
    * @throws XMLSecurityException
    */
   public byte[] wrap(Key keyToBeWrappedK, Key wrapKey, byte[] IV)
           throws XMLSecurityException {

      try {
         byte keyToBeWrapped[] = keyToBeWrappedK.getEncoded();

         if (IV == null) {
            IV = new byte[AESWrapper.DEFAULT_IV.length];

            System.arraycopy(AESWrapper.DEFAULT_IV, 0, IV, 0,
                             AESWrapper.DEFAULT_IV.length);
         }

         if (IV.length != 8) {
            throw new XMLSecurityException("empty");
         }

         int inLen = keyToBeWrapped.length;
         int n = inLen / 8;

         if ((keyToBeWrapped.length % 8) != 0) {
            throw new XMLSecurityException(
               "wrap data must be a multiple of 8 bytes");
         }

         byte[] block = new byte[keyToBeWrapped.length + IV.length];
         byte[] buf = new byte[8 + IV.length];

         System.arraycopy(IV, 0, block, 0, IV.length);
         System.arraycopy(keyToBeWrapped, 0, block, IV.length,
                          keyToBeWrapped.length);
         this._cipher.init(Cipher.ENCRYPT_MODE, wrapKey);

         for (int j = 0; j != 6; j++) {
            for (int i = 1; i <= n; i++) {
               System.arraycopy(block, 0, buf, 0, IV.length);
               System.arraycopy(block, 8 * i, buf, IV.length, 8);
               this._cipher.update(buf, 0, buf.length, buf, 0);

               int t = n * j + i;

               for (int k = 1; t != 0; k++) {
                  byte v = (byte) t;

                  buf[IV.length - k] ^= v;
                  t >>>= 8;
               }

               System.arraycopy(buf, 0, block, 0, 8);
               System.arraycopy(buf, 8, block, 8 * i, 8);
            }
         }

         return block;
      } catch (InvalidKeyException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (ShortBufferException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method unwrap
    *
    * @param wrappedKey
    * @param wrapKey
    *
    * @throws InvalidCipherTextException
    * @throws XMLSecurityException
    */
   public byte[] unwrap(byte[] wrappedKey, Key wrapKey)
           throws InvalidCipherTextException, XMLSecurityException {

      try {
         int n = wrappedKey.length / 8;

         if ((n * 8) != wrappedKey.length) {
            throw new InvalidCipherTextException(
               "unwrap data must be a multiple of 8 bytes");
         }

         byte[] a = new byte[AESWrapper.DEFAULT_IV.length];
         byte[] block = new byte[wrappedKey.length - a.length];
         byte[] buf = new byte[8 + a.length];

         System.arraycopy(wrappedKey, 0, a, 0, a.length);
         System.arraycopy(wrappedKey, a.length, block, 0, wrappedKey.length - a.length);
         this._cipher.init(Cipher.DECRYPT_MODE, wrapKey);

         n = n - 1;

         for (int j = 5; j >= 0; j--) {
            for (int i = n; i >= 1; i--) {
               System.arraycopy(a, 0, buf, 0, a.length);
               System.arraycopy(block, 8 * (i - 1), buf, a.length, 8);

               int t = n * j + i;

               for (int k = 1; t != 0; k++) {
                  byte v = (byte) t;

                  buf[a.length - k] ^= v;
                  t >>>= 8;
               }

               this._cipher.update(buf, 0, buf.length, buf, 0);
               System.arraycopy(buf, 0, a, 0, 8);
               System.arraycopy(buf, 8, block, 8 * (i - 1), 8);
            }
         }

         for (int i = 0; i != a.length; i++) {
            if (a[i] != AESWrapper.DEFAULT_IV[i]) {
               throw new InvalidCipherTextException("checksum failed");
            }
         }

         return block;

      } catch (InvalidKeyException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (ShortBufferException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }
}
