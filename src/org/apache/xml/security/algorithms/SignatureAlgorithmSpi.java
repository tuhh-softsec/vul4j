/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.algorithms;



import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import org.apache.xml.security.signature.XMLSignatureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 *
 * @author $Author$
 */
public abstract class SignatureAlgorithmSpi {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(SignatureAlgorithmSpi.class.getName());

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
    *
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
    * @param signingKey
    * @throws XMLSignatureException if this method is called on a MAC
    */
   protected abstract void engineInitSign(Key signingKey)
      throws XMLSignatureException;

   /**
    * Proxy method for {@link java.security.Signature#initSign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param signingKey
    * @param secureRandom
    * @throws XMLSignatureException if this method is called on a MAC
    */
   protected abstract void engineInitSign(
      Key signingKey, SecureRandom secureRandom) throws XMLSignatureException;

   /**
    * Proxy method for {@link javax.crypto.Mac}
    * which is executed on the internal {@link javax.crypto.Mac#init(Key)} object.
    *
    * @param signingKey
    * @param algorithmParameterSpec
    * @throws XMLSignatureException if this method is called on a Signature
    */
   protected abstract void engineInitSign(
      Key signingKey, AlgorithmParameterSpec algorithmParameterSpec)
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
    * Method engineInitVerify
    *
    * @param verificationKey
    * @throws XMLSignatureException
    */
   protected abstract void engineInitVerify(Key verificationKey)
      throws XMLSignatureException;

   /**
    * Proxy method for {@link java.security.Signature#verify}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param signature
    *
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
}
