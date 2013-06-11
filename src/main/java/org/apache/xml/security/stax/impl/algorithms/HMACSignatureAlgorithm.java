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
package org.apache.xml.security.stax.impl.algorithms;

import org.apache.xml.security.exceptions.XMLSecurityException;

import javax.crypto.Mac;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class HMACSignatureAlgorithm implements SignatureAlgorithm {

    private Mac mac;

    public HMACSignatureAlgorithm(String jceName, String jceProvider) throws NoSuchProviderException, NoSuchAlgorithmException {
        if (jceProvider != null) {
            mac = Mac.getInstance(jceName, jceProvider);
        } else {
            mac = Mac.getInstance(jceName);
        }
    }

    @Override
    public void engineUpdate(byte[] input) throws XMLSecurityException {
        mac.update(input);
    }

    @Override
    public void engineUpdate(byte input) throws XMLSecurityException {
        mac.update(input);
    }

    @Override
    public void engineUpdate(byte[] buf, int offset, int len) throws XMLSecurityException {
        mac.update(buf, offset, len);
    }

    @Override
    public void engineInitSign(Key signingKey) throws XMLSecurityException {
        try {
            mac.init(signingKey);
        } catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineInitSign(Key signingKey, SecureRandom secureRandom) throws XMLSecurityException {
        try {
            mac.init(signingKey);
        } catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineInitSign(Key signingKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSecurityException {
        try {
            mac.init(signingKey, algorithmParameterSpec);
        } catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public byte[] engineSign() throws XMLSecurityException {
        return mac.doFinal();
    }

    @Override
    public void engineInitVerify(Key verificationKey) throws XMLSecurityException {
        try {
            mac.init(verificationKey);
        } catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public boolean engineVerify(byte[] signature) throws XMLSecurityException {
        byte[] completeResult = mac.doFinal();
        return MessageDigest.isEqual(completeResult, signature);
    }

    @Override
    public void engineSetParameter(AlgorithmParameterSpec params) throws XMLSecurityException {
    }
}
