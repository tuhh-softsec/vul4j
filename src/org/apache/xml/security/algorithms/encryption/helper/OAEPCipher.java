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
 * @see org.bouncycastle.crypto.encodings.OAEPEncoding
 */
public class OAEPCipher {

   /** Field _cipher */
   Cipher _cipher;

   /** Field _cipherMode */
   int _cipherMode;

   /** Field _digest */
   MessageDigest _digest;

   /** Field _hLen */
   int _hLen;

   /** Field _encodingParams[] */
   byte _encodingParams[];

   /** Field _encodingParamsHash[] */
   byte _encodingParamsHash[];

   /** Field _encodingParams[] */
   SecureRandom _secureRandom;

   /**
    * Constructor OAEPCipher
    *
    * @param cipher
    * @param digest
    * @param encodingParams
    */
   public OAEPCipher(Cipher cipher, MessageDigest digest,
                     byte encodingParams[]) {

      this._cipher = cipher;
      this._digest = digest;
      this._hLen = this._digest.getDigestLength();

      if (encodingParams != null) {
         this._encodingParams = encodingParams;
      } else {
         this._encodingParams = new byte[0];
      }

      this._digest.reset();
      this._digest.update(this._encodingParams);

      this._encodingParamsHash = this._digest.digest();

      this._digest.reset();
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
      int hLen = this._digest.getDigestLength();

      if (this._cipherMode == Cipher.ENCRYPT_MODE) {
         return baseBlockSize - 1 - 2 * hLen;
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
      int hLen = this._digest.getDigestLength();

      if (this._cipherMode == Cipher.ENCRYPT_MODE) {
         return baseBlockSize;
      } else {
         return baseBlockSize - 1 - 2 * hLen;
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
         return this.encodeBlock(in, inOff, inLen);
      } else if (this._cipherMode == Cipher.DECRYPT_MODE) {
         return this.decodeBlock(in, inOff, inLen);
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

      byte[] block = new byte[getInputBlockSize() + 1 + 2 * this._hLen];

      //
      // copy in the message
      //
      System.arraycopy(in, inOff, block, block.length - inLen, inLen);

      //
      // add sentinel
      //
      block[block.length - inLen - 1] = 0x01;

      //
      // as the block is already zeroed - there's no need to add PS (the >= 0 pad of 0)
      // add the hash of the encoding params.
      //
      System.arraycopy(this._encodingParamsHash, 0, block, this._hLen,
                       this._hLen);

      //
      // generate the seed.
      //
      byte[] seed = new byte[this._hLen];

      this._secureRandom.nextBytes(seed);

      //
      // mask the message block.
      //
      byte[] mask = maskGeneratorFunction1(seed, 0, seed.length,
                                           block.length - this._hLen,
                                           this._digest);

      for (int i = this._hLen; i != block.length; i++) {
         block[i] ^= mask[i - this._hLen];
      }

      //
      // add in the seed
      //
      System.arraycopy(seed, 0, block, 0, this._hLen);

      //
      // mask the seed.
      //
      mask = maskGeneratorFunction1(block, this._hLen,
                                    block.length - this._hLen, this._hLen,
                                    this._digest);

      for (int i = 0; i != this._hLen; i++) {
         block[i] ^= mask[i];
      }

      return this._cipher.doFinal(block, 0, block.length);
   }

   /**
    * @param in
    * @param inOff
    * @param inLen
    *
    * @throws BadPaddingException
    * @throws IllegalBlockSizeException
    * @exception InvalidCipherTextException if the decryypted block turns out to
    * be badly formatted.
    */
   public byte[] decodeBlock(byte[] in, int inOff, int inLen)
           throws InvalidCipherTextException, IllegalBlockSizeException,
                  BadPaddingException {

      byte[] data = this._cipher.doFinal(in, inOff, inLen);
      byte[] block = null;

      //
      // as we may have zeros in our leading bytes for the block we produced
      // on encryption, we need to make sure our decrypted block comes back
      // the same size.
      //
      if (data.length
              < this.getOutputBlockSize()) {    // this._cipher.getBlockSize()
         block =
            new byte[this.getOutputBlockSize()];    // this._cipher.getBlockSize()

         System.arraycopy(data, 0, block, block.length - data.length,
                          data.length);
      } else {
         block = data;
      }

      if (block.length < (2 * this._hLen) + 1) {
         throw new InvalidCipherTextException(
            "encryption.RSAOAEP.dataTooShort");
      }

      //
      // unmask the seed.
      //
      byte[] mask = maskGeneratorFunction1(block, this._hLen,
                                           block.length - this._hLen,
                                           this._hLen, this._digest);

      for (int i = 0; i != this._hLen; i++) {
         block[i] ^= mask[i];
      }

      //
      // unmask the message block.
      //
      mask = maskGeneratorFunction1(block, 0, this._hLen,
                                    block.length - this._hLen, this._digest);

      for (int i = this._hLen; i != block.length; i++) {
         block[i] ^= mask[i - this._hLen];
      }

      //
      // check the hash of the encoding params.
      //
      for (int i = 0; i != this._encodingParamsHash.length; i++) {
         if (this._encodingParamsHash[i]
                 != block[this._encodingParamsHash.length + i]) {
            throw new InvalidCipherTextException(
               "encryption.RSAOAEP.dataHashWrong");
         }
      }

      //
      // find the data block
      //
      int start;

      for (start = 2 * this._hLen; start != block.length; start++) {
         if ((block[start] == 1) || (block[start] != 0)) {
            break;
         }
      }

      if ((start >= (block.length - 1)) || (block[start] != 1)) {
         Object exArgs[] = { new Integer(start) };

         throw new InvalidCipherTextException(
            "encryption.RSAOAEP.dataStartWrong", exArgs);
      }

      start++;

      //
      // extract the data block
      //
      byte[] output = new byte[block.length - start];

      System.arraycopy(block, start, output, 0, output.length);

      return output;
   }

   /**
    * int to octet string.
    *
    * @param i
    * @param sp
    */
   static void ItoOSP(int i, byte[] sp) {

      sp[0] = (byte) (i >>> 24);
      sp[1] = (byte) (i >>> 16);
      sp[2] = (byte) (i >>> 8);
      sp[3] = (byte) (i >>> 0);
   }

   /**
    * mask generator function, as described in PKCS1v2.
    *
    * @param Z
    * @param zOff
    * @param zLen
    * @param length
    * @param Hash
    *
    * @throws IllegalArgumentException
    */
   static byte[] maskGeneratorFunction1(
           byte[] Z, int zOff, int zLen, int length, MessageDigest Hash)
              throws IllegalArgumentException {

      int hLen = Hash.getDigestLength();
      byte[] mask = new byte[length];
      byte[] C = new byte[4];
      int counter = 0;

      Hash.reset();

      do {
         ItoOSP(counter, C);
         Hash.update(Z, zOff, zLen);
         Hash.update(C, 0, C.length);

         byte hashBuf[] = Hash.digest();

         System.arraycopy(hashBuf, 0, mask, counter * hLen, hLen);
      } while (++counter < (length / hLen));

      if ((counter * hLen) < length) {
         ItoOSP(counter, C);
         Hash.update(Z, zOff, zLen);
         Hash.update(C, 0, C.length);

         byte hashBuf[] = Hash.digest();

         System.arraycopy(hashBuf, 0, mask, counter * hLen,
                          mask.length - (counter * hLen));
      }

      return mask;
   }

   /**
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String[] args) throws Exception {

      Security.addProvider(new BouncyCastleProvider());

      Cipher rsaCipher = Cipher.getInstance("RSA", "BC");
      MessageDigest sha1 = MessageDigest.getInstance("SHA-1", "BC");
      byte encodingParams[] = Base64.decode("9lWu3Q==");
      OAEPCipher OAEPCipher1 = new OAEPCipher(rsaCipher, sha1, null);
      //J-
      BigInteger modulus = new BigInteger(
           "bbf82f090682ce9c2338ac2b9da871f7368d07eed41043a4" +
           "40d6b6f07454f51fb8dfbaaf035c02ab61ea48ceeb6fcd48" +
           "76ed520d60e1ec4619719d8a5b8b807fafb8e0a3dfc73772" +
           "3ee6b4b7d93a2584ee6a649d060953748834b2454598394e" +
           "e0aab12d7b61a51f527a9a41f6c1687fe2537298ca2a8f59" +
           "46f8e5fd091dbdcb", 16);
      BigInteger privateExponent = new BigInteger(
           "a5dafc5341faf289c4b988db30c1cdf83f31251e0668b427" +
           "84813801579641b29410b3c7998d6bc465745e5c392669d6" +
            "870da2c082a939e37fdcb82ec93edac97ff3ad5950accfbc" +
           "111c76f1a9529444e56aaf68c56c092cd38dc3bef5d20a93" +
           "9926ed4f74a13eddfbe1a1cecc4894af9428c2b7b8883fe4" +
           "463a4bc85b1cb3c1", 16);
      String cipherText =
         "12 53 e0 4d c0 a5 39 7b b4 4a 7a b8 7e 9b f2 a0 39 a3 3d 1e 99 6f c8 2a " +
         "94 cc d3 00 74 c9 5d f7 63 72 20 17 06 9e 52 68 da 5d 1c 0b 4f 87 2c f6 " +
         "53 c1 1d f8 23 14 a6 79 68 df ea e2 8d ef 04 bb 6d 84 b1 c3 1d 65 4a 19 " +
         "70 e5 78 3b d6 eb 96 a0 24 c2 ca 2f 4a 90 fe 9f 2e f5 c9 c1 40 e5 bb 48 " +
         "da 95 36 ad 87 00 c8 4f c9 13 0a de a7 4e 55 8d 51 a7 4d df 85 d8 b5 0d " +
         "e9 68 38 d6 06 3e 09 55 ";
      //J+
      KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
      KeySpec rsaKeySpec = new RSAPrivateKeySpec(modulus, privateExponent);
      PrivateKey pK = kf.generatePrivate(rsaKeySpec);

      OAEPCipher1.init(Cipher.DECRYPT_MODE, pK, new SecureRandom());

      byte oaepBytes[] = HexDump.hexStringToByteArray(cipherText);
      byte oaepPlainBytes[] = OAEPCipher1.processBlock(oaepBytes, 0,
                                 oaepBytes.length);

      System.out.println(HexDump.byteArrayToHexString(oaepPlainBytes));
   }
}
