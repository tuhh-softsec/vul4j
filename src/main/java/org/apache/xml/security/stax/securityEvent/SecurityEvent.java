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
package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.securityEvent.SecurityEventConstants.Event;

/**
 * @author $Author: giger $
 * @version $Revision: 1350961 $ $Date: 2012-06-16 17:51:00 +0100 (Sat, 16 Jun 2012) $
 */
public abstract class SecurityEvent {
    
    private final Event securityEventType;
    private String correlationID;

    protected SecurityEvent(Event securityEventType) {
        this.securityEventType = securityEventType;
    }

    public Event getSecurityEventType() {
        return securityEventType;
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }
}
