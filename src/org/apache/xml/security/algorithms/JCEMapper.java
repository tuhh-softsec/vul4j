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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.apache.xml.security.Init;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;



/**
 * This class maps algorithm identifier URIs to JAVA JCE class names.
 *
 * @author $Author$
 */
public class JCEMapper {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(JCEMapper.class.getName());


   
   private static Map uriToProvider = new HashMap();
   private static Map cacheProviderIsInClassPath = new HashMap();
   
   private static Map providersMap = new HashMap();
   private static Map algorithmsMap = new HashMap();

   /**
    * Method init
    *
    * @param mappingElement
    * @throws Exception
    */
   public static void init(Element mappingElement) throws Exception {

      //JCEMapper._providerList = mappingElement;

      //Document doc = mappingElement.getOwnerDocument();
      loadProviders((Element)mappingElement.getElementsByTagName("Providers").item(0));
      loadAlgorithms((Element)mappingElement.getElementsByTagName("Algorithms").item(0));
   }

   static void loadProviders( Element providers) {
   	    Element[] elements=XMLUtils.selectNodes(providers.getFirstChild(),Init.CONF_NS,"Provider");
        for (int i=0;i<elements.length;i++) {
            Element el=elements[i];
            String id=el.getAttribute("Id");
            List list=(List) providersMap.get(id);
            if (list==null) {
            	list=new ArrayList();
            }
            list.add(new ProviderJCE(el));
        	providersMap.put(id,list);
        }
   }
   static ProviderJCE getProvider(String id) {
    List list=(List) providersMap.get(id);
   	if (list==null) {
   		return null;
    }
    return (ProviderJCE) list.get(0);
    
   }
   static List getProviders(String id) {
    List list=(List) providersMap.get(id);
    if (list==null) {
        return null;
    }
    return list;    
   }
   static void loadAlgorithms( Element algorithmsEl) {
        Element[] algorithms=XMLUtils.selectNodes(algorithmsEl.getFirstChild(),Init.CONF_NS,"Algorithm");
    for (int i=0;i<algorithms.length;i++) {
        Element el=algorithms[i];
        String id=el.getAttribute("URI");
        Algorithm providerAlgoMap=new Algorithm(el);
        Element []providerAlgos=XMLUtils.selectNodes(el.getFirstChild(),Init.CONF_NS,"ProviderAlgo");
        for (int j=0;j<providerAlgos.length;j++) {
            Element elp=providerAlgos[j];
            AlgorithmMapping idA=new AlgorithmMapping(providerAlgoMap,elp);            
        	providerAlgoMap.put(idA.ProviderId,idA);
        }
        algorithmsMap.put(id,providerAlgoMap);
    }
    
   }
   static AlgorithmMapping getAlgorithmMapping(String algoURI,String providerId) {
   	    Map algo=(Map) algorithmsMap.get(algoURI);
        return (AlgorithmMapping) algo.get(providerId);
   }
   static Algorithm getAlgorithmMapping(String algoURI) {
   	   return ((Algorithm)algorithmsMap.get(algoURI));
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
            String providerClass =  getProvider(Id).providerClass;
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
      } 
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

       
		  /* Allow for mulitple provider entries with same Id */
		  List providers = getProviders(providerId);

         for (int i = 0; available == false && i < providers.size(); i++) {            
			String providerClass = ((ProviderJCE)providers.get(i)).providerClass;
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


      
 
         Iterator providers=getAlgorithmMapping(AlgorithmURI)
         .values()
         .iterator();
         
         while (providers.hasNext()) {
            
            AlgorithmMapping map= (AlgorithmMapping)providers.next();                        

            if (JCEMapper.getProviderIsInClassPath(map.ProviderId)) {
               JCEMapper.addProvider(map.ProviderId);

               ProviderIdClass result = new ProviderIdClass(map.JCEName,
                                           map.ProviderId);

               log.debug("Found " + result.getAlgorithmID() + " from provider "
                         + result.getProviderId());

               uriToProvider.put(AlgorithmURI,result);
               return result;
            }
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


         
         String jceName = getAlgorithmMapping(AlgorithmURI,requestedProviderId).JCEName;

         JCEMapper.addProvider(requestedProviderId);

         ProviderIdClass result = new ProviderIdClass(jceName,
                                     requestedProviderId);

         log.debug("Found " + result.getAlgorithmID() + " from provider "
                   + result.getProviderId());

         return result;
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

         Iterator alth=getAlgorithmMapping(AlgorithmURI).entrySet().iterator();
         String uri=null;
         
         while (alth.hasNext()) {
            String cur=((AlgorithmMapping)alth.next()).JCEName;
            if (uri==null)
                uri=cur;
            log.debug("Found " + cur);
         }

         return uri;
      
   }

   /**
    * Method getKeyTypeFromURI
    *
    * @param AlgorithmURI
    * @return the type of key used fpr the algorithm
    */
   public static int getKeyTypeFromURI(String AlgorithmURI) {

         String algoclass = getAlgorithmMapping(AlgorithmURI).algorithmClass;


            if (algoclass.equals(JCEMapper.KEYTYPE_BLOCK_ENCRYPTION)) {
               return javax.crypto.Cipher.SECRET_KEY;
            } else if (algoclass.equals("Mac")) {
               return javax.crypto.Cipher.SECRET_KEY;
            } else if (algoclass.equals(JCEMapper.KEYTYPE_SYMMETRIC_KEY_WRAP)) {
               return javax.crypto.Cipher.SECRET_KEY;
            } else if (algoclass.equals(JCEMapper.KEYTYPE_KEY_TRANSPORT)) {
               return javax.crypto.Cipher.SECRET_KEY;
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

      
         
            return Integer.parseInt(getAlgorithmMapping(AlgorithmURI).keyLength);
         
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

   
   	return  getAlgorithmMapping(AlgorithmURI,ProviderId).RequiredKey;
    
   }


   /**
    * @param AlgorithmURI
    * @param ProviderId
    * @return The IVJCEName for the given algorithm
    */
    public static String getJCEIVAlgorithmFromURI(String AlgorithmURI,
           String ProviderId) {
    	return  getAlgorithmMapping(AlgorithmURI,ProviderId).IVJCEName;
      
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
      
      //try {
         //Attr URI = (Attr) cx.selectSingleNode(
            //JCEMapper._providerList,
          String xpath=  "./x:Algorithms/x:Algorithm[@KeyLength='" + keyLength
            + "' and @AlgorithmClass='" + type
            + "']/x:ProviderAlgo[@RequiredKey='" + JCEalgo + "']/../@URI";          
            //,JCEMapper._nscontext);

         /*if (URI != null) {
            return URI.getNodeValue();
         }*/
      //} catch (TransformerException ex) {
       //  log.debug("Found nothing: " + ex.getMessage());
      //}
      
      return xpath;
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
    * Represents the ProviderJCE xml element
    */
   public  static class ProviderJCE {
     String providerClass;
     /**
      * Gets the data from element.
      * @param el
      */
     public ProviderJCE(Element el) {
        providerClass = el.getAttributeNS(null, "Class");        
     }	
   }
   /**
    * Represents the Algorithm xml element
    */   
   public static class Algorithm extends HashMap {
   	    String algorithmClass;
   	    String keyLength;
        /**
         * Gets data from element
         * @param el
         */
        public Algorithm(Element el) {
        	algorithmClass=el.getAttribute("AlgorithmClass");
            keyLength=el.getAttribute("KeyLength");
        }
   }
   /**
    * Represents the AlgorithmMapping xml element
    */
   public static class AlgorithmMapping {
   	   String RequiredKey;
       String ProviderId;
       String JCEName;
       String IVJCEName;
       Algorithm algo;
       /**
        * Gets data from element
        * @param algo
        * @param el
        */
       public AlgorithmMapping(Algorithm algo,Element el) {
        this.algo=algo;
        RequiredKey=el.getAttribute("RequiredKey");
        ProviderId=el.getAttribute("ProviderId");
        JCEName=el.getAttribute("JCEName");
        IVJCEName=el.getAttribute("IVJCEName");
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
