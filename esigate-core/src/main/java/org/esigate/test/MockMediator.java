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

package org.esigate.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicRequestLine;
import org.esigate.api.ContainerRequestMediator;
import org.esigate.http.IncomingRequest;
import org.esigate.util.UriUtils;

/**
 * MockMediator can be used in unit test, instead of {@link org.esigate.servlet.HttpServletMediator}.
 * 
 * @see org.esigate.servlet.HttpServletMediator
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
public class MockMediator implements ContainerRequestMediator {
    private final Map<String, Object> sessionAttributes = new HashMap<String, Object>();
    private final ArrayList<Cookie> cookies = new ArrayList<Cookie>();
    private HttpResponse httpResponse;
    private IncomingRequest httpRequest;

    public MockMediator(String uriString) {
        HttpHost httpHost = UriUtils.extractHost(uriString);
        String scheme = httpHost.getSchemeName();
        String host = httpHost.getHostName();
        int port = httpHost.getPort();
        RequestLine requestLine = new BasicRequestLine("GET", uriString, HttpVersion.HTTP_1_1);
        this.httpRequest = new IncomingRequest(requestLine, this);

        // Remove default ports
        if (port == -1 || (port == 80 && "http".equals(scheme)) || (port == 443 && "https".equals(scheme))) {
            this.httpRequest.setHeader("Host", host);
        } else {
            this.httpRequest.setHeader("Host", host + ":" + port);
        }
    }

    public MockMediator() {
        this("http://localhost:8080");
    }

    @Override
    public Cookie[] getCookies() {
        Cookie[] cookiesArray = new Cookie[this.cookies.size()];
        return this.cookies.toArray(cookiesArray);
    }

    @Override
    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }

    @Override
    public void setSessionAttribute(String key, Serializable value) {
        this.sessionAttributes.put(key, value);
    }

    @Override
    public Serializable getSessionAttribute(String key) {
        return (Serializable) this.sessionAttributes.get(key);
    }

    @Override
    public IncomingRequest getHttpRequest() {
        return this.httpRequest;
    }

    public HttpResponse getHttpResponse() {
        return this.httpResponse;
    }

}
