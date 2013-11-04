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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.mockito.Mockito;

/**
 * Fluent-style builder for mocked HttpServletRequest.
 * 
 * <p>
 * Default request is
 * 
 * <pre>
 * GET http://localhost/ HTTP/1.1
 * </pre>
 * 
 * @author Nicolas Richeton
 * 
 */
public class MockHttpServletRequestBuilder {
    private String protocolVersion = "HTTP/1.1";
    private String uriString = "http://localhost/";
    private List<Header> headers = new ArrayList<Header>();
    private String method = "GET";
    private Map<String, Object> session = null;

    // boolean mockMediator = false;
    // ContainerRequestMediator mediator = null;

    public MockHttpServletRequestBuilder uri(String uri) {
        this.uriString = uri;
        return this;
    }

    /**
     * Duplicate headers are not supported currently.
     * 
     * @param name
     * @param value
     * @return this
     */
    public MockHttpServletRequestBuilder header(String name, String value) {
        this.headers.add(new BasicHeader(name, value));
        return this;
    }

    public MockHttpServletRequestBuilder method(String paramMethod) {
        this.method = paramMethod;
        return this;
    }

    public MockHttpServletRequestBuilder protocolVersion(String paramProtocolVersion) {
        this.protocolVersion = paramProtocolVersion;
        return this;
    }

    /**
     * Session values are not yet supported. Null or existing object are honored.
     * 
     * @param paramSession
     * @return this
     */
    public MockHttpServletRequestBuilder session(Map<String, Object> paramSession) {
        this.session = paramSession;
        return this;
    }

    /**
     * Build the request as defined in the current builder.
     * 
     * @return the request
     */
    public HttpServletRequest build() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Mockito.when(request.getMethod()).thenReturn(this.method);
        Mockito.when(request.getProtocol()).thenReturn(this.protocolVersion);
        Mockito.when(request.getRequestURI()).thenReturn(this.uriString);

        Mockito.when(request.getHeaderNames()).thenReturn(Collections.enumeration(headers));
        for (Header h : headers) {
            List<String> hresult = new ArrayList<String>();
            hresult.add(h.getValue());
            Mockito.when(request.getHeaders(h.getName())).thenReturn(Collections.enumeration(hresult));
            Mockito.when(request.getHeader(h.getName())).thenReturn(h.getValue());
        }

        if (session == null) {
            Mockito.when(request.getSession()).thenReturn(null);
        } else {
            HttpSession sessionMock = Mockito.mock(HttpSession.class);
            Mockito.when(request.getSession()).thenReturn(sessionMock);
        }
        return request;
    }
}
