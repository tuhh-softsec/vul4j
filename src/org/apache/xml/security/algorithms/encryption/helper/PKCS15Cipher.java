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



import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.HexDump;
import java.math.BigInteger;


/**
 *
 * @author $Author$
 * @see org.bouncycastle.crypto.encodings.PKCS1Encoding
 */
public class PKCS15Cipher {

   /** Field HEADER_LENGTH */
   private static int HEADER_LENGTH = 10;

   /** Field _cipher */
   Cipher _cipher;

   /** Field _cipherMode */
   int _cipherMode;

   /** Field _encodingParams[] */
   SecureRandom _secureRandom;

   /**
    * Constructor PKCS15Cipher
    *
    * @param rsaCipher
    */
   public PKCS15Cipher(Cipher rsaCipher) {
      this._cipher = rsaCipher;
   }

   /**
    * Method init
    *
    * @param mode
    * @param key
    * @param secureRandom
    * @throws InvalidKeyException
    */
   public void init(int mode, Key key, SecureRandom secureRandom)
           throws InvalidKeyException {

      this._cipherMode = mode;
      this._secureRandom = secureRandom;

      this._cipher.init(this._cipherMode, key, this._secureRandom);
   }

   /**
    * Method getInputBlockSize
    *
    *
    */
   public int getInputBlockSize() {

      int baseBlockSize = this._cipher.getBlockSize();

      if (this._cipherMode == Cipher.ENCRYPT_MODE) {
         return baseBlockSize - PKCS15Cipher.HEADER_LENGTH;
      } else {
         return baseBlockSize;
      }
   }

   /**
    * Method getOutputBlockSize
    *
    *
    */
   public int getOutputBlockSize() {

      int baseBlockSize = this._cipher.getBlockSize();

      if (this._cipherMode == Cipher.ENCRYPT_MODE) {
         return baseBlockSize;
      } else {
         return baseBlockSize - PKCS15Cipher.HEADER_LENGTH - 1;
      }
   }

   /**
    * Method processBlock
    *
    * @param in
    * @param inOff
    * @param inLen
    *
    * @throws BadPaddingException
    * @throws IllegalBlockSizeException
    * @throws InvalidCipherTextException
    */
   public byte[] processBlock(byte[] in, int inOff, int inLen)
           throws InvalidCipherTextException, IllegalBlockSizeException,
                  BadPaddingException {

      if (this._cipherMode == Cipher.ENCRYPT_MODE) {
         return encodeBlock(in, inOff, inLen);
      } else if (this._cipherMode == Cipher.DECRYPT_MODE) {
         return decodeBlock(in, inOff, inLen);
      } else {
         return null;
      }
   }

   /**
    * Method encodeBlock
    *
    * @param in
    * @param inOff
    * @param inLen
    *
    * @throws BadPaddingException
    * @throws IllegalBlockSizeException
    * @throws InvalidCipherTextException
    */
   public byte[] encodeBlock(byte[] in, int inOff, int inLen)
           throws InvalidCipherTextException, IllegalBlockSizeException,
                  BadPaddingException {

      byte[] block = new byte[this.getInputBlockSize()];

      this._secureRandom.nextBytes(block);    // random fill

      block[0] = 0x02;    // type code 2

      //
      // a zero byte marks the end of the padding, so all
      // the pad bytes must be non-zero.
      //
      for (int i = 1; i != block.length - inLen - 1; i++) {
         while (block[i] == 0) {
            block[i] = (byte) this._secureRandom.nextInt();
         }
      }

      block[block.length - inLen - 1] = 0x00;    // mark the end of the padding

      System.arraycopy(in, inOff, block, block.length - inLen, inLen);

      return this._cipher.doFinal(block, 0, block.length);
   }

   /**
    * @param in
    * @param inOff
    * @param inLen
    *
    * @throws BadPaddingException
    * @throws IllegalBlockSizeException
    * @exception InvalidCipherTextException if the decrypted block is not in PKCS1 format.
    */
   public byte[] decodeBlock(byte[] in, int inOff, int inLen)
           throws InvalidCipherTextException, IllegalBlockSizeException,
                  BadPaddingException {

      byte[] block = this._cipher.doFinal(in, inOff, inLen);

      if (block.length < this.getOutputBlockSize()) {
         throw new InvalidCipherTextException(
            "encryption.RSAPKCS15.blockTruncated");
      }

      if (block[0] != 0x02) {
         throw new InvalidCipherTextException(
            "encryption.RSAPKCS15.unknownBlockType");
      }

      //
      // find and extract the message block.
      //
      int start;

      for (start = 1; start != block.length; start++) {
         if (block[start] == 0) {
            break;
         }
      }

      start++;    // data should start at the next byte

      if ((start >= block.length) || (start < HEADER_LENGTH)) {
         throw new InvalidCipherTextException(
            "encryption.RSAPKCS15.noDataInBlock");
      }

      byte[] result = new byte[block.length - start];

      System.arraycopy(block, start, result, 0, result.length);

      return result;
   }
}
