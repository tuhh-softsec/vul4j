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

import org.apache.xml.security.stax.ext.XMLSecurityException;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public interface SignatureAlgorithm {

    void engineUpdate(byte[] input) throws XMLSecurityException;

    void engineUpdate(byte input) throws XMLSecurityException;

    void engineUpdate(byte buf[], int offset, int len) throws XMLSecurityException;

    void engineInitSign(Key signingKey) throws XMLSecurityException;

    void engineInitSign(Key signingKey, SecureRandom secureRandom) throws XMLSecurityException;

    void engineInitSign(Key signingKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSecurityException;

    byte[] engineSign() throws XMLSecurityException;

    void engineInitVerify(Key verificationKey) throws XMLSecurityException;

    boolean engineVerify(byte[] signature) throws XMLSecurityException;

    void engineSetParameter(AlgorithmParameterSpec params) throws XMLSecurityException;
}
