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

import java.util.Properties;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.esigate.ConfigurationException;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.RequestExecutor;
import org.esigate.cookie.CookieManager;
import org.esigate.events.EventManager;
import org.esigate.extension.ExtensionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientRequestExecutor implements RequestExecutor {
	public static final class HttpClientDriverBuilder implements RequestExecutorBuilder {
		private HttpClientRequestExecutor httpClientDriver = new HttpClientRequestExecutor();
		private EventManager eventManager;
		private Properties properties;
		private Driver driver;

		@Override
		public RequestExecutorBuilder setEventManager(EventManager eventManager) {
			this.eventManager = eventManager;
			return this;
		}

		@Override
		public RequestExecutorBuilder setDriver(Driver driver) {
			this.driver = driver;
			return this;
		}

		@Override
		public RequestExecutorBuilder setProperties(Properties properties) {
			this.properties = properties;
			return this;
		}

		public RequestExecutorBuilder setHttpClientHelper(HttpClientHelper httpClientHelper) {
			httpClientDriver.httpClientHelper = httpClientHelper;
			return this;
		}

		@Override
		public RequestExecutor build() {
			if (eventManager == null)
				throw new ConfigurationException("eventManager is mandatory");
			if (driver == null)
				throw new ConfigurationException("driver is mandatory");
			if (properties == null)
				throw new ConfigurationException("properties is mandatory");
			CookieManager cookieManager = ExtensionFactory.getExtension(properties, Parameters.COOKIE_MANAGER, driver);
			if (httpClientDriver.httpClientHelper == null)
				httpClientDriver.httpClientHelper = new HttpClientHelper(eventManager, cookieManager, properties);
			return httpClientDriver;
		}

		@Override
		public RequestExecutorBuilder setContentTypeHelper(ContentTypeHelper contentTypeHelper) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(HttpClientRequestExecutor.class);
	private HttpClientHelper httpClientHelper;

	public static HttpClientDriverBuilder builder() {
		return new HttpClientDriverBuilder();
	}

	private HttpClientRequestExecutor() {
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
		return httpClientHelper.execute(httpRequest);
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
	public HttpResponse createAndExecuteRequest(HttpEntityEnclosingRequest originalRequest, String targetUrl, boolean proxy) throws HttpErrorPage {
		GenericHttpRequest httpRequest = httpClientHelper.createHttpRequest(originalRequest, targetUrl, proxy);
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
