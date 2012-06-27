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
package org.swssf.xmlsec.impl.securityToken;

import org.swssf.binding.xmldsig.KeyInfoType;
import org.swssf.xmlsec.config.ConfigurationProperties;
import org.swssf.xmlsec.crypto.Crypto;
import org.swssf.xmlsec.ext.SecurityContext;
import org.swssf.xmlsec.ext.SecurityToken;
import org.swssf.xmlsec.ext.XMLSecurityException;

import javax.security.auth.callback.CallbackHandler;

/**
 * Factory to create SecurityToken Objects from keys in XML
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public abstract class SecurityTokenFactory {

    private static SecurityTokenFactory securityTokenFactory = null;

    public static synchronized SecurityTokenFactory getInstance() throws XMLSecurityException {
        if (securityTokenFactory == null) {
            String stf = ConfigurationProperties.getProperty("securityTokenFactory");
            if (stf == null) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY, "missingSecurityTokenFactory");
            }

            try {
                @SuppressWarnings("unchecked")
                Class<SecurityTokenFactory> securityTokenFactoryClass = (Class<SecurityTokenFactory>) SecurityTokenFactory.class.getClassLoader().loadClass(stf);
                securityTokenFactory = securityTokenFactoryClass.newInstance();
            } catch (ClassNotFoundException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY, "missingSecurityTokenFactory", e);
            } catch (InstantiationException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY, "missingSecurityTokenFactory", e);
            } catch (IllegalAccessException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY, "missingSecurityTokenFactory", e);
            }
        }
        return securityTokenFactory;
    }

    public abstract SecurityToken getSecurityToken(KeyInfoType keyInfoType, Crypto crypto,
                                                   final CallbackHandler callbackHandler,
                                                   SecurityContext securityContext) throws XMLSecurityException;
}
