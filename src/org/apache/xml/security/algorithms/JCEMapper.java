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
package org.apache.xml.security.algorithms;



import java.security.Key;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * This class maps algorithm identifier URIs to JAVA JCE class names.
 *
 * @author $Author$
 */
public class JCEMapper {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(JCEMapper.class.getName());

   /** Field _providerList */
   private static Element _providerList = null;

   /** Field _nscontext */
   private static Element _nscontext = null;
   
   private static CachedXPathAPI cx=null;
   
   private static Map uriToProvider = new HashMap();
   private static Map cacheProviderIsInClassPath = new HashMap();

   /**
    * Method init
    *
    * @param mappingElement
    * @param cx for using in the xpath searchs
    * @throws Exception
    */
   public static void init(Element mappingElement,CachedXPathAPI cx) throws Exception {

      JCEMapper._providerList = mappingElement;

      Document doc = mappingElement.getOwnerDocument();

      JCEMapper._nscontext =
         XMLUtils.createDSctx(doc, "x",
                              "http://www.xmlsecurity.org/NS/#configuration");
      JCEMapper.cx=cx;
   }

   /**
    * This method takes a Provider ID and tries to register this provider in the JCE.
    *
    * @param Id the provider Id
    * @return true if the provider was registerd
    *
    */
   public static boolean addProvider(String Id) {

      try {
         if (Security.getProvider(Id) == null) {
            Element providerElem = (Element) cx.selectSingleNode(
               JCEMapper._providerList,
               "./x:Providers/x:Provider[@Id='" + Id + "']",
               JCEMapper._nscontext);
            String providerClass = providerElem.getAttributeNS(null, "Class");
            java.security.Provider prov =
               (java.security.Provider) Class.forName(providerClass)
                  .newInstance();

            if (java.security.Security.getProvider(Id) == null) {
               log.debug("The provider " + Id
                         + " had to be added to the java.security.Security");
               java.security.Security.addProvider(prov);

               Provider registeredProvider =
                  java.security.Security.getProvider(Id);

               if (registeredProvider != null) {
                  return true;
               }
            }
         }
      } catch (TransformerException ex) {}
      catch (ClassNotFoundException ex) {}
      catch (IllegalAccessException ex) {}
      catch (InstantiationException ex) {}

      return false;
   }

   /**
    * Method getProviderIsAvailable
    *
    * @param providerId the id to search
    *  @return true if the provider is in the classpath
    *
    */
   public static boolean getProviderIsInClassPath(String providerId) {

      boolean available = false;
      Boolean isInClassPath=(Boolean) cacheProviderIsInClassPath.get(providerId);
      if ((isInClassPath!=null) && isInClassPath.booleanValue()) {
      	//Don't cache the negatives one, in case that latter are added
      	//To the classpath
      	//FIXME: Can the above happend?
      	return true;
      }


      try {
		  /* Allow for mulitple provider entries with same Id */
		  NodeList providers = cx.selectNodeList(JCEMapper._providerList,
													   "./x:Providers/x:Provider[@Id='"
													   + providerId + "']",
													   JCEMapper._nscontext);

         for (int i = 0; available == false && i < providers.getLength(); i++) {
            Element pro = (Element) providers.item(i);

			String providerClass = pro.getAttributeNS(null, "Class");
			try {
				java.security.Provider prov =
					(java.security.Provider) Class.forName(providerClass).newInstance();

				if (prov != null) {
					available = true;
				}
			} catch (ClassNotFoundException ex) {
				//do nothing
			} catch (IllegalAccessException ex) {
				//do nothing
			} catch (InstantiationException ex) {
				//do nothing
			}
		 }
      } catch (TransformerException ex) {
		//do nothing
      }
      
      cacheProviderIsInClassPath.put(providerId,new Boolean(available));
      return available;
   }

   /**
    * Return <CODE>true</CODE> if the Provider with the given
    * <CODE>providerId</CODE> is available in {@link java.security.Security}.
    *
    * @param providerId
    * @return <CODE>true</CODE> if the Provider with the given <CODE>providerId</CODE> is available in {@link java.security.Security}
    */
   public static boolean getProviderIsRegisteredAtSecurity(String providerId) {

      java.security.Provider prov =
         java.security.Security.getProvider(providerId);

      if (prov != null) {
         return true;
      }

      return false;
   }

   /**
    * Method translateURItoJCEID
    *
    * @param AlgorithmURI
    * @return the Provider that manages the given URI
    *
    */
   public static ProviderIdClass translateURItoJCEID(String AlgorithmURI) {

      log.debug("Request for URI " + AlgorithmURI);

      ProviderIdClass prov=(ProviderIdClass) uriToProvider.get(AlgorithmURI);
      if (prov!=null) {
      	return prov;
      }


      try {
 
         NodeList providers = cx.selectNodeList(JCEMapper._providerList,
                                 "./x:Algorithms/x:Algorithm[@URI='"
                                 + AlgorithmURI + "']/x:ProviderAlgo",
                                 JCEMapper._nscontext);

         for (int i = 0; i < providers.getLength(); i++) {
            Element pro = (Element) providers.item(i);
            String jceName = pro.getAttributeNS(null, "JCEName");
            String providerId = pro.getAttributeNS(null, "ProviderId");

            if (JCEMapper.getProviderIsInClassPath(providerId)) {
               JCEMapper.addProvider(providerId);

               ProviderIdClass result = new ProviderIdClass(jceName,
                                           providerId);

               log.debug("Found " + result.getAlgorithmID() + " from provider "
                         + result.getProviderId());

               uriToProvider.put(AlgorithmURI,result);
               return result;
            }
         }
      } catch (TransformerException ex) {
         log.debug("Found nothing: " + ex.getMessage());
      }

      return null;
   }

   /**
    * Method translateURItoJCEID
    *
    * @param AlgorithmURI
    * @param requestedProviderId
    * @return the Provider that manages the given URI
    *
    */
   public static ProviderIdClass translateURItoJCEID(String AlgorithmURI,
           String requestedProviderId) {

      log.debug("Request for URI " + AlgorithmURI + " from provider "
                + requestedProviderId);

      if (!JCEMapper.getProviderIsInClassPath(requestedProviderId)) {
         return null;
      }

      try {
         Element pro = (Element) cx.selectSingleNode(
            JCEMapper._providerList,
            "./x:Algorithms/x:Algorithm[@URI='" + AlgorithmURI
            + "']/x:ProviderAlgo[@ProviderId='" + requestedProviderId + "']",
            JCEMapper._nscontext);
         String jceName = pro.getAttributeNS(null, "JCEName");

         JCEMapper.addProvider(requestedProviderId);

         ProviderIdClass result = new ProviderIdClass(jceName,
                                     requestedProviderId);

         log.debug("Found " + result.getAlgorithmID() + " from provider "
                   + result.getProviderId());

         return result;
      } catch (TransformerException ex) {
         log.debug("Found nothing: " + ex.getMessage());
      }

      return null;
   }

   /**
    * Method getAlgorithmClassFromURI
    * NOTE(Raul Benito) It seems a buggy function the loop doesn't do
    * anything??
    * @param AlgorithmURI
    * @return the class name that implements this algorithm
    *
    */
   public static String getAlgorithmClassFromURI(String AlgorithmURI) {

      log.debug("Request for URI " + AlgorithmURI);

      try {
         NodeList providers = cx.selectNodeList(JCEMapper._providerList,
                                 "./x:Algorithms/x:Algorithm[@URI='"
                                 + AlgorithmURI + "']/x:ProviderAlgo",
                                 JCEMapper._nscontext);

         for (int i = 0; i < providers.getLength(); i++) {
            Element pro = (Element) providers.item(i);
            Attr jceName = pro.getAttributeNodeNS(null, "JCEName");

            log.debug("Found " + jceName.getNodeValue());
         }

         return ((Element) providers.item(0)).getAttributeNS(null, "JCEName");
      } catch (TransformerException ex) {
         log.debug("Found nothing: " + ex.getMessage());

         return "";
      }
   }

   /**
    * Method getKeyTypeFromURI
    *
    * @param AlgorithmURI
    * @return the type of key used fpr the algorithm
    */
   public static int getKeyTypeFromURI(String AlgorithmURI) {

      try {
         Attr algoclassAttr =
            (Attr) cx.selectSingleNode(JCEMapper._providerList,
                                             "./x:Algorithms/x:Algorithm[@URI='"
                                             + AlgorithmURI
                                             + "']/@AlgorithmClass", JCEMapper
                                                ._nscontext);

         if (algoclassAttr != null) {            
            String algoclass = algoclassAttr.getNodeValue();

            if (algoclass.equals(JCEMapper.KEYTYPE_BLOCK_ENCRYPTION)) {
               return javax.crypto.Cipher.SECRET_KEY;
            } else if (algoclass.equals("Mac")) {
               return javax.crypto.Cipher.SECRET_KEY;
            } else if (algoclass.equals(JCEMapper.KEYTYPE_SYMMETRIC_KEY_WRAP)) {
               return javax.crypto.Cipher.SECRET_KEY;
            } else if (algoclass.equals(JCEMapper.KEYTYPE_KEY_TRANSPORT)) {
               return javax.crypto.Cipher.SECRET_KEY;
            }
         }
      } catch (TransformerException ex) {
         log.debug("Found nothing: " + ex.getMessage());
      }

      return -1;
   }

   /**
    * Returns the keylength in bit for a particular algorithm.
    *
    * @param AlgorithmURI
    * @return The length of the key used in the alogrithm
    */
   public static int getKeyLengthFromURI(String AlgorithmURI) {

      try {
         Attr algoclassAttr =
            (Attr) cx.selectSingleNode(JCEMapper._providerList,
                                             "./x:Algorithms/x:Algorithm[@URI='"
                                             + AlgorithmURI + "']/@KeyLength",
                                             JCEMapper._nscontext);

         if (algoclassAttr != null) {
            return Integer.parseInt(algoclassAttr.getNodeValue());
         }
      } catch (TransformerException ex) {
         log.debug("Found nothing: " + ex.getMessage());
      }

      return 0;
   }

   /**
    * Method getJCEKeyAlgorithmFromURI
    *
    * @param AlgorithmURI
    * @param ProviderId
    * @return The KeyAlgorithm for the given URI.
    *
    */
   public static String getJCEKeyAlgorithmFromURI(String AlgorithmURI,
           String ProviderId) {

      try {
         Attr algoclassAttr =
            (Attr) cx.selectSingleNode(JCEMapper._providerList,
                                             "./x:Algorithms/x:Algorithm[@URI='"
                                             + AlgorithmURI
                                             + "']/x:ProviderAlgo[@ProviderId='"
                                             + ProviderId + "']/@RequiredKey",
                                             JCEMapper._nscontext);

         if (algoclassAttr != null) {
            return algoclassAttr.getNodeValue();
         }
      } catch (TransformerException ex) {
         log.debug("Found nothing: " + ex.getMessage());
      }

      return null;
   }


   /**
    * @param AlgorithmURI
    * @param ProviderId
    * @return The IVJCEName for the given algorithm
    */
    public static String getJCEIVAlgorithmFromURI(String AlgorithmURI,
           String ProviderId) {

      try {
         Attr algoclassAttr =
            (Attr) cx.selectSingleNode(JCEMapper._providerList,
                                             "./x:Algorithms/x:Algorithm[@URI='"
                                             + AlgorithmURI
                                             + "']/x:ProviderAlgo[@ProviderId='"
                                             + ProviderId + "']/@IVJCEName",
                                             JCEMapper._nscontext);

         if (algoclassAttr != null) {
            return algoclassAttr.getNodeValue();
         }
      } catch (TransformerException ex) {
         log.debug("Found nothing: " + ex.getMessage());
      }

      return null;
   }

   /** Field KEYTYPE_SYMMETRIC_KEY_WRAP           */
   public static final String KEYTYPE_SYMMETRIC_KEY_WRAP = "SymmetricKeyWrap";

   /** Field KEYTYPE_BLOCK_ENCRYPTION           */
   public static final String KEYTYPE_BLOCK_ENCRYPTION = "BlockEncryption";

   /** Field KEYTYPE_KEY_TRANSPORT           */
   public static final String KEYTYPE_KEY_TRANSPORT = "KeyTransport";

   /**
    * This takes a {@link Key} and one of the <CODE>JCEMapper.KEYTYPE_XXX</CODE>
    * Strings and returns the algorithm for which this key is.
    * <BR />
    * Example: If you enter an AES Key of length 128 bit and the
    * <CODE>JCEMapper.KEYTYPE_SYMMETRIC_KEY_WRAP</CODE>, the result is
    * <CODE>EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128</CODE>.
    *
    *
    * @param key
    * @param type
    * @return the URI for the given type and key length.
    *
    */
   public static String getURIfromKey(Key key, String type) {

      String JCEalgo = key.getAlgorithm();
      String keyLength = new Integer(key.getEncoded().length * 8).toString();

      try {
         Attr URI = (Attr) cx.selectSingleNode(
            JCEMapper._providerList,
            "./x:Algorithms/x:Algorithm[@KeyLength='" + keyLength
            + "' and @AlgorithmClass='" + type
            + "']/x:ProviderAlgo[@RequiredKey='" + JCEalgo + "']/../@URI",
            JCEMapper._nscontext);

         if (URI != null) {
            return URI.getNodeValue();
         }
      } catch (TransformerException ex) {
         log.debug("Found nothing: " + ex.getMessage());
      }

      return null;
   }

   /*
   public static String getWrapURIfromKey(Key key) {
      return JCEMapper.getURIfromKey(key, JCEMapper.KEYTYPE_SYMMETRIC_KEY_WRAP);
   }

   public static String getCipherURIfromKey(Key key) {
      return JCEMapper.getURIfromKey(key, JCEMapper.KEYTYPE_BLOCK_ENCRYPTION);
   }
   */

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
       * @return The algorithmId of this provider       
       */
      public String getAlgorithmID() {
         return this._algorithmId;
      }

      /**
       * Method getProvider
       * @return the providerId of this provider       
       */
      public String getProviderId() {
         return this._providerId;
      }
   }
}
