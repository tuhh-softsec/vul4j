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
package org.apache.xml.security.keys.content.keyvalues;



import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.*;


/**
 *
 * @author $Author$
 */
public class RSAKeyValue extends SignatureElementProxy
        implements KeyValueContent {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(RSAKeyValue.class.getName());

   /**
    * Constructor RSAKeyValue
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public RSAKeyValue(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Constructor RSAKeyValue
    *
    * @param doc
    * @param modulus
    * @param exponent
    */
   public RSAKeyValue(Document doc, BigInteger modulus, BigInteger exponent) {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);
      this.addBigIntegerElement(modulus, Constants._TAG_MODULUS);
      this.addBigIntegerElement(exponent, Constants._TAG_EXPONENT);
   }

   /**
    * Constructor RSAKeyValue
    *
    * @param doc
    * @param key
    * @throws IllegalArgumentException
    */
   public RSAKeyValue(Document doc, Key key) throws IllegalArgumentException {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);

      if (JavaUtils.implementsInterface(
              (Object) key, "java.security.interfaces.RSAPublicKey")) {
         this.addBigIntegerElement(((RSAPublicKey) key).getModulus(),
                                   Constants._TAG_MODULUS);
         this.addBigIntegerElement(((RSAPublicKey) key).getPublicExponent(),
                                   Constants._TAG_EXPONENT);
      } else {
         Object exArgs[] = { Constants._TAG_RSAKEYVALUE,
                             key.getClass().getName() };

         throw new IllegalArgumentException(I18n
            .translate("KeyValue.IllegalArgument", exArgs));
      }
   }

   /**
    * Method getPublicKey
    *
    * @return
    * @throws XMLSecurityException
    */
   public PublicKey getPublicKey() throws XMLSecurityException {

      try {
         KeyFactory rsaFactory = KeyFactory.getInstance("RSA");

         // String JCE_RSA = org.apache.xml.security.algorithms.JCEMapper.translateURItoJCEID(Constants.ALGO_ID_SIGNATURE_RSA);
         // KeyFactory rsaFactory = KeyFactory.getInstance(JCE_RSA);
         RSAPublicKeySpec rsaKeyspec =
            new RSAPublicKeySpec(this
               .getBigIntegerFromChildElement(Constants._TAG_MODULUS, Constants
               .SignatureSpecNS), this
                  .getBigIntegerFromChildElement(Constants
                     ._TAG_EXPONENT, Constants.SignatureSpecNS));
         PublicKey pk = rsaFactory.generatePublic((KeySpec) rsaKeyspec);

         return pk;
      } catch (NoSuchAlgorithmException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (InvalidKeySpecException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   public String getBaseLocalName() {
      return Constants._TAG_RSAKEYVALUE;
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
