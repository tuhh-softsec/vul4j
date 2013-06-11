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

import java.io.IOException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class PKISignatureAlgorithm implements SignatureAlgorithm {

    private final String jceName;
    private final Signature signature;

    public PKISignatureAlgorithm(String jceName, String jceProvider) throws NoSuchProviderException, NoSuchAlgorithmException {
        this.jceName = jceName;
        if (jceProvider != null) {
            signature = Signature.getInstance(this.jceName, jceProvider);
        } else {
            signature = Signature.getInstance(this.jceName);
        }
    }

    @Override
    public void engineUpdate(byte[] input) throws XMLSecurityException {
        try {
            signature.update(input);
        } catch (SignatureException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineUpdate(byte input) throws XMLSecurityException {
        try {
            signature.update(input);
        } catch (SignatureException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineUpdate(byte[] buf, int offset, int len) throws XMLSecurityException {
        try {
            signature.update(buf, offset, len);
        } catch (SignatureException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineInitSign(Key signingKey) throws XMLSecurityException {
        try {
            signature.initSign((PrivateKey) signingKey);
        } catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineInitSign(Key signingKey, SecureRandom secureRandom) throws XMLSecurityException {
        try {
            signature.initSign((PrivateKey) signingKey, secureRandom);
        } catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineInitSign(Key signingKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSecurityException {
        try {
            signature.initSign((PrivateKey) signingKey);
        } catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public byte[] engineSign() throws XMLSecurityException {
        try {
            byte[] jcebytes = signature.sign();
            if (this.jceName.contains("ECDSA")) {
                return ECDSAUtils.convertASN1toXMLDSIG(jcebytes);
            } else if (this.jceName.contains("DSA")) {
                return DSAUtils.convertASN1toXMLDSIG(jcebytes);
            }
            return jcebytes;
        } catch (SignatureException e) {
            throw new XMLSecurityException(e);
        } catch (IOException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineInitVerify(Key verificationKey) throws XMLSecurityException {
        try {
            signature.initVerify((PublicKey) verificationKey);
        } catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public boolean engineVerify(byte[] signature) throws XMLSecurityException {
        try {
            byte[] jcebytes = signature;
            if (this.jceName.contains("ECDSA")) {
                jcebytes = ECDSAUtils.convertXMLDSIGtoASN1(jcebytes);
            } else if (this.jceName.contains("DSA")) {
                jcebytes = DSAUtils.convertXMLDSIGtoASN1(jcebytes);
            }
            return this.signature.verify(jcebytes);
        } catch (SignatureException e) {
            throw new XMLSecurityException(e);
        } catch (IOException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineSetParameter(AlgorithmParameterSpec params) throws XMLSecurityException {
        try {
            signature.setParameter(params);
        } catch (InvalidAlgorithmParameterException e) {
            throw new XMLSecurityException(e);
        }
    }
}
