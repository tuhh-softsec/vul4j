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
package org.apache.xml.security.keys.keyresolver.implementations;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.*;
import java.security.PublicKey;
import java.security.cert.*;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.*;
import org.apache.xml.security.keys.content.keyvalues.*;
import org.apache.xml.security.keys.content.x509.*;
import org.apache.xml.security.keys.keyresolver.*;
import org.apache.xml.security.keys.storage.*;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.utils.resolver.*;


/**
 *
 * @author $Author$
 */
public class RetrievalMethodResolver extends KeyResolverSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(RetrievalMethodResolver.class.getName());

   /**
    * Method engineCanResolve
    *
    * @param element
    * @param BaseURI
    * @param storage
    * @return
    */
   public boolean engineCanResolve(Element element, String BaseURI,
                                   StorageResolver storage) {

      try {
         XMLUtils.guaranteeThatElementInSignatureSpace(element,
                 Constants._TAG_RETRIEVALMETHOD);
      } catch (XMLSignatureException ex) {
         return false;
      }

      return true;
   }

   /**
    * Method engineResolvePublicKey
    *
    * @param element
    * @param BaseURI
    * @param storage
    * @return
    * @throws KeyResolverException
    */
   public PublicKey engineResolvePublicKey(
           Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {

      try {
         RetrievalMethod rm = new RetrievalMethod(element, BaseURI);
         Attr uri = rm.getURIAttr();

         // type can be null because it's optional
         String type = rm.getType();
         Transforms transforms = rm.getTransforms();
         ResourceResolver resRes = ResourceResolver.getInstance(uri, BaseURI);

         if (resRes != null) {
            XMLSignatureInput resource = resRes.resolve(uri, BaseURI);

            cat.debug("Before applying Transforms, resource has "
                      + resource.getBytes().length + "bytes");

            if (transforms != null) {
               cat.debug("We have Transforms");

               resource = transforms.performTransforms(resource);
            }

            cat.debug("After applying Transforms, resource has "
                      + resource.getBytes().length + "bytes");
            cat.debug("Resolved to resource " + resource.getSourceURI());

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
               cat.debug("we have to parse " + inputBytes.length + " bytes");

               Element e = this.getDocFromBytes(inputBytes);

               cat.debug("Now we have a {" + e.getNamespaceURI() + "}"
                         + e.getLocalName() + " Element");

               if (e != null) {
                  KeyResolver newKeyResolver = KeyResolver.getInstance(e,
                                                  BaseURI, storage);

                  if (newKeyResolver != null) {
                     return newKeyResolver.resolvePublicKey(e, BaseURI,
                                                            storage);
                  }
               }
            }
         }
      } catch (XMLSecurityException ex) {
         cat.debug("XMLSecurityException", ex);
      } catch (CertificateException ex) {
         cat.debug("CertificateException", ex);
      } catch (IOException ex) {
         cat.debug("IOException", ex);
      }

      return null;
   }

   /**
    * Method engineResolveX509Certificate
    *
    * @param element
    * @param BaseURI
    * @param storage
    * @return
    * @throws KeyResolverException
    */
   public X509Certificate engineResolveX509Certificate(
           Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {

      try {
         RetrievalMethod rm = new RetrievalMethod(element, BaseURI);
         Attr uri = rm.getURIAttr();
         String type = rm.getType();
         Transforms transforms = rm.getTransforms();

         cat.debug("Asked to resolve URI " + uri);

         ResourceResolver resRes = ResourceResolver.getInstance(uri, BaseURI);

         if (resRes != null) {
            XMLSignatureInput resource = resRes.resolve(uri, BaseURI);

            cat.debug("Before applying Transforms, resource has "
                      + resource.getBytes().length + "bytes");

            if (transforms != null) {
               cat.debug("We have Transforms");

               resource = transforms.performTransforms(resource);
            }

            cat.debug("After applying Transforms, resource has "
                      + resource.getBytes().length + "bytes");
            cat.debug("Resolved to resource " + resource.getSourceURI());

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
               cat.debug("we have to parse " + inputBytes.length + " bytes");

               Element e = this.getDocFromBytes(inputBytes);

               cat.debug("Now we have a {" + e.getNamespaceURI() + "}"
                         + e.getLocalName() + " Element");

               if (e != null) {
                  KeyResolver newKeyResolver = KeyResolver.getInstance(e,
                                                  BaseURI, storage);

                  if (newKeyResolver != null) {
                     return newKeyResolver.resolveX509Certificate(e, BaseURI,
                             storage);
                  }
               }
            }
         }
      } catch (XMLSecurityException ex) {
         cat.debug("XMLSecurityException", ex);
      } catch (CertificateException ex) {
         cat.debug("CertificateException", ex);
      } catch (IOException ex) {
         cat.debug("IOException", ex);
      }

      return null;
   }

   /**
    * Parses a byte array and returns the parsed Element.
    *
    * @param bytes
    * @return
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
}
