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

/**
 * @author $Author: coheigea $
 * @version $Revision: 1354898 $ $Date: 2012-06-28 11:19:02 +0100 (Thu, 28 Jun 2012) $
 */
public class SecurityEventConstants {
    public static final Event SignatureValue = new Event("SignatureValue");
    public static final Event SignedElement = new Event("SignedElement");
    public static final Event KeyValueToken = new Event("KeyValueToken");
    public static final Event X509Token = new Event("X509Token");
    public static final Event AlgorithmSuite = new Event("AlgorithmSuite");

    public static class Event implements Comparable<Event> {
        private final String name;

        public Event(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Event) {
                Event otherEvent = (Event) obj;
                if (this.toString().equals(otherEvent.toString())) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public int compareTo(Event o) {
            return this.toString().compareTo(o.toString());
        }
    }
}
