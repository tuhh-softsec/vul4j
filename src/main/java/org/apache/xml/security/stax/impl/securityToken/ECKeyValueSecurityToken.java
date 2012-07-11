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

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;

import javax.security.auth.callback.CallbackHandler;

import org.apache.xml.security.binding.xmldsig11.ECKeyValueType;
import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.impl.algorithms.ECDSAUtils;

/**
 * @author $Author: coheigea $
 * @version $Revision: 1354898 $ $Date: 2012-06-28 11:19:02 +0100 (Thu, 28 Jun 2012) $
 */
public class ECKeyValueSecurityToken extends AbstractSecurityToken {

    private PublicKey publicKey;

    public ECKeyValueSecurityToken(ECKeyValueType ecKeyValueType, SecurityContext securityContext, CallbackHandler callbackHandler,
                                   XMLSecurityConstants.KeyIdentifierType keyIdentifierType) throws XMLSecurityException {
        super(securityContext, callbackHandler, null, keyIdentifierType);

        if (ecKeyValueType.getECParameters() != null) {
            throw new XMLSecurityException("ECParameters not supported");
        }
        if (ecKeyValueType.getNamedCurve() == null) {
            throw new XMLSecurityException("NamedCurve is missing");
        }

        try {
            this.publicKey = buildPublicKey(ecKeyValueType);
        } catch (InvalidKeySpecException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY_TOKEN, e);
        } catch (NoSuchAlgorithmException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY_TOKEN, e);
        }
    }

    private PublicKey buildPublicKey(ECKeyValueType ecKeyValueType) throws InvalidKeySpecException, NoSuchAlgorithmException, XMLSecurityException {
        String oid = ecKeyValueType.getNamedCurve().getURI();
        if (oid.startsWith("urn:oid:")) {
            oid = oid.substring(8);
        }
        ECDSAUtils.ECCurveDefinition ecCurveDefinition = ECDSAUtils.getECCurveDefinition(oid);
        if (ecCurveDefinition == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY_TOKEN);
        }
        final EllipticCurve curve = new EllipticCurve(
                new ECFieldFp(
                        new BigInteger(ecCurveDefinition.getField(), 16)
                ),
                new BigInteger(ecCurveDefinition.getA(), 16),
                new BigInteger(ecCurveDefinition.getB(), 16)
        );
        ECPoint ecPointG = ECDSAUtils.decodePoint(ecKeyValueType.getPublicKey(), curve);
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(
                new ECPoint(
                        ecPointG.getAffineX(),
                        ecPointG.getAffineY()
                ),
                new ECParameterSpec(
                        curve,
                        new ECPoint(
                                new BigInteger(ecCurveDefinition.getX(), 16),
                                new BigInteger(ecCurveDefinition.getY(), 16)
                        ),
                        new BigInteger(ecCurveDefinition.getN(), 16),
                        ecCurveDefinition.getH()
                )
        );
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePublic(ecPublicKeySpec);
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
