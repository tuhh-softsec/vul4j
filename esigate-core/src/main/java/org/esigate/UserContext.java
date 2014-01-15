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

package org.esigate;

import java.io.Serializable;

import org.esigate.api.Session;
import org.esigate.http.IncomingRequest;

/**
 * User context that can be used in the master application to define the user id. This context will be transmitted to
 * the provider applications.<br />
 * There is one instance of user context associated with each session.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * 
 */
public class UserContext {
    private static final String USER_KEY = "user";
    private final IncomingRequest httpRequest;
    private final String key;

    public UserContext(IncomingRequest httpRequest, String key) {
        this.httpRequest = httpRequest;
        this.key = UserContext.class.getName() + "#" + key;
    }

    private String prefixAttributeName(String name) {
        return key + "#" + name;
    }

    public Object getAttribute(String name) {
        Session session = httpRequest.getSession();
        if (session == null) {
            return null;
        }
        return session.getAttribute(prefixAttributeName(name));
    }

    public void setAttribute(String name, Serializable value) {
        httpRequest.getSession().setAttribute(prefixAttributeName(name), value);
    }

    public String getUser() {
        return (String) getAttribute(USER_KEY);
    }

    public void setUser(String user) {
        setAttribute(USER_KEY, user);
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(Parameters.SMALL_BUFFER_SIZE).append("User=").append(getUser());
        return result.toString();
    }

    /**
     * Returns the key under which the user name is to be stored in the session for a given provider.
     * 
     * @param providerName
     *            The name of the provider
     * @return The session key
     */
    public static String getUserSessionKey(String providerName) {
        return UserContext.class.getName() + "#" + providerName + "#" + USER_KEY;
    }
}
