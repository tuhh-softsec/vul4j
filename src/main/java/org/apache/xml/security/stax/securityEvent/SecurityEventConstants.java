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

import org.apache.xml.security.stax.ext.ComparableType;

/**
 * @author $Author: coheigea $
 * @version $Revision: 1354898 $ $Date: 2012-06-28 11:19:02 +0100 (Thu, 28 Jun 2012) $
 */
public class SecurityEventConstants {

    public static final Event SignatureValue = new Event("SignatureValue");
    public static final Event SignedElement = new Event("SignedElement");
    public static final Event KeyValueToken = new Event("KeyValueToken");
    public static final Event KeyNameToken = new Event("KeyNameToken");
    public static final Event X509Token = new Event("X509Token");
    public static final Event AlgorithmSuite = new Event("AlgorithmSuite");
    public static final Event DefaultToken = new Event("DefaultToken");
    public static final Event ContentEncrypted = new Event("ContentEncrypted");
    public static final Event EncryptedElement = new Event("EncryptedElement");
    public static final Event EncryptedKeyToken = new Event("EncryptedKeyToken");

    public static class Event extends ComparableType<Event> {
        public Event(String name) {
            super(name);
        }
    }
}
