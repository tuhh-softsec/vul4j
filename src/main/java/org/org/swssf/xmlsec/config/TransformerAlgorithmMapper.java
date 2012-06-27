/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.swssf.xmlsec.config;

import org.swssf.xmlsec.ext.XMLSecurityException;
import org.swssf.xmlsec.ext.XMLSecurityUtils;
import org.xmlsecurity.ns.configuration.TransformAlgorithmType;
import org.xmlsecurity.ns.configuration.TransformAlgorithmsType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapping between JCE id and xmlsec uri's for algorithms
 * Class lent from apache santuario (xmlsec)
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class TransformerAlgorithmMapper {

    private static Map<String, Class> algorithmsClassMapInOut;
    private static Map<String, Class> algorithmsClassMapIn;
    private static Map<String, Class> algorithmsClassMapOut;

    private TransformerAlgorithmMapper() {
    }

    @SuppressWarnings("unchecked")
    protected synchronized static void init(TransformAlgorithmsType transformAlgorithms) throws Exception {
        List<TransformAlgorithmType> algorithms = transformAlgorithms.getTransformAlgorithm();
        algorithmsClassMapInOut = new HashMap<String, Class>();
        algorithmsClassMapIn = new HashMap<String, Class>();
        algorithmsClassMapOut = new HashMap<String, Class>();

        for (int i = 0; i < algorithms.size(); i++) {
            TransformAlgorithmType algorithmType = algorithms.get(i);
            if (algorithmType.getINOUT() == null) {
                algorithmsClassMapInOut.put(algorithmType.getURI(), XMLSecurityUtils.loadClass(algorithmType.getJAVACLASS()));
            } else if ("IN".equals(algorithmType.getINOUT().value())) {
                algorithmsClassMapIn.put(algorithmType.getURI(), XMLSecurityUtils.loadClass(algorithmType.getJAVACLASS()));
            } else if ("OUT".equals(algorithmType.getINOUT().value())) {
                algorithmsClassMapOut.put(algorithmType.getURI(), XMLSecurityUtils.loadClass(algorithmType.getJAVACLASS()));
            } else {
                throw new IllegalArgumentException("INOUT parameter " + algorithmType.getINOUT().value() + " unsupported");
            }
        }
    }

    public static Class<?> getTransformerClass(String algoURI, String inOut) throws XMLSecurityException {
        Class clazz = null;
        if (inOut == null) {
            clazz = algorithmsClassMapInOut.get(algoURI);
        } else if ("IN".equals(inOut)) {
            clazz = algorithmsClassMapIn.get(algoURI);
        } else if ("OUT".equals(inOut)) {
            clazz = algorithmsClassMapOut.get(algoURI);
        }
        if (clazz == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK);
        }
        return clazz;
    }
}
