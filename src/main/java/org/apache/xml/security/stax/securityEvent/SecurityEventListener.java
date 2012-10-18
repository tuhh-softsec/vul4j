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


import org.apache.xml.security.exceptions.XMLSecurityException;

/**
 * @author $Author: giger $
 * @version $Revision: 1299273 $ $Date: 2012-03-10 21:05:21 +0000 (Sat, 10 Mar 2012) $
 */
public interface SecurityEventListener {

    /**
     * Registers a SecurityEvent which will be forwarded to the registered SecurityEventListener
     *
     * @param securityEvent The security event for the SecurityEventListener
     * @throws XMLSecurityException when the event will not be accepted (e.g. policy-violation)
     */
    void registerSecurityEvent(SecurityEvent securityEvent) throws XMLSecurityException;
}
