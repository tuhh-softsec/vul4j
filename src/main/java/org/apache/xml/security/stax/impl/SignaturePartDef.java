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

/**
 * SignaturePartDef holds information about parts to be signed
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class SignaturePartDef {

    private SecurePart securePart;
    private String sigRefId;
    private String digestValue;
    private String[] transforms;
    private String digestAlgo;
    private String inclusiveNamespacesPrefixes;
    private boolean excludeVisibleC14Nprefixes;
    private boolean externalResource;
    private boolean generateXPointer;

    public SecurePart getSecurePart() {
        return securePart;
    }

    public void setSecurePart(SecurePart securePart) {
        this.securePart = securePart;
    }

    public String getSigRefId() {
        return sigRefId;
    }

    public void setSigRefId(String sigRefId) {
        this.sigRefId = sigRefId;
    }

    public String getDigestValue() {
        return digestValue;
    }

    public void setDigestValue(String digestValue) {
        this.digestValue = digestValue;
    }

    public String[] getTransforms() {
        return transforms;
    }

    public void setTransforms(String[] transforms) {
        this.transforms = transforms;
    }

    public String getDigestAlgo() {
        return digestAlgo;
    }

    public void setDigestAlgo(String digestAlgo) {
        this.digestAlgo = digestAlgo;
    }

    public String getInclusiveNamespacesPrefixes() {
        return inclusiveNamespacesPrefixes;
    }

    public void setInclusiveNamespacesPrefixes(String inclusiveNamespacesPrefixes) {
        this.inclusiveNamespacesPrefixes = inclusiveNamespacesPrefixes;
    }

    public boolean isExcludeVisibleC14Nprefixes() {
        return excludeVisibleC14Nprefixes;
    }

    public void setExcludeVisibleC14Nprefixes(boolean excludeVisibleC14Nprefixes) {
        this.excludeVisibleC14Nprefixes = excludeVisibleC14Nprefixes;
    }

    public boolean isExternalResource() {
        return externalResource;
    }

    public void setExternalResource(boolean externalResource) {
        this.externalResource = externalResource;
    }

    public boolean isGenerateXPointer() {
        return generateXPointer;
    }

    public void setGenerateXPointer(boolean generateXPointer) {
        this.generateXPointer = generateXPointer;
    }
}