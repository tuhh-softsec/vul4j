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



import java.security.interfaces.DSAPublicKey;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.math.BigInteger;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.*;


/**
 *
 * @author $Author$
 */
public class DSAKeyValue extends ElementProxy implements KeyValueContent {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(DSAKeyValue.class.getName());

   /**
    * Constructor DSAKeyValue
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public DSAKeyValue(Element element, String BaseURI)
           throws XMLSecurityException {

      super(element, BaseURI);

      XMLUtils.guaranteeThatElementInSignatureSpace(element,
              Constants._TAG_DSAKEYVALUE);

      /*
      Text seedText = (Text) XPathAPI.selectSingleNode(element,
                         "./ds:" + Constants._TAG_SEED + "/text()", nscontext);
      Text counterText = (Text) XPathAPI.selectSingleNode(element,
                            "./ds:" + Constants._TAG_PGENCOUNTER + "/text()",
                            nscontext);

      if ((counterText != null) && (seedText != null)) {
         this._PgenCounter = Base64.decodeBigIntegerFromText(counterText);
         this._Seed = Base64.decodeBigIntegerFromText(seedText);
      }
      */
   }

   /**
    * Constructor DSAKeyValue
    *
    * @param doc
    * @param P
    * @param Q
    * @param G
    * @param Y
    */
   public DSAKeyValue(Document doc, BigInteger P, BigInteger Q, BigInteger G,
                      BigInteger Y) {

      super(doc, Constants._TAG_DSAKEYVALUE);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));

      this.addBigIntegerElement(P, Constants._TAG_P);
      this.addBigIntegerElement(Q, Constants._TAG_Q);
      this.addBigIntegerElement(G, Constants._TAG_G);
      this.addBigIntegerElement(Y, Constants._TAG_Y);
   }

   /**
    * Constructor DSAKeyValue
    *
    * @param doc
    * @param key
    * @throws IllegalArgumentException
    */
   public DSAKeyValue(Document doc, Key key) throws IllegalArgumentException {

      super(doc, Constants._TAG_DSAKEYVALUE);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));

      if (JavaUtils.implementsInterface(
              (Object) key, "java.security.interfaces.DSAPublicKey")) {
         this.addBigIntegerElement(((DSAPublicKey) key).getParams().getP(), Constants._TAG_P);
         this.addBigIntegerElement(((DSAPublicKey) key).getParams().getQ(), Constants._TAG_Q);
         this.addBigIntegerElement(((DSAPublicKey) key).getParams().getG(), Constants._TAG_G);
         this.addBigIntegerElement(((DSAPublicKey) key).getY(), Constants._TAG_Y);
      } else {
         Object exArgs[] = { Constants._TAG_DSAKEYVALUE,
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
         DSAPublicKeySpec pkspec =
            new DSAPublicKeySpec(this.getBigIntegerFromChildElement(Constants._TAG_Y),
                                 this.getBigIntegerFromChildElement(Constants._TAG_P),
                                 this.getBigIntegerFromChildElement(Constants._TAG_Q),
                                 this.getBigIntegerFromChildElement(Constants._TAG_G));
         KeyFactory dsaFactory = KeyFactory.getInstance("DSA");
         PublicKey pk = dsaFactory.generatePublic(pkspec);

         return pk;
      } catch (NoSuchAlgorithmException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (InvalidKeySpecException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
