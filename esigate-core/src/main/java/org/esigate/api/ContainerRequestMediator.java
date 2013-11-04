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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;

/**
 * Encapsulates all interactions between EsiGate and the server container it is running in. To run EsiGate inside a new
 * container type, an implementation of this interface is required.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public interface ContainerRequestMediator {

    /**
     * Retrieves the cookies from the Cookie header of the request.
     * 
     * @return the cookies contained in the incoming request
     */
    Cookie[] getCookies();

    /**
     * Sends a cookie to the client by adding a Set-cookie header to the response.
     * 
     * @param cookie
     */
    void addCookie(Cookie cookie);

    /**
     * Returns the Internet Protocol (IP) address of the client or last proxy that sent the request. This is the same as
     * the value of the CGI variable <code>REMOTE_ADDR</code>. Implementation are not required to support this feature
     * and can retur <code>null</code>
     * 
     * @return a <code>String</code> containing the IP address of the client that sent the request or <code>null</code>
     */
    String getRemoteAddr();

    /**
     * Returns the login of the user making this request, if the user has been authenticated, or <code>null</code> if
     * the user has not been authenticated. Whether the user name is sent with each subsequent request depends on the
     * browser and type of authentication. Same as the value of the CGI variable REMOTE_USER.
     * 
     * @return a <code>String</code> specifying the login of the user making this request, or <code>null</code> if the
     *         user login is not known
     */
    String getRemoteUser();

    /**
     * Returns a <code>java.security.Principal</code> object containing the name of the current authenticated user. If
     * the user has not been authenticated, the method returns <code>null</code>.
     * 
     * @return a <code>java.security.Principal</code> containing the name of the user making this request;
     *         <code>null</code> if the user has not been authenticated
     */
    java.security.Principal getUserPrincipal();

    /**
     * Writes the response produced by EsiGate to the client. This includes response status line, headers and HttpEntity
     * 
     * @param response
     * @throws IOException
     *             if an problem occurs while writing to the network connection
     */
    void sendResponse(HttpResponse response) throws IOException;

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
     * Opens an <code>InputStream</code> to some local resource or <code>null</code>. Implementors are not required to
     * implement a local resources store and can return <code>null</code>.
     * 
     * @param path
     * @return the <code>InputStream</code> or <code>null</code>
     */
    InputStream getResourceAsStream(String path);

    /**
     * Returns the <code>HttpRequest</code> representing the request received by the container. Subsequent calls to this
     * method should return the same instance.
     * 
     * @return the <code>HttpRequest</code>
     */
    HttpRequest getHttpRequest();

    /**
     * @return The session id
     */
    // TODO remove this method. Too specific for servlet API
    String getSessionId();

}
