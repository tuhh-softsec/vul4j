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



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.RetrievalMethod;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * The RetrievalMethodResolver can retrieve public keys and certificates from
 * other locations. The location is specified using the ds:RetrievalMethod
 * element which points to the location. This includes the handling of raw
 * (binary) X.509 certificate which are not encapsulated in an XML structure.
 * If the retrieval process encounters an element which the
 * RetrievalMethodResolver cannot handle itself, resolving of the extracted
 * element is delegated back to the KeyResolver mechanism.
 *
 * @author $Author$
 */
public class RetrievalMethodResolver extends KeyResolverSpi {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
                        RetrievalMethodResolver.class.getName());

   /**
    * Method engineCanResolve
    * @inheritDoc
    * @param element
    * @param BaseURI
    * @param storage
    *
    */
   public boolean engineCanResolve(Element element, String BaseURI,
                                   StorageResolver storage) {

      if 
         (!XMLUtils.elementIsInSignatureSpace(element,
                 Constants._TAG_RETRIEVALMETHOD)) {      
         return false;
      }

      return true;
   }

   /**
    * Method engineResolvePublicKey
    * @inheritDoc
    * @param element
    * @param BaseURI
    * @param storage
    *
    */
   public PublicKey engineResolvePublicKey(
           Element element, String BaseURI, StorageResolver storage)
              {

      try {
         RetrievalMethod rm = new RetrievalMethod(element, BaseURI);
         Attr uri = rm.getURIAttr();

         // type can be null because it's optional
         String type = rm.getType();
         Transforms transforms = rm.getTransforms();
         ResourceResolver resRes = ResourceResolver.getInstance(uri, BaseURI);

         if (resRes != null) {
            XMLSignatureInput resource = resRes.resolve(uri, BaseURI);
            if (log.isDebugEnabled())
            	log.debug("Before applying Transforms, resource has "
                      + resource.getBytes().length + "bytes");

            if (transforms != null) {
               log.debug("We have Transforms");

               resource = transforms.performTransforms(resource);
            }
            if (log.isDebugEnabled()) {
            	log.debug("After applying Transforms, resource has "
                      + resource.getBytes().length + "bytes");
            	log.debug("Resolved to resource " + resource.getSourceURI());
            }

            byte inputBytes[] = resource.getBytes();

            if ((type != null) && type.equals(RetrievalMethod.TYPE_RAWX509)) {

               // if the resource stores a raw certificate, we have to handle it
               CertificateFactory certFact =
                  CertificateFactory
                     .getInstance(XMLX509Certificate.JCA_CERT_ID);
               X509Certificate cert =
                  (X509Certificate) certFact
                     .generateCertificate(new ByteArrayInputStream(inputBytes));

               if (cert != null) {
                  return cert.getPublicKey();
               }
            } else {

               // otherwise, we parse the resource, create an Element and delegate
                if (log.isDebugEnabled())
                	log.debug("we have to parse " + inputBytes.length + " bytes");

               Element e = this.getDocFromBytes(inputBytes);
               if (log.isDebugEnabled())
               	    log.debug("Now we have a {" + e.getNamespaceURI() + "}"
                         + e.getLocalName() + " Element");

               if (e != null) {
                  KeyResolver newKeyResolver = KeyResolver.getInstance(getFirstElementChild(e),
                                                  BaseURI, storage);

                  if (newKeyResolver != null) {
                     return newKeyResolver.resolvePublicKey(getFirstElementChild(e), BaseURI,
                                                            storage);
                  }
               }
            }
         }
      } catch (XMLSecurityException ex) {
         log.debug("XMLSecurityException", ex);
      } catch (CertificateException ex) {
         log.debug("CertificateException", ex);
      } catch (IOException ex) {
         log.debug("IOException", ex);
      }

      return null;
   }

   /**
    * Method engineResolveX509Certificate
    * @inheritDoc
    * @param element
    * @param BaseURI
    * @param storage
    *
    */
   public X509Certificate engineResolveX509Certificate(
           Element element, String BaseURI, StorageResolver storage)
              {

      try {
         RetrievalMethod rm = new RetrievalMethod(element, BaseURI);
         Attr uri = rm.getURIAttr();
         Transforms transforms = rm.getTransforms();
         if (log.isDebugEnabled())
         	log.debug("Asked to resolve URI " + uri);

         ResourceResolver resRes = ResourceResolver.getInstance(uri, BaseURI);

         if (resRes != null) {
            XMLSignatureInput resource = resRes.resolve(uri, BaseURI);
            if (log.isDebugEnabled())
            	log.debug("Before applying Transforms, resource has "
                      + resource.getBytes().length + "bytes");

            if (transforms != null) {
               log.debug("We have Transforms");

               resource = transforms.performTransforms(resource);
            }
            
            if (log.isDebugEnabled()) {
            	log.debug("After applying Transforms, resource has "
                      + resource.getBytes().length + "bytes");
            	log.debug("Resolved to resource " + resource.getSourceURI());
            }

            byte inputBytes[] = resource.getBytes();

            if ((rm.getType() != null)
                    && rm.getType().equals(RetrievalMethod.TYPE_RAWX509)) {

               // if the resource stores a raw certificate, we have to handle it
               CertificateFactory certFact =
                  CertificateFactory
                     .getInstance(XMLX509Certificate.JCA_CERT_ID);
               X509Certificate cert =
                  (X509Certificate) certFact
                     .generateCertificate(new ByteArrayInputStream(inputBytes));

               if (cert != null) {
                  return cert;
               }
            } else {

               // otherwise, we parse the resource, create an Element and delegate
                if (log.isDebugEnabled())
                	log.debug("we have to parse " + inputBytes.length + " bytes");

               Element e = this.getDocFromBytes(inputBytes);

               if (log.isDebugEnabled())
               	    log.debug("Now we have a {" + e.getNamespaceURI() + "}"
                         + e.getLocalName() + " Element");

               if (e != null) {
                  KeyResolver newKeyResolver = KeyResolver.getInstance(getFirstElementChild(e),
                                                  BaseURI, storage);

                  if (newKeyResolver != null) {
                     return newKeyResolver.resolveX509Certificate(getFirstElementChild(e), BaseURI,
                             storage);
                  }
               }
            }
         }
      } catch (XMLSecurityException ex) {
         log.debug("XMLSecurityException", ex);
      } catch (CertificateException ex) {
         log.debug("CertificateException", ex);
      } catch (IOException ex) {
         log.debug("IOException", ex);
      }

      return null;
   }

   /**
    * Parses a byte array and returns the parsed Element.
    *
    * @param bytes
    * @return the Document Element after parsing bytes 
    * @throws KeyResolverException if something goes wrong
    */
   Element getDocFromBytes(byte[] bytes) throws KeyResolverException {

      try {
         javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

         dbf.setNamespaceAware(true);

         javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
         org.w3c.dom.Document doc =
            db.parse(new java.io.ByteArrayInputStream(bytes));

         return doc.getDocumentElement();
      } catch (org.xml.sax.SAXException ex) {
         throw new KeyResolverException("empty", ex);
      } catch (java.io.IOException ex) {
         throw new KeyResolverException("empty", ex);
      } catch (javax.xml.parsers.ParserConfigurationException ex) {
         throw new KeyResolverException("empty", ex);
      }
   }

   /**
    * Method engineResolveSecretKey
    * @inheritDoc
    * @param element
    * @param BaseURI
    * @param storage
    *
    */
   public javax.crypto.SecretKey engineResolveSecretKey(
           Element element, String BaseURI, StorageResolver storage)
   {
      return null;
   }
   static Element getFirstElementChild(Element e){
   	    Node n=e.getFirstChild();
   	    while (n!=null && n.getNodeType()!=Node.ELEMENT_NODE) {
   	    	n=n.getNextSibling();
   	    }
   		return (Element)n;
   }
}
