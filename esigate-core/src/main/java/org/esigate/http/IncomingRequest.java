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

package org.esigate.http;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicRequestLine;
import org.esigate.api.ContainerRequestContext;
import org.esigate.api.Session;

/**
 * Represents an incoming http request.
 * 
 * @author fxbonnet
 * 
 */
public final class IncomingRequest extends BasicHttpEntityEnclosingRequest {

    private final Map<String, Object> attributes = new HashMap<String, Object>();
    private ContainerRequestContext context;
    private String remoteUser;
    private String remoteAddr;
    private String sessionId;
    private Principal userPrincipal;
    private List<Cookie> cookies = new ArrayList<Cookie>();
    private List<Cookie> newCookies = new ArrayList<Cookie>();
    private Session session;
    private String contextPath;

    public static final class Builder {
        private final IncomingRequest result;

        private Builder(RequestLine requestline) {
            result = new IncomingRequest(requestline);
        }

        public IncomingRequest build() {
            return result;
        }

        public Builder setContext(ContainerRequestContext context) {
            result.context = context;
            return this;
        }

        public Builder setRemoteAddr(String remoteAddr) {
            result.remoteAddr = remoteAddr;
            return this;
        }

        public Builder setRemoteUser(String remoteUser) {
            result.remoteUser = remoteUser;
            return this;
        }

        public Builder setSessionId(String sessionId) {
            result.sessionId = sessionId;
            return this;
        }

        public Builder setUserPrincipal(Principal userPrincipal) {
            result.userPrincipal = userPrincipal;
            return this;
        }

        public Builder addCookie(Cookie cookie) {
            result.cookies.add(cookie);
            return this;
        }

        public Builder setSession(Session session) {
            result.session = session;
            return this;
        }

        public Builder addHeader(String name, String value) {
            result.addHeader(name, value);
            return this;
        }

        public Builder setEntity(InputStreamEntity entity) {
            result.setEntity(entity);
            return this;
        }

        /**
         * A String to remove before mapping at the begining of the path, typically used for the context path in a
         * servlet engine.
         * 
         * @param contextPath
         *            the context path
         * @return the {@link Builder}
         */
        public Builder setContextPath(String contextPath) {
            result.contextPath = contextPath;
            return this;
        }

    }

    public static Builder builder(RequestLine requestline) {
        return new Builder(requestline);
    }

    public static Builder builder(String uri) {
        return new Builder(new BasicRequestLine("GET", uri, HttpVersion.HTTP_1_1));
    }

    private IncomingRequest(RequestLine requestline) {
        super(requestline);
    }

    public <T> T getAttribute(String name) {
        return (T) attributes.get(name);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public ContainerRequestContext getContext() {
        return context;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    public Cookie[] getCookies() {
        return cookies.toArray(new Cookie[cookies.size()]);
    }

    public Cookie[] getNewCookies() {
        return newCookies.toArray(new Cookie[newCookies.size()]);
    }

    public void addNewCookie(Cookie cookie) {
        newCookies.add(cookie);
    }

    public Session getSession() {
        return session;
    }

    public String getContextPath() {
        return contextPath;
    }

}
