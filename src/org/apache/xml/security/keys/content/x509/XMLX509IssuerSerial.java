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



import java.security.cert.X509Certificate;
import java.math.BigInteger;
import org.w3c.dom.*;
import org.apache.xpath.XPathAPI;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.*;


/**
 *
 * @author $Author$
 */
public class XMLX509IssuerSerial extends ElementProxy
        implements XMLX509DataContent {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(XMLX509IssuerSerial.class.getName());

   /**
    * Constructor XMLX509IssuerSerial
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public XMLX509IssuerSerial(Element element, String BaseURI)
           throws XMLSecurityException {

      super(element, BaseURI);

      XMLUtils.guaranteeThatElementInSignatureSpace(element,
              Constants._TAG_X509ISSUERSERIAL);
   }

   /**
    * Constructor XMLX509IssuerSerial
    *
    * @param doc
    * @param X509IssuerName
    * @param X509SerialNumber
    */
   public XMLX509IssuerSerial(Document doc, String X509IssuerName,
                              BigInteger X509SerialNumber) {

      super(doc, Constants._TAG_X509ISSUERSERIAL);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));

      this.addTextElement(X509IssuerName, Constants._TAG_X509ISSUERNAME);
      this.addTextElement(X509SerialNumber.toString(),
                          Constants._TAG_X509SERIALNUMBER);
   }

   /**
    * Constructor XMLX509IssuerSerial
    *
    * @param doc
    * @param X509IssuerName
    * @param X509SerialNumber
    */
   public XMLX509IssuerSerial(Document doc, String X509IssuerName,
                              String X509SerialNumber) {
      this(doc, X509IssuerName, new BigInteger(X509SerialNumber));
   }

   /**
    * Constructor XMLX509IssuerSerial
    *
    * @param doc
    * @param X509IssuerName
    * @param X509SerialNumber
    */
   public XMLX509IssuerSerial(Document doc, String X509IssuerName,
                              int X509SerialNumber) {
      this(doc, X509IssuerName,
           new BigInteger(Integer.toString(X509SerialNumber)));
   }

   /**
    * Constructor XMLX509IssuerSerial
    *
    * @param doc
    * @param x509certificate
    */
   public XMLX509IssuerSerial(Document doc, X509Certificate x509certificate) {

      this(doc,
           RFC2253Parser.normalize(x509certificate.getIssuerDN().getName()),
           x509certificate.getSerialNumber());
   }

   /**
    * Method getSerialNumber
    *
    * @return
    * @throws XMLSecurityException
    */
   public BigInteger getSerialNumber() throws XMLSecurityException {

      String text =
         this.getTextFromChildElement(Constants._TAG_X509SERIALNUMBER);

      cat.debug("In dem X509SerialNumber wurde gefunden: " + text);

      return new BigInteger(text);
   }

   /**
    * Method getSerialNumberInteger
    *
    * @return
    * @throws XMLSecurityException
    */
   public int getSerialNumberInteger() throws XMLSecurityException {
      return this.getSerialNumber().intValue();
   }

   /**
    * Method getIssuerName
    *
    * @return
    * @throws XMLSecurityException
    */
   public String getIssuerName() throws XMLSecurityException {

      return RFC2253Parser
         .normalize(this
            .getTextFromChildElement(Constants._TAG_X509ISSUERNAME));
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

      XMLX509IssuerSerial other = (XMLX509IssuerSerial) obj;

      try {
         if (other.getSerialNumber().equals(this.getSerialNumber())
                 && other.getIssuerName().equals(this.getIssuerName())) {
            return true;
         }

         return false;
      } catch (XMLSecurityException ex) {
         return false;
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
