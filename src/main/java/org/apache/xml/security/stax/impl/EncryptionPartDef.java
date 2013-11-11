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
package org.apache.xml.security.stax.impl;

import org.apache.xml.security.stax.ext.SecurePart;

import java.security.Key;

/**
 * EncryptionPartDef holds information about parts to be encrypt
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class EncryptionPartDef {

    private SecurePart securePart;
    private SecurePart.Modifier modifier;
    private Key symmetricKey;
    private String keyId;
    private String encRefId;
    private String cipherReferenceId;
    private String mimeType;

    public SecurePart getSecurePart() {
        return securePart;
    }

    public void setSecurePart(SecurePart securePart) {
        this.securePart = securePart;
    }

    public SecurePart.Modifier getModifier() {
        return modifier;
    }

    public void setModifier(SecurePart.Modifier modifier) {
        this.modifier = modifier;
    }

    public Key getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(Key symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getEncRefId() {
        return encRefId;
    }

    public void setEncRefId(String encRefId) {
        this.encRefId = encRefId;
    }

    public String getCipherReferenceId() {
        return cipherReferenceId;
    }

    public void setCipherReferenceId(String cipherReferenceId) {
        this.cipherReferenceId = cipherReferenceId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
