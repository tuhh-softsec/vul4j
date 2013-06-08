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

import org.apache.xml.security.binding.xmldsig11.ECKeyValueType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.impl.algorithms.ECDSAUtils;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.*;

/**
 * @author $Author: coheigea $
 * @version $Revision: 1354898 $ $Date: 2012-06-28 11:19:02 +0100 (Thu, 28 Jun 2012) $
 */
public class ECKeyValueSecurityToken extends AbstractInboundSecurityToken {

    private ECKeyValueType ecKeyValueType;

    public ECKeyValueSecurityToken(ECKeyValueType ecKeyValueType, InboundSecurityContext inboundSecurityContext)
            throws XMLSecurityException {
        super(inboundSecurityContext, IDGenerator.generateID(null), SecurityTokenConstants.KeyIdentifier_KeyValue, true);

        if (ecKeyValueType.getECParameters() != null) {
            throw new XMLSecurityException("stax.ecParametersNotSupported");
        }
        if (ecKeyValueType.getNamedCurve() == null) {
            throw new XMLSecurityException("stax.namedCurveMissing");
        }
        this.ecKeyValueType = ecKeyValueType;
    }

    private PublicKey buildPublicKey(ECKeyValueType ecKeyValueType)
            throws InvalidKeySpecException, NoSuchAlgorithmException, XMLSecurityException {

        String oid = ecKeyValueType.getNamedCurve().getURI();
        if (oid.startsWith("urn:oid:")) {
            oid = oid.substring(8);
        }
        ECDSAUtils.ECCurveDefinition ecCurveDefinition = ECDSAUtils.getECCurveDefinition(oid);
        if (ecCurveDefinition == null) {
            throw new XMLSecurityException("stax.unsupportedKeyValue");
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
    public PublicKey getPublicKey() throws XMLSecurityException {
        if (super.getPublicKey() == null) {
            try {
                setPublicKey(buildPublicKey(this.ecKeyValueType));
            } catch (InvalidKeySpecException e) {
                throw new XMLSecurityException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new XMLSecurityException(e);
            }
        }
        return super.getPublicKey();
    }

    @Override
    public boolean isAsymmetric() {
        return true;
    }

    @Override
    public SecurityTokenConstants.TokenType getTokenType() {
        return SecurityTokenConstants.KeyValueToken;
    }
}
