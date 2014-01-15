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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.http.IncomingRequest;
import org.esigate.impl.UriMapping;
import org.esigate.servlet.impl.DriverSelector;
import org.esigate.servlet.impl.RequestFactory;
import org.esigate.servlet.impl.RequestUrl;
import org.esigate.servlet.impl.ResponseSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyFilter.class);
    private RequestFactory requestFactory;
    private final DriverSelector driverSelector = new DriverSelector();
    private final ResponseSender responseSender = new ResponseSender();

    @Override
    public void init(FilterConfig filterConfig) {
        requestFactory = new RequestFactory(filterConfig.getServletContext());
        // Force esigate configuration parsing to trigger errors right away (if
        // any) and prevent delay on first call.
        DriverFactory.ensureConfigured();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        IncomingRequest incomingRequest = requestFactory.create(httpServletRequest, httpServletResponse, chain);
        Pair<Driver, UriMapping> dm = null;
        try {
            dm = driverSelector.selectProvider(httpServletRequest, false);
            String relUrl = RequestUrl.getRelativeUrl(httpServletRequest, dm.getRight(), false);
            LOG.debug("Proxying {}", relUrl);
            CloseableHttpResponse driverResponse = dm.getLeft().proxy(relUrl, incomingRequest);
            responseSender.sendResponse(driverResponse, incomingRequest, httpServletResponse);
        } catch (HttpErrorPage e) {
            if (!httpServletResponse.isCommitted()) {
                responseSender.sendResponse(e.getHttpResponse(), incomingRequest, httpServletResponse);
            }
        }
    }

    @Override
    public void destroy() {
        // Nothing to do
    }

}
