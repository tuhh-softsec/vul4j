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

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to work with Servlet request urls.
 * 
 * @author Nicolas Richeton
 * 
 */
public final class RequestUrl {
    public static final Logger LOG = LoggerFactory.getLogger(RequestUrl.class);

    private RequestUrl() {

    }

    /**
     * Get the relative url to the current servlet.
     * <p/>
     * Uses the request URI and removes : the context path, the servlet path if used.
     * 
     * @param request
     *            The current HTTP request
     * @param servlet
     *            true to remove the servlet path
     * @return the url, relative to the servlet mapping.
     */
    public static String getRelativeUrl(HttpServletRequest request, boolean servlet) {
        // Raw request url
        String requestURI = request.getRequestURI();
        // Application (war) context path
        String contextPath = request.getContextPath();
        // Servlet mapping
        String servletPath = request.getServletPath();

        String relativeUrl = requestURI;

        // Remove application context path
        if (contextPath != null && relativeUrl.startsWith(contextPath)) {
            relativeUrl = relativeUrl.substring(contextPath.length());
        }

        // Remove servlet mapping path
        if (servlet && servletPath != null && relativeUrl.startsWith(servletPath)) {
            relativeUrl = relativeUrl.substring(servletPath.length());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("requestURI: {}, contextPath: {}, servletPath: {}, relativeUrl: {}, ", new Object[] {requestURI,
                    contextPath, servletPath, relativeUrl});
        }

        return relativeUrl;
    }
}
