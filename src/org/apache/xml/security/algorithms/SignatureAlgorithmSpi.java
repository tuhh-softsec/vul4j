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



import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.signature.XMLSignatureException;
import org.w3c.dom.*;


/**
 *
 * @author $Author$
 */
public abstract class SignatureAlgorithmSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(SignatureAlgorithmSpi.class.getName());

   /** Field _signatureAlgorithmObject */
   private SignatureAlgorithm _signatureAlgorithmObject = null;

   /**
    * Returns the URI representation of <code>Transformation algorithm</code>
    *
    * @return the URI representation of <code>Transformation algorithm</code>
    */
   protected abstract String engineGetURI();

   /**
    * Proxy method for {@link java.security.Signature#getAlgorithm}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @return the result of the {@link java.security.Signature#getAlgorithm} method
    */
   protected abstract String engineGetJCEAlgorithmString();

   /**
    * Method engineGetJCEProviderName
    *
    * @return
    */
   protected abstract String engineGetJCEProviderName();

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param input
    * @throws XMLSignatureException
    */
   protected abstract void engineUpdate(byte[] input)
      throws XMLSignatureException;

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param input
    * @throws XMLSignatureException
    */
   protected abstract void engineUpdate(byte input)
      throws XMLSignatureException;

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param buf
    * @param offset
    * @param len
    * @throws XMLSignatureException
    */
   protected abstract void engineUpdate(byte buf[], int offset, int len)
      throws XMLSignatureException;

   /**
    * Proxy method for {@link java.security.Signature#initSign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param privateKey
    * @param secureRandom
    * @throws XMLSignatureException
    */
   protected abstract void engineInitSign(
      PrivateKey privateKey, SecureRandom secureRandom)
         throws XMLSignatureException;

   /**
    * Proxy method for {@link java.security.Signature#initSign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param privateKey
    * @throws XMLSignatureException
    */
   protected abstract void engineInitSign(PrivateKey privateKey)
      throws XMLSignatureException;

   /**
    * Proxy method for {@link javax.crypto.Mac}
    * which is executed on the internal {@link javax.crypto.Mac#init(Key)} object.
    *
    * @param secretKey
    * @throws XMLSignatureException
    */
   protected abstract void engineInitSign(Key secretKey)
      throws XMLSignatureException;

   /**
    * Proxy method for {@link javax.crypto.Mac}
    * which is executed on the internal {@link javax.crypto.Mac#init(Key)} object.
    *
    * @param secretKey
    * @param algorithmParameterSpec
    * @throws XMLSignatureException
    */
   protected abstract void engineInitSign(
      Key secretKey, AlgorithmParameterSpec algorithmParameterSpec)
         throws XMLSignatureException;

   /**
    * Proxy method for {@link java.security.Signature#sign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @return the result of the {@link java.security.Signature#sign} method
    * @throws XMLSignatureException
    */
   protected abstract byte[] engineSign() throws XMLSignatureException;

   /**
    * Proxy method for {@link java.security.Signature#initVerify}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param publickey
    * @throws XMLSignatureException
    */
   protected abstract void engineInitVerify(PublicKey publickey)
      throws XMLSignatureException;

   /**
    * Method engineInitVerify
    *
    * @param secretkey
    * @throws XMLSignatureException
    */
   protected abstract void engineInitVerify(Key secretkey)
      throws XMLSignatureException;

   /**
    * Proxy method for {@link java.security.Signature#verify}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param signature
    * @return
    * @throws XMLSignatureException
    */
   protected abstract boolean engineVerify(byte[] signature)
      throws XMLSignatureException;

   /**
    * Proxy method for {@link java.security.Signature#setParameter}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param params
    * @throws XMLSignatureException
    */
   protected abstract void engineSetParameter(AlgorithmParameterSpec params)
      throws XMLSignatureException;

   /** Field _doc */
   Document _doc = null;

   /**
    * Method engineSetDocument
    *
    * @param doc
    */
   protected void engineSetDocument(Document doc) {
      this._doc = doc;
   }

   /** Field _constructionElement */
   Element _constructionElement = null;

   /**
    * Method engineGetContextFromElement
    *
    * @param element
    * @throws XMLSignatureException
    */
   protected void engineGetContextFromElement(Element element)
           throws XMLSignatureException {
      this._constructionElement = element;
   }

   /**
    * Method engineAddContextToElement
    *
    * @param element
    * @throws XMLSignatureException
    */
   protected void engineAddContextToElement(Element element)
           throws XMLSignatureException {}

   /**
    * Method engineSetHMACOutputLength
    *
    * @param HMACOutputLength
    * @throws XMLSignatureException
    */
   protected abstract void engineSetHMACOutputLength(int HMACOutputLength)
      throws XMLSignatureException;

   static {
      org.apache.xml.security.Init.init();
   }
}
