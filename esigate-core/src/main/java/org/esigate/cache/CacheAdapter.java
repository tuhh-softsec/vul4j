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
import java.sql.Date;
import java.util.Properties;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.esigate.ConfigurationException;
import org.esigate.Parameters;
import org.esigate.events.EventManager;
import org.esigate.events.impl.FetchEvent;
import org.esigate.http.DateUtils;

/**
 * This class is changes the behavior of the HttpCache by transforming the
 * headers in the requests or response.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class CacheAdapter {
	private int staleIfError;
	private int staleWhileRevalidate;
	private int ttl;
	private boolean xCacheHeader;
	private boolean viaHeader;

	public void init(Properties properties) {
		staleIfError = Parameters.STALE_IF_ERROR.getValueInt(properties);
		staleWhileRevalidate = Parameters.STALE_WHILE_REVALIDATE.getValueInt(properties);
		int maxAsynchronousWorkers = Parameters.MAX_ASYNCHRONOUS_WORKERS.getValueInt(properties);
		if (staleWhileRevalidate > 0 && maxAsynchronousWorkers == 0)
			throw new ConfigurationException("You must set a positive value for maxAsynchronousWorkers in order to enable background revalidation (staleWhileRevalidate)");
		ttl = Parameters.TTL.getValueInt(properties);
		xCacheHeader = Parameters.X_CACHE_HEADER.getValueBoolean(properties);
		viaHeader = Parameters.VIA_HEADER.getValueBoolean(properties);
	}

	private abstract class ClientExecDecorator implements ClientExecChain {
		private final ClientExecChain wrapped;

		ClientExecDecorator(ClientExecChain wrapped) {
			this.wrapped = wrapped;
		}

		public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext clientContext, HttpExecutionAware execAware) throws IOException, HttpException {
			if (transformRequest(request, clientContext)) {
				CloseableHttpResponse response = wrapped.execute(route, request, clientContext, execAware);
				transformResponse(request, response, clientContext);
				return response;
			}
			// TODO: returning null may be hard. However, this only happens if
			// an extension cancels the request. Need to think on the usecase.
			return null;
		}

		/**
		 * 
		 * @param httpRequest
		 * @param context
		 * 
		 * @return true if we should process with the request.
		 */
		abstract boolean transformRequest(HttpRequest httpRequest, HttpContext context);

		abstract void transformResponse(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext context);

	}

	public ClientExecDecorator wrapCachingHttpClient(final ClientExecChain wrapped) {
		return new ClientExecDecorator(wrapped) {

			/**
			 * Removes client http cache directives like "Cache-control" and
			 * "Pragma". Users must not be able to bypass the cache just by
			 * making a refresh in the browser.
			 */
			@Override
			boolean transformRequest(HttpRequest httpRequest, HttpContext context) {
				return true;
			}

			/**
			 * Restores the real http status code if it has been hidden to
			 * HttpCache
			 */
			@Override
			void transformResponse(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext context) {
				// Remove previously added Cache-control header
				if (httpRequest.getRequestLine().getMethod().equalsIgnoreCase("GET") && (staleWhileRevalidate > 0 || staleIfError > 0)) {
					httpResponse.removeHeader(httpResponse.getLastHeader("Cache-control"));
				}
				// Add X-cache header
				if (xCacheHeader) {
					if (context != null) {
						CacheResponseStatus cacheResponseStatus = (CacheResponseStatus) context.getAttribute(HttpCacheContext.CACHE_RESPONSE_STATUS);
						HttpHost host = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
						String xCacheString;
						if (cacheResponseStatus.equals(CacheResponseStatus.CACHE_HIT))
							xCacheString = "HIT";
						else if (cacheResponseStatus.equals(CacheResponseStatus.VALIDATED))
							xCacheString = "VALIDATED";
						else
							xCacheString = "MISS";
						xCacheString += " from " + host.toHostString();
						xCacheString += " (" + httpRequest.getRequestLine().getMethod() + " " + httpRequest.getRequestLine().getUri() + ")";
						httpResponse.addHeader("X-Cache", xCacheString);
					}
				}

				// Remove Via header
				if (!viaHeader && httpResponse.containsHeader("Via")) {
					httpResponse.removeHeaders("Via");
				}
			}
		};
	}

	public ClientExecDecorator wrapBackendHttpClient(final EventManager eventManager, ClientExecChain wrapped) {
		return new ClientExecDecorator(wrapped) {

			/**
			 * Fire pre-Fetch event
			 */
			@Override
			boolean transformRequest(HttpRequest httpRequest, HttpContext context) {
				// Create request event
				FetchEvent e = new FetchEvent();
				e.httpRequest = httpRequest;
				e.httpResponse = null;
				e.httpContext = context;

				// EVENT pre
				eventManager.fire(EventManager.EVENT_FETCH_PRE, e);

				// Continue if exist is not requested
				return !e.exit;
			}

			/**
			 * Enables cache for all GET requests if cache ttl was forced to a
			 * certain duration in the configuration. This is done even for non
			 * 200 return codes! This is a very aggressive but efficient caching
			 * policy. Adds "stale-while-revalidate" and "stale-if-error"
			 * cache-control directives depending on the configuration.
			 */
			@Override
			void transformResponse(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext context) {

				// Create request event
				FetchEvent e = new FetchEvent();
				e.httpRequest = httpRequest;
				e.httpResponse = httpResponse;
				e.httpContext = context;

				// EVENT pre
				eventManager.fire(EventManager.EVENT_FETCH_POST, e);

				String method = httpRequest.getRequestLine().getMethod();
				int statusCode = httpResponse.getStatusLine().getStatusCode();

				// If ttl is set, force caching even for error pages
				if (ttl > 0 && method.equalsIgnoreCase("GET") && isCacheableStatus(statusCode)) {
					httpResponse.removeHeaders("Date");
					httpResponse.removeHeaders("Cache-control");
					httpResponse.removeHeaders("Expires");
					httpResponse.setHeader("Date", DateUtils.formatDate(new Date(System.currentTimeMillis())));
					httpResponse.setHeader("Cache-control", "public, max-age=" + ttl);
					httpResponse.setHeader("Expires", DateUtils.formatDate(new Date(System.currentTimeMillis() + ((long) ttl) * 1000)));
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

			private boolean isCacheableStatus(int statusCode) {
				return (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_NOT_FOUND
						|| statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE || statusCode == HttpStatus.SC_NOT_MODIFIED);
			}

		};
	}
}
