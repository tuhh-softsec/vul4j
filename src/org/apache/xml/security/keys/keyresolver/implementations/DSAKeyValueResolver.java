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
package org.apache.xml.security.keys.keyresolver.implementations;



import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.xml.transform.TransformerException;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.keyvalues.DSAKeyValue;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;


/**
 *
 * @author $Author$
 */
public class DSAKeyValueResolver extends KeyResolverSpi {

   /** Field _dsaKeyElement */
   private Element _dsaKeyElement = null;

   /**
    * Method engineCanResolve
    *
    * @param element
    * @param BaseURI
    * @param storage
    *
    */
   public boolean engineCanResolve(Element element, String BaseURI,
                                   StorageResolver storage) {

      if (element == null) {
         return false;
      }

      boolean isKeyValue = XMLUtils.elementIsInSignatureSpace(element,
                              Constants._TAG_KEYVALUE);
      boolean isDSAKeyValue = XMLUtils.elementIsInSignatureSpace(element,
                                 Constants._TAG_DSAKEYVALUE);

      if (isKeyValue) {
         try {
            Element nscontext = XMLUtils.createDSctx(element.getOwnerDocument(),
                                                     "ds",
                                                     Constants.SignatureSpecNS);

            this._dsaKeyElement = (Element) XPathAPI.selectSingleNode(element,
                    "./ds:" + Constants._TAG_DSAKEYVALUE, nscontext);

            if (this._dsaKeyElement != null) {
               return true;
            }
         } catch (TransformerException ex) {}
      } else if (isDSAKeyValue) {

         // this trick is needed to allow the RetrievalMethodResolver to eat a
         // ds:DSAKeyValue directly (without KeyValue)
         this._dsaKeyElement = element;

         return true;
      }

      return false;
   }

   /**
    * Method engineResolvePublicKey
    *
    * @param element
    * @param BaseURI
    * @param storage
    * @return null if no {@link PublicKey} could be obtained
    * @throws KeyResolverException
    */
   public PublicKey engineResolvePublicKey(
           Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {

      if (this._dsaKeyElement == null) {
         boolean weCanResolve = this.engineCanResolve(element, BaseURI,
                                   storage);

         if (!weCanResolve || (this._dsaKeyElement == null)) {
            return null;
         }
      }

      try {
         DSAKeyValue dsaKeyValue = new DSAKeyValue(this._dsaKeyElement,
                                                   BaseURI);
         PublicKey pk = dsaKeyValue.getPublicKey();

         return pk;
      } catch (XMLSecurityException ex) {
		//do nothing
      }

      return null;
   }

   /**
    * Method engineResolveX509Certificate
    *
    * @param element
    * @param BaseURI
    * @param storage
    *
    * @throws KeyResolverException
    */
   public X509Certificate engineResolveX509Certificate(
           Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {
      return null;
   }

   /**
    * Method engineResolveSecretKey
    *
    * @param element
    * @param BaseURI
    * @param storage
    *
    * @throws KeyResolverException
    */
   public javax.crypto.SecretKey engineResolveSecretKey(
           Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {
      return null;
   }
}
