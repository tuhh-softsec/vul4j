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
package org.apache.xml.security.keys;



import java.lang.reflect.*;
import java.security.*;
import java.security.cert.*;
import java.security.spec.*;
import java.util.*;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.*;
import org.apache.xml.security.keys.content.keyvalues.*;
import org.apache.xml.security.keys.keyresolver.*;
import org.apache.xml.security.keys.storage.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.Init;


/**
 * This class stand for KeyInfo Element that may contain keys, names,
 * certificates and other public key management information,
 * such as in-band key distribution or key agreement data.
 * <BR />
 * KeyInfo Element has two basic functions:
 * One is KeyResolve for getting the public key in signature validation processing.
 * the other one is toElement for getting the element in signature generation processing.
 * <BR />
 * The <CODE>lengthXXX()</CODE> methods provide access to the internal Key
 * objects:
 * <UL>
 * <LI>If the <CODE>KeyInfo</CODE> was constructed from an Element
 * (Signature verification), the <CODE>lengthXXX()</CODE> methods searches
 * for child elements of <CODE>ds:KeyInfo</CODE> for known types. </LI>
 * <LI>If the <CODE>KeyInfo</CODE> was constructed from scratch (during
 * Signature generation), the <CODE>lengthXXX()</CODE> methods return the number
 * of <CODE>XXX</CODE> objects already passed to the KeyInfo</LI>
 * </UL>
 * <BR />
 * The <CODE>addXXX()</CODE> methods are used for adding Objects of the
 * appropriate type to the <CODE>KeyInfo</CODE>. This is used during signature
 * generation.
 * <BR />
 * The <CODE>itemXXX(int i)</CODE> methods return the i'th object of the
 * corresponding type.
 * <BR />
 * The <CODE>containsXXX()</CODE> methods return <I>whether</I> the KeyInfo
 * contains the corresponding type.
 *
 * @author $Author$
 */
public class KeyInfo extends SignatureElementProxy {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(KeyInfo.class.getName());

   /** Field _dsns */
   Element _dsns = null;

   /**
    * Constructor KeyInfo
    * @param doc
    */
   public KeyInfo(Document doc) {

      super(doc, Constants._TAG_KEYINFO);

      this._constructionElement.appendChild(this._doc.createTextNode("\n"));

      this._dsns = XMLUtils.createDSctx(this._doc, "ds",
                                        Constants.SignatureSpecNS);
   }

   /**
    * Constructor KeyInfo
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public KeyInfo(Element element, String BaseURI) throws XMLSecurityException {

      super(element, BaseURI, Constants._TAG_KEYINFO);

      this._dsns = XMLUtils.createDSctx(this._doc, "ds",
                                        Constants.SignatureSpecNS);
   }

   /**
    * Sets the <code>Id</code> attribute
    *
    * @param Id ID
    */
   public void setId(String Id) {

      if ((this._state == MODE_SIGN) && (Id != null)) {
         this._constructionElement.setAttribute(Constants._ATT_ID, Id);
         IdResolver.registerElementById(this._constructionElement, Id);
      }
   }

   /**
    * Returns the <code>Id</code> attribute
    *
    * @return the <code>Id</code> attribute
    */
   public String getId() {
      return this._constructionElement.getAttribute(Constants._ATT_ID);
   }

   /**
    * Method addKeyName
    *
    * @param keynameString
    */
   public void addKeyName(String keynameString) {
      this.add(new KeyName(this._doc, keynameString));
   }

   /**
    * Method add
    *
    * @param keyname
    */
   public void add(KeyName keyname) {

      if (this._state == MODE_SIGN) {
         this._constructionElement.appendChild(keyname.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method addKeyValue
    *
    * @param pk
    */
   public void addKeyValue(PublicKey pk) {
      this.add(new KeyValue(this._doc, pk));
   }

   /**
    * Method addKeyValue
    *
    * @param unknownKeyValueElement
    */
   public void addKeyValue(Element unknownKeyValueElement) {
      this.add(new KeyValue(this._doc, unknownKeyValueElement));
   }

   /**
    * Method add
    *
    * @param dsakeyvalue
    */
   public void add(DSAKeyValue dsakeyvalue) {
      this.add(new KeyValue(this._doc, dsakeyvalue));
   }

   /**
    * Method add
    *
    * @param rsakeyvalue
    */
   public void add(RSAKeyValue rsakeyvalue) {
      this.add(new KeyValue(this._doc, rsakeyvalue));
   }

   /**
    * Method add
    *
    * @param pk
    */
   public void add(PublicKey pk) {
      this.add(new KeyValue(this._doc, pk));
   }

   /**
    * Method add
    *
    * @param keyvalue
    */
   public void add(KeyValue keyvalue) {

      if (this._state == MODE_SIGN) {
         this._constructionElement.appendChild(keyvalue.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method addMgmtData
    *
    * @param mgmtdata
    */
   public void addMgmtData(String mgmtdata) {
      this.add(new MgmtData(this._doc, mgmtdata));
   }

   /**
    * Method add
    *
    * @param mgmtdata
    */
   public void add(MgmtData mgmtdata) {

      if (this._state == MODE_SIGN) {
         this._constructionElement.appendChild(mgmtdata.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method addPGPData
    *
    * @param pgpdata
    */
   public void add(PGPData pgpdata) {

      if (this._state == MODE_SIGN) {
         this._constructionElement.appendChild(pgpdata.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method addRetrievalMethod
    *
    * @param URI
    * @param transforms
    * @param Type
    */
   public void addRetrievalMethod(String URI, Transforms transforms,
                                  String Type) {
      this.add(new RetrievalMethod(this._doc, URI, transforms, Type));
   }

   /**
    * Method add
    *
    * @param retrievalmethod
    */
   public void add(RetrievalMethod retrievalmethod) {

      if (this._state == MODE_SIGN) {
         this._constructionElement.appendChild(retrievalmethod.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method add
    *
    * @param spkidata
    */
   public void add(SPKIData spkidata) {

      if (this._state == MODE_SIGN) {
         this._constructionElement.appendChild(spkidata.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method addX509Data
    *
    * @param x509data
    * @throws XMLSecurityException
    */
   public void add(X509Data x509data) throws XMLSecurityException {

      if (this._state == MODE_SIGN) {
         this._constructionElement.appendChild(x509data.getElement());
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
    * Method lengthKeyName
    *
    * @return
    */
   public int lengthKeyName() {
      return this.length(Constants.SignatureSpecNS, Constants._TAG_KEYNAME);
   }

   /**
    * Method lengthKeyValue
    *
    * @return
    */
   public int lengthKeyValue() {
      return this.length(Constants.SignatureSpecNS, Constants._TAG_KEYVALUE);
   }

   /**
    * Method lengthMgmtData
    *
    * @return
    */
   public int lengthMgmtData() {
      return this.length(Constants.SignatureSpecNS, Constants._TAG_MGMTDATA);
   }

   /**
    * Method lengthPGPData
    *
    * @return
    */
   public int lengthPGPData() {
      return this.length(Constants.SignatureSpecNS, Constants._TAG_PGPDATA);
   }

   /**
    * Method lengthRetrievalMethod
    *
    * @return
    */
   public int lengthRetrievalMethod() {
      return this.length(Constants.SignatureSpecNS,
                         Constants._TAG_RETRIEVALMETHOD);
   }

   /**
    * Method lengthSPKIData
    *
    * @return
    */
   public int lengthSPKIData() {
      return this.length(Constants.SignatureSpecNS, Constants._TAG_SPKIDATA);
   }

   /**
    * Method lengthX509Data
    *
    * @return
    */
   public int lengthX509Data() {
      return this.length(Constants.SignatureSpecNS, Constants._TAG_X509DATA);
   }

   /**
    * Method lengthUnknownElement
    *
    * @return
    */
   public int lengthUnknownElement() {

      int res = 0;
      NodeList nl = this._constructionElement.getChildNodes();

      for (int i = 0; i < nl.getLength(); i++) {
         Node current = nl.item(i);

         /**
          * @todo using this method, we don't see unknown Elements
          *  from Signature NS; revisit
          */
         if ((current.getNodeType() == Node.ELEMENT_NODE)
                 && current.getNamespaceURI()
                    .equals(Constants.SignatureSpecNS)) {
            res++;
         }
      }

      return res;
   }

   /**
    * Method itemKeyName
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public KeyName itemKeyName(int i) throws XMLSecurityException {

      Element e = this.getChildElementLocalName(i, Constants.SignatureSpecNS,
                                                Constants._TAG_KEYNAME);

      if (e != null) {
         return new KeyName(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method itemKeyValue
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public KeyValue itemKeyValue(int i) throws XMLSecurityException {

      Element e = this.getChildElementLocalName(i, Constants.SignatureSpecNS,
                                                Constants._TAG_KEYVALUE);

      if (e != null) {
         return new KeyValue(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method itemMgmtData
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public MgmtData itemMgmtData(int i) throws XMLSecurityException {

      Element e = this.getChildElementLocalName(i, Constants.SignatureSpecNS,
                                                Constants._TAG_MGMTDATA);

      if (e != null) {
         return new MgmtData(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method itemPGPData
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public PGPData itemPGPData(int i) throws XMLSecurityException {

      Element e = this.getChildElementLocalName(i, Constants.SignatureSpecNS,
                                                Constants._TAG_PGPDATA);

      if (e != null) {
         return new PGPData(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method itemRetrievalMethod
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public RetrievalMethod itemRetrievalMethod(int i)
           throws XMLSecurityException {

      Element e = this.getChildElementLocalName(i, Constants.SignatureSpecNS,
                                                Constants._TAG_RETRIEVALMETHOD);

      if (e != null) {
         return new RetrievalMethod(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method itemSPKIData
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public SPKIData itemSPKIData(int i) throws XMLSecurityException {

      Element e = this.getChildElementLocalName(i, Constants.SignatureSpecNS,
                                                Constants._TAG_SPKIDATA);

      if (e != null) {
         return new SPKIData(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method itemX509Data
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public X509Data itemX509Data(int i) throws XMLSecurityException {

      Element e = this.getChildElementLocalName(i, Constants.SignatureSpecNS,
                                                Constants._TAG_X509DATA);

      if (e != null) {
         return new X509Data(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method itemUnknownElement
    *
    * @param i
    * @return
    */
   public Element itemUnknownElement(int i) {

      NodeList nl = this._constructionElement.getChildNodes();
      int res = 0;

      for (int j = 0; j < nl.getLength(); j++) {
         Node current = nl.item(j);

         /**
          * @todo using this method, we don't see unknown Elements
          *  from Signature NS; revisit
          */
         if ((current.getNodeType() == Node.ELEMENT_NODE)
                 && current.getNamespaceURI()
                    .equals(Constants.SignatureSpecNS)) {
            res++;

            if (res == i) {
               return (Element) current;
            }
         }
      }

      return null;
   }

   /**
    * Method isEmpty
    *
    * @return
    */
   public boolean isEmpty() {
      return this._constructionElement.getChildNodes().getLength() == 0;
   }

   /**
    * Method containsKeyName
    *
    * @return
    */
   public boolean containsKeyName() {
      return this.lengthKeyName() > 0;
   }

   /**
    * Method containsKeyValue
    *
    * @return
    */
   public boolean containsKeyValue() {
      return this.lengthKeyValue() > 0;
   }

   /**
    * Method containsMgmtData
    *
    * @return
    */
   public boolean containsMgmtData() {
      return this.lengthMgmtData() > 0;
   }

   /**
    * Method containsPGPData
    *
    * @return
    */
   public boolean containsPGPData() {
      return this.lengthPGPData() > 0;
   }

   /**
    * Method containsRetrievalMethod
    *
    * @return
    */
   public boolean containsRetrievalMethod() {
      return this.lengthRetrievalMethod() > 0;
   }

   /**
    * Method containsSPKIData
    *
    * @return
    */
   public boolean containsSPKIData() {
      return this.lengthSPKIData() > 0;
   }

   /**
    * Method containsUnknownElement
    *
    * @return
    */
   public boolean containsUnknownElement() {
      return this.lengthUnknownElement() > 0;
   }

   /**
    * Method containsX509Data
    *
    * @return
    */
   public boolean containsX509Data() {
      return this.lengthX509Data() > 0;
   }

   /**
    * Method getPublicKey
    *
    * @return
    * @throws KeyResolverException
    */
   public PublicKey getPublicKey() throws KeyResolverException {

      PublicKey pk = this.getPublicKeyFromInternalResolvers();

      if (pk != null) {
         cat.debug("I could find a key using the per-KeyInfo key resolvers");

         return pk;
      } else {
         cat.debug("I couldn't find a key using the per-KeyInfo key resolvers");
      }

      pk = this.getPublicKeyFromStaticResolvers();

      if (pk != null) {
         cat.debug("I could find a key using the system-wide key resolvers");

         return pk;
      } else {
         cat.debug("I couldn't find a key using the system-wide key resolvers");
      }

      return null;
   }

   /**
    * Searches the library wide keyresolvers for public keys
    *
    * @return
    * @throws KeyResolverException
    */
   PublicKey getPublicKeyFromStaticResolvers() throws KeyResolverException {

      for (int i = 0; i < KeyResolver.length(); i++) {
         KeyResolver keyResolver = KeyResolver.item(i);

         for (int j = 0;
                 j < this._constructionElement.getChildNodes().getLength();
                 j++) {
            Node currentChild =
               this._constructionElement.getChildNodes().item(j);

            if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
               if (this._storageResolvers.size() == 0) {

                  // if we do not have storage resolvers, we verify with null
                  StorageResolver storage = null;

                  if (keyResolver.canResolve((Element) currentChild,
                                             this.getBaseURI(), storage)) {
                     PublicKey pk =
                        keyResolver.resolvePublicKey((Element) currentChild,
                                                     this.getBaseURI(),
                                                     storage);

                     if (pk != null) {
                        return pk;
                     }
                  }
               } else {
                  for (int k = 0; k < this._storageResolvers.size(); k++) {
                     StorageResolver storage =
                        (StorageResolver) this._storageResolvers.elementAt(k);

                     if (keyResolver.canResolve((Element) currentChild,
                                                this.getBaseURI(), storage)) {
                        PublicKey pk =
                           keyResolver.resolvePublicKey((Element) currentChild,
                                                        this.getBaseURI(),
                                                        storage);

                        if (pk != null) {
                           return pk;
                        }
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   /**
    * Searches the per-KeyInfo keyresolvers for public keys
    *
    * @return
    * @throws KeyResolverException
    */
   PublicKey getPublicKeyFromInternalResolvers() throws KeyResolverException {

      for (int i = 0; i < this.lengthInternalKeyResolver(); i++) {
         KeyResolverSpi keyResolver = this.itemInternalKeyResolver(i);

         cat.debug("Try " + keyResolver.getClass().getName());

         for (int j = 0;
                 j < this._constructionElement.getChildNodes().getLength();
                 j++) {
            Node currentChild =
               this._constructionElement.getChildNodes().item(j);

            if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
               if (this._storageResolvers.size() == 0) {

                  // if we do not have storage resolvers, we verify with null
                  StorageResolver storage = null;

                  if (keyResolver.engineCanResolve((Element) currentChild,
                                                   this.getBaseURI(),
                                                   storage)) {
                     PublicKey pk =
                        keyResolver
                           .engineResolvePublicKey((Element) currentChild, this
                              .getBaseURI(), storage);

                     if (pk != null) {
                        return pk;
                     }
                  }
               } else {
                  for (int k = 0; k < this._storageResolvers.size(); k++) {
                     StorageResolver storage =
                        (StorageResolver) this._storageResolvers.elementAt(k);

                     if (keyResolver.engineCanResolve((Element) currentChild,
                                                      this.getBaseURI(),
                                                      storage)) {
                        PublicKey pk = keyResolver
                           .engineResolvePublicKey((Element) currentChild, this
                              .getBaseURI(), storage);

                        if (pk != null) {
                           return pk;
                        }
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   /**
    * Method getX509Certificate
    *
    * @return
    * @throws KeyResolverException
    */
   public X509Certificate getX509Certificate() throws KeyResolverException {

      // First search using the individual resolvers from the user
      X509Certificate cert = this.getX509CertificateFromInternalResolvers();

      if (cert != null) {
         cat.debug(
            "I could find a X509Certificate using the per-KeyInfo key resolvers");

         return cert;
      } else {
         cat.debug(
            "I couldn't find a X509Certificate using the per-KeyInfo key resolvers");
      }

      // Then use the system-wide Resolvers
      cert = this.getX509CertificateFromStaticResolvers();

      if (cert != null) {
         cat.debug(
            "I could find a X509Certificate using the system-wide key resolvers");

         return cert;
      } else {
         cat.debug(
            "I couldn't find a X509Certificate using the system-wide key resolvers");
      }

      return null;
   }

   /**
    * This method uses each System-wide {@link KeyResolver} to search the
    * child elements. Each combination of {@link KeyResolver} and child element
    * is checked against all {@link StorageResolver}s.
    *
    * @return
    * @throws KeyResolverException
    */
   X509Certificate getX509CertificateFromStaticResolvers()
           throws KeyResolverException {

      cat.debug("Start getX509CertificateFromStaticResolvers() with "
                + KeyResolver.length() + " resolvers");

      for (int i = 0; i < KeyResolver.length(); i++) {
         KeyResolver keyResolver = KeyResolver.item(i);

         for (int j = 0;
                 j < this._constructionElement.getChildNodes().getLength();
                 j++) {
            Node currentChild =
               this._constructionElement.getChildNodes().item(j);

            if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
               if (this._storageResolvers.size() == 0) {

                  // if we do not have storage resolvers, we verify with null
                  StorageResolver storage = null;

                  if (keyResolver.canResolve((Element) currentChild,
                                             this.getBaseURI(), storage)) {
                     X509Certificate cert =
                        keyResolver
                           .resolveX509Certificate((Element) currentChild, this
                              .getBaseURI(), storage);

                     if (cert != null) {
                        return cert;
                     }
                  }
               } else {
                  for (int k = 0; k < this._storageResolvers.size(); k++) {
                     StorageResolver storage =
                        (StorageResolver) this._storageResolvers.elementAt(k);

                     if (keyResolver.canResolve((Element) currentChild,
                                                this.getBaseURI(), storage)) {
                        X509Certificate cert = keyResolver
                           .resolveX509Certificate((Element) currentChild, this
                              .getBaseURI(), storage);

                        if (cert != null) {
                           return cert;
                        }
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   /**
    * Method getX509CertificateFromInternalResolvers
    *
    * @return
    * @throws KeyResolverException
    */
   X509Certificate getX509CertificateFromInternalResolvers()
           throws KeyResolverException {

      cat.debug("Start getX509CertificateFromInternalResolvers() with "
                + this.lengthInternalKeyResolver() + " resolvers");

      for (int i = 0; i < this.lengthInternalKeyResolver(); i++) {
         KeyResolverSpi keyResolver = this.itemInternalKeyResolver(i);

         cat.debug("Try " + keyResolver.getClass().getName());

         for (int j = 0;
                 j < this._constructionElement.getChildNodes().getLength();
                 j++) {
            Node currentChild =
               this._constructionElement.getChildNodes().item(j);

            if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
               if (this._storageResolvers.size() == 0) {

                  // if we do not have storage resolvers, we verify with null
                  StorageResolver storage = null;

                  if (keyResolver.engineCanResolve((Element) currentChild,
                                                   this.getBaseURI(),
                                                   storage)) {
                     X509Certificate cert =
                        keyResolver.engineResolveX509Certificate(
                           (Element) currentChild, this.getBaseURI(), storage);

                     if (cert != null) {
                        return cert;
                     }
                  }
               } else {
                  for (int k = 0; k < this._storageResolvers.size(); k++) {
                     StorageResolver storage =
                        (StorageResolver) this._storageResolvers.elementAt(k);

                     if (keyResolver.engineCanResolve((Element) currentChild,
                                                      this.getBaseURI(),
                                                      storage)) {
                        X509Certificate cert =
                           keyResolver.engineResolveX509Certificate(
                              (Element) currentChild, this.getBaseURI(),
                              storage);

                        if (cert != null) {
                           return cert;
                        }
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   /**
    * Stores the individual (per-KeyInfo) {@link KeyResolver}s
    */
   Vector _internalKeyResolvers = new Vector();

   /**
    * This method is used to add a custom {@link KeyResolverSpi} to a KeyInfo
    * object.
    *
    * @param realKeyResolver
    */
   public void registerInternalKeyResolver(KeyResolverSpi realKeyResolver) {
      this._internalKeyResolvers.add(realKeyResolver);
   }

   /**
    * Method lengthInternalKeyResolver
    *
    * @return
    */
   int lengthInternalKeyResolver() {
      return this._internalKeyResolvers.size();
   }

   /**
    * Method itemInternalKeyResolver
    *
    * @param i
    * @return
    */
   KeyResolverSpi itemInternalKeyResolver(int i) {
      return (KeyResolverSpi) this._internalKeyResolvers.elementAt(i);
   }

   /** Field _storageResolvers */
   Vector _storageResolvers = new Vector();

   /**
    * Method addStorageResolver
    *
    * @param storageResolver
    */
   public void addStorageResolver(StorageResolver storageResolver) {

      if (storageResolver != null) {
         this._storageResolvers.add(storageResolver);
      }
   }

   /**
    * Method getStorageResolvers
    *
    * @return
    */
   Vector getStorageResolvers() {
      return this._storageResolvers;
   }

   //J-
   static boolean _alreadyInitialized = false;
   public static void init() {

      if (!KeyInfo._alreadyInitialized) {
         if (KeyInfo.cat == null) {

            /**
             * @todo why the hell does the static initialization from the
             *  start not work ?
             */
            KeyInfo.cat =
               org.apache.log4j.Category.getInstance(KeyInfo.class.getName());

            cat.error("Had to assign cat in the init() function");
         }

         // KeyInfo._contentHandlerHash = new HashMap(10);
         KeyInfo._alreadyInitialized = true;
      }
   }
   public static void registerKeyInfoContentHandler(
           String namespace, String localname, String implementingClass)
              throws ContentHandlerAlreadyRegisteredException {
      Init.registerKeyInfoContentHandler(namespace, localname,
                                         implementingClass);
   }
   //J+
   static {
      org.apache.xml.security.Init.init();

      KeyInfo.cat =
         org.apache.log4j.Category.getInstance(KeyInfo.class.getName());
   }
}
