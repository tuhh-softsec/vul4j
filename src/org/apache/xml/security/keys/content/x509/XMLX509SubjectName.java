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



import java.security.Principal;
import java.security.cert.X509Certificate;
import org.w3c.dom.*;
import org.apache.xpath.XPathAPI;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import java.io.IOException;
import sun.security.x509.X500Name;


/**
 *
 * @author $Author$
 */
public class XMLX509SubjectName extends SignatureElementProxy
        implements XMLX509DataContent {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(XMLX509SubjectName.class.getName());

   /**
    * Constructor X509SubjectName
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public XMLX509SubjectName(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Constructor X509SubjectName
    *
    * @param doc
    * @param X509SubjectNameString
    */
   public XMLX509SubjectName(Document doc, String X509SubjectNameString) {

      super(doc);

      this.addText(X509SubjectNameString);
   }

   /**
    * Constructor XMLX509SubjectName
    *
    * @param doc
    * @param x509certificate
    */
   public XMLX509SubjectName(Document doc, X509Certificate x509certificate) {
      this(doc,
           RFC2253Parser.normalize(x509certificate.getSubjectDN().getName()));
   }

   /**
    * Method getSubjectName
    *
    *
    * @throws XMLSecurityException
    */
   public String getSubjectName() throws XMLSecurityException {
      return RFC2253Parser.normalize(this.getTextFromTextChild());
   }

   /**
    * Method createX500Name
    *
    * @param common
    * @param orgUnit
    * @param org
    * @param country
    *
    * @throws IOException
    */
   public static X500Name createX500Name(
           String common, String orgUnit, String org, String country)
              throws IOException {
      return new X500Name(common, orgUnit, org, country);
   }

   /**
    * Method createX500Name
    *
    * @param common
    * @param orgUnit
    * @param org
    * @param locality
    * @param state
    * @param country
    *
    * @throws IOException
    */
   public static X500Name createX500Name(
           String common, String orgUnit, String org, String locality, String state, String country)
              throws IOException {
      return new X500Name(common, orgUnit, org, locality, state, country);
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

      try {
         XMLX509SubjectName other = (XMLX509SubjectName) obj;
         String otherSubject = other.getSubjectName();
         String thisSubject = this.getSubjectName();

         if (otherSubject.equals(thisSubject)) {
            return true;
         }

         return false;
      } catch (XMLSecurityException ex) {
         return false;
      }
   }

   public String getBaseLocalName() {
      return Constants._TAG_X509SUBJECTNAME;
   }
}
