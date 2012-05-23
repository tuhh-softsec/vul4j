/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate.cache;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.esigate.Parameters;
import org.esigate.extension.Extension;

/**
 * This class is changes the behavior of the HttpCache by transforming the headers in the requests or response.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class CacheAdapter implements Extension {
	private final static String STATUS_CODE_HEADER = "Status-code";
	private final static String REASON_PHRASE_HEADER = "Reason-phrase";
	private int staleIfError;
	private int staleWhileRevalidate;
	private int ttl;

	public void init(Properties properties) {
		staleIfError = Parameters.STALE_IF_ERROR.getValueInt(properties);
		staleWhileRevalidate = Parameters.STALE_WHILE_REVALIDATE.getValueInt(properties);
		ttl = Parameters.TTL.getValueInt(properties);
	}

	private abstract class HttpClientWrapper implements HttpClient {
		private final HttpClient wrapped;

		HttpClientWrapper(HttpClient wrapped) {
			this.wrapped = wrapped;
		}

		public HttpParams getParams() {
			return wrapped.getParams();
		}

		public ClientConnectionManager getConnectionManager() {
			return wrapped.getConnectionManager();
		}

		public <T> T execute(HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
			transformRequest(request);
			return wrapped.execute(target, request, new ResponseHandler<T>() {
				public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					transformResponse(request, response);
					return responseHandler.handleResponse(response);
				}
			}, context);
		}

		public <T> T execute(HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
			transformRequest(request);
			return wrapped.execute(target, request, new ResponseHandler<T>() {
				public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					transformResponse(request, response);
					return responseHandler.handleResponse(response);
				}
			});
		}

		public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
			transformRequest(request);
			return wrapped.execute(request, new ResponseHandler<T>() {
				public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					transformResponse(request, response);
					return responseHandler.handleResponse(response);
				}
			}, context);
		}

		public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
			transformRequest(request);
			HttpResponse response = wrapped.execute(target, request, context);
			transformResponse(request, response);
			return response;
		}

		public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
			transformRequest(request);
			return wrapped.execute(request, new ResponseHandler<T>() {
				public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					transformResponse(request, response);
					return responseHandler.handleResponse(response);
				}
			});
		}

		public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
			transformRequest(request);
			HttpResponse response = wrapped.execute(target, request);
			transformResponse(request, response);
			return response;
		}

		public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
			transformRequest(request);
			HttpResponse response = wrapped.execute(request, context);
			transformResponse(request, response);
			return response;
		}

		public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
			transformRequest(request);
			HttpResponse response = wrapped.execute(request);
			transformResponse(request, response);
			return response;
		}

		abstract void transformRequest(HttpRequest httpRequest);

		abstract void transformResponse(HttpRequest httpRequest, HttpResponse httpResponse);

	}

	public HttpClient wrapCachingHttpClient(final HttpClient wrapped) {
		return new HttpClientWrapper(wrapped) {

			/**
			 * Removes client http cache directives like "Cache-control" and "Pragma". Users must not be able to bypass the cache just by making a refresh in the browser.
			 */
			@Override
			void transformRequest(HttpRequest httpRequest) {
				httpRequest.removeHeaders("Cache-control");
				httpRequest.removeHeaders("Pragma");
			}

			/**
			 * Restores the real http status code if it has been hidden to HttpCache
			 */
			@Override
			void transformResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
				// Transform status code
				Header realStatusCode = httpResponse.getFirstHeader(STATUS_CODE_HEADER);
				Header realStatusText = httpResponse.getFirstHeader(REASON_PHRASE_HEADER);
				if (realStatusCode != null) {
					int realStatusCodeInt = Integer.parseInt(realStatusCode.getValue());
					String realReasonPhraseString = realStatusText.getValue();
					httpResponse.setStatusCode(realStatusCodeInt);
					httpResponse.setReasonPhrase(realReasonPhraseString);
					httpResponse.removeHeaders(STATUS_CODE_HEADER);
					httpResponse.removeHeaders(REASON_PHRASE_HEADER);
				}
				// Remove previously added Cache-control header
				if (httpRequest.getRequestLine().getMethod().equalsIgnoreCase("GET") && (staleWhileRevalidate > 0 || staleIfError > 0)) {
					httpResponse.removeHeader(httpResponse.getLastHeader("Cache-control"));
				}

			}
		};
	}

	public HttpClient wrapBackendHttpClient(HttpClient wrapped) {
		return new HttpClientWrapper(wrapped) {

			/**
			 * Currently does nothing
			 */
			@Override
			void transformRequest(HttpRequest httpRequest) {
				// Nothing to do
			}

			/**
			 * Enables cache for all GET requests if cache ttl was forced to a certain duration in the configuration. This is done even for non 200 return codes! This is a very aggressive but
			 * efficient caching policy. Adds "stale-while-revalidate" and "stale-if-error" cache-control directives depending on the configuration.
			 */
			@Override
			void transformResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
				if (ttl > 0 && httpRequest.getRequestLine().getMethod().equalsIgnoreCase("GET")) {
					int statusCode = httpResponse.getStatusLine().getStatusCode();
					if (statusCode != 200) {
						httpResponse.setHeader(STATUS_CODE_HEADER, Integer.toString(httpResponse.getStatusLine().getStatusCode()));
						httpResponse.setHeader(REASON_PHRASE_HEADER, httpResponse.getStatusLine().getReasonPhrase());
						httpResponse.setStatusCode(200);
						httpResponse.setReasonPhrase("OK");
					}
					httpResponse.removeHeaders("Cache-control");
					httpResponse.removeHeaders("Expires");
					httpResponse.setHeader("Cache-control", "public,max-age=" + ttl);
				}
				if (httpRequest.getRequestLine().getMethod().equalsIgnoreCase("GET")) {
					String cacheControlHeader = "";
					if (staleWhileRevalidate > 0)
						cacheControlHeader += "stale-while-revalidate=" + staleWhileRevalidate;
					if (staleIfError > 0) {
						if (cacheControlHeader.length() > 0)
							cacheControlHeader += ",";
						cacheControlHeader += "stale-if-error=" + staleIfError;
					}
					if (cacheControlHeader.length() > 0)
						httpResponse.addHeader("Cache-control", cacheControlHeader);
				}
			}
		};
	}
}
