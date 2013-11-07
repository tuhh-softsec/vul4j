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
package org.esigate.servlet.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.esigate.ConfigurationException;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.RequestExecutor;
import org.esigate.events.EventManager;
import org.esigate.http.ContentTypeHelper;
import org.esigate.http.GenericHttpRequest;
import org.esigate.http.IOExceptionHandler;
import org.esigate.servlet.HttpServletMediator;
import org.esigate.servlet.ResponseCapturingWrapper;
import org.esigate.util.HttpRequestHelper;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServletRequestExecutor implements RequestExecutor {
    private Driver driver;
    private ContentTypeHelper contentTypeHelper;

    private static final class HttpServletDriverBuilder implements RequestExecutorBuilder {
        private ServletRequestExecutor servletRequestExecutor = new ServletRequestExecutor();

        @Override
        public RequestExecutorBuilder setEventManager(EventManager eventManager) {
            return this;
        }

        @Override
        public RequestExecutorBuilder setDriver(Driver driver) {
            servletRequestExecutor.driver = driver;
            return this;
        }

        @Override
        public RequestExecutorBuilder setProperties(Properties properties) {
            return this;
        }

        @Override
        public RequestExecutor build() {
            if (servletRequestExecutor.driver == null) {
                throw new ConfigurationException("driver is mandatory");
            }
            return servletRequestExecutor;
        }

        @Override
        public RequestExecutorBuilder setContentTypeHelper(ContentTypeHelper contentTypeHelper) {
            servletRequestExecutor.contentTypeHelper = contentTypeHelper;
            return this;
        }

    }

    public static RequestExecutorBuilder builder() {
        return new HttpServletDriverBuilder();
    }

    private static final Logger LOG = LoggerFactory.getLogger(ServletRequestExecutor.class);

    private ServletRequestExecutor() {
    }

    @Override
    public HttpResponse createAndExecuteRequest(HttpEntityEnclosingRequest request, String url, boolean proxy)
            throws HttpErrorPage {
        HttpServletMediator mediator = (HttpServletMediator) HttpRequestHelper.getMediator(request);
        ResponseCapturingWrapper wrappedResponse = new ResponseCapturingWrapper(mediator.getResponse(),
                contentTypeHelper);
        try {
            if (proxy) {
                mediator.getFilterChain().doFilter(mediator.getRequest(), wrappedResponse);
            } else {
                mediator.getRequest().getRequestDispatcher(getRelUrl(request, url))
                        .forward(mediator.getRequest(), wrappedResponse);
            }
        } catch (IOException e) {
            throw new HttpErrorPage(HttpStatus.SC_BAD_GATEWAY, e.getMessage(), e);
        } catch (ServletException e) {
            throw new HttpErrorPage(HttpStatus.SC_BAD_GATEWAY, e.getMessage(), e);
        }
        HttpResponse result = wrappedResponse.getResponse();
        if (result.getStatusLine().getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
            throw new HttpErrorPage(result);
        }
        return result;
    }

    @Override
    public HttpResponse execute(GenericHttpRequest request) {
        HttpServletMediator mediator = (HttpServletMediator) HttpRequestHelper.getMediator(request);
        ResponseCapturingWrapper wrappedResponse = new ResponseCapturingWrapper(mediator.getResponse(),
                contentTypeHelper);
        try {
            mediator.getRequest().getRequestDispatcher(request.getRequestLine().getUri())
                    .include(mediator.getRequest(), wrappedResponse);
        } catch (IOException e) {
            int statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            String statusText = "Error retrieving URL";
            LOG.warn(request.getRequestLine() + " -> " + statusCode + " " + statusText);
            return IOExceptionHandler.toHttpResponse(e);
        } catch (ServletException e) {
            int statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            String statusText = "Error retrieving URL";
            LOG.warn(request.getRequestLine() + " -> " + statusCode + " " + statusText);
            return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusText));
        }
        return wrappedResponse.getResponse();
    }

    private String getRelUrl(HttpEntityEnclosingRequest request, String url) {
        URI uri = UriUtils.createUri(url);
        String relUrl = uri.getPath();
        HttpServletMediator mediator = (HttpServletMediator) HttpRequestHelper.getMediator(request);
        relUrl = relUrl.substring(mediator.getRequest().getContextPath().length());
        if (uri.getRawQuery() != null) {
            relUrl += "?" + uri.getRawQuery();
        }
        return relUrl;
    }

}
