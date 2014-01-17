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

package org.esigate.servlet;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.api.ContainerRequestContext;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FetchEvent;
import org.esigate.extension.Extension;
import org.esigate.http.OutgoingRequest;
import org.esigate.http.OutgoingRequestContext;
import org.esigate.servlet.impl.ResponseCapturingWrapper;
import org.esigate.util.UriUtils;

public class ServletExtension implements Extension, IEventListener {
    private Driver driver;
    private String context;
    private int maxObjectSize;

    @Override
    public void init(Driver driver, Properties properties) {
        this.driver = driver;
        driver.getEventManager().register(EventManager.EVENT_FETCH_PRE, this);
        context = properties.getProperty("context");
        maxObjectSize = Parameters.MAX_OBJECT_SIZE.getValueInt(properties);
    }

    @Override
    public boolean event(EventDefinition id, Event event) {
        FetchEvent fetchEvent = (FetchEvent) event;
        if (EventManager.EVENT_FETCH_PRE.equals(id)) {
            String uriString = fetchEvent.getHttpRequest().getRequestLine().getUri();
            OutgoingRequest outgoingRequest = OutgoingRequestContext.adapt(fetchEvent.getHttpContext())
                    .getOutgoingRequest();
            String baseUrl = outgoingRequest.getBaseUrl().toString();
            if (outgoingRequest.getOriginalRequest().isExternal()) {
                // Non local absolute uri
                return true;
            } else {
                String relUrl = uriString;
                if (UriUtils.isAbsolute(relUrl)) {
                    relUrl = relUrl.substring(UriUtils.extractHost(relUrl).toURI().length());
                }
                relUrl = relUrl.substring(UriUtils.getPath(baseUrl).length());
                if (!relUrl.startsWith("/")) {
                    relUrl = "/" + relUrl;
                }
                ContainerRequestContext requestContext = outgoingRequest.getContainerRequestContext();
                CloseableHttpResponse result;
                if (!(requestContext instanceof HttpServletRequestContext)) {
                    String message = ServletExtension.class.getName()
                            + " can be used only inside a java servlet engine";
                    result = HttpErrorPage.generateHttpResponse(HttpStatus.SC_BAD_GATEWAY, message);
                } else {
                    HttpServletRequestContext httpServletRequestContext = (HttpServletRequestContext) requestContext;
                    try {
                        if (fetchEvent.getHttpContext().isProxy()) {
                            ResponseCapturingWrapper wrappedResponse = new ResponseCapturingWrapper(
                                    httpServletRequestContext.getResponse(), driver.getContentTypeHelper(), true,
                                    maxObjectSize);
                            if (context == null) {
                                httpServletRequestContext.getFilterChain().doFilter(
                                        httpServletRequestContext.getRequest(), wrappedResponse);
                                result = wrappedResponse.getResponse();
                            } else {
                                ServletContext crossContext = httpServletRequestContext.getServletContext().getContext(
                                        context);
                                if (crossContext == null) {
                                    String message = "Context " + context + " does not exist or cross context disabled";
                                    result = HttpErrorPage.generateHttpResponse(HttpStatus.SC_BAD_GATEWAY, message);
                                } else {
                                    crossContext.getRequestDispatcher(relUrl).forward(
                                            httpServletRequestContext.getRequest(), wrappedResponse);
                                    result = wrappedResponse.getResponse();
                                }
                            }
                        } else {
                            ResponseCapturingWrapper wrappedResponse = new ResponseCapturingWrapper(
                                    httpServletRequestContext.getResponse(), driver.getContentTypeHelper(), false,
                                    maxObjectSize);
                            if (context == null) {
                                httpServletRequestContext.getRequest().getRequestDispatcher(relUrl)
                                        .forward(httpServletRequestContext.getRequest(), wrappedResponse);
                                result = wrappedResponse.getResponse();
                            } else {
                                ServletContext crossContext = httpServletRequestContext.getServletContext().getContext(
                                        context);
                                if (crossContext == null) {
                                    String message = "Context " + context + " does not exist or cross context disabled";
                                    result = HttpErrorPage.generateHttpResponse(HttpStatus.SC_BAD_GATEWAY, message);
                                } else {
                                    crossContext.getRequestDispatcher(relUrl).include(
                                            httpServletRequestContext.getRequest(), wrappedResponse);
                                    result = wrappedResponse.getResponse();
                                }
                            }
                        }
                    } catch (IOException e) {
                        result = HttpErrorPage.generateHttpResponse(e);
                    } catch (ServletException e) {
                        result = HttpErrorPage.generateHttpResponse(e);
                    }
                }
                fetchEvent.setHttpResponse(result);
                // Stop execution
                fetchEvent.setExit(true);
            }
        }
        return true;
    }
}
