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

import java.security.Key;
import java.security.PublicKey;

import javax.security.auth.callback.CallbackHandler;

import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;

/**
 * @author $Author: coheigea $
 * @version $Revision: 1354898 $ $Date: 2012-06-28 11:19:02 +0100 (Thu, 28 Jun 2012) $
 */
public class KeyNameSecurityToken extends AbstractSecurityToken {

    private Key key;
    private String keyName;

    public KeyNameSecurityToken(String keyName, SecurityContext securityContext, CallbackHandler callbackHandler,
                                    XMLSecurityConstants.KeyIdentifierType keyIdentifierType) throws XMLSecurityException {
        super(securityContext, callbackHandler, null, keyIdentifierType);
        this.keyName = keyName;
    }

    @Override
    protected Key getKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage) throws XMLSecurityException {
        return key;
    }

    @Override
    protected PublicKey getPubKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage) throws XMLSecurityException {
        if (key instanceof PublicKey) {
            return (PublicKey)key;
        }
        return null;
    }

    public void setKey(Key key) {
        this.key = key;
    }
    
    @Override
    public boolean isAsymmetric() {
        if (key instanceof PublicKey) {
            return true;
        }
        return false;
    }

    @Override
    public XMLSecurityConstants.TokenType getTokenType() {
        return XMLSecurityConstants.KeyNameToken;
    }

    //todo move to super class?
    @Override
    public SecurityToken getKeyWrappingToken() throws XMLSecurityException {
        return null;
    }
    
    public String getKeyName() {
        return keyName;
    }
}
