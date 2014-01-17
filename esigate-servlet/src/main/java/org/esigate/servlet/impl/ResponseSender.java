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

import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.esigate.http.IncomingRequest;

/**
 * Renders a response to the HttpSerlvetResponse.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class ResponseSender {

    public javax.servlet.http.Cookie rewriteCookie(Cookie src) {
        javax.servlet.http.Cookie servletCookie = new javax.servlet.http.Cookie(src.getName(), src.getValue());

        if (src.getDomain() != null) {
            servletCookie.setDomain(src.getDomain());
        }
        servletCookie.setPath(src.getPath());
        servletCookie.setSecure(src.isSecure());
        servletCookie.setComment(src.getComment());
        servletCookie.setVersion(src.getVersion());
        if (src.getExpiryDate() != null) {
            int maxAge = (int) ((src.getExpiryDate().getTime() - System.currentTimeMillis()) / 1000);
            // According to Cookie class specification, a negative value
            // would be considered as no value. That is not what we want!
            if (maxAge < 0) {
                maxAge = 0;
            }
            servletCookie.setMaxAge(maxAge);
        }
        return servletCookie;
    }

    public void sendResponse(HttpResponse httpResponse, IncomingRequest httpRequest, HttpServletResponse response)
            throws IOException {
        response.setStatus(httpResponse.getStatusLine().getStatusCode());
        for (Header header : httpResponse.getAllHeaders()) {
            String name = header.getName();
            String value = header.getValue();
            response.addHeader(name, value);
        }

        // Copy new cookies
        Cookie[] newCookies = httpRequest.getNewCookies();
        for (int i = 0; i < newCookies.length; i++) {
            response.addCookie(rewriteCookie(newCookies[i]));
        }

        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            long contentLength = httpEntity.getContentLength();
            if (contentLength > -1 && contentLength < Integer.MAX_VALUE) {
                response.setContentLength((int) contentLength);
            }
            Header contentType = httpEntity.getContentType();
            if (contentType != null) {
                response.setContentType(contentType.getValue());
            }
            Header contentEncoding = httpEntity.getContentEncoding();
            if (contentEncoding != null) {
                response.setHeader(contentEncoding.getName(), contentEncoding.getValue());
            }

            httpEntity.writeTo(response.getOutputStream());
        } else {
            response.sendError(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine()
                    .getReasonPhrase());
        }
    }

}
