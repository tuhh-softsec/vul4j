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
package org.apache.xml.security.algorithms.implementations;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.apache.xml.security.algorithms.*;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.utils.*;
import org.w3c.dom.*;


/*
import org.bouncycastle.asn1.DERConstructedSequence;
import org.bouncycastle.asn1.DERInputStream;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DEROutputStream;
*/

/**
 *
 * @author $Author$
 */
public class SignatureDSA extends SignatureAlgorithmSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(SignatureDSA.class.getName());

   /** Field _URI */
   public static final String _URI = Constants.SignatureSpecNS + "dsa-sha1";

   /** Field algorithm */
   private java.security.Signature _signatureAlgorithm = null;

   /**
    * Method engineGetURI
    *
    *
    */
   protected String engineGetURI() {
      return this._URI;
   }

   /**
    * Constructor SignatureDSA
    *
    * @throws XMLSignatureException
    */
   public SignatureDSA() throws XMLSignatureException {

      JCEMapper.ProviderIdClass algorithmID =
         JCEMapper.translateURItoJCEID(this._URI);

      cat.debug("Created SignatureDSA using " + algorithmID.getAlgorithmID()
                + " " + algorithmID.getProviderId());

      try {
         this._signatureAlgorithm =
            Signature.getInstance(algorithmID.getAlgorithmID(),
                                  algorithmID.getProviderId());
      } catch (java.security.NoSuchAlgorithmException ex) {
         Object[] exArgs = { algorithmID.getAlgorithmID(),
                             ex.getLocalizedMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
      } catch (java.security.NoSuchProviderException ex) {
         Object[] exArgs = { algorithmID.getProviderId(),
                             ex.getLocalizedMessage() };

         throw new XMLSignatureException("algorithms.NoSuchProvider", exArgs);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#setParameter}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param params
    * @throws XMLSignatureException
    */
   protected void engineSetParameter(AlgorithmParameterSpec params)
           throws XMLSignatureException {

      try {
         this._signatureAlgorithm.setParameter(params);
      } catch (InvalidAlgorithmParameterException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#verify}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param signature
    *
    * @throws XMLSignatureException
    */
   protected boolean engineVerify(byte[] signature)
           throws XMLSignatureException {

      try {
         cat.debug("Called DSA.verify() on " + Base64.encode(signature));

         byte[] jcebytes = SignatureDSA.convertXMLDSIGtoASN1(signature);

         return this._signatureAlgorithm.verify(jcebytes);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      } catch (IOException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#initVerify}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param publicKey
    * @throws XMLSignatureException
    */
   protected void engineInitVerify(Key publicKey) throws XMLSignatureException {

      if (!(publicKey instanceof PublicKey)) {
         String supplied = publicKey.getClass().getName();
         String needed = PublicKey.class.getName();
         Object exArgs[] = { supplied, needed };

         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation",
                                         exArgs);
      }

      try {
         this._signatureAlgorithm.initVerify((PublicKey) publicKey);
      } catch (InvalidKeyException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#sign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @return the result of the {@link java.security.Signature#sign} method
    * @throws XMLSignatureException
    */
   protected byte[] engineSign() throws XMLSignatureException {

      try {
         byte jcebytes[] = this._signatureAlgorithm.sign();

         return SignatureDSA.convertASN1toXMLDSIG(jcebytes);
      } catch (IOException ex) {
         throw new XMLSignatureException("empty", ex);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#initSign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param privateKey
    * @param secureRandom
    * @throws XMLSignatureException
    */
   protected void engineInitSign(Key privateKey, SecureRandom secureRandom)
           throws XMLSignatureException {

      if (!(privateKey instanceof PrivateKey)) {
         String supplied = privateKey.getClass().getName();
         String needed = PrivateKey.class.getName();
         Object exArgs[] = { supplied, needed };

         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation",
                                         exArgs);
      }

      try {
         this._signatureAlgorithm.initSign((PrivateKey) privateKey,
                                           secureRandom);
      } catch (InvalidKeyException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#initSign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param privateKey
    * @throws XMLSignatureException
    */
   protected void engineInitSign(Key privateKey) throws XMLSignatureException {

      if (!(privateKey instanceof PrivateKey)) {
         String supplied = privateKey.getClass().getName();
         String needed = PrivateKey.class.getName();
         Object exArgs[] = { supplied, needed };

         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation",
                                         exArgs);
      }

      try {
         this._signatureAlgorithm.initSign((PrivateKey) privateKey);
      } catch (InvalidKeyException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param input
    * @throws XMLSignatureException
    */
   protected void engineUpdate(byte[] input) throws XMLSignatureException {

      try {
         this._signatureAlgorithm.update(input);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param input
    * @throws XMLSignatureException
    */
   protected void engineUpdate(byte input) throws XMLSignatureException {

      try {
         this._signatureAlgorithm.update(input);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param buf
    * @param offset
    * @param len
    * @throws XMLSignatureException
    */
   protected void engineUpdate(byte buf[], int offset, int len)
           throws XMLSignatureException {

      try {
         this._signatureAlgorithm.update(buf, offset, len);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Method engineGetJCEAlgorithmString
    *
    *
    */
   protected String engineGetJCEAlgorithmString() {
      return this._signatureAlgorithm.getAlgorithm();
   }

   /**
    * Method engineGetJCEProviderName
    *
    *
    */
   protected String engineGetJCEProviderName() {
      return this._signatureAlgorithm.getProvider().getName();
   }

   /*
    * Converts a XML Signature DSA Value to an ASN.1 DSA value.
    *
    * The JAVA JCE DSA Signature algorithm creates ASN.1 encoded (r,s) value
    * pairs; the XML Signature requires the core BigInteger values.
    *
    * @param xmldsigbytes
    *
    * @throws IOException
    * @see org.bouncycastle.jce.provider.JDKDSASigner#derEncode
    * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#dsa-sha1">6.4.1 DSA</A>
   private static byte[] convertXMLDSIGtoASN1_BOUNCY(byte[] xmldsigbytes)
           throws IOException {

      byte rbytes[] = new byte[21];
      byte sbytes[] = new byte[21];

      rbytes[0] = (byte) 0x00;
      sbytes[0] = (byte) 0x00;

      System.arraycopy(xmldsigbytes, 0, rbytes, 1, 20);
      System.arraycopy(xmldsigbytes, 20, sbytes, 1, 20);

      BigInteger r = new BigInteger(rbytes);
      BigInteger s = new BigInteger(sbytes);

      cat.debug("Verify DSA r=" + r.toString());
      cat.debug("Verify DSA s=" + s.toString());

      ByteArrayOutputStream bOut = new ByteArrayOutputStream();
      DEROutputStream dOut = new DEROutputStream(bOut);
      DERConstructedSequence seq = new DERConstructedSequence();

      seq.addObject(new DERInteger(r));
      seq.addObject(new DERInteger(s));
      dOut.writeObject(seq);

      return bOut.toByteArray();
   }
    */

   /*
    * Converts an ASN.1 DSA value to a XML Signature DSA Value.
    *
    * The JAVA JCE DSA Signature algorithm creates ASN.1 encoded (r,s) value
    * pairs; the XML Signature requires the core BigInteger values.
    *
    * @param derbytes
    *
    * @throws IOException
    * @see org.bouncycastle.jce.provider.JDKDSASigner#derDecode
    * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#dsa-sha1">6.4.1 DSA</A>
   private static byte[] convertASN1toXMLDSIG_BOUNCY(byte derbytes[])
           throws IOException {

      cat.debug("Input convertASN1toXMLDSIG("
                + HexDump.byteArrayToHexString(derbytes) + ")");

      ByteArrayInputStream bIn = new ByteArrayInputStream(derbytes);
      DERInputStream dIn = new DERInputStream(bIn);
      DERConstructedSequence seq = (DERConstructedSequence) dIn.readObject();
      BigInteger r = ((DERInteger) seq.getObjectAt(0)).getValue();
      BigInteger s = ((DERInteger) seq.getObjectAt(1)).getValue();

      cat.debug("Created DSA r=" + r.toString());
      cat.debug("Created DSA s=" + s.toString());

      byte rbytes[] = r.toByteArray();
      byte sbytes[] = s.toByteArray();

      cat.debug("r1=" + HexDump.byteArrayToHexString(rbytes));
      cat.debug("s1=" + HexDump.byteArrayToHexString(sbytes));

      rbytes = SignatureDSA.normalizeBigIntegerArray(rbytes);
      sbytes = SignatureDSA.normalizeBigIntegerArray(sbytes);

      cat.debug("r2=" + HexDump.byteArrayToHexString(rbytes));
      cat.debug("s2=" + HexDump.byteArrayToHexString(sbytes));

      byte result[] = new byte[40];

      System.arraycopy(rbytes, 0, result, 0, 20);
      System.arraycopy(sbytes, 0, result, 20, 20);

      return result;
   }
    */

   /*
    *
    * @see org.bouncycastle.jce.provider.JDKDSASigner#derEncode
    * @param bigIntegerArray
    *
    * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#dsa-sha1">6.4.1 DSA</A>
   private static byte[] normalizeBigIntegerArray(byte bigIntegerArray[]) {

      byte resultBytes[] = new byte[20];

      for (int i = 0; i < 20; i++) {
         resultBytes[i] = (byte) 0x00;
      }

      if (bigIntegerArray.length == 21) {
         System.arraycopy(bigIntegerArray, 1, resultBytes, 0, 20);
      } else {
         System.arraycopy(bigIntegerArray, 0, resultBytes,
                          20 - bigIntegerArray.length, bigIntegerArray.length);
      }

      return resultBytes;
   }
    */

   /**
    * Converts an ASN.1 DSA value to a XML Signature DSA Value.
    *
    * The JAVA JCE DSA Signature algorithm creates ASN.1 encoded (r,s) value
    * pairs; the XML Signature requires the core BigInteger values.
    *
    * @param asn1Bytes
    *
    * @throws IOException
    * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#dsa-sha1">6.4.1 DSA</A>
    */
   private static byte[] convertASN1toXMLDSIG(byte asn1Bytes[])
           throws IOException {

      byte rLength = asn1Bytes[3];
      int i;

      for (i = rLength; (i > 0) && (asn1Bytes[(4 + rLength) - i] == 0); i--);

      byte sLength = asn1Bytes[5 + rLength];
      int j;

      for (j = sLength;
              (j > 0) && (asn1Bytes[(6 + rLength + sLength) - j] == 0); j--);

      if ((asn1Bytes[0] != 48) || (asn1Bytes[1] != asn1Bytes.length - 2)
              || (asn1Bytes[2] != 2) || (i > 20)
              || (asn1Bytes[4 + rLength] != 2) || (j > 20)) {
         throw new IOException("Invalid ASN.1 format of DSA signature");
      } else {
         byte xmldsigBytes[] = new byte[40];

         System.arraycopy(asn1Bytes, (4 + rLength) - i, xmldsigBytes, 20 - i,
                          i);
         System.arraycopy(asn1Bytes, (6 + rLength + sLength) - j, xmldsigBytes,
                          40 - j, j);

         return xmldsigBytes;
      }
   }

   /**
    * Converts a XML Signature DSA Value to an ASN.1 DSA value.
    *
    * The JAVA JCE DSA Signature algorithm creates ASN.1 encoded (r,s) value
    * pairs; the XML Signature requires the core BigInteger values.
    *
    * @param xmldsigBytes
    *
    * @throws IOException
    * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#dsa-sha1">6.4.1 DSA</A>
    */
   private static byte[] convertXMLDSIGtoASN1(byte xmldsigBytes[])
           throws IOException {

      if (xmldsigBytes.length != 40) {
         throw new IOException("Invalid XMLDSIG format of DSA signature");
      }

      int i;

      for (i = 20; (i > 0) && (xmldsigBytes[20 - i] == 0); i--);

      int j = i;

      if (xmldsigBytes[20 - i] < 0) {
         j += 1;
      }

      int k;

      for (k = 20; (k > 0) && (xmldsigBytes[40 - k] == 0); k--);

      int l = k;

      if (xmldsigBytes[40 - k] < 0) {
         l += 1;
      }

      byte asn1Bytes[] = new byte[6 + j + l];

      asn1Bytes[0] = 48;
      asn1Bytes[1] = (byte) (4 + j + l);
      asn1Bytes[2] = 2;
      asn1Bytes[3] = (byte) j;

      System.arraycopy(xmldsigBytes, 20 - i, asn1Bytes, (4 + j) - i, i);

      asn1Bytes[4 + j] = 2;
      asn1Bytes[5 + j] = (byte) l;

      System.arraycopy(xmldsigBytes, 40 - k, asn1Bytes, (6 + j + l) - k, k);

      return asn1Bytes;
   }

   /**
    * Method engineSetHMACOutputLength
    *
    * @param HMACOutputLength
    * @throws XMLSignatureException
    */
   protected void engineSetHMACOutputLength(int HMACOutputLength)
           throws XMLSignatureException {
      throw new XMLSignatureException("algorithms.HMACOutputLengthOnlyForHMAC");
   }

   /**
    * Method engineInitSign
    *
    * @param signingKey
    * @param algorithmParameterSpec
    * @throws XMLSignatureException
    */
   protected void engineInitSign(
           Key signingKey, AlgorithmParameterSpec algorithmParameterSpec)
              throws XMLSignatureException {
      throw new XMLSignatureException(
         "algorithms.CannotUseAlgorithmParameterSpecOnDSA");
   }
}
