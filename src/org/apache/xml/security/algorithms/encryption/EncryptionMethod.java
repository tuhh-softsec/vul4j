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
package org.apache.xml.security.algorithms.encryption;



import java.io.IOException;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.algorithms.Algorithm;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.*;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;


/**
 *
 * What has to be set and get?
 * - BlockLength (implicitly given by URI)
 * - KeyLength (implicitly given by URI)
 * - Padding Mechanism (implicitly given by URI)
 * - Mode of operation (Cipher Block Chaining (CBC)) (implicitly given by URI)
 *
 * - initialization vector IV
 *   - length
 *   - value
 * - the key itself
 * - Encrypt / Decrypt mode
 *
 * @author $Author$
 */
public class EncryptionMethod extends Algorithm {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(EncryptionMethod.class.getName());

   /** Contains the Element from the configuration file */
   static Element _cipherAlgos;

   /** Field _cipherAlgorithmSpi */
   EncryptionMethodSpi _cipherAlgorithmSpi = null;

   /** Needed for stream ciphers */
   int _keySize = -1;

   /** Needed for RSA-OAEP Key Transport */
   MessageDigestAlgorithm _OAEPdigestAlgorithm = null;

   /** Needed for RSA-OAEP Key Transport */
   byte[] _OAEPparams = null;

   /**
    * Method init
    *
    * @param cipherAlgos
    */
   public synchronized static void init(Element cipherAlgos) {

      if (EncryptionMethod.cat == null) {
         EncryptionMethod.cat =
            org.apache.log4j.Category
               .getInstance(EncryptionMethod.class.getName());

         cat.error(
            "init: The EncryptionMethod.cat was null; I had to create it");
      }

      if (EncryptionMethod._cipherAlgos == null) {

         // only set it during the first call
         EncryptionMethod._cipherAlgos = cipherAlgos;
      }

      cat.debug("init: The EncryptionMethod._cipherAlgos element I have is "
                + EncryptionMethod._cipherAlgos);
   }

   /**
    * Constructor EncryptionMethod
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public EncryptionMethod(Element element, String BaseURI)
           throws XMLSecurityException {

      super(element, BaseURI, EncryptionConstants._TAG_ENCRYPTIONMETHOD);

      try {
         Element nscontext = this._doc.createElement("prefixResolver");

         nscontext.setAttribute("xmlns:ds", Constants.SignatureSpecNS);
         nscontext.setAttribute("xmlns:xenc",
                                EncryptionConstants.EncryptionSpecNS);

         // This is needed for Stream Encryption Algorithms
         {
            Text KeySizeText =
               (Text) XPathAPI.selectSingleNode(this._constructionElement,
                                                "./xenc:KeySize/text()",
                                                nscontext);

            if (KeySizeText != null) {
               cat.debug("Found a KeySize of " + KeySizeText.getData());

               this._keySize = Integer.parseInt(KeySizeText.getData());
            } else {
               cat.debug("Didn't find a KeySize");
            }
         }

         // This is needed for RSA-OAEP Key Transport
         {
            Attr DigestMethod =
               (Attr) XPathAPI.selectSingleNode(this._constructionElement,
                                                "./ds:"
                                                + Constants._TAG_DIGESTMETHOD
                                                + "/@"
                                                + Constants
                                                   ._ATT_ALGORITHM, nscontext);

            if (DigestMethod != null) {
               this._OAEPdigestAlgorithm =
                  MessageDigestAlgorithm.getInstance(this._doc,
                                                     DigestMethod
                                                        .getNodeValue());
            }

            Text oaepParam =
               (Text) XPathAPI
                  .selectSingleNode(this._constructionElement, "./xenc:"
                                    + EncryptionConstants._TAG_OAEPPARAMS
                                    + "/text()", nscontext);

            if (oaepParam != null) {
               this._OAEPparams = Base64.decode(oaepParam.getData());
            }
         }

         // after parsing all the EncryptionMethod children, check the
         this._cipherAlgorithmSpi =
            EncryptionMethod.createEncryptionMethodSpi(this.getAlgorithmURI());

         if ((this._keySize != -1)
                 && (this._keySize
                     != this._cipherAlgorithmSpi.getImplementedKeySize())) {
            Object exArgs[] = { Integer.toString(this._keySize),
                                Integer
                                   .toString(this._cipherAlgorithmSpi
                                      .getImplementedKeySize()) };

            throw new XMLSecurityException("encryption.ExplicitKeySizeMismatch",
                                           exArgs);
         }
      } catch (TransformerException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Constructor EncryptionMethod
    *
    * @param doc
    * @param AlgorithmURI
    * @throws XMLSecurityException
    */
   public EncryptionMethod(Document doc, String AlgorithmURI)
           throws XMLSecurityException {

      super(doc, EncryptionConstants._TAG_ENCRYPTIONMETHOD,
            EncryptionConstants.EncryptionSpecNS, AlgorithmURI);

      this._cipherAlgorithmSpi =
         EncryptionMethod.createEncryptionMethodSpi(AlgorithmURI);
   }

   /**
    * Method createEncryptionMethodSpi
    *
    * @param AlgorithmURI
    * @return
    * @throws XMLSecurityException
    */
   private synchronized static EncryptionMethodSpi createEncryptionMethodSpi(
           String AlgorithmURI) throws XMLSecurityException {

      cat.debug("createEncryptionMethodSpi called()");

      EncryptionMethodSpi encryptionMethodSpi = null;

      if (EncryptionMethod._cipherAlgos == null) {

         // not yet initialized ?!?
         Object exArgs[] = { EncryptionMethod.class.getName() };

         throw new XMLSecurityException("notYetInitialized", exArgs);
      }

      try {
         Element nscontext =
            EncryptionMethod._cipherAlgos.getOwnerDocument()
               .createElement("nscontext");

         nscontext.setAttribute("xmlns:x",
                                "http://www.xmlsecurity.org/NS/#configuration");

         NodeList nl =
            XPathAPI.selectNodeList(EncryptionMethod._cipherAlgos,
                                    "./x:EncryptionMethod[@URI='"
                                    + AlgorithmURI + "']/x:ProviderAlgo",
                                    nscontext);

         for (int i = 0; i < nl.getLength(); i++) {
            Element provElem = (Element) nl.item(i);
            String ProviderIdAttr = provElem.getAttribute("ProviderId");
            String JCENameAttr = provElem.getAttribute("JCEName");
            String JAVACLASSAttr = provElem.getAttribute("JAVACLASS");

            cat.debug("ProviderIdAttr = " + ProviderIdAttr);
            cat.debug("JCENameAttr = " + JCENameAttr);
            cat.debug("JAVACLASSAttr = " + JAVACLASSAttr);

            if (JCEMapper.getProviderIsAvailable(ProviderIdAttr)) {
               try {
                  encryptionMethodSpi =
                     (EncryptionMethodSpi) Class.forName(JAVACLASSAttr)
                        .newInstance();
               } catch (java.lang.ClassNotFoundException ex) {

                  // ignore
               } catch (java.lang.IllegalAccessException ex) {

                  // ignore
               } catch (java.lang.InstantiationException ex) {

                  // ignore
               }
            }
         }
      } catch (TransformerException ex) {
         throw new XMLSecurityException("empty", ex);
      }

      if (encryptionMethodSpi == null) {

         // no Provider found who could do the job?
         throw new XMLSecurityException("empty");
      }

      return encryptionMethodSpi;
   }

   /**
    * Factory method for constructing a message digest algorithm by name.
    *
    * @param doc
    * @param AlgorithmURI
    * @return
    * @throws XMLSecurityException
    */
   public static EncryptionMethod getInstance(Document doc, String AlgorithmURI)
           throws XMLSecurityException {
      return new EncryptionMethod(doc, AlgorithmURI);
   }

   /**
    * Method getExplicitKeySize
    *
    * @return
    */
   public int getKeySize() {
      return this._keySize;
   }

   /**
    * Method getOAEPMessageDigest
    *
    * @return
    */
   public MessageDigestAlgorithm getOAEPMessageDigest() {
      return this._OAEPdigestAlgorithm;
   }

   /**
    * Method getOAEPParams
    *
    * @return
    */
   public byte[] getOAEPParams() {
      return this._OAEPparams;
   }

   /**
    * Proxy method for {@link javax.crypto.Cipher#getAlgorithm}
    * which is executed on the internal {@link javax.crypto.Cipher} object.
    *
    * @return the result of the {@link javax.crypto.Cipher#getAlgorithm} method
    */
   public String getJCEAlgorithmString() {
      return this._cipherAlgorithmSpi.engineGetJCEAlgorithmString();
   }

   /**
    * Proxy method for {@link javax.crypto.Cipher#getProvider}
    * which is executed on the internal {@link javax.crypto.Cipher} object.
    *
    * @return the result of the {@link javax.crypto.Cipher#getProvider} method
    */
   public java.security.Provider getJCEProvider() {
      return this._cipherAlgorithmSpi.engineGetJCEProvider();
   }

   /**
    * Proxy method for {@link javax.crypto.Cipher#getBlockSize}
    * which is executed on the internal {@link javax.crypto.Cipher} object.
    *
    * @return the result of the {@link javax.crypto.Cipher#getBlockSize} method
    */
   public int getBlockSize() {
      return this._cipherAlgorithmSpi.engineGetBlockSize();
   }

   /**
    * Proxy method for {@link javax.crypto.Cipher#getExemptionMechanism}
    * which is executed on the internal {@link javax.crypto.Cipher} object.
    *
    * @return the result of the {@link javax.crypto.Cipher#getExemptionMechanism} method
    */
   public ExemptionMechanism getExemptionMechanism() {
      return this._cipherAlgorithmSpi.engineGetExemptionMechanism();
   }

   /**
    * Proxy method for {@link javax.crypto.Cipher#getIV}
    * which is executed on the internal {@link javax.crypto.Cipher} object.
    *
    * @return the result of the {@link javax.crypto.Cipher#getIV} method
    */
   public byte[] getIV() {
      return this._cipherAlgorithmSpi.engineGetIV();
   }

   /**
    * Proxy method for {@link javax.crypto.Cipher#getOutputSize(int)}
    * which is executed on the internal {@link javax.crypto.Cipher} object.
    *
    * @param inputLen
    * @return the result of the {@link javax.crypto.Cipher#getOutputSize(int)} method
    */
   public int getOutputSize(int inputLen) {
      return this._cipherAlgorithmSpi.engineGetOutputSize(inputLen);
   }

   /**
    * Proxy method for {@link javax.crypto.Cipher#update}
    * which is executed on the internal {@link javax.crypto.Cipher} object.
    *
    * @param input
    * @throws XMLSecurityException
    */
   public void update(byte[] input) throws XMLSecurityException {
      this._cipherAlgorithmSpi.engineUpdate(input);
   }

   /**
    * Proxy method for {@link javax.crypto.Cipher#update}
    * which is executed on the internal {@link javax.crypto.Cipher} object.
    *
    * @param buf
    * @param offset
    * @param len
    * @throws XMLSecurityException
    */
   public void update(byte buf[], int offset, int len)
           throws XMLSecurityException {
      this._cipherAlgorithmSpi.engineUpdate(buf, offset, len);
   }

   /**
    * Method getBaseNamespace
    *
    * @return
    */
   public String getBaseNamespace() {
      return EncryptionConstants.EncryptionSpecNS;
   }

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    * @todo delete from production version
    */
   public static void main(String unused[]) throws Exception {

      org.apache.xml.security.Init.init();

      //J-
      final String xmlStr = "" +
      "<EncryptionMethod Algorithm='http://www.w3.org/2001/04/xmlenc#aes128-cbc' xmlns='http://www.w3.org/2001/04/xmlenc#'>" + "\n" +
      "  <KeySize>192</KeySize>" + "\n" +
      "  <DigestMethod Algorithm='http://www.w3.org/2000/09/xmldsig#sha1' xmlns='http://www.w3.org/2000/09/xmldsig#' />" + "\n" +
      "  <OAEPparams> 91Wu3Q== </OAEPparams>" + "\n" +
      "</EncryptionMethod>" + "\n" +
      "" + "\n" +
      "";
      //J+
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc =
         db.parse(new java.io.ByteArrayInputStream(xmlStr.getBytes()));
      EncryptionMethod c = new EncryptionMethod(doc.getDocumentElement(),
                                                "file://1.xml");

      System.out.println(c.getAlgorithmURI());
      System.out.println(c.getKeySize());
      System.out.println(c.getOAEPMessageDigest().getAlgorithmURI());

      if (c.getOAEPParams() != null) {
         System.out.println("OAEPParams.length=" + c.getOAEPParams().length);
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
