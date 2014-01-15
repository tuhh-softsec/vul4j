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

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.esigate.api.ContainerRequestContext;

/**
 * Contains the object related to the request.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * 
 */
public class HttpServletRequestContext implements ContainerRequestContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext servletContext;
    private final FilterChain filterChain;

    public HttpServletRequestContext(HttpServletRequest request, HttpServletResponse response,
            ServletContext servletContext) {
        this(request, response, servletContext, null);
    }

    public HttpServletRequestContext(HttpServletRequest request, HttpServletResponse response,
            ServletContext servletContext, FilterChain filterChain) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
        this.filterChain = filterChain;
    }

    HttpServletResponse getResponse() {
        return response;
    }

    HttpServletRequest getRequest() {
        return request;
    }

    FilterChain getFilterChain() {
        return filterChain;
    }

    ServletContext getServletContext() {
        return servletContext;
    }

}
