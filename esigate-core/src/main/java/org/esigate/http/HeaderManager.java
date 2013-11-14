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

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.params.CoreProtocolPNames;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.util.FilterList;
import org.esigate.util.HttpRequestHelper;
import org.esigate.util.PropertiesUtil;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class is responsible for copying headers from incoming requests to outgoing requests and from incoming responses
 * to outgoing responses.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class HeaderManager {
    private static final Logger LOG = LoggerFactory.getLogger(HeaderManager.class);

    private final FilterList requestHeadersFilterList;
    private final FilterList responseHeadersFilterList;

    public HeaderManager(Properties properties) {
        // Populate headers filter lists
        requestHeadersFilterList = new FilterList();
        responseHeadersFilterList = new FilterList();
        // By default all headers are forwarded
        requestHeadersFilterList.add(Collections.singletonList("*"));
        responseHeadersFilterList.add(Collections.singletonList("*"));
        PropertiesUtil
                .populate(requestHeadersFilterList, properties, Parameters.FORWARD_REQUEST_HEADERS.getName(),
                        Parameters.DISCARD_REQUEST_HEADERS.getName(), "",
                        Parameters.DISCARD_REQUEST_HEADERS.getDefaultValue());
        PropertiesUtil.populate(responseHeadersFilterList, properties, Parameters.FORWARD_RESPONSE_HEADERS.getName(),
                Parameters.DISCARD_RESPONSE_HEADERS.getName(), "",
                Parameters.DISCARD_RESPONSE_HEADERS.getDefaultValue());
    }

    protected boolean isForwardedRequestHeader(String headerName) {
        return requestHeadersFilterList.contains(headerName);
    }

    protected boolean isForwardedResponseHeader(String headerName) {
        return responseHeadersFilterList.contains(headerName);
    }

    /**
     * Copy header from originalRequest to httpRequest.
     * <p>
     * Referer is rewritten. X-Forwarded-* headers are updated.
     * 
     * @param originalRequest
     *            source request
     * @param httpRequest
     *            destination request
     * @throws HttpErrorPage
     *             if one of the header value is invalid.
     */
    public void copyHeaders(HttpRequest originalRequest, HttpRequest httpRequest) throws HttpErrorPage {
        String originalUri = originalRequest.getRequestLine().getUri();
        String uri = httpRequest.getRequestLine().getUri();
        for (Header header : originalRequest.getAllHeaders()) {
            // Special headers
            // User-agent must be set in a specific way
            if (HttpHeaders.USER_AGENT.equalsIgnoreCase(header.getName())
                    && isForwardedRequestHeader(HttpHeaders.USER_AGENT)) {
                httpRequest.getParams().setParameter(CoreProtocolPNames.USER_AGENT, header.getValue());
            } else if (HttpHeaders.REFERER.equalsIgnoreCase(header.getName())
                    && isForwardedRequestHeader(HttpHeaders.REFERER)) {
                String value = header.getValue();
                try {
                    value = UriUtils.translateUrl(value, originalUri, uri);
                } catch (MalformedURLException e) {
                    throw new HttpErrorPage(HttpStatus.SC_BAD_REQUEST, "Bad request", e);
                }
                httpRequest.addHeader(header.getName(), value);
                // All other headers are copied if allowed
            } else if (isForwardedRequestHeader(header.getName())) {
                httpRequest.addHeader(header);
            }
        }
        // process X-Forwarded-For header (is missing in request and not
        // blacklisted) -> use remote address instead

        if (isForwardedRequestHeader("X-Forwarded-For")) {
            String remoteAddr = HttpRequestHelper.getMediator(originalRequest).getRemoteAddr();

            if (remoteAddr != null) {
                String forwardedFor = HttpRequestHelper.getFirstHeader("X-Forwarded-For", originalRequest);

                if (forwardedFor == null) {
                    forwardedFor = remoteAddr;
                } else {
                    forwardedFor = forwardedFor + ", " + remoteAddr;
                }

                httpRequest.setHeader("X-Forwarded-For", forwardedFor);
            }
        }

        if (isForwardedRequestHeader("X-Forwarded-Proto")) {
            if (HttpRequestHelper.getFirstHeader("X-Forwarded-Proto", originalRequest) == null) {
                // add X-Forwarded-Proto header
                httpRequest.addHeader("X-Forwarded-Proto",
                        UriUtils.extractScheme(originalRequest.getRequestLine().getUri()));
            }
        }
    }

    /**
     * Copies end-to-end headers from a resource to an output.
     * 
     * @param httpRequest
     * @param originalRequest
     * @param httpClientResponse
     * @param output
     * @throws MalformedURLException
     */
    public void copyHeaders(HttpRequest httpRequest, HttpEntityEnclosingRequest originalRequest,
            HttpResponse httpClientResponse, HttpResponse output) throws MalformedURLException {
        String originalUri = originalRequest.getRequestLine().getUri();
        String uri = httpRequest.getRequestLine().getUri();
        for (Header header : httpClientResponse.getAllHeaders()) {
            String name = header.getName();
            String value = header.getValue();
            try {
                // Ignore Content-Encoding and Content-Type as these headers are
                // set
                // in HttpEntity
                if (!HttpHeaders.CONTENT_ENCODING.equalsIgnoreCase(name)) {
                    if (isForwardedResponseHeader(name)) {
                        // Some headers containing an URI have to be rewritten
                        if (HttpHeaders.LOCATION.equalsIgnoreCase(name)
                                || HttpHeaders.CONTENT_LOCATION.equalsIgnoreCase(name)) {
                            // Header contains only an url
                            value = UriUtils.translateUrl(value, uri, originalUri);
                            value = HttpResponseUtils.removeSessionId(value, httpClientResponse);
                            output.addHeader(name, value);
                        } else if ("Link".equalsIgnoreCase(name)) {
                            // Header has the following format
                            // Link: </feed>; rel="alternate"

                            if (value.startsWith("<") && value.contains(">")) {
                                String urlValue = value.substring(1, value.indexOf(">"));

                                String targetUrlValue = UriUtils.translateUrl(urlValue, uri, originalUri);
                                targetUrlValue = HttpResponseUtils.removeSessionId(targetUrlValue, httpClientResponse);

                                value = value.replace("<" + urlValue + ">", "<" + targetUrlValue + ">");
                            }

                            output.addHeader(name, value);

                        } else if ("Refresh".equalsIgnoreCase(name)) {
                            // Header has the following format
                            // Refresh: 5;
                            // url=http://www.w3.org/pub/WWW/People.html
                            int urlPosition = value.indexOf("url=");
                            if (urlPosition >= 0) {
                                String urlValue = value.substring(urlPosition + "url=".length());

                                String targetUrlValue = UriUtils.translateUrl(urlValue, uri, originalUri);
                                targetUrlValue = HttpResponseUtils.removeSessionId(targetUrlValue, httpClientResponse);

                                value = value.substring(0, urlPosition) + "url=" + targetUrlValue;
                            }
                            output.addHeader(name, value);
                        } else if ("P3p".equalsIgnoreCase(name)) {
                            // Do not translate url yet.
                            // P3P is used with a default fixed url most of the
                            // time.
                            output.addHeader(name, value);
                        } else {
                            output.addHeader(header.getName(), header.getValue());
                        }
                    }
                }
            } catch (Exception e1) {
                // It's important not to fail here.
                // An application can always send corrupted headers, and we
                // should not crash
                LOG.error("Error while copying headers", e1);
                output.addHeader("X-Esigate-Error", "Error processing header " + name + ": " + value);
            }
        }
    }

}
