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

            if (java.security.Security.getProvider(Id) == null) {
               cat.debug("The provider " + Id
                         + " had to be added to the java.security.Security");
               java.security.Security.addProvider(prov);
            }
         }
      } catch (TransformerException ex) {}
      catch (ClassNotFoundException ex) {}
      catch (IllegalAccessException ex) {}
      catch (InstantiationException ex) {}
   }

   /**
    * Method getProviderIsAvailable
    *
    * @param providerId
    * @return
    */
   public static boolean getProviderIsAvailable(String providerId) {

      boolean available = false;

      try {
         Element pro =
            (Element) XPathAPI.selectSingleNode(JCEMapper._providerList,
                                                "./x:Providers/x:Provider[@Id='"
                                                + providerId + "']",
                                                JCEMapper._nscontext);
         String providerClass = pro.getAttribute("Class");
         java.security.Provider prov =
            (java.security.Provider) Class.forName(providerClass).newInstance();

         if (prov != null) {
            available = true;
         }
      } catch (TransformerException ex) {
         ;
      } catch (ClassNotFoundException ex) {
         ;
      } catch (IllegalAccessException ex) {
         ;
      } catch (InstantiationException ex) {
         ;
      }

      return available;
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

            if (JCEMapper.getProviderIsAvailable(providerId)) {
               JCEMapper.addProvider(providerId);

               ProviderIdClass result = new ProviderIdClass(jceName,
                                           providerId);

               cat.debug("Found " + result.getAlgorithmID() + " from provider "
                         + result.getProviderId());

               return result;
            }
         }
      } catch (TransformerException ex) {
         cat.debug("Found nothing: " + ex.getMessage());
      }

      return null;
   }

   /**
    * Method translateURItoJCEID
    *
    * @param AlgorithmURI
    * @param requestedProviderId
    * @return
    */
   public static ProviderIdClass translateURItoJCEID(String AlgorithmURI,
           String requestedProviderId) {

      cat.debug("Request for URI " + AlgorithmURI + " from provider "
                + requestedProviderId);

      if (!JCEMapper.getProviderIsAvailable(requestedProviderId)) {
         return null;
      }

      try {
         Element pro = (Element) XPathAPI.selectSingleNode(
            JCEMapper._providerList,
            "./x:Algorithms/x:Algorithm[@URI='" + AlgorithmURI
            + "']/x:ProviderAlgo[@ProviderId='" + requestedProviderId + "']",
            JCEMapper._nscontext);
         String jceName = pro.getAttribute("JCEName");

         JCEMapper.addProvider(requestedProviderId);

         ProviderIdClass result = new ProviderIdClass(jceName, requestedProviderId);

         cat.debug("Found " + result.getAlgorithmID() + " from provider "
                   + result.getProviderId());

         return result;
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
    * Class ProviderIdClass
    *
    * @author $Author$
    * @version $Revision$
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
