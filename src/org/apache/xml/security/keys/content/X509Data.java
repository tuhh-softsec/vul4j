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
package org.apache.xml.security.keys.content;



import java.util.Vector;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import org.w3c.dom.*;
import org.apache.xpath.XPathAPI;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.keys.content.x509.*;
import org.apache.xml.security.exceptions.XMLSecurityException;


/**
 *
 * @author $Author$
 */
public class X509Data extends SignatureElementProxy implements KeyInfoContent {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(X509Data.class.getName());

   /**
    * Constructor X509Data
    *
    * @param doc
    */
   public X509Data(Document doc) {

      super(doc, Constants._TAG_X509DATA);

      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   /**
    * Constructor X509Data
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public X509Data(Element element, String BaseURI)
           throws XMLSecurityException {

      super(element, BaseURI, Constants._TAG_X509DATA);

      NodeList children = this._constructionElement.getChildNodes();
      HelperNodeList nodes = new HelperNodeList();

      for (int i = 0; i < children.getLength(); i++) {
         if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
            nodes.appendChild(children.item(i));
         }
      }

      if (nodes.getLength() == 0) {
         Object exArgs[] = { "Elements", Constants._TAG_X509DATA };

         throw new XMLSecurityException("xml.WrongContent", exArgs);
      }

      for (int i = 0; i < nodes.getLength(); i++) {
         Element currentElem = (Element) nodes.item(i);
         String localname = currentElem.getLocalName();

         if (currentElem.getNamespaceURI().equals(Constants.SignatureSpecNS)) {
            if (localname.equals(Constants._TAG_X509ISSUERSERIAL)) {
               XMLX509IssuerSerial is = new XMLX509IssuerSerial(currentElem,
                                           BaseURI);

               this.add(is);
            } else if (localname.equals(Constants._TAG_X509SKI)) {
               XMLX509SKI ski = new XMLX509SKI(currentElem, BaseURI);

               this.add(ski);
            } else if (localname.equals(Constants._TAG_X509SUBJECTNAME)) {
               XMLX509SubjectName sn = new XMLX509SubjectName(currentElem,
                                          BaseURI);

               this.add(sn);
            } else if (localname.equals(Constants._TAG_X509CERTIFICATE)) {
               XMLX509Certificate cert = new XMLX509Certificate(currentElem,
                                            BaseURI);

               this.add(cert);
            } else if (localname.equals(Constants._TAG_X509CRL)) {
               XMLX509CRL crl = new XMLX509CRL(currentElem, BaseURI);

               this.add(crl);
            } else {
               cat.warn("Found a " + currentElem.getTagName() + " element in "
                        + Constants._TAG_X509DATA);
               this.addUnknownElement(currentElem);
            }
         } else {
            cat.warn("Found a " + currentElem.getTagName() + " element in "
                     + Constants._TAG_X509DATA);
            this.addUnknownElement(currentElem);
         }
      }
   }

   /**
    * Method addIssuerSerial
    *
    * @param X509IssuerName
    * @param X509SerialNumber
    */
   public void addIssuerSerial(String X509IssuerName,
                               BigInteger X509SerialNumber) {
      this.add(new XMLX509IssuerSerial(this._doc, X509IssuerName,
                                       X509SerialNumber));
   }

   /**
    * Method addIssuerSerial
    *
    * @param X509IssuerName
    * @param X509SerialNumber
    */
   public void addIssuerSerial(String X509IssuerName, String X509SerialNumber) {
      this.add(new XMLX509IssuerSerial(this._doc, X509IssuerName,
                                       X509SerialNumber));
   }

   /**
    * Method addIssuerSerial
    *
    * @param X509IssuerName
    * @param X509SerialNumber
    */
   public void addIssuerSerial(String X509IssuerName, int X509SerialNumber) {
      this.add(new XMLX509IssuerSerial(this._doc, X509IssuerName,
                                       X509SerialNumber));
   }

   /**
    * Method add
    *
    * @param xmlX509IssuerSerial
    */
   public void add(XMLX509IssuerSerial xmlX509IssuerSerial) {

      if (this._state == MODE_SIGN) {
         this._constructionElement
            .appendChild(xmlX509IssuerSerial.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method addSKI
    *
    * @param skiBytes
    */
   public void addSKI(byte[] skiBytes) {
      this.add(new XMLX509SKI(this._doc, skiBytes));
   }

   /**
    * Method addSKI
    *
    * @param x509certificate
    * @throws XMLSecurityException
    */
   public void addSKI(X509Certificate x509certificate)
           throws XMLSecurityException {
      this.add(new XMLX509SKI(this._doc, x509certificate));
   }

   /**
    * Method add
    *
    * @param xmlX509SKI
    */
   public void add(XMLX509SKI xmlX509SKI) {

      if (this._state == MODE_SIGN) {
         this._constructionElement.appendChild(xmlX509SKI.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method addSubjectName
    *
    * @param subjectName
    */
   public void addSubjectName(String subjectName) {
      this.add(new XMLX509SubjectName(this._doc, subjectName));
   }

   /**
    * Method addSubjectName
    *
    * @param x509certificate
    */
   public void addSubjectName(X509Certificate x509certificate) {
      this.add(new XMLX509SubjectName(this._doc, x509certificate));
   }

   /**
    * Method add
    *
    * @param xmlX509SubjectName
    */
   public void add(XMLX509SubjectName xmlX509SubjectName) {

      if (this._state == MODE_SIGN) {
         this._constructionElement.appendChild(xmlX509SubjectName.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method addCertificate
    *
    * @param x509certificate
    * @throws XMLSecurityException
    */
   public void addCertificate(X509Certificate x509certificate)
           throws XMLSecurityException {
      this.add(new XMLX509Certificate(this._doc, x509certificate));
   }

   /**
    * Method addCertificate
    *
    * @param x509certificateBytes
    */
   public void addCertificate(byte[] x509certificateBytes) {
      this.add(new XMLX509Certificate(this._doc, x509certificateBytes));
   }

   /**
    * Method add
    *
    * @param xmlX509Certificate
    */
   public void add(XMLX509Certificate xmlX509Certificate) {

      if (this._state == MODE_SIGN) {
         this._constructionElement.appendChild(xmlX509Certificate.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method addCRL
    *
    * @param crlBytes
    */
   public void addCRL(byte[] crlBytes) {
      this.add(new XMLX509CRL(this._doc, crlBytes));
   }

   /**
    * Method add
    *
    * @param xmlX509CRL
    */
   public void add(XMLX509CRL xmlX509CRL) {

      if (this._state == MODE_SIGN) {
         this._constructionElement.appendChild(xmlX509CRL.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method addUnknownElement
    *
    * @param element
    */
   public void addUnknownElement(Element element) {

      if (this._state == MODE_SIGN) {
         this._constructionElement.appendChild(element);
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method lengthIssuerSerial
    *
    * @return
    */
   public int lengthIssuerSerial() {
      return this.length(Constants.SignatureSpecNS,
                         Constants._TAG_X509ISSUERSERIAL);
   }

   /**
    * Method lengthSKI
    *
    * @return
    */
   public int lengthSKI() {
      return this.length(Constants.SignatureSpecNS, Constants._TAG_X509SKI);
   }

   /**
    * Method lengthSubjectName
    *
    * @return
    */
   public int lengthSubjectName() {
      return this.length(Constants.SignatureSpecNS,
                         Constants._TAG_X509SUBJECTNAME);
   }

   /**
    * Method lengthCertificate
    *
    * @return
    */
   public int lengthCertificate() {
      return this.length(Constants.SignatureSpecNS,
                         Constants._TAG_X509CERTIFICATE);
   }

   /**
    * Method lengthCRL
    *
    * @return
    */
   public int lengthCRL() {
      return this.length(Constants.SignatureSpecNS, Constants._TAG_X509CRL);
   }

   /**
    * Method lengthUnknownElement
    *
    * @return
    */
   public int lengthUnknownElement() {

      NodeList nl = this._constructionElement.getChildNodes();
      int result = 0;

      for (int i = 0; i < nl.getLength(); i++) {
         Node n = nl.item(i);

         if ((n.getNodeType() == Node.ELEMENT_NODE)
                 &&!n.getNamespaceURI().equals(Constants.SignatureSpecNS)) {
            result += 1;
         }
      }

      return result;
   }

   /**
    * Method itemIssuerSerial
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public XMLX509IssuerSerial itemIssuerSerial(int i)
           throws XMLSecurityException {

      Element e =
         this.getChildElementLocalName(i, Constants.SignatureSpecNS,
                                       Constants._TAG_X509ISSUERSERIAL);

      if (e != null) {
         return new XMLX509IssuerSerial(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method itemSKI
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public XMLX509SKI itemSKI(int i) throws XMLSecurityException {

      Element e = this.getChildElementLocalName(i, Constants.SignatureSpecNS,
                                                Constants._TAG_X509SKI);

      if (e != null) {
         return new XMLX509SKI(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method itemSubjectName
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public XMLX509SubjectName itemSubjectName(int i)
           throws XMLSecurityException {

      Element e = this.getChildElementLocalName(i, Constants.SignatureSpecNS,
                                                Constants._TAG_X509SUBJECTNAME);

      if (e != null) {
         return new XMLX509SubjectName(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method itemCertificate
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public XMLX509Certificate itemCertificate(int i)
           throws XMLSecurityException {

      Element e = this.getChildElementLocalName(i, Constants.SignatureSpecNS,
                                                Constants._TAG_X509CERTIFICATE);

      if (e != null) {
         return new XMLX509Certificate(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method itemCRL
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public XMLX509CRL itemCRL(int i) throws XMLSecurityException {

      Element e = this.getChildElementLocalName(i, Constants.SignatureSpecNS,
                                                Constants._TAG_X509CRL);

      if (e != null) {
         return new XMLX509CRL(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method itemUnknownElement
    *
    * @param i
    * @return
    * @todo implement
    */
   public Element itemUnknownElement(int i) {
      return null;
   }

   /**
    * Method containsIssuerSerial
    *
    * @return
    */
   public boolean containsIssuerSerial() {
      return this.lengthIssuerSerial() > 0;
   }

   /**
    * Method containsSKI
    *
    * @return
    */
   public boolean containsSKI() {
      return this.lengthSKI() > 0;
   }

   /**
    * Method containsSubjectName
    *
    * @return
    */
   public boolean containsSubjectName() {
      return this.lengthSubjectName() > 0;
   }

   /**
    * Method containsCertificate
    *
    * @return
    */
   public boolean containsCertificate() {
      return this.lengthCertificate() > 0;
   }

   /**
    * Method containsCRL
    *
    * @return
    */
   public boolean containsCRL() {
      return this.lengthCRL() > 0;
   }

   /**
    * Method containsUnknownElement
    *
    * @return
    */
   public boolean containsUnknownElement() {
      return this.lengthUnknownElement() > 0;
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
