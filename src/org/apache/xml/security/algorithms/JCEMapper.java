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
package org.apache.xml.security.algorithms;



import java.util.*;
import java.security.*;
import org.w3c.dom.*;
import org.apache.xpath.XPathAPI;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.utils.*;


/**
 * This class maps algorithm identifier URIs to JAVA JCE class names.
 *
 * @author $Author$
 */
public class JCEMapper {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(JCEMapper.class.getName());

   /** Field _providerList */
   private static Element _providerList = null;

   /** Field _nscontext */
   private static Element _nscontext = null;

   /**
    * Method init
    *
    * @param mappingElement
    * @throws Exception
    */
   public static void init(Element mappingElement) throws Exception {

      JCEMapper._providerList = mappingElement;

      Document doc = mappingElement.getOwnerDocument();

      JCEMapper._nscontext =
         XMLUtils.createDSctx(doc, "x",
                              "http://www.xmlsecurity.org/NS/#configuration");
   }

   /**
    * This method takes a Provider ID and tries to register this provider in the JCE.
    *
    * @param Id
    */
   public static void addProvider(String Id) {

      try {
         if (Security.getProvider(Id) == null) {
            Element providerElem = (Element) XPathAPI.selectSingleNode(
               JCEMapper._providerList,
               "./x:Providers/x:Provider[@Id='" + Id + "']",
               JCEMapper._nscontext);
            String providerClass = providerElem.getAttribute("Class");
            java.security.Provider prov =
               (java.security.Provider) Class.forName(providerClass)
                  .newInstance();

            java.security.Security.addProvider(prov);
         }
      } catch (TransformerException ex) {}
      catch (ClassNotFoundException ex) {}
      catch (IllegalAccessException ex) {}
      catch (InstantiationException ex) {}
   }

   /**
    * Method translateURItoJCEID
    *
    * @param AlgorithmURI
    * @return
    */
   public static ProviderIdClass translateURItoJCEID(String AlgorithmURI) {

      cat.debug("Request for URI " + AlgorithmURI);

      try {

         /*
         Attr jceName = (Attr) XPathAPI.selectSingleNode(
            JCEMapper._providerList,
            "./x:Algorithms/x:Algorithm[@URI='" + AlgorithmURI
            + "']/x:Provider[1]/@JCEName", JCEMapper._nscontext);
         */
         NodeList providers = XPathAPI.selectNodeList(JCEMapper._providerList,
                                 "./x:Algorithms/x:Algorithm[@URI='"
                                 + AlgorithmURI + "']/x:ProviderAlgo",
                                 JCEMapper._nscontext);

         for (int i = 0; i < providers.getLength(); i++) {
            Element pro = (Element) providers.item(i);
            String jceName = pro.getAttribute("JCEName");
            String providerId = pro.getAttribute("ProviderId");

            JCEMapper.addProvider(providerId);

            Element providerElem = (Element) XPathAPI.selectSingleNode(
               JCEMapper._providerList,
               "./x:Providers/x:Provider[@Id='" + providerId + "']",
               JCEMapper._nscontext);
            String providerClass = providerElem.getAttribute("Class");
            ProviderIdClass result = new ProviderIdClass(jceName, providerId);

            cat.debug("Found " + result.getAlgorithmID() + " from provider "
                      + result.getProviderId());

            return result;
         }
      } catch (TransformerException ex) {
         cat.debug("Found nothing: " + ex.getMessage());
      }

      return null;
   }

   /**
    * Method getAlgorithmClassFromURI
    *
    * @param AlgorithmURI
    * @return
    */
   public static String getAlgorithmClassFromURI(String AlgorithmURI) {

      cat.debug("Request for URI " + AlgorithmURI);

      try {
         NodeList providers = XPathAPI.selectNodeList(JCEMapper._providerList,
                                 "./x:Algorithms/x:Algorithm[@URI='"
                                 + AlgorithmURI + "']/x:ProviderAlgo",
                                 JCEMapper._nscontext);

         for (int i = 0; i < providers.getLength(); i++) {
            Element pro = (Element) providers.item(i);
            Attr jceName = pro.getAttributeNode("JCEName");

            cat.debug("Found " + jceName.getNodeValue());
         }

         return ((Element) providers.item(0)).getAttribute("JCEName");
      } catch (TransformerException ex) {
         cat.debug("Found nothing: " + ex.getMessage());

         return "";
      }
   }

   /**
    * Converts a algorithm URI into a JCE algorithm identifier.
    *
    * @param AlgorithmURI is the URI of the algorithm
    * @return the JCE identifier.
    * public static String translateURItoJCEID_old(String AlgorithmURI) {
    *
    *  for (int i = 0; i < algorithms.length; i++) {
    *     if (((String) algorithms[i][_INDEX_URI]).equals(AlgorithmURI)) {
    *        return algorithms[i][_INDEX_SHORT_ALIAS];
    *     }
    *  }
    *
    *  return "";
    * }
    *
    * //J-
    * static int _INDEX_TYPE        = 0;
    * static int _INDEX_LONG_ALIAS  = 1;
    * static int _INDEX_SHORT_ALIAS = 2;
    * static int _INDEX_URI         = 3;
    *
    * public static String algorithms[][] = {
    *  { "Signature",
    *    "Alg.Alias.Signature.DSAWithSHA1",
    *    "DSAWithSHA1",
    *    Constants.ALGO_ID_SIGNATURE_DSA },
    *  { "Signature",
    *    "Signature.SHA1withRSA",
    *    "SHA1withRSA",
    *    Constants.ALGO_ID_SIGNATURE_RSA },
    *  { "MessageDigest",
    *    "Alg.Alias.MessageDigest.SHA-1",
    *    "SHA-1",
    *    Constants.ALGO_ID_DIGEST_SHA1 },
    *  { "Mac",
    *    "HmacSHA1",
    *    "HmacSHA1",
    *    Constants.ALGO_ID_MAC_HMAC_SHA1 },
    * };
    * //J+
    *
    * public static void a(String url) {
    *  System.out.println("Mapped " + url + " to " + JCEMapper.translateURItoJCEID(url));
    * }
    *
    * public static void main(String unused[]) throws Exception {
    *  a(Constants.ALGO_ID_SIGNATURE_DSA);
    *  a(Constants.ALGO_ID_SIGNATURE_RSA);
    *  a(Constants.ALGO_ID_DIGEST_SHA1);
    *  a(Constants.ALGO_ID_MAC_HMAC_SHA1);
    * }
    */
   public static class ProviderIdClass {

      /** Field _jceid */
      private String _algorithmId;

      /** Field _providerClass */
      private String _providerId;

      /**
       * Constructor ProviderIdClass
       *
       * @param AlgorithmID
       * @param ProviderId
       */
      protected ProviderIdClass(String AlgorithmID, String ProviderId) {
         this._algorithmId = AlgorithmID;
         this._providerId = ProviderId;
      }

      /**
       * Method getJceId
       *
       * @return
       */
      public String getAlgorithmID() {
         return this._algorithmId;
      }

      /**
       * Method getProvider
       *
       * @return
       */
      public String getProviderId() {
         return this._providerId;
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
