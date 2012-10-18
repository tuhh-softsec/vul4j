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

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.ConfigurationProperties;
import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityTokenProvider;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.stax.securityEvent.SecurityEventListener;

import java.util.*;

/**
 * Concrete security context implementation
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class SecurityContextImpl implements SecurityContext {

    private static final Boolean allowMD5Algorithm = Boolean.valueOf(ConfigurationProperties.getProperty("AllowMD5Algorithm"));
    private final Map<String, SecurityTokenProvider> securityTokenProviders = new HashMap<String, SecurityTokenProvider>();

    @SuppressWarnings("unchecked")
    private final Map content = Collections.synchronizedMap(new HashMap());
    private final List<SecurityEventListener> securityEventListeners = new ArrayList<SecurityEventListener>(2);

    @Override
    public void addSecurityEventListener(SecurityEventListener securityEventListener) {
        if (securityEventListener != null) {
            this.securityEventListeners.add(securityEventListener);
        }
    }

    @Override
    public synchronized void registerSecurityEvent(SecurityEvent securityEvent) throws XMLSecurityException {
        forwardSecurityEvent(securityEvent);
    }

    protected void forwardSecurityEvent(SecurityEvent securityEvent) throws XMLSecurityException {
        if (!allowMD5Algorithm && SecurityEventConstants.AlgorithmSuite.equals(securityEvent.getSecurityEventType())) {
            AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = (AlgorithmSuiteSecurityEvent)securityEvent;
            if (algorithmSuiteSecurityEvent.getAlgorithmURI().contains("md5") ||
                    algorithmSuiteSecurityEvent.getAlgorithmURI().contains("MD5")) {
                throw new XMLSecurityException("secureProcessing.AllowMD5Algorithm");
            }
        }
        for (int i = 0; i < securityEventListeners.size(); i++) {
            SecurityEventListener securityEventListener = securityEventListeners.get(i);
            securityEventListener.registerSecurityEvent(securityEvent);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void put(String key, T value) {
        content.put(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        return (T) content.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T remove(String key) {
        return (T) content.remove(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends List> void putList(Object key, T value) {
        if (value == null) {
            return;
        }
        List<T> entry = (List<T>) content.get(key);
        if (entry == null) {
            entry = new ArrayList<T>();
            content.put(key, entry);
        }
        entry.addAll(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void putAsList(Object key, T value) {
        List<T> entry = (List<T>) content.get(key);
        if (entry == null) {
            entry = new ArrayList<T>();
            content.put(key, entry);
        }
        entry.add(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getAsList(Object key) {
        return (List<T>) content.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, U> void putAsMap(Object key, T mapKey, U mapValue) {
        Map<T, U> entry = (Map<T, U>) content.get(key);
        if (entry == null) {
            entry = new HashMap<T, U>();
            content.put(key, entry);
        }
        entry.put(mapKey, mapValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, U> Map<T, U> getAsMap(Object key) {
        return (Map<T, U>) content.get(key);
    }

    @Override
    public void registerSecurityTokenProvider(String id, SecurityTokenProvider securityTokenProvider) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        securityTokenProviders.put(id, securityTokenProvider);
    }

    @Override
    public SecurityTokenProvider getSecurityTokenProvider(String id) {
        return securityTokenProviders.get(id);
    }
}
