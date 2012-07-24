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
package org.apache.xml.security.test.stax.signature;

import java.util.ArrayList;
import java.util.List;

import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.securityEvent.SecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants.Event;
import org.apache.xml.security.stax.securityEvent.SecurityEventListener;

public class TestSecurityEventListener implements SecurityEventListener {
    List<SecurityEvent> events = new ArrayList<SecurityEvent>();

    @Override
    public void registerSecurityEvent(SecurityEvent securityEvent)
            throws XMLSecurityException {
        if (!events.contains(securityEvent)) {
            events.add(securityEvent);
        }
    }
    
    public SecurityEvent getTokenEvent(Event securityEvent) {
        for (SecurityEvent event : events) {
            if (event.getSecurityEventType() == securityEvent) {
                return event;
            }
        }
        return null;
    }
    
    public List<SecurityEvent> getTokenEvents(Event securityEvent) {
        List<SecurityEvent> foundEvents = new ArrayList<SecurityEvent>();
        for (SecurityEvent event : events) {
            if (event.getSecurityEventType() == securityEvent) {
                foundEvents.add(event);
            }
        }
        return foundEvents;
    }
}