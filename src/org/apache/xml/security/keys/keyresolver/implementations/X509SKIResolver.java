
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
import org.apache.xml.security.keys.content.x509.XMLX509SKI;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 *
 *
 * @author $Author$
 */
public class X509SKIResolver extends KeyResolverSpi {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(X509SKIResolver.class.getName());

   /** Field _x509childNodes */
   private NodeList _x509childNodes = null;

   /** Field _x509childObject[] */
   private XMLX509SKI _x509childObject[] = null;

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

      log.debug("Can I resolve " + element.getTagName() + "?");

      try {
         XMLUtils.guaranteeThatElementInSignatureSpace(element,
                 Constants._TAG_X509DATA);
      } catch (XMLSignatureException ex) {
         log.debug("I can't");

         return false;
      }

      try {
         Element nscontext = XMLUtils.createDSctx(element.getOwnerDocument(), "ds", Constants.SignatureSpecNS);

         this._x509childNodes = XPathAPI.selectNodeList(element,
                 "./ds:" + Constants._TAG_X509SKI, nscontext);

         if ((this._x509childNodes != null)
                 && (this._x509childNodes.getLength() > 0)) {
            log.debug("Yes Sir, I can");

            return true;
         }
      } catch (TransformerException ex) {}

      log.debug("I can't");

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

      X509Certificate cert = this.engineResolveX509Certificate(element,
                                BaseURI, storage);

      if (cert != null) {
         return cert.getPublicKey();
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

      try {
         if (this._x509childNodes == null) {
            boolean weCanResolve = this.engineCanResolve(element, BaseURI,
                                      storage);

            if (!weCanResolve || (this._x509childNodes == null)) {
               return null;
            }
         }

         if (storage == null) {
            Object exArgs[] = { Constants._TAG_X509SKI };
            KeyResolverException ex =
               new KeyResolverException("KeyResolver.needStorageResolver",
                                        exArgs);

            log.info("", ex);

            throw ex;
         }

         this._x509childObject =
            new XMLX509SKI[this._x509childNodes.getLength()];

         for (int i = 0; i < this._x509childNodes.getLength(); i++) {
            this._x509childObject[i] =
               new XMLX509SKI((Element) this._x509childNodes.item(i), BaseURI);
         }

         while (storage.hasNext()) {
            X509Certificate cert = storage.next();
            XMLX509SKI certSKI = new XMLX509SKI(element.getOwnerDocument(), cert);

            for (int i = 0; i < this._x509childObject.length; i++) {
               if (certSKI.equals(this._x509childObject[i])) {
                  log.debug("Return PublicKey from "
                            + cert.getSubjectDN().getName());

                  return cert;
               }
            }
         }
      } catch (XMLSecurityException ex) {
         throw new KeyResolverException("empty", ex);
      }

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
