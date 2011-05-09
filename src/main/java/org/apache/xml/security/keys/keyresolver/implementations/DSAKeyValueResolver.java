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
package org.apache.xml.security.keys.keyresolver.implementations;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.keyvalues.DSAKeyValue;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;

public class DSAKeyValueResolver extends KeyResolverSpi {
    
    /** {@link org.apache.commons.logging} logging facility */
    private static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(DSAKeyValueResolver.class);


    /**
     * Method engineResolvePublicKey
     *
     * @param element
     * @param BaseURI
     * @param storage
     * @return null if no {@link PublicKey} could be obtained
     */
    public PublicKey engineLookupAndResolvePublicKey(
        Element element, String BaseURI, StorageResolver storage
    ) {
        if (element == null) {
            return null;
        }
        Element dsaKeyElement = null;
        boolean isKeyValue = 
            XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_KEYVALUE);
        if (isKeyValue) {         	     
            dsaKeyElement =
                XMLUtils.selectDsNode(element.getFirstChild(), Constants._TAG_DSAKEYVALUE, 0);                    
        } else if (XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_DSAKEYVALUE)) {
            // this trick is needed to allow the RetrievalMethodResolver to eat a
            // ds:DSAKeyValue directly (without KeyValue)
            dsaKeyElement = element;
        }	      

        if (dsaKeyElement == null) {
            return null;
        }

        try {
            DSAKeyValue dsaKeyValue = new DSAKeyValue(dsaKeyElement, BaseURI);
            PublicKey pk = dsaKeyValue.getPublicKey();

            return pk;
        } catch (XMLSecurityException ex) {
            if (log.isDebugEnabled()) {
                log.debug(ex);
            }
            //do nothing
        }

        return null;
    }


    /** @inheritDoc */
    public X509Certificate engineLookupResolveX509Certificate(
        Element element, String BaseURI, StorageResolver storage
    ) {
        return null;
    }

    /** @inheritDoc */
    public javax.crypto.SecretKey engineLookupAndResolveSecretKey(
        Element element, String BaseURI, StorageResolver storage
    ) {
        return null;
    }
}
