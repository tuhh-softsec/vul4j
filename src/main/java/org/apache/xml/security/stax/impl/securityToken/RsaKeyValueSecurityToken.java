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
package org.apache.xml.security.stax.impl.securityToken;

import org.apache.xml.security.binding.xmldsig.RSAKeyValueType;
import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;

import javax.security.auth.callback.CallbackHandler;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

/**
 * @author $Author: coheigea $
 * @version $Revision: 1354898 $ $Date: 2012-06-28 11:19:02 +0100 (Thu, 28 Jun 2012) $
 */
public class RsaKeyValueSecurityToken extends AbstractSecurityToken {

    private PublicKey publicKey;

    public RsaKeyValueSecurityToken(RSAKeyValueType rsaKeyValueType, SecurityContext securityContext, CallbackHandler callbackHandler,
                                    XMLSecurityConstants.KeyIdentifierType keyIdentifierType) throws XMLSecurityException {
        super(securityContext, callbackHandler, null, keyIdentifierType);

        try {
            this.publicKey = buildPublicKey(rsaKeyValueType);
        } catch (InvalidKeySpecException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY_TOKEN, e);
        } catch (NoSuchAlgorithmException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY_TOKEN, e);
        }
    }

    private PublicKey buildPublicKey(RSAKeyValueType rsaKeyValueType) throws InvalidKeySpecException, NoSuchAlgorithmException {
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(
                new BigInteger(1, rsaKeyValueType.getModulus()),
                new BigInteger(1, rsaKeyValueType.getExponent()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(rsaPublicKeySpec);
    }

    @Override
    protected Key getKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage) throws XMLSecurityException {
        return null;
    }

    @Override
    protected PublicKey getPubKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage) throws XMLSecurityException {
        return this.publicKey;
    }

    @Override
    public boolean isAsymmetric() {
        return true;
    }

    @Override
    public XMLSecurityConstants.TokenType getTokenType() {
        return XMLSecurityConstants.KeyValueToken;
    }

    //todo move to super class?
    @Override
    public SecurityToken getKeyWrappingToken() throws XMLSecurityException {
        return null;
    }
}
