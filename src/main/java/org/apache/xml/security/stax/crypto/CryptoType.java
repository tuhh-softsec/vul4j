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

package org.apache.xml.security.stax.crypto;

import java.math.BigInteger;

/**
 * This class represents a way of passing information to the Crypto.getX509Certificates() method.
 * The TYPE enum describes which method to use to retrieve the Certificate(s). The corresponding
 * get accessor must be set accordingly.
 */
public class CryptoType {

    /**
     * TYPE.ISSUER_SERIAL - A certificate (chain) is located by the issuer name and serial number
     * TYPE.THUMBPRINT_SHA1 - A certificate (chain) is located by the SHA1 of the (root) cert
     * TYPE.SKI_BYTES - A certificate (chain) is located by the SKI bytes of the (root) cert
     * TYPE.SUBJECT_DN - A certificate (chain) is located by the Subject DN of the (root) cert
     * TYPE.ALIAS - A certificate (chain) is located by an alias. This alias is implementation
     * specific, for example - it could be a java KeyStore alias.
     */
    public enum TYPE {
        ISSUER_SERIAL, THUMBPRINT_SHA1, SKI_BYTES, SUBJECT_DN, ALIAS
    }

    private TYPE type;
    private String issuer;
    private BigInteger serial;
    private byte[] bytes;
    private String subjectDN;
    private String alias;

    /**
     * Default constructor
     */
    public CryptoType() {
        //
    }

    /**
     * Constructor with a TYPE argument
     *
     * @param type describes which method to use to retrieve a certificate (chain)
     */
    public CryptoType(TYPE type) {
        this.type = type;
    }

    /**
     * Set the type.
     *
     * @param type describes which method to use to retrieve a certificate (chain)
     */
    public void setType(TYPE type) {
        this.type = type;
    }

    /**
     * Get the type
     *
     * @return which method to use to retrieve a certificate (chain)
     */
    public TYPE getType() {
        return type;
    }

    /**
     * Set the Issuer String, and Serial number of the cert (chain) to retrieve.
     *
     * @param issuer the issuer String
     * @param serial the serial number
     */
    public void setIssuerSerial(String issuer, BigInteger serial) {
        this.issuer = issuer;
        this.serial = serial;
    }

    /**
     * Get the issuer String.
     *
     * @return the issuer String
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Get the serial number
     *
     * @return the serial number
     */
    public BigInteger getSerial() {
        return serial;
    }

    /**
     * Set the byte[], which could be the SHA1 thumbprint, or SKI bytes of the cert.
     *
     * @param bytes an array of bytes
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Get the array of bytes, which could be the SHA1 thumbprint, or SKI bytes of the cert.
     *
     * @return an array of bytes
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Set the Subject DN of the cert (chain) to locate
     *
     * @param subjectDN the Subject DN of the cert (chain) to locate
     */
    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }

    /**
     * Get the Subject DN of the cert (chain) to locate
     *
     * @return the Subject DN of the cert (chain) to locate
     */
    public String getSubjectDN() {
        return subjectDN;
    }

    /**
     * Set the alias of the cert (chain) to locate.
     *
     * @param alias the alias of the cert (chain) to locate.
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Get the alias of the cert (chain) to locate.
     *
     * @return the alias of the cert (chain) to locate.
     */
    public String getAlias() {
        return alias;
    }

}
