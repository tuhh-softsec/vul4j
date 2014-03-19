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

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.securityEvent.SecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants.Event;
import org.apache.xml.security.stax.securityEvent.SecurityEventListener;
import org.junit.Assert;

public class TestSecurityEventListener implements SecurityEventListener {
    private List<SecurityEvent> events = new ArrayList<SecurityEvent>();

    @Override
    public void registerSecurityEvent(SecurityEvent securityEvent)
            throws XMLSecurityException {

        Assert.assertNotNull("CorrelationID of SecurityEvent is null: " + securityEvent.getSecurityEventType(), securityEvent.getCorrelationID());
        Assert.assertTrue("CorrelationID of SecurityEvent is empty: " + securityEvent.getSecurityEventType(), securityEvent.getCorrelationID().length() > 0);
        events.add(securityEvent);
    }

    @SuppressWarnings("unchecked")
    public <T> T getSecurityEvent(Event securityEvent) {
        for (SecurityEvent event : events) {
            if (event.getSecurityEventType() == securityEvent) {
                return (T)event;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getSecurityEvents(Event securityEvent) {
        List<T> foundEvents = new ArrayList<T>();
        for (SecurityEvent event : events) {
            if (event.getSecurityEventType() == securityEvent) {
                foundEvents.add((T)event);
            }
        }
        return foundEvents;
    }

    public List<SecurityEvent> getSecurityEvents() {
        return events;
    }
}