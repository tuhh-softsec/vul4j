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

import org.apache.http.client.methods.CloseableHttpResponse;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.http.IncomingRequest;
import org.esigate.servlet.impl.RequestFactory;
import org.esigate.servlet.impl.ResponseSender;

/**
 * {@link Filter} that can mix local and distant contents using EsiGate.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class ProxyFilter implements Filter {
    private RequestFactory requestFactory;
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

        try {
            CloseableHttpResponse driverResponse = DriverFactory.proxy(incomingRequest);
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
