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

import java.net.URL;

import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicRequestLine;
import org.esigate.UserContext;
import org.esigate.api.ContainerRequestContext;
import org.esigate.impl.DriverRequest;

/**
 * Allows request line / uri modification after request creation.
 * 
 * @author fxbonnet
 * 
 */
public class OutgoingRequest extends BasicHttpEntityEnclosingRequest implements Configurable {

    private RequestLine requestLine;
    private final RequestConfig requestConfig;
    private final OutgoingRequestContext context;
    private final DriverRequest originalRequest;

    public OutgoingRequest(String method, String uri, ProtocolVersion version, DriverRequest originalRequest,
            RequestConfig requestConfig, OutgoingRequestContext context) {
        super(method, uri, version);
        requestLine = new BasicRequestLine(method, uri, version);
        this.requestConfig = requestConfig;
        this.context = context;
        this.originalRequest = originalRequest;
    }

    public void setUri(String uri) {
        requestLine = new BasicRequestLine(requestLine.getMethod(), uri, requestLine.getProtocolVersion());
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    @Override
    public RequestLine getRequestLine() {
        return requestLine;
    }

    @Override
    public String toString() {
        return requestLine.toString();
    }

    @Override
    public RequestConfig getConfig() {
        return requestConfig;
    }

    public OutgoingRequestContext getContext() {
        return context;
    }

    public UserContext getUserContext() {
        return originalRequest.getUserContext();
    }

    public ContainerRequestContext getContainerRequestContext() {
        return originalRequest.getContext();
    }

    public URL getBaseUrl() {
        return originalRequest.getBaseUrl();
    }

    public DriverRequest getOriginalRequest() {
        return originalRequest;
    }

}
