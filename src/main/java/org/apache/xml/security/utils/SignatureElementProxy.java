/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.utils;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class SignatureElementProxy
 *
 * @author $Author$
 */
public abstract class SignatureElementProxy extends ElementProxy {
    
    protected SignatureElementProxy() {
    };
    
    /**
     * Constructor SignatureElementProxy
     *
     * @param doc
     */
    public SignatureElementProxy(Document doc) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }

        this.doc = doc;
        this.constructionElement = 
            XMLUtils.createElementInSignatureSpace(this.doc, this.getBaseLocalName());
    }

    /**
     * Constructor SignatureElementProxy
     *
     * @param element
     * @param BaseURI
     * @throws XMLSecurityException
     */
    public SignatureElementProxy(Element element, String BaseURI) throws XMLSecurityException {
        super(element, BaseURI);

    }

    /** @inheritDoc */
    public String getBaseNamespace() {
        return Constants.SignatureSpecNS;
    }
}
