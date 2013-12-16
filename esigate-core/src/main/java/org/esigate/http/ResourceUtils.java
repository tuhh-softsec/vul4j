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

package org.esigate.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpRequest;
import org.esigate.api.ContainerRequestMediator;
import org.esigate.util.HttpRequestHelper;
import org.esigate.util.UriUtils;

/**
 * Utility class to generate URL and path for Resources.
 * 
 * @author Francois-Xavier Bonnet
 */
public final class ResourceUtils {

    /**
     * Private constructor
     */
    private ResourceUtils() {

    }

    private static String buildQueryString(HttpRequest originalRequest, boolean proxy) {
        try {
            StringBuilder queryString = new StringBuilder();
            String charset = HttpRequestHelper.getCharacterEncoding(originalRequest);
            if (charset == null) {
                charset = "ISO-8859-1";
            }
            String originalQuerystring = UriUtils.createUri(originalRequest.getRequestLine().getUri()).getRawQuery();
            if (proxy && originalQuerystring != null) {
                // Remove jsessionid from request if it is present
                // As we are in a java application, the container might add
                // jsessionid to the querystring. We must not forward it to
                // included applications.
                String jsessionid = null;
                ContainerRequestMediator mediator = HttpRequestHelper.getMediator(originalRequest);
                jsessionid = mediator.getSessionId();
                if (jsessionid != null) {
                    originalQuerystring = UriUtils.removeSessionId(jsessionid, originalQuerystring);
                }
                queryString.append(originalQuerystring);
            }
            Map<String, String> parameters = HttpRequestHelper.getParameters(originalRequest);
            if (parameters != null) {
                ResourceUtils.appendParameters(queryString, charset, parameters);
            }
            return queryString.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static void appendParameters(StringBuilder buf, String charset, Map<String, String> params)
            throws UnsupportedEncodingException {
        for (Entry<String, String> param : params.entrySet()) {
            if (buf.length() > 0) {
                buf.append("&");
            }
            buf.append(URLEncoder.encode(param.getKey(), charset));
            buf.append("=");
            buf.append(URLEncoder.encode(param.getValue(), charset));
        }
    }

    private static String concatUrl(String baseUrl, String relUrl) {
        StringBuilder url = new StringBuilder();
        if (baseUrl != null && relUrl != null && (baseUrl.endsWith("/") || baseUrl.endsWith("\\"))
                && relUrl.startsWith("/")) {
            url.append(baseUrl.substring(0, baseUrl.length() - 1)).append(relUrl);
        } else {
            url.append(baseUrl).append(relUrl);
        }
        return url.toString();
    }

    public static String getHttpUrlWithQueryString(String url, HttpRequest originalRequest, boolean proxy) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            // Relative URL, we need to add the driver base url
            String baseUrl = HttpRequestHelper.getBaseUrl(originalRequest).toString();
            if (baseUrl != null) {
                url = concatUrl(baseUrl, url);
            }
        }
        String queryString = ResourceUtils.buildQueryString(originalRequest, proxy);
        if (queryString.length() == 0) {
            return url;
        } else {
            return url + "?" + queryString;
        }
    }

}
