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
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * $Id$
 */
package org.apache.jcp.xml.dsig.internal.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.xml.security.Init;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.signature.XMLSignatureInput;

import javax.xml.crypto.*;
import javax.xml.crypto.dom.*;

/**
 * DOM-based implementation of URIDereferencer.
 *
 * @author Sean Mullan
 */
public class DOMURIDereferencer implements URIDereferencer {
    
    static final URIDereferencer INSTANCE = new DOMURIDereferencer();

    private DOMURIDereferencer() {
        // need to call org.apache.xml.security.Init.init() 
        // before calling any apache security code
        Init.init();
    }

    @Override
    public Data dereference(URIReference uriRef, XMLCryptoContext context)
        throws URIReferenceException {

        if (uriRef == null) {
            throw new NullPointerException("uriRef cannot be null");
        }
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }

        DOMURIReference domRef = (DOMURIReference) uriRef;
        Attr uriAttr = (Attr) domRef.getHere();
        String uri = uriRef.getURI();
        DOMCryptoContext dcc = (DOMCryptoContext) context;
        String baseURI = context.getBaseURI();
        
        Boolean secureValidation = (Boolean)
            context.getProperty("org.apache.jcp.xml.dsig.secureValidation");
        boolean secVal = false;
        if (secureValidation != null && secureValidation.booleanValue()) {
            secVal = true;
        }
        
        // Check if same-document URI and already registered on the context
        if (uri != null && uri.length() != 0 && uri.charAt(0) == '#') {
            String id = uri.substring(1);

            if (id.startsWith("xpointer(id(")) {
                int i1 = id.indexOf('\'');
                int i2 = id.indexOf('\'', i1+1);
                id = id.substring(i1+1, i2);
            }

            Node referencedElem = dcc.getElementById(id);
            if (referencedElem != null) {
                if (secVal) {
                    Element start = referencedElem.getOwnerDocument().getDocumentElement();
                    if (!XMLUtils.protectAgainstWrappingAttack(start, (Element)referencedElem, id)) {
                        String error = "Multiple Elements with the same ID " + id + " were detected";
                        throw new URIReferenceException(error);
                    }
                }
                
                XMLSignatureInput result = new XMLSignatureInput(referencedElem);
                if (!uri.substring(1).startsWith("xpointer(id(")) {
                    result.setExcludeComments(true);
                }

                result.setMIMEType("text/xml");
                if (baseURI != null && baseURI.length() > 0) {
                    result.setSourceURI(baseURI.concat(uriAttr.getNodeValue()));      
                } else {
                    result.setSourceURI(uriAttr.getNodeValue());      
                }
                return new ApacheNodeSetData(result);
            }
        } 

        try {
            ResourceResolver apacheResolver = 
                ResourceResolver.getInstance(uriAttr, baseURI, secVal);
            XMLSignatureInput in = apacheResolver.resolve(uriAttr, baseURI, secVal);
            if (in.isOctetStream()) {
                return new ApacheOctetStreamData(in);
            } else {
                return new ApacheNodeSetData(in);
            }
        } catch (Exception e) {
            throw new URIReferenceException(e);
        }
    }
}
