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



import java.util.Collection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.w3c.dom.*;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import java.io.IOException;


/**
 * Digest Message wrapper & selector class.
 *
 * <pre>
 * MessageDigestAlgorithm.getInstance()
 * </pre>
 *
 */
public class MessageDigestAlgorithm extends Algorithm {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(MessageDigestAlgorithm.class.getName());

   // Message Digest - NOT RECOMMENDED MD5
   public static final String ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5 = Constants.MoreAlgorithmsSpecNS + "md5";
   // Digest - Required SHA1
   public static final String ALGO_ID_DIGEST_SHA1 = Constants.SignatureSpecNS + "sha1";
   // Message Digest - RECOMMENDED SHA256
   public static final String ALGO_ID_DIGEST_SHA256 = EncryptionConstants.EncryptionSpecNS + "sha256";
   // Message Digest - OPTIONAL SHA384
   public static final String ALGO_ID_DIGEST_SHA384 = Constants.MoreAlgorithmsSpecNS + "sha384";
   // Message Digest - OPTIONAL SHA512
   public static final String ALGO_ID_DIGEST_SHA512 = EncryptionConstants.EncryptionSpecNS + "sha512";
   // Message Digest - OPTIONAL RIPEMD-160
   public static final String ALGO_ID_DIGEST_RIPEMD160 = EncryptionConstants.EncryptionSpecNS + "ripemd160";

   /** Field algorithm stores the actual {@link java.security.MessageDigest} */
   java.security.MessageDigest algorithm = null;

   /**
    * Constructor for the brave who pass their own message digest algorithms and the corresponding URI.
    * @param doc
    * @param messageDigest
    * @param algorithmURI
    */
   private MessageDigestAlgorithm(Document doc, MessageDigest messageDigest,
                                  String algorithmURI) {

      super(doc, algorithmURI);

      this.algorithm = messageDigest;
   }

   /**
    * Factory method for constructing a message digest algorithm by name.
    *
    * @param doc
    * @param algorithmURI
    * @return
    * @throws XMLSignatureException
    */
   public static MessageDigestAlgorithm getInstance(
           Document doc, String algorithmURI) throws XMLSignatureException {

      JCEMapper.ProviderIdClass algorithmID =
         JCEMapper.translateURItoJCEID(algorithmURI);
      MessageDigest md;

      try {
         md = MessageDigest.getInstance(algorithmID.getAlgorithmID(),
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

      return new MessageDigestAlgorithm(doc, md, algorithmURI);
   }

   /**
    * Returns the actual {@link java.security.MessageDigest} algorithm object
    *
    * @return the actual {@link java.security.MessageDigest} algorithm object
    */
   public java.security.MessageDigest getAlgorithm() {
      return this.algorithm;
   }

   /**
    * Proxy method for {@link java.security.MessageDigest#isEqual}
    * which is executed on the internal {@link java.security.MessageDigest} object.
    *
    * @param digesta
    * @param digestb
    * @return the result of the {@link java.security.MessageDigest#isEqual} method
    * @see org.apache.xml.security.util.JavaUtils#binaryCompare
    */
   public static boolean isEqual(byte[] digesta, byte[] digestb) {
      return java.security.MessageDigest.isEqual(digesta, digestb);
   }

   /**
    * Proxy method for {@link java.security.MessageDigest#digest}
    * which is executed on the internal {@link java.security.MessageDigest} object.
    *
    * @return the result of the {@link java.security.MessageDigest#digest} method
    */
   public byte[] digest() {
      return this.algorithm.digest();
   }

   /**
    * Proxy method for {@link java.security.MessageDigest#digest}
    * which is executed on the internal {@link java.security.MessageDigest} object.
    *
    * @param input
    * @return the result of the {@link java.security.MessageDigest#digest} method
    */
   public byte[] digest(byte input[]) {
      return this.algorithm.digest(input);
   }

   /**
    * Proxy method for {@link java.security.MessageDigest#digest}
    * which is executed on the internal {@link java.security.MessageDigest} object.
    *
    * @param buf
    * @param offset
    * @param len
    * @return the result of the {@link java.security.MessageDigest#digest} method
    * @throws java.security.DigestException
    */
   public int digest(byte buf[], int offset, int len)
           throws java.security.DigestException {
      return this.algorithm.digest(buf, offset, len);
   }

   /**
    * Proxy method for {@link java.security.MessageDigest#getAlgorithm}
    * which is executed on the internal {@link java.security.MessageDigest} object.
    *
    * @return the result of the {@link java.security.MessageDigest#getAlgorithm} method
    */
   public String getJCEAlgorithmString() {
      return this.algorithm.getAlgorithm();
   }

   /**
    * Proxy method for {@link java.security.MessageDigest#getProvider}
    * which is executed on the internal {@link java.security.MessageDigest} object.
    *
    * @return the result of the {@link java.security.MessageDigest#getProvider} method
    */
   public java.security.Provider getJCEProvider() {
      return this.algorithm.getProvider();
   }

   /**
    * Proxy method for {@link java.security.MessageDigest#getDigestLength}
    * which is executed on the internal {@link java.security.MessageDigest} object.
    *
    * @return the result of the {@link java.security.MessageDigest#getDigestLength} method
    */
   public int getDigestLength() {
      return this.algorithm.getDigestLength();
   }

   /**
    * Proxy method for {@link java.security.MessageDigest#reset}
    * which is executed on the internal {@link java.security.MessageDigest} object.
    *
    */
   public void reset() {
      this.algorithm.reset();
   }

   /**
    * Proxy method for {@link java.security.MessageDigest#update}
    * which is executed on the internal {@link java.security.MessageDigest} object.
    *
    * @param input
    */
   public void update(byte[] input) {
      this.algorithm.update(input);
   }

   /**
    * Proxy method for {@link java.security.MessageDigest#update}
    * which is executed on the internal {@link java.security.MessageDigest} object.
    *
    * @param input
    */
   public void update(byte input) {
      this.algorithm.update(input);
   }

   /**
    * Proxy method for {@link java.security.MessageDigest#update}
    * which is executed on the internal {@link java.security.MessageDigest} object.
    *
    * @param buf
    * @param offset
    * @param len
    */
   public void update(byte buf[], int offset, int len) {
      this.algorithm.update(buf, offset, len);
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
      return Constants._TAG_DIGESTMETHOD;
   }
}
