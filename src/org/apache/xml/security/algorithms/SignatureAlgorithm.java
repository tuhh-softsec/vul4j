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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.MessageDigest;
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
import java.util.HashMap;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.algorithms.implementations.*;


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

   /** Field _alreadyInitialized */
   static boolean _alreadyInitialized = false;

   /** All available algorithm classes are registered here */
   static HashMap _algorithmHash = null;

   /** Field _signatureAlgorithm */
   protected SignatureAlgorithmSpi _signatureAlgorithm = null;

   /**
    * Constructor SignatureAlgorithm
    *
    * @param doc
    * @param algorithmURI
    * @throws XMLSecurityException
    */
   public SignatureAlgorithm(Document doc, String algorithmURI)
           throws XMLSecurityException {

      super(doc, algorithmURI);

      try {
         String implementingClass =
            SignatureAlgorithm.getImplementingClass(algorithmURI);

         cat.debug("Create URI \"" + algorithmURI + "\" class \""
                   + implementingClass + "\"");

         this._signatureAlgorithm =
            (SignatureAlgorithmSpi) Class.forName(implementingClass)
               .newInstance();
      } catch (ClassNotFoundException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
      } catch (IllegalAccessException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
      } catch (InstantiationException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
      } catch (NullPointerException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
      }
   }

   /**
    * Constructor SignatureAlgorithm
    *
    * @param doc
    * @param algorithmURI
    * @param HMACOutputLength
    * @throws XMLSecurityException
    */
   public SignatureAlgorithm(
           Document doc, String algorithmURI, int HMACOutputLength)
              throws XMLSecurityException {

      this(doc, algorithmURI);

      this._signatureAlgorithm.engineSetHMACOutputLength(HMACOutputLength);
      this._signatureAlgorithm
         .engineAddContextToElement(this._constructionElement);
   }

   /**
    * Constructor SignatureAlgorithm
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public SignatureAlgorithm(Element element, String BaseURI)
           throws XMLSecurityException {

      super(element, BaseURI);

      String algorithmURI = this.getURI();

      try {
         String implementingClass =
            SignatureAlgorithm.getImplementingClass(algorithmURI);

         cat.debug("Create URI \"" + algorithmURI + "\" class \""
                   + implementingClass + "\"");

         this._signatureAlgorithm =
            (SignatureAlgorithmSpi) Class.forName(implementingClass)
               .newInstance();

         this._signatureAlgorithm
            .engineGetContextFromElement(this._constructionElement);
      } catch (ClassNotFoundException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
      } catch (IllegalAccessException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
      } catch (InstantiationException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
      } catch (NullPointerException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };
         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#sign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @return the result of the {@link java.security.Signature#sign} method
    * @throws XMLSignatureException
    */
   public byte[] sign() throws XMLSignatureException {
      return this._signatureAlgorithm.engineSign();
   }

   /**
    * Proxy method for {@link java.security.Signature#getAlgorithm}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @return the result of the {@link java.security.Signature#getAlgorithm} method
    */
   public String getJCEAlgorithmString() {
      return this._signatureAlgorithm.engineGetJCEAlgorithmString();
   }

   /**
    * Method getJCEProviderName
    *
    * @return
    */
   public String getJCEProviderName() {
      return this._signatureAlgorithm.engineGetJCEProviderName();
   }

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param input
    * @throws XMLSignatureException
    */
   public void update(byte[] input) throws XMLSignatureException {
      this._signatureAlgorithm.engineUpdate(input);
   }

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param input
    * @throws XMLSignatureException
    */
   public void update(byte input) throws XMLSignatureException {
      this._signatureAlgorithm.engineUpdate(input);
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
   public void update(byte buf[], int offset, int len)
           throws XMLSignatureException {
      this._signatureAlgorithm.engineUpdate(buf, offset, len);
   }

   /**
    * Proxy method for {@link java.security.Signature#initSign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param privateKey
    * @param secureRandom
    * @throws XMLSignatureException
    */
   public void initSign(PrivateKey privateKey, SecureRandom secureRandom)
           throws XMLSignatureException {
      this._signatureAlgorithm.engineInitSign(privateKey, secureRandom);
   }

   /**
    * Proxy method for {@link java.security.Signature#initSign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param privateKey
    * @throws XMLSignatureException
    */
   public void initSign(PrivateKey privateKey) throws XMLSignatureException {
      this._signatureAlgorithm.engineInitSign(privateKey);
   }

   /**
    * Method initSign
    *
    * @param secretKey
    * @throws XMLSignatureException
    */
   public void initSign(Key secretKey) throws XMLSignatureException {
      this._signatureAlgorithm.engineInitSign(secretKey);
   }

   /**
    * Method initSign
    *
    * @param secretKey
    * @param algorithmParameterSpec
    * @throws XMLSignatureException
    */
   public void initSign(
           Key secretKey, AlgorithmParameterSpec algorithmParameterSpec)
              throws XMLSignatureException {
      this._signatureAlgorithm.engineInitSign(secretKey,
                                              algorithmParameterSpec);
   }

   /**
    * Proxy method for {@link java.security.Signature#setParameter}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param params
    * @throws XMLSignatureException
    */
   public void setParameter(AlgorithmParameterSpec params)
           throws XMLSignatureException {
      this._signatureAlgorithm.engineSetParameter(params);
   }

   /**
    * Proxy method for {@link java.security.Signature#initVerify}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param publickey
    * @throws XMLSignatureException
    */
   public void initVerify(PublicKey publickey) throws XMLSignatureException {
      this._signatureAlgorithm.engineInitVerify(publickey);
   }

   /**
    * Method initVerify
    *
    * @param secretkey
    * @throws XMLSignatureException
    */
   public void initVerify(Key secretkey) throws XMLSignatureException {

      if (secretkey instanceof java.security.PublicKey) {
         this.initVerify((PublicKey) secretkey);
      } else {
         this._signatureAlgorithm.engineInitVerify(secretkey);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#verify}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param signature
    * @return
    * @throws XMLSignatureException
    */
   public boolean verify(byte[] signature) throws XMLSignatureException {
      return this._signatureAlgorithm.engineVerify(signature);
   }

   /**
    * Returns the URI representation of Transformation algorithm
    *
    * @return the URI representation of Transformation algorithm
    */
   public final String getURI() {
      return this._constructionElement.getAttributeNS(null, Constants._ATT_ALGORITHM);
   }

   /**
    * Initalizes for this {@link Transform}
    *
    */
   public static void providerInit() {

      if (SignatureAlgorithm.cat == null) {
         SignatureAlgorithm.cat =
            org.apache.log4j.Category
               .getInstance(SignatureAlgorithm.class.getName());
      }

      cat.debug("Init() called");

      if (!SignatureAlgorithm._alreadyInitialized) {
         SignatureAlgorithm._algorithmHash = new HashMap(10);
         SignatureAlgorithm._alreadyInitialized = true;
      }
   }

   /**
    * Registers implementing class of the transfrom algorithm with algorithmURI
    *
    * @param algorithmURI algorithmURI URI representation of <code>transfrom algorithm</code> will be specified as parameter of {@link #getInstance}, when generate. </br>
    * @param implementingClass <code>implementingClass</code> the implementing class of {@link TransformSpi}
    * @throws AlgorithmAlreadyRegisteredException if specified algorithmURI is already registered
    */
   public static void register(String algorithmURI, String implementingClass)
           throws AlgorithmAlreadyRegisteredException {

      {
         cat.debug("Try to register " + algorithmURI + " " + implementingClass);

         // are we already registered?
         String registeredClass =
            SignatureAlgorithm.getImplementingClass(algorithmURI);

         if ((registeredClass != null) && (registeredClass.length() != 0)) {
            Object exArgs[] = { algorithmURI, registeredClass };

            throw new AlgorithmAlreadyRegisteredException(
               "algorithm.alreadyRegistered", exArgs);
         }

         SignatureAlgorithm._algorithmHash.put(algorithmURI, implementingClass);
      }
   }

   /**
    * Method getImplementingClass
    *
    * @param URI
    * @return
    */
   private static String getImplementingClass(String URI) {

      if (SignatureAlgorithm._algorithmHash == null) {
         return null;
      }

      return (String) SignatureAlgorithm._algorithmHash.get(URI);
   }

   /**
    * Method getBaseNamespace
    *
    * @return
    */
   public String getBaseNamespace() {
      return Constants.SignatureSpecNS;
   }

   public String getBaseLocalName() {
      return Constants._TAG_SIGNATUREMETHOD;
   }
}
