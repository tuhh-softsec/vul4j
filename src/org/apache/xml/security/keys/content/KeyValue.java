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
package org.apache.xml.security.keys.content;



import java.security.*;
import java.security.spec.*;
import java.util.*;
import org.w3c.dom.*;
import org.apache.xpath.XPathAPI;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.keys.content.keyvalues.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.exceptions.XMLSecurityException;


/**
 * The KeyValue element contains a single public key that may be useful in
 * validating the signature. Structured formats for defining DSA (REQUIRED)
 * and RSA (RECOMMENDED) public keys are defined in Signature Algorithms
 * (section 6.4). The KeyValue element may include externally defined public
 * keys values represented as PCDATA or element types from an external namespace.
 *
 * @author $Author$
 */
public class KeyValue extends ElementProxy implements KeyInfoContent {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(KeyValue.class.getName());

   /**
    * Constructor KeyValue
    *
    * @param doc
    * @param dsaKeyValue
    */
   public KeyValue(Document doc, DSAKeyValue dsaKeyValue) {

      super(doc, Constants._TAG_KEYVALUE);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));

      this._constructionElement.appendChild(dsaKeyValue.getElement());
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   /**
    * Constructor KeyValue
    *
    * @param doc
    * @param rsaKeyValue
    */
   public KeyValue(Document doc, RSAKeyValue rsaKeyValue) {

      super(doc, Constants._TAG_KEYVALUE);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));

      this._constructionElement.appendChild(rsaKeyValue.getElement());
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   public KeyValue(Document doc, Element unknownKeyValue) {

      super(doc, Constants._TAG_KEYVALUE);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));

      this._constructionElement.appendChild(unknownKeyValue);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   /**
    * Constructor KeyValue
    *
    * @param doc
    * @param pk
    */
   public KeyValue(Document doc, PublicKey pk) {

      super(doc, Constants._TAG_KEYVALUE);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));

      if (JavaUtils.implementsInterface(
              (Object) pk, "java.security.interfaces.DSAPublicKey")) {
         DSAKeyValue dsa = new DSAKeyValue(this._doc, (Key) pk);

         this._constructionElement.appendChild(dsa.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      } else if (JavaUtils.implementsInterface(
              (Object) pk, "java.security.interfaces.RSAPublicKey")) {
         RSAKeyValue rsa = new RSAKeyValue(this._doc, (Key) pk);

         this._constructionElement.appendChild(rsa.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Constructor KeyValue
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public KeyValue(Element element, String BaseURI)
           throws XMLSecurityException {

      super(element, BaseURI);

      XMLUtils.guaranteeThatElementInSignatureSpace(element,
              Constants._TAG_KEYVALUE);
   }

   /**
    * Method getPublicKey
    *
    * @return
    * @throws XMLSecurityException
    */
   public PublicKey getPublicKey() throws XMLSecurityException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds",
                                                  Constants.SignatureSpecNS);
         NodeList rsa =
            XPathAPI.selectNodeList(this._constructionElement,
                                    "./ds:" + Constants._TAG_RSAKEYVALUE,
                                    nscontext);

         if (rsa.getLength() > 0) {
            RSAKeyValue kv = new RSAKeyValue((Element) rsa.item(0),
                                             this._baseURI);

            return kv.getPublicKey();
         }

         NodeList dsa =
            XPathAPI.selectNodeList(this._constructionElement,
                                    "./ds:" + Constants._TAG_DSAKEYVALUE,
                                    nscontext);

         if (dsa.getLength() > 0) {
            DSAKeyValue kv = new DSAKeyValue((Element) dsa.item(0),
                                             this._baseURI);

            return kv.getPublicKey();
         }
      } catch (TransformerException ex) {}

      return null;
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
