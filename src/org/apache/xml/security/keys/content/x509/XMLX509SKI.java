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



import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import org.w3c.dom.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import sun.security.util.DerValue;


/**
 * Handles SubjectKeyIdentifier (SKI) for X.509v3.
 *
 * @author $Author$
 * @see <A HREF="http://java.sun.com/products/jdk/1.2/docs/api/java/security/cert/X509Extension.html">Interface X509Extension</A>
 */
public class XMLX509SKI extends ElementProxy implements XMLX509DataContent {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(XMLX509SKI.class.getName());

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

      super(doc, Constants._TAG_X509SKI);

      this.addBase64Text(skiBytes);
   }

   /**
    * Constructor XMLX509SKI
    *
    * @param doc
    * @param x509certificate
    * @throws KeyResolverException
    */
   public XMLX509SKI(Document doc, X509Certificate x509certificate)
           throws XMLSecurityException {

      super(doc, Constants._TAG_X509SKI);

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

      XMLUtils.guaranteeThatElementInSignatureSpace(element,
              Constants._TAG_X509SKI);
   }

   /**
    * Method getSKIBytes
    *
    * @return
    * @throws XMLSecurityException
    */
   public byte[] getSKIBytes() throws XMLSecurityException {
      return this.getBytesFromTextChild();
   }

   /**
    * Method getSKIBytesFromCert
    *
    * @param cert
    * @return
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
         cat.debug("Base64 of SKI is " + Base64.encode(abyte0));

         return abyte0;
      } catch (IOException ex) {
         throw new XMLSecurityException("generic.EmptyMessage", ex);
      }
   }

   /**
    * Method decode
    *
    * @param cert
    * @throws KeyResolverException
    */
   private void createSKIElementFromCert(X509Certificate cert)
           throws XMLSecurityException {

      byte[] abyte0 = this.getSKIBytesFromCert(cert);

      Base64.encodeToElement(this._doc, Constants._TAG_X509SKI, abyte0);
   }

   /**
    * Method equals
    *
    * @param obj
    * @return
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

   static {
      org.apache.xml.security.Init.init();
   }
}
