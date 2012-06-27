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
package org.swssf.xmlsec.impl.algorithms;

import org.swssf.xmlsec.config.JCEAlgorithmMapper;
import org.swssf.xmlsec.ext.XMLSecurityException;
import org.xmlsecurity.ns.configuration.AlgorithmType;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class SignatureAlgorithmFactory {

    private static SignatureAlgorithmFactory instance = null;

    private SignatureAlgorithmFactory() {
    }

    public static synchronized SignatureAlgorithmFactory getInstance() {
        if (instance == null) {
            instance = new SignatureAlgorithmFactory();
        }
        return instance;
    }

    public SignatureAlgorithm getSignatureAlgorithm(String algoURI) throws XMLSecurityException, NoSuchProviderException, NoSuchAlgorithmException {
        AlgorithmType algorithmType = JCEAlgorithmMapper.getAlgorithmMapping(algoURI);
        if (algorithmType == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.UNSUPPORTED_ALGORITHM, "unknownSignatureAlgorithm", algoURI);
        }
        String algorithmClass = algorithmType.getAlgorithmClass();
        if ("MAC".equalsIgnoreCase(algorithmClass)) {
            return new HMACSignatureAlgorithm(algorithmType);
        } else if ("Signature".equalsIgnoreCase(algorithmClass)) {
            return new PKISignatureAlgorithm(algorithmType);
        } else {
            return null;
        }
    }
}
