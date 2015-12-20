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

package org.esigate.events.impl;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.esigate.events.Event;
import org.esigate.http.IncomingRequest;
import org.esigate.http.OutgoingRequest;
import org.esigate.http.OutgoingRequestContext;

/**
 * Fragment event : when a fragment (a complete or partial page) is required for rendering.
 * 
 * <p>
 * This event may trigger a fetch event in case of a cache miss. If the cache already contains a non stale version of
 * the response, the fragment is retrieved from the cache.
 * 
 * <p>
 * In proxy mode, the first fragment is a complete page, and following fragments are complete and/or partial page
 * according to ESI directives.
 * 
 * @author Nicolas Richeton
 * 
 */
public class FragmentEvent extends Event {

    /**
     * The response data.
     * <p>
     * May be null if the request has not been executed yet. If this case, setting a response cancels the HTTP call and
     * use the given object instead.
     * 
     */
    private CloseableHttpResponse httpResponse;
    /**
     * The request context.
     */
    private final OutgoingRequestContext httpContext;
    /**
     * The new HTTP call details.
     * <p>
     * This object can been updated during pre-event processing and the HTTP call will use the updated object.
     */
    private final OutgoingRequest httpRequest;

    /**
     * The request which was received by ESIgate.
     * <p>
     * It is ready only and not intended to be altered.
     */
    private final IncomingRequest originalRequest;

    public FragmentEvent(IncomingRequest originalRequest, OutgoingRequest httpRequest,
            OutgoingRequestContext httpContext) {
        this.originalRequest = originalRequest;
        this.httpRequest = httpRequest;
        this.httpContext = httpContext;
    }

    public CloseableHttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(CloseableHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public OutgoingRequestContext getHttpContext() {
        return httpContext;
    }

    public OutgoingRequest getHttpRequest() {
        return httpRequest;
    }

    public IncomingRequest getOriginalRequest() {
        return originalRequest;
    }

    @Override
    public String toString() {
        String result = httpRequest.toString();
        if (httpResponse != null)
            result = result + " -> " + httpResponse;
        return result;
    }
}
