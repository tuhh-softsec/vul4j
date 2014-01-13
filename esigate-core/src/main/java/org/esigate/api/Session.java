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

/**
 * The session can be used to store objects available successibe several http interactions.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public interface Session {

    /**
     * Stores an object that can be reused across successive http requests from the same user. Implementations can
     * decide to store the objects serialized in a cookie on the client side or server side with some session tracking
     * mechanism.
     * 
     * @param key
     * @param value
     */
    void setAttribute(String key, Serializable value);

    /**
     * Retrieves an Object previously stored with method @see #setSessionAttribute(String, Serializable) or
     * <code>null</code>.
     * 
     * @param key
     * @return the previously stored object or <code>null</code>
     */
    Serializable getAttribute(String key);

}
