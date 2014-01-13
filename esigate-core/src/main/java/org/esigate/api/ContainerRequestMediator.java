/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate.api;

import java.io.Serializable;

import org.esigate.http.IncomingRequest;

/**
 * Encapsulates all interactions between EsiGate and the server container it is running in. To run EsiGate inside a new
 * container type, an implementation of this interface is required.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public interface ContainerRequestMediator {

    /**
     * Stores an object that can be reused across successive http requests from the same user. Implementations can
     * decide to store the objects serialized in a cookie on the client side or server side with some session tracking
     * mechanism.
     * 
     * @param key
     * @param value
     */
    void setSessionAttribute(String key, Serializable value);

    /**
     * Retrieves an Object previously stored with method @see #setSessionAttribute(String, Serializable) or
     * <code>null</code>.
     * 
     * @param key
     * @return the previously stored object or <code>null</code>
     */
    Serializable getSessionAttribute(String key);

    /**
     * Returns the <code>IncomingRequest</code> representing the request received by the container. Subsequent calls to
     * this method should return the same instance.
     * 
     * @return the <code>IncomingRequest</code>
     */
    IncomingRequest getHttpRequest();

}
