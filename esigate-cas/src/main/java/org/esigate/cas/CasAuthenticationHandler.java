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

package org.esigate.cas;

import java.security.Principal;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FragmentEvent;
import org.esigate.extension.Extension;
import org.esigate.http.IncomingRequest;
import org.esigate.http.OutgoingRequest;
import org.esigate.util.Parameter;
import org.esigate.util.ParameterString;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CasAuthenticationHandler implements IEventListener, Extension {
    // Configuration properties names
    public static final Parameter<String> CAS_LOGIN_URL = new ParameterString("casLoginUrl", "/login");
    private static final Logger LOG = LoggerFactory.getLogger(CasAuthenticationHandler.class);
    private Driver driver;
    private String loginUrl;

    private void addCasAuthentication(OutgoingRequest outgoingRequest, IncomingRequest incomingRequest) {
        String location = outgoingRequest.getRequestLine().getUri();
        String resultLocation = location;
        AttributePrincipal principal = getCasAuthentication(incomingRequest);
        if (principal != null) {
            LOG.debug("User logged in CAS as: " + principal.getName());
            String casProxyTicket = principal.getProxyTicketFor(resultLocation);
            LOG.debug("Proxy ticket retrieved: " + principal.getName() + " for service: " + location + " : "
                    + casProxyTicket);
            if (casProxyTicket != null) {
                if (resultLocation.indexOf("?") > 0) {
                    resultLocation = resultLocation + "&ticket=" + casProxyTicket;
                } else {
                    resultLocation = resultLocation + "?ticket=" + casProxyTicket;
                }
            }
        }
        outgoingRequest.setUri(resultLocation);
    }

    @Override
    public boolean event(EventDefinition id, Event event) {
        if (EventManager.EVENT_FRAGMENT_POST.equals(id)) {
            FragmentEvent e = (FragmentEvent) event;
            IncomingRequest incomingRequest = e.getOriginalRequest();
            CloseableHttpResponse httpResponse = e.getHttpResponse();
            if (isRedirectToCasServer(httpResponse)) {
                if (getCasAuthentication(incomingRequest) != null) {
                    LOG.debug("CAS authentication required for {}", e);
                    EntityUtils.consumeQuietly(e.getHttpResponse().getEntity());
                    e.setHttpResponse(null);
                    addCasAuthentication(e.getHttpRequest(), e.getOriginalRequest());
                    try {
                        LOG.debug("Sending new request {}", e);
                        e.setHttpResponse(this.driver.getRequestExecutor().execute(e.getHttpRequest()));
                    } catch (HttpErrorPage e1) {
                        e.setHttpResponse(e1.getHttpResponse());
                    }
                } else {
                    LOG.debug("CAS authentication required but we are not authenticated for {}", e);
                    e.setHttpResponse(HttpErrorPage.generateHttpResponse(HttpStatus.SC_UNAUTHORIZED,
                            "CAS authentication required"));
                }
            }
        }
        return true;
    }

    private AttributePrincipal getCasAuthentication(IncomingRequest incomingRequest) {
        Principal principal = incomingRequest.getUserPrincipal();
        if (principal != null && principal instanceof AttributePrincipal) {
            return (AttributePrincipal) principal;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.esigate.extension.Extension#init(org.esigate.Driver, java.util.Properties)
     */
    @Override
    public final void init(Driver d, Properties properties) {
        this.driver = d;
        this.driver.getEventManager().register(EventManager.EVENT_FRAGMENT_PRE, this);
        this.driver.getEventManager().register(EventManager.EVENT_FRAGMENT_POST, this);
        loginUrl = CAS_LOGIN_URL.getValue(properties);
    }

    private boolean isRedirectToCasServer(HttpResponse httpResponse) {
        Header locationHeader = httpResponse.getFirstHeader("Location");
        if (locationHeader != null) {
            String locationHeaderValue = locationHeader.getValue();
            if (locationHeaderValue != null && locationHeaderValue.contains(loginUrl)) {
                return true;
            }
        }
        return false;
    }

}
