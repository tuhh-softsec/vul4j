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
package org.apache.xml.security.algorithms;



import java.security.cert.Certificate;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Collection;
import javax.crypto.Mac;
import javax.crypto.ShortBufferException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.*;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERInputStream;
import org.bouncycastle.asn1.DERConstructedSequence;
import org.bouncycastle.asn1.DERInteger;


/**
 * Allows selection of digital signature's algorithm, private keys, other security parameters, and algorithm's ID.
 *
 * <p>The exists no constructor but the {@link #getInstance} methods to obtain instances of this class.
 *
 * <pre>
 * SignatureAlgorithm.getInstance()
 * </pre>
 *
 * @author Christian Geuer-Pollmann
 */
public class SignatureAlgorithm extends Algorithm {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(SignatureAlgorithm.class.getName());

   /** Field algorithm */
   private java.security.Signature _signatureAlgorithm = null;

   /** Field algorithm */
   private Mac _macAlgorithm = null;

   /**
    * Constructor for the brave who pass their own signature algorithms and the corresponding URI.
    *
    * @param doc
    * @param signature
    * @param algorithmURI
    */
   private SignatureAlgorithm(Document doc, Signature signature,
                              String algorithmURI) {

      super(doc, Constants._TAG_SIGNATUREMETHOD, algorithmURI);

      this._signatureAlgorithm = signature;
   }

   /**
    * SignatureAlgorithm constructor chooses the best match for
    * the requested algorithm and constructs the full URI for it.
    *
    * @param doc
    * @param algorithmURI
    * @return
    * @throws XMLSignatureException
    */
   public static SignatureAlgorithm getInstance(
           Document doc, String algorithmURI) throws XMLSignatureException {

      JCEMapper.ProviderIdClass algorithmID =
         JCEMapper.translateURItoJCEID(algorithmURI);
      Signature algorithm = null;

      try {
         algorithm = Signature.getInstance(algorithmID.getAlgorithmID(),
                                           algorithmID.getProviderId());
      } catch (java.security.NoSuchAlgorithmException ex) {
         Object[] exArgs = { algorithmID.getAlgorithmID(),
                             ex.getLocalizedMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
      } catch (java.security.NoSuchProviderException ex) {
         Object[] exArgs = { algorithmID.getProviderId(),
                             ex.getLocalizedMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
      }

      return new SignatureAlgorithm(doc, algorithm, algorithmURI);
   }

   /**
    * Returns the actual {@link java.security.Signature} algorithm object
    *
    * @return the actual {@link java.security.Signature} algorithm object
    */
   public java.security.Signature getAlgorithm() {
      return this._signatureAlgorithm;
   }

   /**
    * Proxy method for {@link java.security.Signature#sign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @return the result of the {@link java.security.Signature#sign} method
    * @throws SignatureException
    */
   public byte[] sign() throws SignatureException {
      return this._signatureAlgorithm.sign();
   }

   /**
    * Proxy method for {@link java.security.Signature#sign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param outbuf
    * @param offset
    * @param len
    * @return the result of the {@link java.security.Signature#sign} method
    * @throws java.security.SignatureException
    */
   public int sign(byte outbuf[], int offset, int len)
           throws java.security.SignatureException {
      return this._signatureAlgorithm.sign(outbuf, offset, len);
   }

   /**
    * Proxy method for {@link java.security.Signature#getAlgorithm}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @return the result of the {@link java.security.Signature#getAlgorithm} method
    */
   public String getJCEAlgorithmString() {
      return this._signatureAlgorithm.getAlgorithm();
   }

   /**
    * Proxy method for {@link java.security.Signature#getProvider}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @return the result of the {@link java.security.Signature#getProvider} method
    */
   public java.security.Provider getJCEProvider() {
      return this._signatureAlgorithm.getProvider();
   }

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param input
    * @throws SignatureException
    */
   public void update(byte[] input) throws SignatureException {
      this._signatureAlgorithm.update(input);
   }

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param input
    * @throws SignatureException
    */
   public void update(byte input) throws SignatureException {
      this._signatureAlgorithm.update(input);
   }

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param buf
    * @param offset
    * @param len
    * @throws SignatureException
    */
   public void update(byte buf[], int offset, int len)
           throws SignatureException {
      this._signatureAlgorithm.update(buf, offset, len);
   }

   /**
    * Proxy method for {@link java.security.Signature#initSign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param privateKey
    * @param secureRandom
    * @throws InvalidKeyException
    */
   public void initSign(PrivateKey privateKey, SecureRandom secureRandom)
           throws InvalidKeyException {
      this._signatureAlgorithm.initSign(privateKey, secureRandom);
   }

   /**
    * Proxy method for {@link java.security.Signature#initSign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param privateKey
    * @throws InvalidKeyException
    */
   public void initSign(PrivateKey privateKey) throws InvalidKeyException {
      this._signatureAlgorithm.initSign(privateKey);
   }

   /**
    * Proxy method for {@link java.security.Signature#setParameter}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param params
    * @throws InvalidAlgorithmParameterException
    */
   public void setParameter(AlgorithmParameterSpec params)
           throws InvalidAlgorithmParameterException {
      this._signatureAlgorithm.setParameter(params);
   }

   /**
    * Proxy method for {@link java.security.Signature#initVerify}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param publickey
    * @throws InvalidKeyException
    */
   public void initVerify(PublicKey publickey) throws InvalidKeyException {
      this._signatureAlgorithm.initVerify(publickey);
   }

   /**
    * Proxy method for {@link java.security.Signature#verify}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param signature
    * @return
    * @throws SignatureException
    */
   public boolean verify(byte[] signature) throws SignatureException {
      return this._signatureAlgorithm.verify(signature);
   }

   /**
    * Method convertXMLDSIGtoASN1
    *
    * @param xmldsigbytes
    * @return
    * @throws IOException
    */
   public static byte[] convertXMLDSIGtoASN1(byte[] xmldsigbytes)
           throws IOException {

      byte rbytes[] = new byte[20];
      byte sbytes[] = new byte[20];

      System.arraycopy(xmldsigbytes, 0, rbytes, 0, 20);
      System.arraycopy(xmldsigbytes, 20, sbytes, 0, 20);

      BigInteger r = new BigInteger(rbytes);
      BigInteger s = new BigInteger(sbytes);
      ByteArrayOutputStream bOut = new ByteArrayOutputStream();
      DEROutputStream dOut = new DEROutputStream(bOut);
      DERConstructedSequence seq = new DERConstructedSequence();

      seq.addObject(new DERInteger(r));
      seq.addObject(new DERInteger(s));
      dOut.writeObject(seq);

      return bOut.toByteArray();
   }

   /**
    * Method convertASN1toXMLDSIG
    *
    * @param derbytes
    * @return
    * @throws IOException
    */
   public static byte[] convertASN1toXMLDSIG(byte derbytes[])
           throws IOException {

      ByteArrayInputStream bIn = new ByteArrayInputStream(derbytes);
      DERInputStream dIn = new DERInputStream(bIn);
      DERConstructedSequence seq = (DERConstructedSequence) dIn.readObject();
      BigInteger r = ((DERInteger) seq.getObjectAt(0)).getValue();
      BigInteger s = ((DERInteger) seq.getObjectAt(1)).getValue();
      byte rbytes[] = r.toByteArray();
      byte sbytes[] = s.toByteArray();
      byte result[] = new byte[40];

      System.arraycopy(rbytes, 0, result, 0, 20);
      System.arraycopy(sbytes, 0, result, 20, 20);

      return result;
   }
}
