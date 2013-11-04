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

import java.net.URI;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedirectStrategy extends DefaultRedirectStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(RedirectStrategy.class);

    private static final String LAST_REQUEST = "LAST_REQUEST";

    @Override
    public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        URI redirectLocation = super.getLocationURI(request, response, context);
        if (LOG.isInfoEnabled()) {
            LOG.info(request.getRequestLine() + " -> " + response.getStatusLine()
                    + " -> automaticaly following redirect to " + redirectLocation.toString());
        }
        return redirectLocation;
    }

    @Override
    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
            throws ProtocolException {
        HttpUriRequest newRequest = super.getRedirect(request, response, context);
        context.setAttribute(LAST_REQUEST, newRequest);
        return newRequest;
    }

    public static HttpRequest getLastRequest(HttpRequest httpRequest, HttpContext httpContext) {
        HttpRequest lastRedirectionRequest = (HttpRequest) httpContext.getAttribute(LAST_REQUEST);
        if (lastRedirectionRequest != null) {
            return lastRedirectionRequest;
        }
        return httpRequest;
    }

}
