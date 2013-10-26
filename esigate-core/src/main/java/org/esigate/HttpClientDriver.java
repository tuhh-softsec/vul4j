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

package org.esigate;

import java.util.Properties;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.esigate.cookie.CookieManager;
import org.esigate.extension.ExtensionFactory;
import org.esigate.http.GenericHttpRequest;
import org.esigate.http.HttpClientHelper;
import org.esigate.http.HttpResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientDriver extends Driver {
	private static final Logger LOG = LoggerFactory.getLogger(HttpClientDriver.class);
	private HttpClientHelper httpClientHelper;

	public HttpClientDriver(String name, Properties properties) {
		super(name, properties);
		CookieManager cookieManager = ExtensionFactory.getExtension(properties, Parameters.COOKIE_MANAGER, this);
		httpClientHelper = new HttpClientHelper(getEventManager(), cookieManager, properties);
	}

	public HttpClientDriver(String name, Properties properties, HttpClientHelper httpClientHelper) {
		super(properties, name, httpClientHelper.getEventManager());
		this.httpClientHelper = httpClientHelper;
	}

	/**
	 * Execute a HTTP request
	 * <p>
	 * No special handling.
	 * 
	 * @param httpRequest
	 *            HTTP request to execute.
	 * @return HTTP response.
	 */
	@Override
	public HttpResponse executeSingleRequest(GenericHttpRequest httpRequest) {
		return this.httpClientHelper.execute(httpRequest);
	}

	/**
	 * Execute a HTTP request and handle errors as HttpErrorPage exceptions.
	 * 
	 * @param httpRequest
	 *            HTTP request to execute.
	 * @return HTTP response
	 * @throws HttpErrorPage
	 *             if server returned no response or if the response as an error
	 *             status code.
	 */
	@Override
	protected HttpResponse createAndExecuteRequest(HttpEntityEnclosingRequest originalRequest, String targetUrl, boolean proxy) throws HttpErrorPage {
		GenericHttpRequest httpRequest = this.httpClientHelper.createHttpRequest(originalRequest, targetUrl, proxy);
		HttpResponse httpResponse = executeSingleRequest(httpRequest);
		if (httpResponse == null) {
			throw new HttpErrorPage(500, "Request was cancelled by server", "Request was cancelled by server");
		}
		if (HttpResponseUtils.isError(httpResponse)) {
			throw new HttpErrorPage(httpResponse);
		}
		return httpResponse;
	}

}
