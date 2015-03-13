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

package org.esigate.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpStatus;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.UserContext;
import org.esigate.api.ContainerRequestContext;
import org.esigate.http.IncomingRequest;
import org.esigate.util.UriUtils;

/**
 * A request to be executed by a given {@link Driver} instance.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class DriverRequest {
    private final IncomingRequest wrappedRequest;
    private final Driver driver;
    private final UserContext userContext;
    private final URL baseUrl;
    private final String visibleBaseUrl;
    private final boolean external;
    private String characterEncoding;

    /**
     * 
     * @param request
     * @param driver
     * @param relUrl
     * @throws HttpErrorPage
     */
    public DriverRequest(IncomingRequest request, Driver driver, String relUrl) throws HttpErrorPage {
        this.wrappedRequest = request;
        this.driver = driver;
        this.external = UriUtils.isAbsolute(relUrl);
        this.userContext = new UserContext(request, driver.getConfiguration().getInstanceName());
        try {
            this.baseUrl = new URL(driver.getConfiguration().getBaseUrlRetrieveStrategy().getBaseURL(request));
        } catch (MalformedURLException e) {
            throw new HttpErrorPage(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
        String visibleBase = driver.getConfiguration().getVisibleBaseURL();
        if (visibleBase == null) {
            String requestUri = request.getRequestLine().getUri();
            requestUri = UriUtils.removeQuerystring(requestUri);
            if (!this.external && requestUri.endsWith(relUrl)) {
                visibleBase = requestUri.substring(0, requestUri.length() - relUrl.length());
            } else {
                visibleBase = this.baseUrl.toString();
            }
        }
        this.visibleBaseUrl = UriUtils.rewriteURI(visibleBase, UriUtils.extractHost(request.getRequestLine().getUri()));
    }

    /**
     * Returns the driver instance that handles this request.
     * 
     * @return the driver instance
     */
    public Driver getDriver() {
        return driver;
    }

    /**
     * Returns the user context associated with this request.
     * 
     * @return the user context
     */
    public UserContext getUserContext() {
        return userContext;
    }

    /**
     * Returns the base url selected to send this request.
     * 
     * @return the base url
     */
    public URL getBaseUrl() {
        return baseUrl;
    }

    /**
     * Returns the charset used to encode and decode the request parameters.
     * 
     * @return the charset
     */
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    /**
     * Sets the charset to use to encode and decode the parameters for this request.
     * 
     * @param characterEncoding
     *            the charset name
     */
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    /**
     * Returns the context specifit to the container (servlet container for instance).
     * 
     * @return the context
     */
    public ContainerRequestContext getContext() {
        return wrappedRequest.getContext();
    }

    /**
     * Returns the original request received from the client.
     * 
     * @return the original request
     */
    public IncomingRequest getOriginalRequest() {
        return wrappedRequest;
    }

    /**
     * Returns true if the target url is not one the provider application.
     * 
     * @return true if the url is on the provider
     */
    public boolean isExternal() {
        return external;
    }

    /**
     * Returns the base url as seen by the client.
     * 
     * @return the visible base url
     */
    public String getVisibleBaseUrl() {
        return visibleBaseUrl;
    }

}
