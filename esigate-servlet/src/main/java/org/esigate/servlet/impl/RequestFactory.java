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
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpHeaders;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicRequestLine;
import org.esigate.http.IncomingRequest;
import org.esigate.servlet.HttpServletRequestContext;
import org.esigate.util.UriUtils;

/**
 * Converts the {@link HttpServletRequest} to an {@link IncomingRequest}.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class RequestFactory {
    private final ServletContext servletContext;

    public RequestFactory(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public IncomingRequest create(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        HttpServletRequestContext context =
                new HttpServletRequestContext(request, response, servletContext, filterChain);
        // create request line
        String uri =
                UriUtils.createURI(request.getScheme(), request.getServerName(), request.getServerPort(),
                        request.getRequestURI(), request.getQueryString(), null);
        ProtocolVersion protocolVersion = BasicLineParser.parseProtocolVersion(request.getProtocol(), null);
        IncomingRequest.Builder builder =
                IncomingRequest.builder(new BasicRequestLine(request.getMethod(), uri, protocolVersion));
        builder.setContext(context);
        // copy headers
        @SuppressWarnings("rawtypes")
        Enumeration names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            @SuppressWarnings("rawtypes")
            Enumeration values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = (String) values.nextElement();
                builder.addHeader(name, value);
            }
        }
        // create entity
        ServletInputStream inputStream = request.getInputStream();
        if (inputStream != null) {
            // Copy entity-related headers
            String contentLengthHeader = request.getHeader(HttpHeaders.CONTENT_LENGTH);
            long contentLength = -1;
            if (contentLengthHeader != null) {
                contentLength = Long.parseLong(contentLengthHeader);
            }
            InputStreamEntity entity = new InputStreamEntity(inputStream, contentLength);
            String contentTypeHeader = request.getContentType();
            if (contentTypeHeader != null) {
                entity.setContentType(contentTypeHeader);
            }
            String contentEncodingHeader = request.getCharacterEncoding();
            if (contentEncodingHeader != null) {
                entity.setContentEncoding(contentEncodingHeader);
            }
            builder.setEntity(entity);
        }

        builder.setRemoteAddr(request.getRemoteAddr());
        builder.setRemoteUser(request.getRemoteUser());
        HttpSession session = request.getSession(false);
        if (session != null) {
            builder.setSessionId(session.getId());
        }
        builder.setUserPrincipal(request.getUserPrincipal());

        // Copy cookies
        // As cookie header contains only name=value so we don't need to copy
        // all attributes!
        javax.servlet.http.Cookie[] src = request.getCookies();
        if (src != null) {
            for (int i = 0; i < src.length; i++) {
                javax.servlet.http.Cookie c = src[i];
                BasicClientCookie dest = new BasicClientCookie(c.getName(), c.getValue());
                builder.addCookie(dest);
            }
        }
        builder.setSession(new HttpServletSession(request));
        return builder.build();
    }
}
