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
package org.apache.xml.security.stax.impl.util;

import org.w3c.dom.ls.LSInput;

import java.io.InputStream;
import java.io.Reader;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class ConcreteLSInput implements LSInput {

    private Reader reader;
    private InputStream inputStream;
    private String stringData;
    private String systemId;
    private String publicId;
    private String baseURI;
    private String encoding;
    private boolean certifiedText;

    @Override
    public Reader getCharacterStream() {
        return this.reader;
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
        this.reader = characterStream;
    }

    @Override
    public InputStream getByteStream() {
        return this.inputStream;
    }

    @Override
    public void setByteStream(InputStream byteStream) {
        this.inputStream = byteStream;
    }

    @Override
    public String getStringData() {
        return this.stringData;
    }

    @Override
    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    @Override
    public String getSystemId() {
        return this.systemId;
    }

    @Override
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public String getPublicId() {
        return this.publicId;
    }

    @Override
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    @Override
    public String getBaseURI() {
        return this.baseURI;
    }

    @Override
    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public boolean getCertifiedText() {
        return this.certifiedText;
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
        this.certifiedText = certifiedText;
    }
}