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

/**
 * 
 * This class is responsible for copying headers from incoming requests to
 * outgoing requests and from incoming responses to outgoing responses.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class HeaderManager {
	private final FilterList requestHeadersFilterList;
	private final FilterList responseHeadersFilterList;

	public HeaderManager(Properties properties) {
		// Populate headers filter lists
		requestHeadersFilterList = new FilterList();
		responseHeadersFilterList = new FilterList();
		// By default all headers are forwarded
		requestHeadersFilterList.add(Collections.singletonList("*"));
		responseHeadersFilterList.add(Collections.singletonList("*"));
		PropertiesUtil.populate(requestHeadersFilterList, properties, Parameters.FORWARD_REQUEST_HEADERS.name, Parameters.DISCARD_REQUEST_HEADERS.name, "",
				Parameters.DISCARD_REQUEST_HEADERS.defaultValue);
		PropertiesUtil.populate(responseHeadersFilterList, properties, Parameters.FORWARD_RESPONSE_HEADERS.name, Parameters.DISCARD_RESPONSE_HEADERS.name, "",
				Parameters.DISCARD_RESPONSE_HEADERS.defaultValue);
	}

	protected boolean isForwardedRequestHeader(String headerName) {
		return requestHeadersFilterList.contains(headerName);
	}

	protected boolean isForwardedResponseHeader(String headerName) {
		return responseHeadersFilterList.contains(headerName);
	}

	public void copyHeaders(HttpRequest originalRequest, HttpRequest httpRequest) throws HttpErrorPage {
		String originalUri = originalRequest.getRequestLine().getUri();
		String uri = httpRequest.getRequestLine().getUri();
		for (Header header : originalRequest.getAllHeaders()) {
			// Special headers
			// User-agent must be set in a specific way
			if (HttpHeaders.USER_AGENT.equalsIgnoreCase(header.getName()) && isForwardedRequestHeader(HttpHeaders.USER_AGENT))
				httpRequest.getParams().setParameter(CoreProtocolPNames.USER_AGENT, header.getValue());
			// Referer must be rewritten
			else if (HttpHeaders.REFERER.equalsIgnoreCase(header.getName()) && isForwardedRequestHeader(HttpHeaders.REFERER)) {
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
		String remoteAddr = HttpRequestHelper.getMediator(originalRequest).getRemoteAddr();
		if (HttpRequestHelper.getFirstHeader("X-Forwarded-For", originalRequest) == null && isForwardedRequestHeader("X-Forwarded-For") && remoteAddr != null) {
			httpRequest.addHeader("X-Forwarded-For", remoteAddr);
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
	public void copyHeaders(HttpRequest httpRequest, HttpEntityEnclosingRequest originalRequest, HttpResponse httpClientResponse, HttpResponse output) throws MalformedURLException {
		String originalUri = originalRequest.getRequestLine().getUri();
		String uri = httpRequest.getRequestLine().getUri();
		for (Header header : httpClientResponse.getAllHeaders()) {
			String name = header.getName();
			String value = header.getValue();
			// Ignore Content-Encoding and Content-Type as these headers are set
			// in HttpEntity
			if (!HttpHeaders.CONTENT_ENCODING.equalsIgnoreCase(name)) {
				if (isForwardedResponseHeader(name)) {
					// Some headers containing an URI have to be rewritten
					if (HttpHeaders.LOCATION.equalsIgnoreCase(name) || HttpHeaders.CONTENT_LOCATION.equalsIgnoreCase(name) || "Link".equalsIgnoreCase(name) || "P3p".equalsIgnoreCase(name)) {
						value = UriUtils.translateUrl(value, uri, originalUri);
						value = HttpResponseUtils.removeSessionId(value, httpClientResponse);
						output.addHeader(name, value);
					} else {
						output.addHeader(header.getName(), header.getValue());
					}
				}
			}
		}
	}

}
