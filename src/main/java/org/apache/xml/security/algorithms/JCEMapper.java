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

import java.util.HashMap;
import java.util.Map;

import org.apache.xml.security.Init;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;


/**
 * This class maps algorithm identifier URIs to JAVA JCE class names.
 */
public class JCEMapper {

    /** {@link org.apache.commons.logging} logging facility */
    private static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(JCEMapper.class.getName());

    private static Map<String, String> uriToJCEName;

    private static Map<String, Algorithm> algorithmsMap;

    private static String providerName = null;
    
    /**
     * Method init
     *
     * @param mappingElement
     * @throws Exception
     */
    public static void init(Element mappingElement) throws Exception {
        loadAlgorithms((Element)mappingElement.getElementsByTagName("Algorithms").item(0));
    }

    static void loadAlgorithms(Element algorithmsEl) {
        Element[] algorithms = XMLUtils.selectNodes(algorithmsEl.getFirstChild(),Init.CONF_NS,"Algorithm");
        uriToJCEName = new HashMap( algorithms.length * 2); 
        algorithmsMap = new HashMap( algorithms.length * 2);
        for (int i = 0 ;i < algorithms.length ;i ++) {
            Element el = algorithms[i];
            String id = el.getAttribute("URI");
            String jceName = el.getAttribute("JCEName");
            uriToJCEName.put(id, jceName);
            algorithmsMap.put(id, new Algorithm(el));
        }

    }

    /**
     * Method translateURItoJCEID
     *
     * @param AlgorithmURI
     * @return the JCE standard name corresponding to the given URI
     */
    public static String translateURItoJCEID(String AlgorithmURI) {
        if (log.isDebugEnabled()) {
            log.debug("Request for URI " + AlgorithmURI);
        }

        return uriToJCEName.get(AlgorithmURI);
    }

    /**
     * Method getAlgorithmClassFromURI
     * @param AlgorithmURI
     * @return the class name that implements this algorithm
     */
    public static String getAlgorithmClassFromURI(String AlgorithmURI) {
        if (log.isDebugEnabled()) {
            log.debug("Request for URI " + AlgorithmURI);
        }

        return (algorithmsMap.get(AlgorithmURI)).algorithmClass;
    }

    /**
     * Returns the key length in bits for a particular algorithm.
     *
     * @param AlgorithmURI
     * @return The length of the key used in the algorithm
     */
    public static int getKeyLengthFromURI(String AlgorithmURI) {
        return Integer.parseInt((algorithmsMap.get(AlgorithmURI)).keyLength);
    }

    /**
     * Method getJCEKeyAlgorithmFromURI
     *
     * @param AlgorithmURI
     * @return The KeyAlgorithm for the given URI.
     */
    public static String getJCEKeyAlgorithmFromURI(String AlgorithmURI) {
        return (algorithmsMap.get(AlgorithmURI)).requiredKey;
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
        
        String algorithmClass;
        String keyLength;
        String requiredKey;
        
        /**
         * Gets data from element
         * @param el
         */
        public Algorithm(Element el) {
            algorithmClass = el.getAttribute("AlgorithmClass");
            keyLength = el.getAttribute("KeyLength");
            requiredKey = el.getAttribute("RequiredKey");
        }
    }
    
}
