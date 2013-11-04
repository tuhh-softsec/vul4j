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

import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.esigate.Driver;
import org.esigate.UserContext;
import org.esigate.api.ContainerRequestMediator;

public final class HttpRequestHelper {
    private static final String USER_CONTEXT = UserContext.class.getName();
    private static final String BASEURLASURL = URL.class.getName();
    private static final String DRIVER = Driver.class.getName();
    private static final String PARAMETERS = "parameters";
    private static final String CHARACTER_ENCODING = "character encoding";
    private static final String MEDIATOR = ContainerRequestMediator.class.getName();

    private HttpRequestHelper() {
    }

    public static UserContext getUserContext(HttpRequest request) {
        return (UserContext) request.getParams().getParameter(USER_CONTEXT);
    }

    public static void setUserContext(HttpRequest request, UserContext userContext) {
        request.getParams().setParameter(USER_CONTEXT, userContext);
    }

    public static URL getBaseUrl(HttpRequest request) {
        return (URL) request.getParams().getParameter(BASEURLASURL);
    }

    public static void setBaseUrl(HttpRequest request, URL url) {
        request.getParams().setParameter(BASEURLASURL, url);
    }

    public static Driver getDriver(HttpRequest request) {
        return (Driver) request.getParams().getParameter(DRIVER);
    }

    public static void setDriver(HttpRequest request, Driver driver) {
        request.getParams().setParameter(DRIVER, driver);
    }

    public static Map<String, String> getParameters(HttpRequest request) {
        return (Map<String, String>) request.getParams().getParameter(PARAMETERS);
    }

    public static void setParameters(HttpRequest request, Map<String, String> parameters) {
        request.getParams().setParameter(PARAMETERS, parameters);
    }

    public static String getFirstHeader(String name, HttpRequest request) {
        Header[] values = request.getHeaders(name);
        if (values.length > 0) {
            return values[0].getValue();
        }
        return null;
    }

    public static String getCharacterEncoding(HttpRequest request) {
        return (String) request.getParams().getParameter(CHARACTER_ENCODING);
    }

    public static void setCharacterEncoding(HttpRequest request, String characterEncoding) {
        request.getParams().setParameter(CHARACTER_ENCODING, characterEncoding);
    }

    public static String getParameter(HttpRequest request, String name) {
        String characterEncoding = getCharacterEncoding(request);
        if (characterEncoding == null) {
            characterEncoding = "ISO-8859-1";
        }
        URI uri = UriUtils.createUri(request.getRequestLine().getUri());
        List<NameValuePair> parameters = URLEncodedUtils.parse(uri, characterEncoding);
        Iterator<NameValuePair> it = parameters.iterator();
        while (it.hasNext()) {
            NameValuePair nameValuePair = (NameValuePair) it.next();
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
        URI uri = UriUtils.createUri(request.getRequestLine().getUri());
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
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

    /**
     * Returns the <code>IncomingRequestMediator</code> object used to interact with the original container-specific
     * request.
     * 
     * @param request
     * @return the <code>IncomingRequestMediator</code>
     */
    public static ContainerRequestMediator getMediator(HttpRequest request) {
        return (ContainerRequestMediator) request.getParams().getParameter(MEDIATOR);
    }

    public static void setMediator(HttpRequest request, ContainerRequestMediator mediator) {
        request.getParams().setParameter(MEDIATOR, mediator);
    }

}
