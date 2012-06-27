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
package org.swssf.xmlsec.impl.stax;

import org.swssf.xmlsec.ext.stax.XMLSecStartDocument;

import javax.xml.stream.XMLStreamConstants;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecStartDocumentImpl extends XMLSecEventBaseImpl implements XMLSecStartDocument {

    private final String systemId;
    private final String characterEncodingScheme;
    private final Boolean isStandAlone;
    private String version = "1.0";

    public XMLSecStartDocumentImpl(String systemId, String characterEncodingScheme, Boolean standAlone, String version) {
        this.systemId = systemId;
        this.characterEncodingScheme = characterEncodingScheme;
        isStandAlone = standAlone;
        this.version = version;
    }

    @Override
    public int getEventType() {
        return XMLStreamConstants.START_DOCUMENT;
    }

    @Override
    public String getSystemId() {
        return systemId != null ? systemId : "";
    }

    @Override
    public String getCharacterEncodingScheme() {
        return characterEncodingScheme != null ? characterEncodingScheme : "UTF-8";
    }

    @Override
    public boolean encodingSet() {
        return characterEncodingScheme != null;
    }

    @Override
    public boolean isStandalone() {
        return isStandAlone != null ? isStandAlone : false;
    }

    @Override
    public boolean standaloneSet() {
        return isStandAlone != null;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isStartDocument() {
        return true;
    }

    @Override
    public XMLSecStartDocument asStartDocument() {
        return this;
    }
}
