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
package org.apache.xml.security.keys.provider;



import java.security.Key;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.*;
import org.w3c.dom.*;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import java.io.ByteArrayInputStream;

/**
 *
 * @author $Author$
 */
public class CertificateElement extends KeyBaseType {

   /**
    * Constructor CertificateElement
    *
    * @param doc
    * @param alias
    * @param cert
    * @throws XMLSecurityException
    */
   public CertificateElement(Document doc, String alias, Certificate cert)
           throws XMLSecurityException {

      super(doc, alias);

      try {
         XMLX509Certificate xCert = new XMLX509Certificate(doc,
                                       cert.getEncoded());

         this._constructionElement.appendChild(xCert.getElement());
         XMLUtils.addReturnToElement(this);
      } catch (CertificateEncodingException ex) {
         throw new XMLSecurityException("empty", ex);
      }
      this.setJCAType(cert);
   }

   public CertificateElement(Element element, String BaseURI) throws XMLSecurityException {
      super(element, BaseURI);
   }

   public void setJCAType(Certificate cert) {
      if (this._state == ElementProxy.MODE_CREATE) {
         this._constructionElement.setAttribute("JCAType", cert.getType());
      }
   }

   public String getJCAType() {
      return this._constructionElement.getAttribute("JCAType");
   }

   public Certificate getCertificate() throws XMLSecurityException, CertificateException {
      Element certElem = this.getChildElementLocalName(0, Constants.SignatureSpecNS, Constants._TAG_X509CERTIFICATE);
      if (certElem == null) {
         return null;
      }
      XMLX509Certificate xCert = new XMLX509Certificate(certElem, this._baseURI);
      CertificateFactory cf = CertificateFactory.getInstance(this.getJCAType());
      Certificate cert = cf.generateCertificate(new ByteArrayInputStream(xCert.getCertificateBytes()));
      return cert;
   }

   /**
    * Method getBaseLocalName
    *
    * @return
    */
   public String getBaseLocalName() {
      return "Certificate";
   }
}
