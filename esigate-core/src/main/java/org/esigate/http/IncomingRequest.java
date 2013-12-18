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
import java.util.HashMap;
import java.util.Map;

import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.esigate.UserContext;
import org.esigate.api.ContainerRequestMediator;

/**
 * Represents an incoming http request
 * 
 * @author fxbonnet
 * 
 */
public class IncomingRequest extends BasicHttpEntityEnclosingRequest {

    private final Map<String, Object> attributes = new HashMap<String, Object>();
    private ContainerRequestMediator mediator;
    private Map<String, String> parameters;
    private UserContext userContext;
    private URL baseUrl;

    public IncomingRequest(RequestLine requestline) {
        super(requestline);
    }

    public <T> T getAttribute(String name) {
        return (T) attributes.get(name);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public ContainerRequestMediator getMediator() {
        return mediator;
    }

    public void setMediator(ContainerRequestMediator mediator) {
        this.mediator = mediator;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public UserContext getUserContext() {
        return userContext;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

}
