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
package org.apache.xml.security.keys.content;



import java.math.BigInteger;
import java.security.cert.X509Certificate;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.x509.XMLX509CRL;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.content.x509.XMLX509IssuerSerial;
import org.apache.xml.security.keys.content.x509.XMLX509SKI;
import org.apache.xml.security.keys.content.x509.XMLX509SubjectName;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.HelperNodeList;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 * @author $Author$
 */
public class X509Data extends SignatureElementProxy implements KeyInfoContent {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(X509Data.class.getName());

   /**
    * Constructor X509Data
    *
    * @param doc
    */
   public X509Data(Document doc) {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);
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

      super(element, BaseURI);

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
               log.warn("Found a " + currentElem.getTagName() + " element in "
                        + Constants._TAG_X509DATA);
               this.addUnknownElement(currentElem);
            }
         } else {
            log.warn("Found a " + currentElem.getTagName() + " element in "
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
         XMLUtils.addReturnToElement(this._constructionElement);
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
         XMLUtils.addReturnToElement(this._constructionElement);
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
         XMLUtils.addReturnToElement(this._constructionElement);
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
         XMLUtils.addReturnToElement(this._constructionElement);
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
         XMLUtils.addReturnToElement(this._constructionElement);
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
         XMLUtils.addReturnToElement(this._constructionElement);
      }
   }

   /**
    * Method lengthIssuerSerial
    *
    *
    */
   public int lengthIssuerSerial() {
      return this.length(Constants.SignatureSpecNS,
                         Constants._TAG_X509ISSUERSERIAL);
   }

   /**
    * Method lengthSKI
    *
    *
    */
   public int lengthSKI() {
      return this.length(Constants.SignatureSpecNS, Constants._TAG_X509SKI);
   }

   /**
    * Method lengthSubjectName
    *
    *
    */
   public int lengthSubjectName() {
      return this.length(Constants.SignatureSpecNS,
                         Constants._TAG_X509SUBJECTNAME);
   }

   /**
    * Method lengthCertificate
    *
    *
    */
   public int lengthCertificate() {
      return this.length(Constants.SignatureSpecNS,
                         Constants._TAG_X509CERTIFICATE);
   }

   /**
    * Method lengthCRL
    *
    *
    */
   public int lengthCRL() {
      return this.length(Constants.SignatureSpecNS, Constants._TAG_X509CRL);
   }

   /**
    * Method lengthUnknownElement
    *
    *
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
    *
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
    *
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
    *
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
    *
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
    *
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
    *
    * $todo$ implement
    */
   public Element itemUnknownElement(int i) {
      return null;
   }

   /**
    * Method containsIssuerSerial
    *
    *
    */
   public boolean containsIssuerSerial() {
      return this.lengthIssuerSerial() > 0;
   }

   /**
    * Method containsSKI
    *
    *
    */
   public boolean containsSKI() {
      return this.lengthSKI() > 0;
   }

   /**
    * Method containsSubjectName
    *
    *
    */
   public boolean containsSubjectName() {
      return this.lengthSubjectName() > 0;
   }

   /**
    * Method containsCertificate
    *
    *
    */
   public boolean containsCertificate() {
      return this.lengthCertificate() > 0;
   }

   /**
    * Method containsCRL
    *
    *
    */
   public boolean containsCRL() {
      return this.lengthCRL() > 0;
   }

   /**
    * Method containsUnknownElement
    *
    *
    */
   public boolean containsUnknownElement() {
      return this.lengthUnknownElement() > 0;
   }

   public String getBaseLocalName() {
      return Constants._TAG_X509DATA;
   }
}
