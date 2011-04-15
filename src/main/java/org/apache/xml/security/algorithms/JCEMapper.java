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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Element;


/**
 * This class maps algorithm identifier URIs to JAVA JCE class names.
 */
public class JCEMapper {

    /** {@link org.apache.commons.logging} logging facility */
    private static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(JCEMapper.class);

    private static Map<String, Algorithm> algorithmsMap = 
        new ConcurrentHashMap<String, Algorithm>();

    private static String providerName = null;
    
    /**
     * Method register
     *
     * @param element
     * @throws Exception
     */
    public static void register(String id, Algorithm algorithm) throws Exception {
        algorithmsMap.put(id, algorithm);
    }

    /**
     * Method translateURItoJCEID
     *
     * @param algorithmURI
     * @return the JCE standard name corresponding to the given URI
     */
    public static String translateURItoJCEID(String algorithmURI) {
        if (log.isDebugEnabled()) {
            log.debug("Request for URI " + algorithmURI);
        }

        Algorithm algorithm = algorithmsMap.get(algorithmURI);
        if (algorithm != null) {
            return algorithm.jceName;
        }
        return null;
    }

    /**
     * Method getJCEKeyAlgorithmFromURI
     *
     * @param AlgorithmURI
     * @return The KeyAlgorithm for the given URI.
     */
    public static String getJCEKeyAlgorithmFromURI(String algorithmURI) {
        Algorithm algorithm = algorithmsMap.get(algorithmURI);
        if (algorithm != null) {
            return algorithm.requiredKey;
        }
        return null;
    }

    /**
     * Gets the default Provider for obtaining the security algorithms
     * @return the default providerId.  
     */
    public static String getProviderId() {
        return providerName;
    }

    /**
     * Sets the default Provider for obtaining the security algorithms
     * @param provider the default providerId.  
     */
    public static void setProviderId(String provider) {
        providerName = provider;
    }

    /**
     * Represents the Algorithm xml element
     */   
    public static class Algorithm {
        
        final String requiredKey;
        final String jceName;
        
        /**
         * Gets data from element
         * @param el
         */
        public Algorithm(Element el) {
            requiredKey = el.getAttribute("RequiredKey");
            jceName = el.getAttribute("JCEName");
        }
        
        public Algorithm(String requiredKey, String jceName) {
            this.requiredKey = requiredKey;
            this.jceName = jceName;
        }
    }
    
}
