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
import org.swssf.xmlsec.ext.stax.XMLSecEndElement;
import org.swssf.xmlsec.ext.stax.XMLSecEvent;
import org.swssf.xmlsec.ext.stax.XMLSecStartElement;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public abstract class XMLSecEventBaseImpl implements XMLSecEvent {

    protected static final Location location = new LocationImpl();
    protected XMLSecStartElement parentXMLSecStartELement;

    @Override
    public void setParentXMLSecStartElement(XMLSecStartElement xmlSecStartElement) {
        this.parentXMLSecStartELement = xmlSecStartElement;
    }

    @Override
    public XMLSecStartElement getParentXMLSecStartElement() {
        return parentXMLSecStartELement;
    }

    @Override
    public int getDocumentLevel() {
        if (parentXMLSecStartELement != null) {
            return parentXMLSecStartELement.getDocumentLevel();
        }
        return 0;
    }

    @Override
    public void getElementPath(List<QName> list) {
        if (parentXMLSecStartELement != null) {
            parentXMLSecStartELement.getElementPath(list);
        }
    }

    @Override
    public List<QName> getElementPath() {
        final List<QName> elementPath = new ArrayList<QName>();
        getElementPath(elementPath);
        return elementPath;
    }

    @Override
    public XMLSecStartElement getStartElementAtLevel(int level) {
        if (getDocumentLevel() < level) {
            return null;
        }
        return parentXMLSecStartELement.getStartElementAtLevel(level);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isStartElement() {
        return false;
    }

    @Override
    public boolean isAttribute() {
        return false;
    }

    @Override
    public boolean isNamespace() {
        return false;
    }

    @Override
    public boolean isEndElement() {
        return false;
    }

    @Override
    public boolean isEntityReference() {
        return false;
    }

    @Override
    public boolean isProcessingInstruction() {
        return false;
    }

    @Override
    public boolean isCharacters() {
        return false;
    }

    @Override
    public boolean isStartDocument() {
        return false;
    }

    @Override
    public boolean isEndDocument() {
        return false;
    }

    @Override
    public XMLSecStartElement asStartElement() {
        throw new ClassCastException();
    }

    @Override
    public XMLSecEndElement asEndElement() {
        throw new ClassCastException();
    }

    @Override
    public XMLSecCharacters asCharacters() {
        throw new ClassCastException();
    }

    @Override
    public QName getSchemaType() {
        return null;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    static final class LocationImpl implements Location {

        @Override
        public int getLineNumber() {
            return 0;
        }

        @Override
        public int getColumnNumber() {
            return 0;
        }

        @Override
        public int getCharacterOffset() {
            return 0;
        }

        @Override
        public String getPublicId() {
            return null;
        }

        @Override
        public String getSystemId() {
            return null;
        }
    }
}
