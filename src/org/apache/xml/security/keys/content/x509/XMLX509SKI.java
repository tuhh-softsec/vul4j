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



import java.io.IOException;
import java.security.cert.X509Certificate;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sun.security.util.DerValue;


/**
 * Handles SubjectKeyIdentifier (SKI) for X.509v3.
 *
 * @author $Author$
 * @see <A HREF="http://java.sun.com/products/jdk/1.2/docs/api/java/security/cert/X509Extension.html">Interface X509Extension</A>
 */
public class XMLX509SKI extends SignatureElementProxy
        implements XMLX509DataContent {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(XMLX509SKI.class.getName());

   /**
    * <CODE>SubjectKeyIdentifier (id-ce-subjectKeyIdentifier) (2.5.29.14)</CODE>:
    * This extension identifies the public key being certified. It enables
    * distinct keys used by the same subject to be differentiated
    * (e.g., as key updating occurs).
    * <BR />
    * A key identifer shall be unique with respect to all key identifiers
    * for the subject with which it is used. This extension is always non-critical.
    */
   public static final String SKI_OID = "2.5.29.14";

   /**
    * Constructor X509SKI
    *
    * @param doc
    * @param skiBytes
    */
   public XMLX509SKI(Document doc, byte[] skiBytes) {

      super(doc);

      this.addBase64Text(skiBytes);
   }

   /**
    * Constructor XMLX509SKI
    *
    * @param doc
    * @param x509certificate
    * @throws XMLSecurityException
    */
   public XMLX509SKI(Document doc, X509Certificate x509certificate)
           throws XMLSecurityException {

      super(doc);

      this.addBase64Text(XMLX509SKI.getSKIBytesFromCert(x509certificate));
   }

   /**
    * Constructor XMLX509SKI
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public XMLX509SKI(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Method getSKIBytes
    *
    *
    * @throws XMLSecurityException
    */
   public byte[] getSKIBytes() throws XMLSecurityException {
      return this.getBytesFromTextChild();
   }

   /**
    * Method getSKIBytesFromCert
    *
    * @param cert
    *
    * @throws XMLSecurityException
    * @see java.security.cert.X509Extension#getExtensionValue(java.lang.String)
    */
   public static byte[] getSKIBytesFromCert(X509Certificate cert)
           throws XMLSecurityException {

      try {

         /*
          * Gets the DER-encoded OCTET string for the extension value (extnValue)
          * identified by the passed-in oid String. The oid string is
          * represented by a set of positive whole numbers separated by periods.
          */
         byte[] derEncodedValue = cert.getExtensionValue(XMLX509SKI.SKI_OID);

         if (cert.getVersion() < 3) {
            Object exArgs[] = { new Integer(cert.getVersion()) };

            throw new XMLSecurityException("certificate.noSki.lowVersion",
                                           exArgs);
         }

         DerValue dervalue = new DerValue(derEncodedValue);

         if (dervalue == null) {
            throw new XMLSecurityException("certificate.noSki.null");
         }

         if (dervalue.tag != DerValue.tag_OctetString) {
            throw new XMLSecurityException("certificate.noSki.notOctetString");
         }

         byte[] extensionValue = dervalue.getOctetString();

         /**
          * Strip away first two bytes from the DerValue (tag and length)
          */
         byte abyte0[] = new byte[extensionValue.length - 2];

         System.arraycopy(extensionValue, 2, abyte0, 0, abyte0.length);

         /*
         byte abyte0[] = new byte[derEncodedValue.length - 4];
         System.arraycopy(derEncodedValue, 4, abyte0, 0, abyte0.length);
         */
         log.debug("Base64 of SKI is " + Base64.encode(abyte0));

         return abyte0;
      } catch (IOException ex) {
         throw new XMLSecurityException("generic.EmptyMessage", ex);
      }
   }

   /**
    * Method equals
    *
    * @param obj
    *
    */
   public boolean equals(Object obj) {

      if (!obj.getClass().getName().equals(this.getClass().getName())) {
         return false;
      }

      XMLX509SKI other = (XMLX509SKI) obj;

      try {
         return JavaUtils.binaryCompare(other.getSKIBytes(),
                                        this.getSKIBytes());
      } catch (XMLSecurityException ex) {
         return false;
      }
   }

   public String getBaseLocalName() {
      return Constants._TAG_X509SKI;
   }
}
