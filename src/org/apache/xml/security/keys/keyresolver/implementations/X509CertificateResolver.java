
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
import java.security.*;
import java.security.spec.*;
import java.security.cert.*;
import org.w3c.dom.*;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.keyresolver.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.keys.storage.*;


/**
 * Resolves Certificates which are directly contained inside a
 * <CODE>ds:X509Certificate</CODE> Element.
 *
 * @author $Author$
 */
public class X509CertificateResolver extends KeyResolverSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(X509CertificateResolver.class.getName());

   /** Field _dsaKeyElement */
   NodeList _x509CertKeyElements = null;

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

      cat.debug("Can I resolve " + element.getTagName() + "?");

      try {
         XMLUtils.guaranteeThatElementInSignatureSpace(element,
                 Constants._TAG_X509DATA);
      } catch (XMLSignatureException ex) {
         cat.debug("I can't");

         return false;
      }

      try {
         Element nscontext = XMLUtils.createDSctx(element.getOwnerDocument(), "ds", Constants.SignatureSpecNS);

         this._x509CertKeyElements = XPathAPI.selectNodeList(element,
                 "./ds:" + Constants._TAG_X509CERTIFICATE, nscontext);

         if ((this._x509CertKeyElements != null)
                 && (this._x509CertKeyElements.getLength() > 0)) {
            cat.debug("Yes Sir, I can");

            return true;
         }
      } catch (TransformerException ex) {}

      cat.debug("I can't");

      return false;
   }

   /** Field _x509certObject[] */
   XMLX509Certificate _x509certObject[] = null;

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
    * @return
    * @throws KeyResolverException
    */
   public X509Certificate engineResolveX509Certificate(
           Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {

      try {
         if ((this._x509CertKeyElements == null)
                 || (this._x509CertKeyElements.getLength() == 0)) {
            boolean weCanResolve = this.engineCanResolve(element, BaseURI,
                                      storage);

            if (!weCanResolve || (this._x509CertKeyElements == null)
                    || (this._x509CertKeyElements.getLength() == 0)) {
               return null;
            }
         }

         this._x509certObject =
            new XMLX509Certificate[this._x509CertKeyElements.getLength()];

         // populate Object array
         for (int i = 0; i < this._x509CertKeyElements.getLength(); i++) {
            this._x509certObject[i] =
               new XMLX509Certificate((Element) this._x509CertKeyElements
                  .item(i), BaseURI);
         }

         for (int i = 0; i < this._x509certObject.length; i++) {
            X509Certificate cert = this._x509certObject[i].getX509Certificate();

            if (cert != null) {
               return cert;
            }
         }

         return null;
      } catch (XMLSecurityException ex) {
         cat.debug("XMLSecurityException", ex);

         throw new KeyResolverException("generic.EmptyMessage", ex);
      }
   }

   /**
    * Method engineResolveSecretKey
    *
    * @param element
    * @param BaseURI
    * @param storage
    * @return
    * @throws KeyResolverException
    */
   public javax.crypto.SecretKey engineResolveSecretKey(
           Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {
      return null;
   }
}
