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

import org.swssf.xmlsec.ext.stax.XMLSecCharacters;
import org.swssf.xmlsec.ext.stax.XMLSecStartElement;

import javax.xml.stream.XMLStreamConstants;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecCharactersImpl extends XMLSecEventBaseImpl implements XMLSecCharacters {

    private final String data;
    private final boolean isCData;
    private final boolean isIgnorableWhiteSpace;
    private final boolean isWhiteSpace;

    public XMLSecCharactersImpl(String data, boolean isCData, boolean isIgnorableWhiteSpace, boolean isWhiteSpace, XMLSecStartElement parentXmlSecStartElement) {
        this.data = data;
        this.isCData = isCData;
        this.isIgnorableWhiteSpace = isIgnorableWhiteSpace;
        this.isWhiteSpace = isWhiteSpace;
        setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public boolean isWhiteSpace() {
        return isWhiteSpace;
    }

    @Override
    public boolean isCData() {
        return isCData;
    }

    @Override
    public boolean isIgnorableWhiteSpace() {
        return isIgnorableWhiteSpace;
    }

    @Override
    public int getEventType() {
        if (isCData) {
            return XMLStreamConstants.CDATA;
        }
        return XMLStreamConstants.CHARACTERS;
    }

    @Override
    public boolean isCharacters() {
        return true;
    }

    @Override
    public XMLSecCharacters asCharacters() {
        return this;
    }
}
