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

/**
 * SignaturePartDef holds information about parts to be signed
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class SignaturePartDef {

    private String sigRefId;
    private String digestValue;
    private String transformAlgo;
    private String c14nAlgo;
    private String inclusiveNamespaces;

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

    public String getTransformAlgo() {
        return transformAlgo;
    }

    public void setTransformAlgo(String transformAlgo) {
        this.transformAlgo = transformAlgo;
    }

    public String getC14nAlgo() {
        return c14nAlgo;
    }

    public void setC14nAlgo(String c14nAlgo) {
        this.c14nAlgo = c14nAlgo;
    }

    public String getInclusiveNamespaces() {
        return inclusiveNamespaces;
    }

    public void setInclusiveNamespaces(String inclusiveNamespaces) {
        this.inclusiveNamespaces = inclusiveNamespaces;
    }
}