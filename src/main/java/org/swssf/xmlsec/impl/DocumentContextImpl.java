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
package org.swssf.xmlsec.impl;

import org.swssf.xmlsec.ext.DocumentContext;
import org.swssf.xmlsec.ext.XMLSecurityConstants;

import java.util.*;

/**
 * A concrete DocumentContext Implementation
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class DocumentContextImpl implements DocumentContext, Cloneable {

    private String encoding;
    private final Map<Integer, XMLSecurityConstants.ContentType> contentTypeMap = new TreeMap<Integer, XMLSecurityConstants.ContentType>();
    private final Map<Object, Integer> processorToIndexMap = new HashMap<Object, Integer>();

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public synchronized void setIsInEncryptedContent(int index, Object key) {
        contentTypeMap.put(index, XMLSecurityConstants.ContentType.ENCRYPTION);
        processorToIndexMap.put(key, index);
    }

    public synchronized void unsetIsInEncryptedContent(Object key) {
        Integer index = processorToIndexMap.remove(key);
        contentTypeMap.remove(index);
    }

    public boolean isInEncryptedContent() {
        return contentTypeMap.containsValue(XMLSecurityConstants.ContentType.ENCRYPTION);
    }

    public synchronized void setIsInSignedContent(int index, Object key) {
        contentTypeMap.put(index, XMLSecurityConstants.ContentType.SIGNATURE);
        processorToIndexMap.put(key, index);
    }

    public synchronized void unsetIsInSignedContent(Object key) {
        Integer index = processorToIndexMap.remove(key);
        contentTypeMap.remove(index);
    }

    public boolean isInSignedContent() {
        return contentTypeMap.containsValue(XMLSecurityConstants.ContentType.SIGNATURE);
    }

    @Override
    public List<XMLSecurityConstants.ContentType> getProtectionOrder() {
        return new ArrayList<XMLSecurityConstants.ContentType>(contentTypeMap.values());
    }

    public Map<Integer, XMLSecurityConstants.ContentType> getContentTypeMap() {
        return Collections.unmodifiableMap(contentTypeMap);
    }

    protected void setContentTypeMap(Map<Integer, XMLSecurityConstants.ContentType> contentTypeMap) {
        this.contentTypeMap.putAll(contentTypeMap);
    }

    @Override
    protected DocumentContextImpl clone() throws CloneNotSupportedException {
        DocumentContextImpl documentContext = new DocumentContextImpl();
        documentContext.setEncoding(this.encoding);
        documentContext.setContentTypeMap(getContentTypeMap());
        return documentContext;
    }
}
