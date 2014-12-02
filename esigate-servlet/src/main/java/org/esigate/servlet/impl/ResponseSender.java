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
import org.esigate.http.cookie.CookieUtil;

/**
 * Renders a response to the HttpSerlvetResponse.
 * 
 * @author Francois-Xavier Bonnet
 */
public class ResponseSender {

    public void sendResponse(HttpResponse httpResponse, IncomingRequest httpRequest, HttpServletResponse response)
            throws IOException {
        if (response.isCommitted()) {
            return; // Response already sent
        }
        sendHeaders(httpResponse, httpRequest, response);
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            httpEntity.writeTo(response.getOutputStream());
        } else {
            response.sendError(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine()
                    .getReasonPhrase());
        }
    }

    void sendHeaders(HttpResponse httpResponse, IncomingRequest httpRequest, HttpServletResponse response) {
        response.setStatus(httpResponse.getStatusLine().getStatusCode());
        for (Header header : httpResponse.getAllHeaders()) {
            String name = header.getName();
            String value = header.getValue();
            response.addHeader(name, value);
        }

        // Copy new cookies
        Cookie[] newCookies = httpRequest.getNewCookies();

        for (int i = 0; i < newCookies.length; i++) {
            response.addHeader("Set-Cookie", CookieUtil.encodeCookie(newCookies[i]));
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
        }
    }

}
