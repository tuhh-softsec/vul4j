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
package org.apache.xml.security.keys.content.x509;



import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.*;
import org.w3c.dom.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.exceptions.XMLSecurityException;


/**
 *
 * @author $Author$
 */
public class XMLX509Certificate extends SignatureElementProxy
        implements XMLX509DataContent {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(XMLX509Certificate.class.getName());

   /** Field JCA_CERT_ID */
   public static final String JCA_CERT_ID = "X.509";

   /**
    * Constructor X509Certificate
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public XMLX509Certificate(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Constructor X509Certificate
    *
    * @param doc
    * @param certificateBytes
    */
   public XMLX509Certificate(Document doc, byte[] certificateBytes) {

      super(doc);

      this.addBase64Text(certificateBytes);
   }

   /**
    * Constructor XMLX509Certificate
    *
    * @param doc
    * @param x509certificate
    * @throws XMLSecurityException
    */
   public XMLX509Certificate(Document doc, X509Certificate x509certificate)
           throws XMLSecurityException {

      super(doc);

      try {
         this.addBase64Text(x509certificate.getEncoded());
      } catch (java.security.cert.CertificateEncodingException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method getCertificateBytes
    *
    * @return
    * @throws XMLSecurityException
    */
   public byte[] getCertificateBytes() throws XMLSecurityException {
      return this.getBytesFromTextChild();
   }

   /**
    * Method getX509Certificate
    *
    * @return
    * @throws XMLSecurityException
    */
   public X509Certificate getX509Certificate() throws XMLSecurityException {

      try {
         byte certbytes[] = this.getCertificateBytes();
         CertificateFactory certFact =
            CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
         X509Certificate cert =
            (X509Certificate) certFact
               .generateCertificate(new ByteArrayInputStream(certbytes));

         if (cert != null) {
            return cert;
         }

         return null;
      } catch (CertificateException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method getPublicKey
    *
    * @return
    * @throws XMLSecurityException
    */
   public PublicKey getPublicKey() throws XMLSecurityException {

      X509Certificate cert = this.getX509Certificate();

      if (cert != null) {
         return cert.getPublicKey();
      }

      return null;
   }

   /**
    * Method equals
    *
    * @param obj
    * @return
    */
   public boolean equals(Object obj) {

      try {
         if (!obj.getClass().getName().equals(this.getClass().getName())) {
            return false;
         }

         XMLX509Certificate other = (XMLX509Certificate) obj;

         /** @todo or should be create X509Certificates and use the equals() from the Certs */
         return JavaUtils.binaryCompare(other.getCertificateBytes(),
                                        this.getCertificateBytes());
      } catch (XMLSecurityException ex) {
         return false;
      }
   }

   public String getBaseLocalName() {
      return Constants._TAG_X509CERTIFICATE;
   }
}
