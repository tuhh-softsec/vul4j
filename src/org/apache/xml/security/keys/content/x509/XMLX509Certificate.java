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
package org.apache.xml.security.keys.content.x509;



import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 *
 * @author $Author$
 */
public class XMLX509Certificate extends SignatureElementProxy
        implements XMLX509DataContent {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(XMLX509Certificate.class.getName());

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

   /** @inheritDoc */
   public boolean equals(Object obj) {

      try {
         if (!obj.getClass().getName().equals(this.getClass().getName())) {
            return false;
         }

         XMLX509Certificate other = (XMLX509Certificate) obj;

         /** $todo$ or should be create X509Certificates and use the equals() from the Certs */
         return JavaUtils.binaryCompare(other.getCertificateBytes(),
                                        this.getCertificateBytes());
      } catch (XMLSecurityException ex) {
         return false;
      }
   }

   /** @inheritDoc */
   public String getBaseLocalName() {
      return Constants._TAG_X509CERTIFICATE;
   }
}
