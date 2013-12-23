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

package org.esigate.util;

import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.esigate.impl.DriverRequest;

public final class HttpRequestHelper {

    private HttpRequestHelper() {
    }

    public static String getFirstHeader(String name, HttpRequest request) {
        Header[] values = request.getHeaders(name);
        if (values.length > 0) {
            return values[0].getValue();
        }
        return null;
    }

    public static String getParameter(DriverRequest request, String name) {
        String characterEncoding = request.getCharacterEncoding();
        if (characterEncoding == null) {
            characterEncoding = "ISO-8859-1";
        }
        List<NameValuePair> parameters = UriUtils.parse(request.getRequestLine().getUri(), characterEncoding);
        Iterator<NameValuePair> it = parameters.iterator();
        while (it.hasNext()) {
            NameValuePair nameValuePair = it.next();
            if (nameValuePair.getName().equals(name)) {
                return nameValuePair.getValue();
            }
        }
        return null;
    }

    /**
     * Returns the target host as defined in the Host header or extracted from the request URI.
     * 
     * Usefull to generate Host header in a HttpRequest
     * 
     * @param request
     * @return the host formatted as host:port
     */
    public static HttpHost getHost(HttpRequest request) {
        HttpHost httpHost = UriUtils.extractHost(request.getRequestLine().getUri());
        String scheme = httpHost.getSchemeName();
        String host = httpHost.getHostName();
        int port = httpHost.getPort();
        Header[] headers = request.getHeaders(HttpHeaders.HOST);
        if (headers != null && headers.length != 0) {
            String headerValue = headers[0].getValue();
            String[] splitted = headerValue.split(":");
            host = splitted[0];
            if (splitted.length > 1) {
                port = Integer.parseInt(splitted[1]);
            } else {
                port = -1;
            }
        }
        return new HttpHost(host, port, scheme);
    }

}
