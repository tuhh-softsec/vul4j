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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.esigate.UserContext;
import org.esigate.api.HttpRequest;
import org.esigate.authentication.AuthenticationHandler;
import org.esigate.filter.Filter;
import org.esigate.output.Output;
import org.esigate.resource.Resource;
import org.esigate.resource.ResourceUtils;

/**
 * Resource implementation pointing to a resource on an external application.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
public class HttpResource extends Resource {
	private HttpClientResponse httpClientResponse;
	private final ResourceContext target;
	private final String url;

	public HttpResource(HttpClient httpClient, ResourceContext resourceContext) throws IOException, HttpErrorPage {
		this.target = resourceContext;
		this.url = ResourceUtils.getHttpUrlWithQueryString(resourceContext);

		Driver driver = resourceContext.getDriver();
		HttpRequest originalRequest = resourceContext.getOriginalRequest();

		// Retrieve session and other cookies
		UserContext userContext = resourceContext.getUserContext();

		// Proceed with request
		boolean proxy = resourceContext.isProxy();
		boolean preserveHost = resourceContext.isPreserveHost();
		HttpClientRequest httpClientRequest = new HttpClientRequest(url, originalRequest, proxy, preserveHost);
		httpClientRequest.setCookieStore(CookieAdapter.convertCookieStore(userContext.getCookieStore()));
		httpClientRequest.setConfiguration(driver.getConfiguration());

		// Auth handler
		AuthenticationHandler authenticationHandler = driver.getAuthenticationHandler();
		authenticationHandler.preRequest(httpClientRequest, resourceContext);

		// Filter
		Filter filter = driver.getFilter();
		filter.preRequest(httpClientRequest, resourceContext);

		httpClientResponse = httpClientRequest.execute(httpClient);
		// Save the cookies to session if necessary
		resourceContext.getDriver().saveUserContext(resourceContext.getOriginalRequest());

		while (authenticationHandler.needsNewRequest(httpClientResponse, resourceContext)) {
			// We must first ensure that the connection is always released, if
			// not the connection manager's pool may be exhausted soon !
			httpClientResponse.finish();
			httpClientRequest = new HttpClientRequest(url, originalRequest, proxy, preserveHost);
			httpClientRequest.setCookieStore(CookieAdapter.convertCookieStore(userContext.getCookieStore()));
			httpClientRequest.setConfiguration(driver.getConfiguration());
			// Auth handler
			authenticationHandler.preRequest(httpClientRequest, resourceContext);
			// Filter
			filter.preRequest(httpClientRequest, resourceContext);
			httpClientResponse = httpClientRequest.execute(httpClient);

			// Save the cookies to session if necessary
			resourceContext.getDriver().saveUserContext(resourceContext.getOriginalRequest());
		}
	}

	@Override
	public void render(Output output) throws IOException {
		output.setStatus(httpClientResponse.getStatusCode(), httpClientResponse.getStatusText());
		Resource.copyHeaders(target.getDriver().getConfiguration(), this, output);
		target.getDriver().getFilter().postRequest(httpClientResponse, target);
		// In case of a redirect, we need to rewrite the location header to
		// match provider application and remove any jsessionid in the URL
		copyHeaderAndRewriteUri(HttpHeaders.LOCATION, output);
		copyHeaderAndRewriteUri(HttpHeaders.CONTENT_LOCATION, output);
		copyHeaderAndRewriteUri(HttpHeaders.LINK, output);
		copyHeaderAndRewriteUri(HttpHeaders.P3P, output);
		String charset = httpClientResponse.getContentCharset();
		if (charset != null) {
			output.setCharsetName(charset);
		}
		try {
			output.open();
			InputStream inputStream = httpClientResponse.openStream();
			if (inputStream != null) {
				removeSessionId(inputStream, output);
			}
		} finally {
			output.close();
		}
	}

	private void copyHeaderAndRewriteUri(String headerName, Output output) throws MalformedURLException {
		String headerValue = httpClientResponse.getHeader(headerName);
		if (headerValue != null) {
			headerValue = rewriteLocation(headerValue);
			headerValue = removeSessionId(headerValue);
			output.setHeader(headerName, headerValue);
		}
	}

	/**
	 * Location header rewriting
	 * @param location
	 * @return
	 * @throws MalformedURLException
	 */
	private String rewriteLocation(String location) throws MalformedURLException {
		// TODO Check that this method works for relative URI or URI starting with /
		// TODO refactor with URI rewriting in request headers

		// Make the url absolute
		location = new URL(new URL(url), location).toString();

		HttpRequest request = target.getOriginalRequest();
		String originalRequestURL = request.getRequestURL();

		// Look for the relUrl starting from the end of the url
		int pos = originalRequestURL.lastIndexOf(target.getRelUrl());

		String driverBaseUrl = target.getBaseURL();
		if (pos >= 0) {
			// Remove relUrl from originalBase.
			originalRequestURL = originalRequestURL.substring(0, pos);
			// Add '/' at the end if absent
			if (originalRequestURL.charAt(originalRequestURL.length() - 1) != '/' && driverBaseUrl.charAt(driverBaseUrl.length() - 1) == '/') {
				originalRequestURL += "/";
			}
		}

		return location.replaceFirst(driverBaseUrl, originalRequestURL);
	}

	private void removeSessionId(InputStream inputStream, Output output) throws IOException {
		String jsessionid = RewriteUtils.getSessionId(target);
		boolean textContentType = ResourceUtils.isTextContentType(httpClientResponse.getHeader(HttpHeaders.CONTENT_TYPE), target.getDriver().getConfiguration().getParsableContentTypes());
		if (jsessionid == null || !textContentType) {
			IOUtils.copy(inputStream, output.getOutputStream());
		} else {
			String charset = httpClientResponse.getContentCharset();
			if (charset == null) {
				charset = "ISO-8859-1";
			}
			String content = IOUtils.toString(inputStream, charset);
			content = removeSessionId(jsessionid, content);
			if (output.getHeader(HttpHeaders.CONTENT_LENGTH) != null) {
				output.setHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(content.length()));
			}
			OutputStream outputStream = output.getOutputStream();
			IOUtils.write(content, outputStream, charset);
		}
		inputStream.close();
	}

	private String removeSessionId(String src) {
		String sessionId = RewriteUtils.getSessionId(target);
		return removeSessionId(sessionId, src);
	}

	private String removeSessionId(String sessionId, String src) {
		if (sessionId == null) {
			return src;
		} else {
			return RewriteUtils.removeSessionId(sessionId, src);
		}
	}

	@Override
	public void release() {
		httpClientResponse.finish();
	}

	@Override
	public int getStatusCode() {
		return httpClientResponse.getStatusCode();
	}

	@Override
	public String getStatusMessage() {
		return httpClientResponse.getStatusText();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(target.getOriginalRequest().getMethod());
		result.append(" ");
		result.append(ResourceUtils.getHttpUrlWithQueryString(target));
		result.append("\n");
		result.append(target.getUserContext().toString());
		return result.toString();
	}

	@Override
	public String getHeader(String name) {
		return httpClientResponse.getHeader(name);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return Arrays.asList(httpClientResponse.getHeaders(name));
	}

	@Override
	public Collection<String> getHeaderNames() {
		return httpClientResponse.getHeaderNames();
	}
}
