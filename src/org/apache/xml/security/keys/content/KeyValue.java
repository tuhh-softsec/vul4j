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
package org.apache.xml.security.keys.content;



import java.security.PublicKey;

import javax.xml.transform.TransformerException;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.keyvalues.DSAKeyValue;
import org.apache.xml.security.keys.content.keyvalues.RSAKeyValue;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * The KeyValue element contains a single public key that may be useful in
 * validating the signature. Structured formats for defining DSA (REQUIRED)
 * and RSA (RECOMMENDED) public keys are defined in Signature Algorithms
 * (section 6.4). The KeyValue element may include externally defined public
 * keys values represented as PCDATA or element types from an external namespace.
 *
 * @author $Author$
 */
public class KeyValue extends SignatureElementProxy implements KeyInfoContent {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(KeyValue.class.getName());

   /**
    * Constructor KeyValue
    *
    * @param doc
    * @param dsaKeyValue
    */
   public KeyValue(Document doc, DSAKeyValue dsaKeyValue) {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);
      this._constructionElement.appendChild(dsaKeyValue.getElement());
      XMLUtils.addReturnToElement(this._constructionElement);
   }

   /**
    * Constructor KeyValue
    *
    * @param doc
    * @param rsaKeyValue
    */
   public KeyValue(Document doc, RSAKeyValue rsaKeyValue) {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);
      this._constructionElement.appendChild(rsaKeyValue.getElement());
      XMLUtils.addReturnToElement(this._constructionElement);
   }

   /**
    * Constructor KeyValue
    *
    * @param doc
    * @param unknownKeyValue
    */
   public KeyValue(Document doc, Element unknownKeyValue) {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);
      this._constructionElement.appendChild(unknownKeyValue);
      XMLUtils.addReturnToElement(this._constructionElement);
   }

   /**
    * Constructor KeyValue
    *
    * @param doc
    * @param pk
    */
   public KeyValue(Document doc, PublicKey pk) {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);

      if (JavaUtils.implementsInterface(
              pk, "java.security.interfaces.DSAPublicKey")) {
         DSAKeyValue dsa = new DSAKeyValue(this._doc, pk);

         this._constructionElement.appendChild(dsa.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      } else if (JavaUtils.implementsInterface(
              pk, "java.security.interfaces.RSAPublicKey")) {
         RSAKeyValue rsa = new RSAKeyValue(this._doc, pk);

         this._constructionElement.appendChild(rsa.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
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
   }

   /**
    * Method getPublicKey
    *
    *
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

   public String getBaseLocalName() {
      return Constants._TAG_KEYVALUE;
   }
}
